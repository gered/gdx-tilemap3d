package ca.blarg.gdx.tilemap3d.json.tilemap;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.tilemap3d.ChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.TileChunk;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.lighting.LightSpreadingTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.LitChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.lighting.SimpleTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TileMapJsonLoader {
	public static JsonTileMap load(FileHandle file) {
		Json json = new Json();
		return json.fromJson(JsonTileMap.class, file);
	}

	public static TileMap create(JsonTileMap definition, AssetManager assetManager) {
		if (definition.chunks == null || definition.chunks.size() == 0)
			throw new RuntimeException("Invalid map: no chunks.");
		int numChunks = (definition.widthInChunks * definition.heightInChunks * definition.depthInChunks);
		if (definition.chunks.size() != numChunks)
			throw new RuntimeException("Inconsistent map dimensions and number of chunks.");
		if (definition.tileMeshes == null)
			throw new RuntimeException("No tile mesh collection specified.");

		TileMeshCollection tileMeshes = assetManager.get(definition.tileMeshes, TileMeshCollection.class);

		ChunkVertexGenerator chunkVertexGenerator = null;
		TileMapLighter lighter = null;

		if (Strings.isNullOrEmpty(definition.lightingMode)) {
			chunkVertexGenerator = new ChunkVertexGenerator();
			lighter = null;
		} else if (definition.lightingMode.equalsIgnoreCase("simple")) {
			chunkVertexGenerator = new LitChunkVertexGenerator();
			lighter = new SimpleTileMapLighter();
		} else if (definition.lightingMode.equalsIgnoreCase("skyAndSources")) {
			chunkVertexGenerator = new LitChunkVertexGenerator();
			lighter = new LightSpreadingTileMapLighter(true, true);
		} else
			throw new RuntimeException("Invalid lighting mode.");

		TileMap tileMap = new TileMap(
			definition.chunkWidth, definition.chunkHeight, definition.chunkDepth,
			definition.widthInChunks, definition.heightInChunks, definition.depthInChunks,
			tileMeshes,
			chunkVertexGenerator,
			lighter
		);

		for (int i = 0; i < definition.chunks.size(); ++i) {
			String encodedChunk = definition.chunks.get(i);
			TileChunk outputChunk = tileMap.getChunks()[i];

			byte[] chunkBytes = Base64Coder.decode(encodedChunk);
			ByteBuffer buffer = ByteBuffer.wrap(chunkBytes);

			TileDataSerializer.deserialize(buffer, outputChunk);
		}

		return tileMap;
	}

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
