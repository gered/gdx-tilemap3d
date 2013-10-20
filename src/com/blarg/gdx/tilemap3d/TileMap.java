package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.math.IntersectionTester;
import com.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import com.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;

public class TileMap extends TileContainer implements Disposable {
	final TileChunk[] chunks;
	final BoundingBox bounds;
	final BoundingBox tmpBounds = new BoundingBox();
	final Vector3 tmpPosition = new Vector3();

	public final int chunkWidth;
	public final int chunkHeight;
	public final int chunkDepth;
	public final int widthInChunks;
	public final int heightInChunks;
	public final int depthInChunks;

	public final TileMeshCollection tileMeshes;
	public final ChunkVertexGenerator vertexGenerator;
	public final TileMapLighter lighter;
	public byte ambientLightValue;
	public byte skyLightValue;

	public TileChunk[] getChunks() {
		return chunks;
	}

	@Override
	public int getWidth() {
		return widthInChunks * chunkWidth;
	}

	@Override
	public int getHeight() {
		return heightInChunks * chunkHeight;
	}

	@Override
	public int getDepth() {
		return depthInChunks * chunkDepth;
	}

	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getMinZ() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return getWidth() - 1;
	}

	@Override
	public int getMaxY() {
		return getHeight() - 1;
	}

	@Override
	public int getMaxZ() {
		return getDepth() - 1;
	}

	@Override
	public Vector3 getPosition() {
		tmpPosition.set(Vector3.Zero);
		return tmpPosition;
	}

	@Override
	public BoundingBox getBounds() {
		tmpBounds.set(bounds);
		return bounds;
	}

	public TileMap(
			int chunkWidth, int chunkHeight, int chunkDepth,
	        int widthInChunks, int heightInChunks, int depthInChunks,
	        TileMeshCollection tileMeshes,
	        ChunkVertexGenerator vertexGenerator,
	        TileMapLighter lighter
			) {
		if (tileMeshes == null)
			throw new IllegalArgumentException();
		if (vertexGenerator == null)
			throw new IllegalArgumentException();

		this.tileMeshes = tileMeshes;
		this.vertexGenerator = vertexGenerator;
		this.lighter = lighter;
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;
		this.chunkDepth = chunkDepth;
		this.widthInChunks = widthInChunks;
		this.heightInChunks = heightInChunks;
		this.depthInChunks = depthInChunks;

		ambientLightValue = 0;
		skyLightValue = Tile.LIGHT_VALUE_SKY;

		int numChunks = widthInChunks * heightInChunks * depthInChunks;
		chunks = new TileChunk[numChunks];

		for (int y = 0; y < heightInChunks; ++y)
		{
			for (int z = 0; z < depthInChunks; ++z)
			{
				for (int x = 0; x < widthInChunks; ++x)
				{
					TileChunk chunk = new TileChunk(
							x * chunkWidth, y * chunkHeight, z * chunkDepth,
							chunkWidth, chunkHeight, chunkDepth,
							this
					);

					int index = getChunkIndex(x, y, z);
					chunks[index] = chunk;
				}
			}
		}

		bounds = new BoundingBox();
		bounds.min.set(Vector3.Zero);
		bounds.max.set(getWidth(), getHeight(), getDepth());
	}

	public void updateVertices() {
		for (int i = 0; i < chunks.length; ++i)
			chunks[i].updateVertices(vertexGenerator);
	}

	public void updateLighting() {
		if (lighter != null)
			lighter.light(this);
	}

	public boolean getOverlappedChunks(BoundingBox box, TileCoord min, TileCoord max) {
		// make sure the given box actually intersects with the map in the first place
		if (!IntersectionTester.test(bounds, box))
			return false;

		// convert to tile coords. this is in "tilemap space" which is how we want it
		// HACK: ceil() calls and "-1"'s keep us from picking up too many/too few
		// blocks. these were arrived at through observation
		int minX = (int)box.min.x;
		int minY = (int)box.min.y;
		int minZ = (int)box.min.z;
		int maxX = MathUtils.ceil(box.max.x);
		int maxY = MathUtils.ceil(box.max.y - 1.0f);
		int maxZ = MathUtils.ceil(box.max.z);

		// now convert to chunk coords
		int minChunkX = minX / chunkWidth;
		int minChunkY = minY / chunkHeight;
		int minChunkZ = minZ / chunkDepth;
		int maxChunkX = maxX / chunkWidth;
		int maxChunkY = maxY / chunkHeight;
		int maxChunkZ = maxZ / chunkDepth;

		// trim off the excess bounds so that we end up with a min-to-max area
		// that is completely within the chunk bounds of the map
		// HACK: "-1"'s keep us from picking up too many chunks. these were arrived
		// at through observation
		minChunkX = MathUtils.clamp(minChunkX, 0, widthInChunks);
		minChunkY = MathUtils.clamp(minChunkY, 0, (heightInChunks - 1));
		minChunkZ = MathUtils.clamp(minChunkZ, 0, depthInChunks);
		maxChunkX = MathUtils.clamp(maxChunkX, 0, widthInChunks);
		maxChunkY = MathUtils.clamp(maxChunkY, 0, (heightInChunks - 1));
		maxChunkZ = MathUtils.clamp(maxChunkZ, 0, depthInChunks);

		// return the leftover area
		min.x = minChunkX;
		min.y = minChunkY;
		min.z = minChunkZ;
		max.x = maxChunkX;
		max.y = maxChunkY;
		max.z = maxChunkZ;

		return true;
	}

	@Override
	public Tile get(int x, int y, int z) {
		TileChunk chunk = getChunkContaining(x, y, z);
		int chunkX = x - chunk.x;
		int chunkY = y - chunk.y;
		int chunkZ = z - chunk.z;

		return chunk.get(chunkX, chunkY, chunkZ);
	}

	@Override
	public Tile getSafe(int x, int y, int z) {
		if (!isWithinBounds(x, y, z))
			return null;
		else
			return get(x, y, z);
	}

	public TileChunk getChunk(int chunkX, int chunkY, int chunkZ) {
		int index = getChunkIndex(chunkX, chunkY, chunkZ);
		return chunks[index];
	}

	public TileChunk getChunkSafe(int chunkX, int chunkY, int chunkZ) {
		if (
				(chunkX >= widthInChunks) ||
				(chunkY >= heightInChunks) ||
				(chunkZ >= depthInChunks)
			)
			return null;
		else
			return getChunk(chunkX, chunkY, chunkZ);
	}

	public TileChunk getChunkNextTo(TileChunk chunk, int chunkOffsetX, int chunkOffsetY, int chunkOffsetZ) {
		int checkX = chunk.x + chunkOffsetX;
		int checkY = chunk.y + chunkOffsetY;
		int checkZ = chunk.z + chunkOffsetZ;

		if (
				(checkX < 0 || checkX >= widthInChunks) ||
				(checkY < 0 || checkY >= heightInChunks) ||
				(checkZ < 0 || checkZ >= depthInChunks)
			)
			return null;
		else
			return getChunk(checkX, checkY, checkZ);
	}

	public TileChunk getChunkContaining(int x, int y, int z) {
		int index = getChunkIndexAt(x, y, z);
		return chunks[index];
	}

	private int getChunkIndexAt(int x, int y, int z) {
		return getChunkIndex(x / chunkWidth, y / chunkHeight, z / chunkDepth);
	}

	private int getChunkIndex(int chunkX, int chunkY, int chunkZ) {
		return (chunkY * widthInChunks * depthInChunks) + (chunkZ * widthInChunks) + chunkX;
	}

	@Override
	public void dispose() {
		for (int i = 0; i < chunks.length; ++i)
			chunks[i].dispose();
	}
}
