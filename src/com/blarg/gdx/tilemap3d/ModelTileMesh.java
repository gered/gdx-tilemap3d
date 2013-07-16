package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.graphics.TextureAtlas;
import com.blarg.gdx.graphics.Vertices;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

public class ModelTileMesh extends TileMesh {
	final Vector3 tmpPosition = new Vector3();
	final Vector3 tmpNormal = new Vector3();

	Vertices vertices;
	Array<Vector3> collisionVertices;

	@Override
	public Vertices getVertices() {
		return vertices;
	}

	@Override
	public Vector3[] getCollisionVertices() {
		return collisionVertices.items;
	}

	public ModelTileMesh(Model model, Map<String, TextureRegion> textures, byte opaqueSides, byte lightValue, boolean alpha, float translucency, Color color) {
		super(opaqueSides, alpha, translucency, lightValue, color);
		setupMesh(model, textures);
		setupCollisionVertices(model);
	}

	public ModelTileMesh(Model model, Model collisionModel, Map<String, TextureRegion> textures, byte opaqueSides, byte lightValue, boolean alpha, float translucency, Color color) {
		super(opaqueSides, alpha, translucency, lightValue, color);
		setupMesh(model, textures);
		setupCollisionVertices(collisionModel);
	}

	private void setupMesh(Model model, Map<String, TextureRegion> textures) {
		int numVertices = countModelVertices(model);
		vertices = new Vertices(
				numVertices,
				VertexAttribute.Position(),
				VertexAttribute.ColorUnpacked(),
				VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0)
		);

		for (int i = 0; i < model.nodes.size; ++i)
			addModelNodeVertices(model.nodes.get(i), textures);
	}

	private void addModelNodeVertices(Node node, Map<String, TextureRegion> textures) {
		final Matrix4 transform = node.globalTransform; // TODO: test that this is the right transform to use?

		for (int i = 0; i < node.parts.size; ++i) {
			NodePart nodePart = node.parts.get(i);
			TextureRegion texture = textures.get(nodePart.material.id);
			MeshPart meshPart = nodePart.meshPart;
			ShortBuffer indices = meshPart.mesh.getIndicesBuffer();
			FloatBuffer vertices = meshPart.mesh.getVerticesBuffer();
			final int strideInFloats = meshPart.mesh.getVertexSize() / (Float.SIZE / 8);

			for (int j = 0; j < meshPart.numVertices; ++j) {
				int index = indices.get(meshPart.indexOffset + j);
				int offset = index * strideInFloats;

				tmpPosition.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2));
				tmpPosition.mul(transform);
				this.vertices.setPos(tmpPosition);
				offset += 3;

				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Color) != null) {
					this.vertices.setCol(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2), vertices.get(offset + 3));
					offset += 4;
				} else
					this.vertices.setCol(Color.WHITE);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal) != null) {
					tmpNormal.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2));
					tmpNormal.mul(transform);
					this.vertices.setNor(tmpNormal);
					offset += 3;
				} else
					this.vertices.setNor(Vector3.Zero);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates) != null) {
					this.vertices.setUV(
							TextureAtlas.scaleTexCoordU(vertices.get(offset), texture),
							TextureAtlas.scaleTexCoordV(vertices.get(offset + 1), texture)
					);
					offset += 3;
				} else
					this.vertices.setUV(Vector2.Zero);

				this.vertices.moveNext();
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			addModelNodeVertices(node.children.get(i), textures);
	}

	private void setupCollisionVertices(Model collisionModel) {
		int numVertices = countModelVertices(collisionModel);
		collisionVertices = new Array<Vector3>(true, numVertices, Vector3.class);
		for (int i = 0; i < collisionModel.nodes.size; ++i)
			addModelNodeCollisionVertices(collisionModel.nodes.get(i));
	}

	private void addModelNodeCollisionVertices(Node node) {
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

				tmpPosition.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2));
				tmpPosition.mul(transform);
				collisionVertices.add(new Vector3(tmpPosition));
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			addModelNodeCollisionVertices(node.children.get(i));
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
