package ca.blarg.gdx.tilemap3d.json.tilemesh;

import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

class TileMeshCollectionJsonLoader {
	public static JsonTileMeshCollection load(FileHandle file) {
		Json json = new Json();
		return json.fromJson(JsonTileMeshCollection.class, file);
	}

	public static TileMeshCollection create(JsonTileMeshCollection definition, AssetManager assetManager) {
		if (definition.tiles == null || definition.tiles.size() == 0)
			throw new RuntimeException("No tiles defined.");
		if (definition.textureAtlas == null)
			throw new RuntimeException("No texture atlas defined.");

		TextureAtlas atlas = assetManager.get(definition.textureAtlas, TextureAtlas.class);
		TileMeshCollection collection = new TileMeshCollection(atlas);

		for (String tileMeshFile : definition.tiles) {
			TileMesh mesh = assetManager.get(tileMeshFile, TileMesh.class);
			collection.addMesh(mesh);
		}

		return collection;
	}
}
