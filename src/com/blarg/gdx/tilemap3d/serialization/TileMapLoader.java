package com.blarg.gdx.tilemap3d.serialization;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.blarg.gdx.Strings;
import com.blarg.gdx.tilemap3d.ChunkVertexGenerator;
import com.blarg.gdx.tilemap3d.TileChunk;
import com.blarg.gdx.tilemap3d.TileMap;
import com.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import com.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;

public class TileMapLoader {
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

			TileChunkSerializer.deserialize(buffer, outputChunk);
		}

		return tileMap;
	}
}
