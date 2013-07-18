package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

public class TileChunk extends TileContainer implements Disposable {
	final int x;
	final int y;
	final int z;
	final int width;
	final int height;
	final int depth;

	final Tile[] data;
	final BoundingBox bounds;
	final BoundingBox tmpBounds = new BoundingBox();
	final Vector3 position;
	final Vector3 tmpPosition = new Vector3();

	public TileChunkMesh mesh;
	public TileChunkMesh alphaMesh;
	public final TileMap tileMap;

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public int getMinX() {
		return x;
	}

	@Override
	public int getMinY() {
		return y;
	}

	@Override
	public int getMinZ() {
		return z;
	}

	@Override
	public int getMaxX() {
		return x + width - 1;
	}

	@Override
	public int getMaxY() {
		return y + height - 1;
	}

	@Override
	public int getMaxZ() {
		return z + depth - 1;
	}

	@Override
	public Vector3 getPosition() {
		tmpPosition.set(position);
		return tmpPosition;
	}

	@Override
	public BoundingBox getBounds() {
		tmpBounds.set(bounds);
		return tmpBounds;
	}

	public int getNumVertices() {
		return mesh.mesh != null ? mesh.mesh.getNumVertices() : 0;
	}

	public int getNumAlphaVertices() {
		return alphaMesh.mesh != null ? alphaMesh.mesh.getNumVertices() : 0;
	}

	public TileChunk(int x, int y, int z, int width, int height, int depth, TileMap tileMap) {
		if (tileMap == null)
			throw new IllegalArgumentException();

		this.tileMap = tileMap;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.position = new Vector3(x, y, z);
		bounds = new BoundingBox();
		bounds.min.set(x, y, z);
		bounds.max.set(x + width, y + height, z + depth);

		int numTiles = width * height * depth;
		data = new Tile[numTiles];
		for (int i = 0; i < numTiles; ++i)
			data[i] = new Tile();

		mesh = new TileChunkMesh(this, false);
		alphaMesh = new TileChunkMesh(this, true);
	}

	public void updateVertices(ChunkVertexGenerator generator) {
		generator.generate(this);
	}

	public Tile getWithinSelfOrNeighbour(int x, int y, int z) {
		int checkX = x + this.x;
		int checkY = y + this.y;
		int checkZ = z + this.z;
		return tileMap.get(checkX, checkY, checkZ);
	}

	public Tile getWithinSelfOrNeighbourSafe(int x, int y, int z) {
		int checkX = x + this.x;
		int checkY = y + this.y;
		int checkZ = z + this.z;
		if (!tileMap.isWithinBounds(checkX, checkY, checkZ))
			return null;
		else
			return tileMap.get(checkX, checkY, checkZ);
	}

	@Override
	public Tile get(int x, int y, int z) {
		int index = getIndexOf(x, y, z);
		return data[index];
	}

	@Override
	public Tile getSafe(int x, int y, int z) {
		if (!isWithinLocalBounds(x, y, z))
			return null;
		else
			return get(x, y, z);
	}

	private int getIndexOf(int x, int y, int z) {
		return (y * width * depth) + (z * width) + x;
	}

	@Override
	public void dispose() {
		mesh.dispose();
		alphaMesh.dispose();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TileChunk tileChunk = (TileChunk)o;

		if (x != tileChunk.x)
			return false;
		if (y != tileChunk.y)
			return false;
		if (z != tileChunk.z)
			return false;
		if (tileMap != null ? !tileMap.equals(tileChunk.tileMap) : tileChunk.tileMap != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		result = 31 * result + (tileMap != null ? tileMap.hashCode() : 0);
		return result;
	}
}
