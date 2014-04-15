package ca.blarg.gdx.tilemap3d.assets.tilemesh;

import ca.blarg.gdx.assets.AssetLoadingException;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.MaterialTileMapping;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

class MaterialTileMappingJsonLoader {
	public static JsonMaterialMapping load(FileHandle file) {
		Json json = new Json();
		return json.fromJson(JsonMaterialMapping.class, file);
	}

	public static MaterialTileMapping create(FileHandle file, JsonMaterialMapping definition, AssetManager assetManager) {
		if (definition.materials == null || definition.materials.size() == 0)
			throw new AssetLoadingException(file.path(), "No material mappings defined.");
		if (definition.textureAtlas == null)
			throw new AssetLoadingException(file.path(), "No texture atlas specified.");

		TextureAtlas atlas = assetManager.get(definition.textureAtlas, TextureAtlas.class);

		MaterialTileMapping materialMapping = new MaterialTileMapping();
		for (int i = 0; i < definition.materials.size(); ++i) {
			JsonMaterialMapping.Material mapping = definition.materials.get(i);
			materialMapping.add(
				mapping.name, atlas.get(mapping.tile),
				mapping.minU, mapping.maxU, mapping.minV, mapping.maxV
			);
		}

		return materialMapping;
	}
}
