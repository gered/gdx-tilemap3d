package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.blarg.gdx.Bitfield;

public final class Tile {
	static final Matrix4 faceNorthRotation = new Matrix4().setToRotation(Vector3.Y, 0.0f);
	static final Matrix4 faceEastRotation = new Matrix4().setToRotation(Vector3.Y, 90.0f);
	static final Matrix4 faceSouthRotation = new Matrix4().setToRotation(Vector3.Y, 180.0f);
	static final Matrix4 faceWestRotation = new Matrix4().setToRotation(Vector3.Y, 270.0f);

	public static final short NO_TILE = 0;

	public static final byte LIGHT_VALUE_MAX = 15;
	public static final byte LIGHT_VALUE_SKY = LIGHT_VALUE_MAX;

	public static final short FLAG_COLLIDEABLE = 1;
	public static final short FLAG_FACE_NORTH = 2;
	public static final short FLAG_FACE_EAST = 4;
	public static final short FLAG_FACE_SOUTH = 8;
	public static final short FLAG_FACE_WEST = 16;
	public static final short FLAG_CUSTOM_COLOR = 32;
	public static final short FLAG_FRICTION_SLIPPERY = 64;
	public static final short FLAG_LIGHT_SKY = 128;
	public static final short FLAG_WALKABLE_SURFACE = 256;

	public short tile;
	public short flags;
	public byte tileLight;
	public byte skyLight;
	public int color;

	// remaining padding bytes to keep total size as a multiple of 2
	// TODO: use these for something useful!
	public short padding1;
	public short padding2;
	public short padding3;

	public Tile() {
		tile = NO_TILE;
	}

	public Tile set(short tileIndex) {
		this.tile = tileIndex;
		return this;
	}

	public Tile set(short tileIndex, short flags) {
		this.tile = tileIndex;
		this.flags = flags;
		return this;
	}

	public Tile set(short tileIndex, short flags, final Color color) {
		return set(tileIndex, flags, color.toIntBits());
	}

	public Tile set(short tileIndex, short flags, int color) {
		flags = Bitfield.set(FLAG_CUSTOM_COLOR, flags);
		this.tile = tileIndex;
		this.flags = flags;
		this.color = color;
		return this;
	}

	public Tile setCustomColor(final Color color) {
		return setCustomColor(color.toIntBits());
	}

	public Tile setCustomColor(int color) {
		flags = Bitfield.set(FLAG_CUSTOM_COLOR, flags);
		this.color = color;
		return this;
	}

	public Tile clearCustomColor() {
		flags = Bitfield.clear(FLAG_CUSTOM_COLOR, flags);
		color = 0;
		return this;
	}

	public float getBrightness() {
		if (tileLight > skyLight)
			return getBrightness(tileLight);
		else
			return getBrightness(skyLight);
	}

	public boolean isEmptySpace() {
		return tile == NO_TILE;
	}

	public boolean isCollideable() {
		return Bitfield.isSet(FLAG_COLLIDEABLE, flags);
	}

	public boolean hasCustomColor() {
		return Bitfield.isSet(FLAG_CUSTOM_COLOR, flags);
	}

	public boolean isSlippery() {
		return Bitfield.isSet(FLAG_FRICTION_SLIPPERY, flags);
	}

	public boolean isSkyLit() {
		return Bitfield.isSet(FLAG_LIGHT_SKY, flags);
	}

	public static float getBrightness(byte light) {
		// this is a copy of the brightness formula listed here:
		// http://gamedev.stackexchange.com/a/21247

		final float BASE_BRIGHTNESS = 0.086f;
		float normalizedLightValue = (float)light / (float)(LIGHT_VALUE_MAX + 1);
		return (float)Math.pow((float)normalizedLightValue, 1.4f) + BASE_BRIGHTNESS;
	}

	public static byte adjustLightForTranslucency(byte light, float translucency) {
		return (byte)Math.round((float)light * translucency);
	}

	public static Matrix4 getTransformationFor(Tile tile) {
		if (Bitfield.isSet(FLAG_FACE_NORTH, tile.flags))
			return faceNorthRotation;
		else if (Bitfield.isSet(FLAG_FACE_EAST, tile.flags))
			return faceEastRotation;
		else if (Bitfield.isSet(FLAG_FACE_SOUTH, tile.flags))
			return faceSouthRotation;
		else if (Bitfield.isSet(FLAG_FACE_WEST, tile.flags))
			return faceWestRotation;
		else
			return null;
	}
}
