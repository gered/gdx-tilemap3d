package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.math.Vector3;

public final class TileCoord {
	public int x;
	public int y;
	public int z;

	public TileCoord() {
		x = 0;
		y = 0;
		z = 0;
	}

	public TileCoord(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public TileCoord(TileCoord coord) {
		x = coord.x;
		y = coord.y;
		z = coord.z;
	}

	public TileCoord(Vector3 vector) {
		x = (int)vector.x;
		y = (int)vector.y;
		z = (int)vector.z;
	}

	public TileCoord set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public TileCoord set(TileCoord coord) {
		return set(coord.x, coord.y, coord.z);
	}

	public TileCoord set(Vector3 vector) {
		return set((int)vector.x, (int)vector.y, (int)vector.z);
	}

	public TileCoord add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public TileCoord add(TileCoord coord) {
		return add(coord.x, coord.y, coord.z);
	}

	public TileCoord add(Vector3 vector) {
		return add((int)vector.x, (int)vector.y, (int)vector.z);
	}

	public TileCoord sub(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public TileCoord sub(TileCoord coord) {
		return sub(coord.x, coord.y, coord.z);
	}

	public TileCoord sub(Vector3 vector) {
		return sub((int)vector.x, (int)vector.y, (int)vector.z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TileCoord tileCoord = (TileCoord)o;

		if (x != tileCoord.x)
			return false;
		if (y != tileCoord.y)
			return false;
		if (z != tileCoord.z)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}
}
