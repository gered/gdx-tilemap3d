package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.graphics.atlas.TextureAtlas;
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.math.MathHelpers;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ModelTileMesh extends TileMesh {
	static final Vector3 tmpPosition = new Vector3();
	static final Vector3 tmpNormal = new Vector3();
	static final BoundingBox tmpModelBounds = new BoundingBox();
	static final Vector3 tmpScaleFactor = new Vector3();

	BoundingBox bounds;
	Vertices vertices;
	Array<Vector3> collisionVertices;
	Vector3 scaleToSize;
	Vector3 positionOffset;
	Vector3 collisionPositionOffset;

	@Override
	public BoundingBox getBounds() {
		return bounds;
	}

	@Override
	public Vertices getVertices() {
		return vertices;
	}

	@Override
	public Vector3[] getCollisionVertices() {
		return collisionVertices.items;
	}

	public ModelTileMesh(
			Model model,
			MaterialTileMapping textures,
			byte opaqueSides,
			byte lightValue,
			boolean alpha,
			float translucency,
			Color color,
			Vector3 scaleToSize,
	        Vector3 positionOffset
	) {
		super(opaqueSides, alpha, translucency, lightValue, color);
		initScalingAndOffsetParams(scaleToSize, positionOffset, null);
		setupMesh(model, textures);
		setupCollisionVertices(model);
	}

	public ModelTileMesh(
			Model model,
			Model collisionModel,
			MaterialTileMapping textures,
			byte opaqueSides,
			byte lightValue,
			boolean alpha,
			float translucency,
			Color color,
			Vector3 scaleToSize,
	        Vector3 positionOffset,
	        Vector3 collisionPositionOffset
	) {
		super(opaqueSides, alpha, translucency, lightValue, color);
		initScalingAndOffsetParams(scaleToSize, positionOffset, collisionPositionOffset);
		setupMesh(model, textures);
		setupCollisionVertices(collisionModel);
	}

	private void initScalingAndOffsetParams(Vector3 scaleToSize, Vector3 positionOffset, Vector3 collisionPositionOffset) {
		this.scaleToSize = (scaleToSize == null ? null : new Vector3(scaleToSize));
		this.positionOffset = (positionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(positionOffset));
		this.collisionPositionOffset = (collisionPositionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(collisionPositionOffset));
	}

	private void setupMesh(Model model, MaterialTileMapping textures) {
		int numVertices = countModelVertices(model);
		vertices = new Vertices(
				numVertices,
				VertexAttribute.Position(),
				VertexAttribute.ColorUnpacked(),
				VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0)
		);

		model.getBoundingBox(tmpModelBounds);
		if (scaleToSize != null) {
			model.getBoundingBox(tmpModelBounds);
			MathHelpers.getScaleFactor(tmpModelBounds.getDimensions(), scaleToSize, tmpScaleFactor);
			bounds = new BoundingBox().set(Vector3.Zero, scaleToSize);
		} else {
			bounds = new BoundingBox().set(Vector3.Zero, tmpModelBounds.getDimensions());
			tmpScaleFactor.set(1.0f, 1.0f, 1.0f);
		}

		for (int i = 0; i < model.nodes.size; ++i)
			addModelNodeVertices(model.nodes.get(i), textures, tmpScaleFactor);
	}

	private void addModelNodeVertices(Node node, MaterialTileMapping textures, Vector3 scaleFactor) {
		final Matrix4 transform = node.globalTransform; // TODO: test that this is the right transform to use?

		for (int i = 0; i < node.parts.size; ++i) {
			NodePart nodePart = node.parts.get(i);
			MaterialTileMapping.TileTexture texture = textures.get(nodePart.material.id);
			MeshPart meshPart = nodePart.meshPart;
			ShortBuffer indices = meshPart.mesh.getIndicesBuffer();
			FloatBuffer vertices = meshPart.mesh.getVerticesBuffer();
			final int strideInFloats = meshPart.mesh.getVertexSize() / (Float.SIZE / 8);

			for (int j = 0; j < meshPart.numVertices; ++j) {
				int index = indices.get(meshPart.indexOffset + j);
				int offset = index * strideInFloats;

				tmpPosition.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2))
				           .add(positionOffset)
				           .scl(scaleFactor)
				           .mul(transform);
				this.vertices.setPos(tmpPosition);
				offset += 3;

				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Color) != null) {
					// TODO: blend mesh color and source model color somehow?
					this.vertices.setCol(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2), vertices.get(offset + 3));
					offset += 4;
				} else
					this.vertices.setCol(color);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal) != null) {
					tmpNormal.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2))
					         .rot(transform);
					this.vertices.setNor(tmpNormal);
					offset += 3;
				} else
					this.vertices.setNor(Vector3.Zero);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates) != null) {
					this.vertices.setUV(
							TextureAtlas.scaleTexCoordU(vertices.get(offset), texture.materialMinU, texture.materialMaxU, texture.region),
							TextureAtlas.scaleTexCoordV(vertices.get(offset + 1), texture.materialMinV, texture.materialMaxV, texture.region)
					);
					offset += 3;
				} else
					this.vertices.setUV(Vector2.Zero);

				this.vertices.moveNext();
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			addModelNodeVertices(node.children.get(i), textures, tmpScaleFactor);
	}

	private void setupCollisionVertices(Model collisionModel) {
		if (scaleToSize != null) {
			collisionModel.getBoundingBox(tmpModelBounds);
			MathHelpers.getScaleFactor(tmpModelBounds.getDimensions(), scaleToSize, tmpScaleFactor);
		} else
			tmpScaleFactor.set(1.0f, 1.0f, 1.0f);

		int numVertices = countModelVertices(collisionModel);
		collisionVertices = new Array<Vector3>(true, numVertices, Vector3.class);
		for (int i = 0; i < collisionModel.nodes.size; ++i)
			addModelNodeCollisionVertices(collisionModel.nodes.get(i), tmpScaleFactor);
	}

	private void addModelNodeCollisionVertices(Node node, Vector3 scaleFactor) {
		final Matrix4 transform = node.globalTransform; // TODO: test that this is the right transform to use?

		for (int i = 0; i < node.parts.size; ++i) {
			NodePart nodePart = node.parts.get(i);
			MeshPart meshPart = nodePart.meshPart;
			ShortBuffer indices = meshPart.mesh.getIndicesBuffer();
			FloatBuffer vertices = meshPart.mesh.getVerticesBuffer();
			final int strideInFloats = meshPart.mesh.getVertexSize() / (Float.SIZE / 8);

			for (int j = 0; j < meshPart.numVertices; ++j) {
				int index = indices.get(meshPart.indexOffset + j);
				int offset = index * strideInFloats;

				tmpPosition.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2))
						   .add(positionOffset)
				           .scl(scaleFactor)
				           .mul(transform);
				collisionVertices.add(new Vector3(tmpPosition));
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			addModelNodeCollisionVertices(node.children.get(i), tmpScaleFactor);
	}

	private int countModelVertices(Model model) {
		int numVertices = 0;
		for (int i = 0; i < model.meshParts.size; ++i)
			numVertices += model.meshParts.get(i).numVertices;
		return numVertices;
	}

	@Override
	public void dispose() {
	}
}
