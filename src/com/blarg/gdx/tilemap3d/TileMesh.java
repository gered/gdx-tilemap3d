package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.Vertices;

public abstract class TileMesh implements Disposable {
	public static final Vector3 OFFSET = new Vector3(0.5f, 0.5f, 0.5f);
	public static final Vector3 UNIT_SIZE = new Vector3(1.0f, 1.0f, 1.0f);
	public static final BoundingBox UNIT_BOUNDS = new BoundingBox(new Vector3(-0.5f, -0.5f, -0.5f), new Vector3(0.5f, 0.5f, 0.5f));

	public static final byte SIDE_TOP = 1;
	public static final byte SIDE_BOTTOM = 2;
	public static final byte SIDE_FRONT = 4;
	public static final byte SIDE_BACK = 8;
	public static final byte SIDE_LEFT = 16;
	public static final byte SIDE_RIGHT = 32;
	public static final byte SIDE_ALL = (SIDE_TOP | SIDE_BOTTOM | SIDE_FRONT | SIDE_BACK | SIDE_LEFT | SIDE_RIGHT);

	public static final int CUBE_VERTICES_PER_FACE = 6;

	public final byte opaqueSides;
	public final boolean alpha;
	public final float translucency;
	public final byte lightValue;
	public final Color color;

	public abstract BoundingBox getBounds();
	public abstract Vertices getVertices();
	public abstract Vector3[] getCollisionVertices();

	public boolean isCompletelyOpaque() {
		return opaqueSides == SIDE_ALL;
	}

	public boolean isOpaque(byte side) {
		return Bitfield.isSet(side, opaqueSides);
	}

	public boolean isLightSource() {
		return lightValue > 0;
	}

	public TileMesh(byte opaqueSides, boolean alpha, float translucency, byte lightValue, Color color) {
		this.opaqueSides = opaqueSides;
		this.alpha = alpha;
		this.translucency = translucency;
		this.lightValue = lightValue;
		this.color = new Color(color);
	}
}
