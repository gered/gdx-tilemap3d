package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.TextureAtlas;
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.math.MathHelpers;

public class CubeTileMesh extends TileMesh {
	static final Vector3 A = new Vector3(-0.5f, -0.5f, -0.5f);
	static final Vector3 B = new Vector3(0.5f, 0.5f, 0.5f);

	BoundingBox bounds;
	Vertices vertices;
	Vector3[] collisionVertices;

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
	public BoundingBox getBounds() {
		return bounds;
	}

	@Override
	public Vertices getVertices() {
		return vertices;
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

		setupFaceVertices(numVertices, topTexture, bottomTexture, frontTexture, backTexture, leftTexture, rightTexture);
		setupCollisionVertices();
		bounds = new BoundingBox(TileMesh.UNIT_BOUNDS);
	}

	private void setupFaceVertices(
			int numVertices,
			TextureRegion topTexture,
			TextureRegion bottomTexture,
			TextureRegion frontTexture,
			TextureRegion backTexture,
			TextureRegion leftTexture,
			TextureRegion rightTexture
	) {
		vertices = new Vertices(
				numVertices,
				VertexAttribute.Position(),
				VertexAttribute.ColorUnpacked(),
				VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0)
		);

		if (hasFace(SIDE_TOP)) {
			vertices.setPos(A.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(1.0f, topTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.UP_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, topTexture), TextureAtlas.scaleTexCoordV(0.0f, topTexture));
			vertices.moveNext();
		}

		if (hasFace(SIDE_BOTTOM)) {
			vertices.setPos(B.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(1.0f, bottomTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.DOWN_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, bottomTexture), TextureAtlas.scaleTexCoordV(0.0f, bottomTexture));
			vertices.moveNext();
		}

		if (hasFace(SIDE_FRONT)) {
			vertices.setPos(B.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(1.0f, frontTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.FORWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, frontTexture), TextureAtlas.scaleTexCoordV(0.0f, frontTexture));
			vertices.moveNext();
		}

		if (hasFace(SIDE_BACK)) {
			vertices.setPos(A.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(1.0f, backTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.BACKWARD_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, backTexture), TextureAtlas.scaleTexCoordV(0.0f, backTexture));
			vertices.moveNext();
		}

		if (hasFace(SIDE_LEFT)) {
			vertices.setPos(A.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			vertices.moveNext();

			vertices.setPos(A.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(1.0f, leftTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			vertices.moveNext();

			vertices.setPos(A.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.LEFT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, leftTexture), TextureAtlas.scaleTexCoordV(0.0f, leftTexture));
			vertices.moveNext();
		}

		if (hasFace(SIDE_RIGHT)) {
			vertices.setPos(B.x, A.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			vertices.moveNext();

			vertices.setPos(B.x, A.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(1.0f, rightTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, A.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(1.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			vertices.moveNext();

			vertices.setPos(B.x, B.y, B.z);
			vertices.setCol(color);
			vertices.setNor(MathHelpers.RIGHT_VECTOR3);
			vertices.setUV(TextureAtlas.scaleTexCoordU(0.0f, rightTexture), TextureAtlas.scaleTexCoordV(0.0f, rightTexture));
			vertices.moveNext();
		}
	}

	private void setupCollisionVertices() {
		collisionVertices = new Vector3[vertices.count()];
		for (int i = 0; i < vertices.count(); ++i) {
			Vector3 v = new Vector3();
			vertices.getPos(i, v);
			collisionVertices[i] = v;
		}
	}

	@Override
	public void dispose() {
	}
}
