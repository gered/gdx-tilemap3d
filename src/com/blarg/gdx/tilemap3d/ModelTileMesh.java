package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.graphics.TextureAtlas;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

public class ModelTileMesh extends TileMesh {
	final MeshPartBuilder.VertexInfo vertex = new MeshPartBuilder.VertexInfo();
	final Vector3 tmpPosition = new Vector3();
	final Vector3 tmpNormal = new Vector3();

	Mesh mesh;
	Array<Vector3> collisionVertices;

	@Override
	public Mesh getMesh() {
		return mesh;
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
		MeshBuilder builder = new MeshBuilder();
		builder.begin(
				VertexAttributes.Usage.Position |
				VertexAttributes.Usage.Color |
				VertexAttributes.Usage.Normal |
				VertexAttributes.Usage.TextureCoordinates
		);

		for (int i = 0; i < model.nodes.size; ++i)
			addModelNodeVertices(model.nodes.get(i), builder, textures);

		mesh = builder.end();
	}

	private void addModelNodeVertices(Node node, MeshBuilder builder, Map<String, TextureRegion> textures) {
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
				vertex.setPos(tmpPosition);
				offset += 3;

				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Color) != null) {
					vertex.setCol(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2), vertices.get(offset + 3));
					offset += 4;
				} else
					vertex.setCol(Color.WHITE);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal) != null) {
					tmpNormal.set(vertices.get(offset), vertices.get(offset + 1), vertices.get(offset + 2));
					tmpNormal.mul(transform);
					vertex.setNor(tmpNormal);
					offset += 3;
				} else
					vertex.setNor(Vector3.Zero);

				// TODO: better to throw exception (or check beforehand) if this is missing? setting zero's doesn't feel like the best solution
				if (meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates) != null) {
					vertex.setUV(
							TextureAtlas.scaleTexCoordU(vertices.get(offset), texture),
							TextureAtlas.scaleTexCoordV(vertices.get(offset + 1), texture)
					);
					offset += 3;
				} else
					vertex.setUV(Vector2.Zero);

				builder.vertex(vertex);
			}
		}

		for (int i = 0; i < node.children.size; ++i)
			addModelNodeVertices(node.children.get(i), builder, textures);
	}

	private void setupCollisionVertices(Model collisionModel) {
		int numVertices = 0;
		for (int i = 0; i < collisionModel.meshParts.size; ++i)
			numVertices += collisionModel.meshParts.get(i).numVertices;

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

	@Override
	public void dispose() {
		mesh.dispose();
	}
}
