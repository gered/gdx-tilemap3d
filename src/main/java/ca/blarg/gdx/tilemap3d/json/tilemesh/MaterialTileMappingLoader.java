package ca.blarg.gdx.tilemap3d.json.tilemesh;

import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.MaterialTileMapping;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class MaterialTileMappingLoader extends AsynchronousAssetLoader<MaterialTileMapping, MaterialTileMappingLoader.MaterialTileMappingParameter> {
	public static class MaterialTileMappingParameter extends AssetLoaderParameters<MaterialTileMapping> {
	}

	public MaterialTileMappingLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonMaterialMapping definition;
	MaterialTileMapping mapping;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MaterialTileMappingParameter parameter) {
		definition = MaterialTileMappingJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(definition.textureAtlas, TextureAtlas.class));
		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, MaterialTileMappingParameter parameter) {
		mapping = MaterialTileMappingJsonLoader.create(definition, manager);
	}

	@Override
	public MaterialTileMapping loadSync(AssetManager manager, String fileName, FileHandle file, MaterialTileMappingParameter parameter) {
		return mapping;
	}
}
