package ca.blarg.gdx.tilemap3d.json;

import ca.blarg.gdx.tilemap3d.Tile;
import ca.blarg.gdx.tilemap3d.TileRawDataContainer;

import java.nio.ByteBuffer;

public class TileDataSerializer {
	public static final int TILE_SIZE_BYTES = 17;  // TODO: is there some kind of java sizeof() type thing?

	public static void serialize(TileRawDataContainer tileData, ByteBuffer buffer) {
		Tile[] tiles = tileData.getData();
		for (int i = 0; i < tiles.length; ++i)
			serialize(tiles[i], buffer);
	}

	public static void deserialize(ByteBuffer buffer, TileRawDataContainer out) {
		Tile[] tiles = out.getData();
		for (int i = 0; i < tiles.length; ++i)
			deserialize(buffer, tiles[i]);
	}

	public static void serialize(Tile tile, ByteBuffer buffer) {
		buffer.putShort(tile.tile);
		buffer.putShort(tile.flags);
		buffer.put(tile.tileLight);
		buffer.put(tile.skyLight);
		buffer.put(tile.rotation);
		buffer.put(tile.parentTileOffsetX);
		buffer.put(tile.parentTileOffsetY);
		buffer.put(tile.parentTileOffsetZ);
		buffer.put(tile.parentTileWidth);
		buffer.put(tile.parentTileHeight);
		buffer.put(tile.parentTileDepth);
		buffer.putInt(tile.color);
	}

	public static void deserialize(ByteBuffer buffer, Tile out) {
		out.tile = buffer.getShort();
		out.flags = buffer.getShort();
		out.tileLight = buffer.get();
		out.skyLight = buffer.get();
		out.rotation = buffer.get();
		out.parentTileOffsetX = buffer.get();
		out.parentTileOffsetY = buffer.get();
		out.parentTileOffsetZ = buffer.get();
		out.parentTileWidth = buffer.get();
		out.parentTileHeight = buffer.get();
		out.parentTileDepth = buffer.get();
		out.color = buffer.getInt();
	}
}
