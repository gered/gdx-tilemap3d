package ca.blarg.gdx.tilemap3d.json.tilemap;

import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class TileMapLoader extends AsynchronousAssetLoader<TileMap, TileMapLoader.TileMapParameters> {
	public static class TileMapParameters extends AssetLoaderParameters<TileMap> {
	}

	public TileMapLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonTileMap definition;
	TileMap map;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TileMapParameters parameter) {
		definition = TileMapJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(definition.tileMeshes, TileMeshCollection.class));
		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TileMapParameters parameter) {
		map = TileMapJsonLoader.create(definition, manager);
	}

	@Override
	public TileMap loadSync(AssetManager manager, String fileName, FileHandle file, TileMapParameters parameter) {
		return map;
	}
}
