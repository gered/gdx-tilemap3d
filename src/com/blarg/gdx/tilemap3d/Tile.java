package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.math.MathHelpers;

public final class Tile {
	static final Matrix4 faceNorthRotation = new Matrix4().setToRotation(Vector3.Y, 0.0f);
	static final Matrix4 faceEastRotation = new Matrix4().setToRotation(Vector3.Y, 90.0f);
	static final Matrix4 faceSouthRotation = new Matrix4().setToRotation(Vector3.Y, 180.0f);
	static final Matrix4 faceWestRotation = new Matrix4().setToRotation(Vector3.Y, 270.0f);

	public static final byte ROTATION_0 = 0;
	public static final byte ROTATION_90 = 1;
	public static final byte ROTATION_180 = 2;
	public static final byte ROTATION_270 = 3;

	public static final short NO_TILE = 0;

	public static final byte LIGHT_VALUE_MAX = 15;
	public static final byte LIGHT_VALUE_SKY = LIGHT_VALUE_MAX;

	public static final short FLAG_COLLIDEABLE = 1;
	public static final short FLAG_ROTATED = 2;
	public static final short FLAG_LARGE_TILE = 4;
	public static final short FLAG_LARGE_TILE_OWNER = 8;
	public static final short FLAG_CUSTOM_COLOR = 16;
	public static final short FLAG_FRICTION_SLIPPERY = 32;
	public static final short FLAG_LIGHT_SKY = 64;
	public static final short FLAG_WALKABLE_SURFACE = 128;

	public short tile;
	public short flags;
	public byte tileLight;
	public byte skyLight;
	public byte rotation;
	public byte parentTileOffsetX;
	public byte parentTileOffsetY;
	public byte parentTileOffsetZ;
	public byte parentTileWidth;
	public byte parentTileHeight;
	public byte parentTileDepth;
	public int color;

	public Tile() {
		tile = NO_TILE;
	}

	public Tile set(int tileIndex) {
		this.tile = (short)tileIndex;
		return this;
	}

	public Tile set(int tileIndex, short flags) {
		this.tile = (short)tileIndex;
		this.flags = flags;
		return this;
	}

	public Tile set(int tileIndex, short flags, final Color color) {
		return set(tileIndex, flags, color.toIntBits());
	}

	public Tile set(int tileIndex, short flags, int color) {
		flags = Bitfield.set(FLAG_CUSTOM_COLOR, flags);
		this.tile = (short)tileIndex;
		this.flags = flags;
		this.color = color;
		return this;
	}

	public Tile set(Tile other) {
		this.tile = other.tile;
		this.flags = other.flags;
		this.tileLight = other.tileLight;
		this.skyLight = other.skyLight;
		this.rotation = other.rotation;
		this.parentTileOffsetX = other.parentTileOffsetX;
		this.parentTileOffsetY = other.parentTileOffsetY;
		this.parentTileOffsetZ = other.parentTileOffsetZ;
		this.parentTileWidth = other.parentTileWidth;
		this.parentTileHeight = other.parentTileHeight;
		this.parentTileDepth = other.parentTileDepth;
		this.color = other.color;
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

	public boolean isRotated() {
		return Bitfield.isSet(FLAG_ROTATED, flags);
	}

	public boolean isLargeTile() {
		return Bitfield.isSet(FLAG_LARGE_TILE, flags);
	}

	public boolean isLargeTileRoot() {
		return Bitfield.isSet(FLAG_LARGE_TILE_OWNER, flags);
	}

	public Tile rotate(byte facingDirection) {
		if (facingDirection < 0 || facingDirection > 3)
			throw new RuntimeException("Use one of the ROTATION_X constants.");
		flags = Bitfield.set(FLAG_ROTATED, flags);
		rotation = facingDirection;
		return this;
	}

	public Tile rotateClockwise() {
		flags = Bitfield.set(FLAG_ROTATED, flags);
		rotation -= 1;
		if (rotation < ROTATION_0)
			rotation = ROTATION_270;
		return this;
	}

	public Tile rotateClockwise(int times) {
		flags = Bitfield.set(FLAG_ROTATED, flags);
		rotation = (byte)MathHelpers.rolloverClamp((int)(rotation -= times), (int)ROTATION_0, (int)ROTATION_270 + 1);
		return this;
	}

	public Tile rotateCounterClockwise() {
		flags = Bitfield.set(FLAG_ROTATED, flags);
		rotation += 1;
		if (rotation > ROTATION_270)
			rotation = ROTATION_0;
		return this;
	}

	public Tile rotateCounterClockwise(int times) {
		flags = Bitfield.set(FLAG_ROTATED, flags);
		rotation = (byte)MathHelpers.rolloverClamp((int)(rotation += times), (int)ROTATION_0, (int)ROTATION_270 + 1);
		return this;
	}

	public float getRotationAngle() {
		if (rotation < 0 || rotation > 3)
			return 0.0f;
		else
			return rotation * 90.0f;
	}

	public static float getBrightness(byte light) {
		// this is a copy of the brightness formula listed here:
		// http://gamedev.stackexchange.com/a/21247

		final float BASE_BRIGHTNESS = 0.086f;
		float normalizedLightValue = (float)light / (float)(LIGHT_VALUE_MAX + 1);
		return (float)Math.pow((float)normalizedLightValue, 1.4f) + BASE_BRIGHTNESS;
	}

	public static byte adjustLightForTranslucency(byte light, float translucency) {
		return (byte)Math.round((float)light * (1.0f - translucency));
	}

	public static Matrix4 getTransformationFor(Tile tile) {
		if (!tile.isRotated())
			return null;
		switch (tile.rotation) {
			case 0: return faceNorthRotation;
			case 1: return faceEastRotation;
			case 2: return faceSouthRotation;
			case 3: return faceWestRotation;
			default: return null;
		}
	}
}
