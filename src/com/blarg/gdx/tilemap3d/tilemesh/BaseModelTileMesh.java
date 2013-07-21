package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.Color;
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
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.graphics.atlas.TextureAtlas;
import com.blarg.gdx.math.MathHelpers;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class BaseModelTileMesh extends TileMesh {
	private final Vector3 tmpPosition = new Vector3();
	private final Vector3 tmpNormal = new Vector3();
	private final BoundingBox tmpBounds = new BoundingBox();

	public BaseModelTileMesh(byte opaqueSides, boolean alpha, float translucency, byte lightValue, Color color) {
		super(opaqueSides, alpha, translucency, lightValue, color);
	}


	protected void collectModelNodeVertices(Node node, Vertices destVertices, MaterialTileMapping textures, Color color, Vector3 scaleFactor, Vector3 positionOffset) {
		final Matrix4 transform = node.globalTransform;

		for (int i = 0; i < node.parts.size; ++i) {
			NodePart nodePart = node.parts.get(i);
			MaterialTileMapping.TileTexture texture = textures.get(nodePart.material.id);
			MeshPart meshPart = nodePart.meshPart;
			ShortBuffer indices = meshPart.mesh.getIndicesBuffer();
			FloatBuffer vertices = meshPart.mesh.getVerticesBuffer();
			final int strideInFloats = meshPart.mesh.getVertexSize() / (Float.SIZE / 8);

			if (destVertices.remainingSpace() < meshPart.numVertices)
				throw new RuntimeException("Not enough space to collect this MeshPart's vertices.");

			for (int j = 0; j < meshPart.numVertices; ++j) {
				int index = indices.get(meshPart.indexOffset + j);
				int offset = index * strideInFloats;

				tmpPosition.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2))
				           .add(positionOffset)
				           .scl(scaleFactor)
				           .mul(transform);
				destVertices.setPos(tmpPosition);
				offset += 3;

				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Color) != null) {
					// TODO: blend mesh color and source model color somehow?
					destVertices.setCol(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2), vertices.get(offset + 3));
					offset += 4;
				} else
					destVertices.setCol(color);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal) != null) {
					tmpNormal.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2))
					         .rot(transform);
					destVertices.setNor(tmpNormal);
					offset += 3;
				} else
					destVertices.setNor(Vector3.Zero);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates) != null) {
					destVertices.setUV(
							TextureAtlas.scaleTexCoordU(vertices.get(offset), texture.materialMinU, texture.materialMaxU, texture.region),
							TextureAtlas.scaleTexCoordV(vertices.get(offset + 1), texture.materialMinV, texture.materialMaxV, texture.region)
					);
					offset += 3;
				} else
					destVertices.setUV(Vector2.Zero);

				destVertices.moveNext();
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			collectModelNodeVertices(node.children.get(i), destVertices, textures, color, scaleFactor, positionOffset);
	}

	protected void collectModelNodeVertexPositions(Node node, Array<Vector3> destVertices, Vector3 scaleFactor, Vector3 positionOffset) {
		final Matrix4 transform = node.globalTransform;

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
				destVertices.add(new Vector3(tmpPosition));
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			collectModelNodeVertexPositions(node.children.get(i), destVertices, scaleFactor, positionOffset);
	}

	protected void getScaleFactorForModel(Model model, Vector3 scaleToSize, Vector3 out) {
		model.getBoundingBox(tmpBounds);
		MathHelpers.getScaleFactor(tmpBounds.getDimensions(), scaleToSize, out);
	}

	protected int countModelVertices(Model model) {
		int numVertices = 0;
		for (int i = 0; i < model.meshParts.size; ++i)
			numVertices += model.meshParts.get(i).numVertices;
		return numVertices;
	}
}
