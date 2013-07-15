package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.TextureAtlas;
import com.blarg.gdx.math.MathHelpers;

import java.nio.FloatBuffer;

public class CubeTileMesh extends TileMesh {
	static final Vector3 A = new Vector3(-0.5f, -0.5f, -0.5f);
	static final Vector3 B = new Vector3(0.5f, 0.5f, 0.5f);
	
	final Mesh mesh;
	final Vector3[] collisionVertices;

	public final byte faces;
	public final int topFaceVertexOffset;
	public final int bottomFaceVertexOffset;
	public final int frontFaceVertexOffset;
	public final int backFaceVertexOffset;
	public final int leftFaceVertexOffset;
	public final int rightFaceVertexOffset;

	public boolean hasFace(byte side) {
		return Bitfield.isSet(side, faces);
	}

	@Override
	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public Vector3[] getCollisionVertices() {
		return collisionVertices;
	}

	public CubeTileMesh(
			TextureRegion topTexture,
			TextureRegion bottomTexture,
			TextureRegion frontTexture,
			TextureRegion backTexture,
			TextureRegion leftTexture,
			TextureRegion rightTexture,
			byte faces, byte opaqueSides, byte lightValue, boolean alpha, float translucency, Color color) {
		super(opaqueSides, alpha, translucency, lightValue, color);
		this.faces = faces;

		if (faces == 0)
			throw new IllegalArgumentException();

		int numVertices = 0;

		if (hasFace(SIDE_TOP)) {
			topFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			topFaceVertexOffset = 0;

		if (hasFace(SIDE_BOTTOM)) {
			bottomFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			bottomFaceVertexOffset = 0;

		if (hasFace(SIDE_FRONT)) {
			frontFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			frontFaceVertexOffset = 0;

		if (hasFace(SIDE_BACK)) {
			backFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			backFaceVertexOffset = 0;

		if (hasFace(SIDE_LEFT)) {
			leftFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			leftFaceVertexOffset = 0;

		if (hasFace(SIDE_RIGHT)) {
			rightFaceVertexOffset = numVertices;
			numVertices += CUBE_VERTICES_PER_FACE;
		} else
			rightFaceVertexOffset = 0;

		mesh = setupFaceVertices(topTexture, bottomTexture, frontTexture, backTexture, leftTexture, rightTexture);
		collisionVertices = setupCollisionVertices();
	}

	private Mesh setupFaceVertices(
			TextureRegion topTexture,
			TextureRegion bottomTexture,
			TextureRegion frontTexture,
			TextureRegion backTexture,
			TextureRegion leftTexture,
			TextureRegion rightTexture
	) {
		MeshPartBuilder.VertexInfo vertex = new MeshPartBuilder.VertexInfo();

		MeshBuilder builder = new MeshBuilder();
		builder.begin(
				VertexAttributes.Usage.Position |
				VertexAttributes.Usage.Color |
				VertexAttributes.Usage.Normal |
				VertexAttributes.Usage.TextureCoordinates
		);

		if (hasFace(SIDE_TOP)) {
			vertex.setPos(A.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.UP_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			builder.vertex(vertex);
		}

		if (hasFace(SIDE_BOTTOM)) {
			vertex.setPos(B.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.DOWN_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			builder.vertex(vertex);
		}

		if (hasFace(SIDE_FRONT)) {
			vertex.setPos(B.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.FORWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			builder.vertex(vertex);
		}

		if (hasFace(SIDE_BACK)) {
			vertex.setPos(A.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			builder.vertex(vertex);
		}

		if (hasFace(SIDE_LEFT)) {
			vertex.setPos(A.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			builder.vertex(vertex);

			vertex.setPos(A.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.LEFT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			builder.vertex(vertex);
		}

		if (hasFace(SIDE_RIGHT)) {
			vertex.setPos(B.x, A.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, A.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, A.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			builder.vertex(vertex);

			vertex.setPos(B.x, B.y, B.z);
			vertex.setCol(color);
			vertex.setNor(MathHelpers.RIGHT_VECTOR3);
			vertex.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			builder.vertex(vertex);
		}

		return builder.end();
	}

	private Vector3[] setupCollisionVertices() {
		Vector3[] vertices = new Vector3[mesh.getNumVertices()];
		FloatBuffer meshVertices = mesh.getVerticesBuffer();
		int strideInFloats = mesh.getVertexSize() / (Float.SIZE / 8);

		for (int i = 0; i < mesh.getNumVertices(); ++i) {
			int offset = i * strideInFloats;
			// NOTE: we assume position is always at the start of each vertex... I believe libgdx will _always_ do this
			Vector3 v = new Vector3();
			v.x = meshVertices.get(offset);
			v.y = meshVertices.get(offset + 1);
			v.z = meshVertices.get(offset + 2);
			vertices[i] = v;
		}

		return vertices;
	}

	@Override
	public void dispose() {
		mesh.dispose();
	}
}
