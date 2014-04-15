package ca.blarg.gdx.tilemap3d.assets.tilemap;

import ca.blarg.gdx.tilemap3d.TileChunk;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.lighting.LightSpreadingTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.SimpleTileMapLighter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TileMapSaver {
	public static void save(TileMap tileMap, String outputFilename) {
		if (tileMap == null)
			throw new IllegalArgumentException();

		JsonTileMap jsonMap = new JsonTileMap();
		jsonMap.chunkWidth = tileMap.getChunks()[0].getWidth();
		jsonMap.chunkHeight = tileMap.getChunks()[0].getHeight();
		jsonMap.chunkDepth = tileMap.getChunks()[0].getDepth();
		jsonMap.widthInChunks = tileMap.getWidth() / jsonMap.chunkWidth;
		jsonMap.heightInChunks = tileMap.getHeight() / jsonMap.chunkHeight;
		jsonMap.depthInChunks = tileMap.getDepth() / jsonMap.chunkDepth;

		if (tileMap.lighter == null)
			jsonMap.lightingMode = null;
		else if (tileMap.lighter instanceof SimpleTileMapLighter)
			jsonMap.lightingMode = "simple";
		else if (tileMap.lighter instanceof LightSpreadingTileMapLighter)
			jsonMap.lightingMode = "skyAndSources";

		jsonMap.ambientLightValue = (int)tileMap.ambientLightValue;
		jsonMap.skyLightValue = (int)tileMap.skyLightValue;

		// each serialized chunk will be the same size in bytes (same number of tiles in each)
		int chunkSizeInBytes = tileMap.getChunks()[0].getData().length * TileDataSerializer.TILE_SIZE_BYTES;

		jsonMap.chunks = new ArrayList<String>(tileMap.getChunks().length);
		for (int i = 0; i < tileMap.getChunks().length; ++i) {
			TileChunk chunk = tileMap.getChunks()[i];

			byte[] chunkBytes = new byte[chunkSizeInBytes];
			ByteBuffer buffer = ByteBuffer.wrap(chunkBytes);

			TileDataSerializer.serialize(chunk, buffer);

			jsonMap.chunks.add(new String(Base64Coder.encode(chunkBytes)));
		}

		Json json = new Json();
		String output = json.prettyPrint(jsonMap);
		FileHandle outputFile = Gdx.files.local(outputFilename);
		outputFile.writeString(output, false);
	}
}
