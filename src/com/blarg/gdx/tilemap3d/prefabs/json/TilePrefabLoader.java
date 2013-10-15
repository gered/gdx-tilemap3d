package com.blarg.gdx.tilemap3d.prefabs.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.blarg.gdx.Strings;
import com.blarg.gdx.tilemap3d.json.TileDataSerializer;
import com.blarg.gdx.tilemap3d.prefabs.TilePrefab;

import java.nio.ByteBuffer;

public class TilePrefabLoader {
	public static TilePrefab load(String prefabFile) {
		return load(Gdx.files.internal(prefabFile));
	}

	public static TilePrefab load(FileHandle prefabFile) {
		if (prefabFile == null)
			throw new IllegalArgumentException();

		Json json = new Json();
		JsonTilePrefab jsonPrefab = json.fromJson(JsonTilePrefab.class, prefabFile);

		if (Strings.isNullOrEmpty(jsonPrefab.data))
			throw new RuntimeException("Invalid prefab: no tile data.");

		TilePrefab prefab = new TilePrefab(jsonPrefab.width, jsonPrefab.height, jsonPrefab.depth);

		byte[] dataBytes = Base64Coder.decode(jsonPrefab.data);
		ByteBuffer buffer = ByteBuffer.wrap(dataBytes);

		TileDataSerializer.deserialize(buffer, prefab);

		return prefab;
	}

	public static void save(TilePrefab prefab, String outputFilename) {
		if (prefab == null)
			throw new IllegalArgumentException();

		JsonTilePrefab jsonPrefab = new JsonTilePrefab();
		jsonPrefab.width = prefab.getWidth();
		jsonPrefab.height = prefab.getHeight();
		jsonPrefab.depth = prefab.getDepth();

		byte[] dataBytes = new byte[prefab.getData().length * TileDataSerializer.TILE_SIZE_BYTES];
		ByteBuffer buffer = ByteBuffer.wrap(dataBytes);

		TileDataSerializer.serialize(prefab, buffer);
		jsonPrefab.data = new String(Base64Coder.encode(dataBytes));

		Json json = new Json();
		String output = json.prettyPrint(jsonPrefab);
		FileHandle outputFile = Gdx.files.local(outputFilename);
		outputFile.writeString(output, false);
	}
}
