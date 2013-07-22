package com.blarg.gdx.tilemap3d.prefabs;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.blarg.gdx.tilemap3d.Tile;
import com.blarg.gdx.tilemap3d.TileContainer;

public class TilePrefab {
	public enum Rotation {
		ROT0(0),
		ROT90(90),
		ROT180(180),
		ROT270(270);

		int value;

		private Rotation(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
	};

	Tile[] data;
	int width;
	int height;
	int depth;
	final BoundingBox bounds = new BoundingBox();
	final BoundingBox tmpBounds = new BoundingBox();

	Rotation rotation;
	int rotationWidth;
	int rotationDepth;
	int rotationXOffset;
	int rotationZOffset;
	int rotationXMultiplier;
	int rotationZMultiplier;
	int rotationXPreMultiplier;
	int rotationZPreMultiplier;
	final BoundingBox rotationBounds = new BoundingBox();
	final BoundingBox tmpRotationBounds = new BoundingBox();

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	public BoundingBox getBounds() {
		tmpBounds.set(bounds);
		return tmpBounds;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public int getRotationAngle() {
		return rotation.value;
	}

	public int getRotatedWidth() {
		return rotationWidth;
	}

	public int getRotatedDepth() {
		return rotationDepth;
	}

	public BoundingBox getRotatedBounds() {
		tmpRotationBounds.set(rotationBounds);
		return tmpRotationBounds;
	}

	public TilePrefab(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;

		bounds.min.set(Vector3.Zero);
		bounds.max.set(width, height, depth);

		int numTiles = width * height * depth;
		data = new Tile[numTiles];
		for (int i = 0; i < numTiles; ++i)
			data[i] = new Tile();

		rotate(Rotation.ROT0);
	}

	public Tile get(int x, int y, int z) {
		int index = getIndexOf(x, y, z);
		return data[index];
	}

	public void rotate(Rotation rotation) {
		this.rotation = rotation;

		switch (rotation)
		{
			case ROT0:
				rotationWidth = width;
				rotationDepth = depth;
				rotationXOffset = 0;
				rotationZOffset = 0;
				rotationXMultiplier = 1;
				rotationZMultiplier = rotationWidth;
				rotationXPreMultiplier = 1;
				rotationZPreMultiplier = 1;
				rotationBounds.min.set(Vector3.Zero);
				rotationBounds.max.set(rotationWidth, height, rotationDepth);
				break;
			case ROT90:
				rotationWidth = depth;
				rotationDepth = width;
				rotationXOffset = rotationWidth - 1;
				rotationZOffset = 0;
				rotationXMultiplier = rotationDepth;
				rotationZMultiplier = 1;
				rotationXPreMultiplier = -1;
				rotationZPreMultiplier = 1;
				rotationBounds.min.set(Vector3.Zero);
				rotationBounds.max.set(rotationWidth, height, rotationDepth);
				break;
			case ROT180:
				rotationWidth = width;
				rotationDepth = depth;
				rotationXOffset = rotationWidth - 1;
				rotationZOffset = rotationDepth - 1;
				rotationXMultiplier = 1;
				rotationZMultiplier = rotationWidth;
				rotationXPreMultiplier = -1;
				rotationZPreMultiplier = -1;
				rotationBounds.min.set(Vector3.Zero);
				rotationBounds.max.set(rotationWidth, height, rotationDepth);
				break;
			case ROT270:
				rotationWidth = depth;
				rotationDepth = width;
				rotationXOffset = 0;
				rotationZOffset = rotationDepth - 1;
				rotationXMultiplier = rotationDepth;
				rotationZMultiplier = 1;
				rotationXPreMultiplier = 1;
				rotationZPreMultiplier = -1;
				rotationBounds.min.set(Vector3.Zero);
				rotationBounds.max.set(rotationWidth, height, rotationDepth);
				break;
		}
	}

	public Tile getWithRotation(int x, int y, int z) {
		int index = getIndexOfWithRotation(x, y, z);
		return data[index];
	}

	public void placeIn(TileContainer destination, int minX, int minY, int minZ, Rotation rotation, boolean copyEmptyTiles) {
		if (destination == null)
			throw new IllegalArgumentException();

		if (!this.rotation.equals(rotation))
			rotate(rotation);
		if (!((minX + rotationWidth) < destination.getWidth()))
			throw new RuntimeException("Destination not large enough.");
		if (!((minY + height) < destination.getHeight()))
			throw new RuntimeException("Destination not large enough.");
		if (!((minZ + rotationDepth) < destination.getDepth()))
			throw new RuntimeException("Destination not large enough.");

		for (int y = 0; y < height; ++y)
		{
			for (int z = 0; z < rotationDepth; ++z)
			{
				for (int x = 0; x < rotationWidth; ++x)
				{
					Tile sourceTile = getWithRotation(x, y, z);
					if (!copyEmptyTiles && sourceTile.isEmptySpace())
						continue;

					Tile destTile = destination.get(minX + x, minY + y, minZ + z);
					destTile.set(sourceTile);
				}
			}
		}
	}

	private int getIndexOf(int x, int y, int z) {
		return (y * width * depth) + (z * width) + x;
	}

	private int getIndexOfWithRotation(int x, int y, int z) {
		return (y * rotationWidth * rotationDepth)
				+ ((rotationZPreMultiplier * z + rotationZOffset) * rotationZMultiplier)
				+ ((rotationXPreMultiplier * x + rotationXOffset) * rotationXMultiplier);
	}
}
