package ca.blarg.gdx.tilemap3d.assets.prefabs;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.tilemap3d.assets.tilemap.TileDataSerializer;
import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.nio.ByteBuffer;

class TilePrefabJsonLoader {
	public static JsonTilePrefab load(FileHandle file) {
		Json json = new Json();
		return json.fromJson(JsonTilePrefab.class, file);
	}

	public static TilePrefab create(JsonTilePrefab definition, AssetManager assetManager) {
		if (Strings.isNullOrEmpty(definition.data))
			throw new RuntimeException("Invalid prefab: no tile data.");

		TilePrefab prefab = new TilePrefab(definition.width, definition.height, definition.depth);

		byte[] dataBytes = Base64Coder.decode(definition.data);
		ByteBuffer buffer = ByteBuffer.wrap(dataBytes);

		TileDataSerializer.deserialize(buffer, prefab);

		return prefab;
	}
}
