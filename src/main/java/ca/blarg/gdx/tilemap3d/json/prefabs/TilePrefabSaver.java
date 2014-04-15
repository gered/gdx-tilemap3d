package ca.blarg.gdx.tilemap3d.json.prefabs;

import ca.blarg.gdx.tilemap3d.json.tilemap.TileDataSerializer;
import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.nio.ByteBuffer;

public class TilePrefabSaver {
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
