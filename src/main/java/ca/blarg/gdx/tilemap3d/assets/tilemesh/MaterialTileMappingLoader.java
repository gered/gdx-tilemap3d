package ca.blarg.gdx.tilemap3d.assets.tilemesh;

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
public class MaterialTileMappingLoader extends AsynchronousAssetLoader<MaterialTileMapping, MaterialTileMappingLoader.MaterialTileMappingParameters> {
	public static class MaterialTileMappingParameters extends AssetLoaderParameters<MaterialTileMapping> {
	}

	public MaterialTileMappingLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonMaterialMapping definition;
	MaterialTileMapping mapping;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MaterialTileMappingParameters parameter) {
		definition = MaterialTileMappingJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(definition.textureAtlas, TextureAtlas.class));
		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, MaterialTileMappingParameters parameter) {
		mapping = MaterialTileMappingJsonLoader.create(definition, manager);
	}

	@Override
	public MaterialTileMapping loadSync(AssetManager manager, String fileName, FileHandle file, MaterialTileMappingParameters parameter) {
		return mapping;
	}
}
