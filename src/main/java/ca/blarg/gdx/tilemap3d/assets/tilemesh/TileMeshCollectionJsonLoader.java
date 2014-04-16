package ca.blarg.gdx.tilemap3d.assets.tilemesh;

import ca.blarg.gdx.assets.AssetLoadingException;
import ca.blarg.gdx.assets.AssetLoadingUtils;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

class TileMeshCollectionJsonLoader {
	public static JsonTileMeshCollection load(FileHandle file) {
		Json json = new Json();
		JsonTileMeshCollection definition = json.fromJson(JsonTileMeshCollection.class, file);

		String path = file.parent().path();
		definition.textureAtlas = AssetLoadingUtils.addPathIfNone(definition.textureAtlas, path);
		if (definition.tiles != null) {
			for (int i = 0; i < definition.tiles.size(); ++i)
				definition.tiles.set(i, AssetLoadingUtils.addPathIfNone(definition.tiles.get(i), path));
		}

		return definition;
	}

	public static TileMeshCollection create(FileHandle file, JsonTileMeshCollection definition, AssetManager assetManager) {
		if (definition.tiles == null || definition.tiles.size() == 0)
			throw new AssetLoadingException(file.path(), "No tiles defined.");
		if (definition.textureAtlas == null)
			throw new AssetLoadingException(file.path(), "No texture atlas defined.");

		TextureAtlas atlas = assetManager.get(definition.textureAtlas, TextureAtlas.class);
		TileMeshCollection collection = new TileMeshCollection(atlas);

		for (String tileMeshFile : definition.tiles) {
			TileMesh mesh = assetManager.get(tileMeshFile, TileMesh.class);
			collection.addMesh(mesh);
		}

		return collection;
	}
}
