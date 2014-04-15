package ca.blarg.gdx.tilemap3d.json.tilemap;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.tilemap3d.ChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.TileChunk;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.json.tilemap.JsonTileMap;
import ca.blarg.gdx.tilemap3d.json.tilemap.TileDataSerializer;
import ca.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TileMapJsonLoader {
	public static TileMap load(String mapFile, TileMeshCollection tileMeshes) {
		return load(Gdx.files.internal(mapFile), tileMeshes);
	}

	public static TileMap load(FileHandle mapFile, TileMeshCollection tileMeshes) {
		if (mapFile == null)
			throw new IllegalArgumentException();
		if (tileMeshes == null)
			throw new IllegalArgumentException();

		Json json = new Json();
		JsonTileMap jsonMap = json.fromJson(JsonTileMap.class, mapFile);

		if (jsonMap.chunks == null || jsonMap.chunks.size() == 0)
			throw new RuntimeException("Invalid map: no chunks.");
		int numChunks = (jsonMap.widthInChunks * jsonMap.heightInChunks * jsonMap.depthInChunks);
		if (jsonMap.chunks.size() != numChunks)
			throw new RuntimeException("Inconsistent map dimensions and number of chunks.");

		ChunkVertexGenerator chunkVertexGenerator = null;
		TileMapLighter lighter = null;

		if (Strings.isNullOrEmpty(jsonMap.lightingMode)) {
			chunkVertexGenerator = new ChunkVertexGenerator();
			lighter = null;
		} else if (jsonMap.lightingMode.equalsIgnoreCase("simple")) {
			throw new NotImplementedException();
		} else if (jsonMap.lightingMode.equalsIgnoreCase("skyAndSources")) {
			throw new NotImplementedException();
		} else
			throw new RuntimeException("Invalid lighting mode.");

		TileMap tileMap = new TileMap(
				jsonMap.chunkWidth, jsonMap.chunkHeight, jsonMap.chunkDepth,
				jsonMap.widthInChunks, jsonMap.heightInChunks, jsonMap.depthInChunks,
				tileMeshes,
				chunkVertexGenerator,
				lighter
		);

		for (int i = 0; i < jsonMap.chunks.size(); ++i) {
			String encodedChunk = jsonMap.chunks.get(i);
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

		// TODO: figure out real lighting mode from the types of vertex generator / lighter objects set
		jsonMap.lightingMode = null;

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
