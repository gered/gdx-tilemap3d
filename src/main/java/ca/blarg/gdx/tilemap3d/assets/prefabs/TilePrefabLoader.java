package ca.blarg.gdx.tilemap3d.assets.prefabs;

import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class TilePrefabLoader extends AsynchronousAssetLoader<TilePrefab, TilePrefabLoader.TilePrefabParameters> {
	public static class TilePrefabParameters extends AssetLoaderParameters<TilePrefab> {
	}

	JsonTilePrefab definition;
	TilePrefab prefab;

	public TilePrefabLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TilePrefabParameters parameter) {
		definition = TilePrefabJsonLoader.load(file);
		return null;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TilePrefabParameters parameter) {
		prefab = TilePrefabJsonLoader.create(definition, manager);
	}

	@Override
	public TilePrefab loadSync(AssetManager manager, String fileName, FileHandle file, TilePrefabParameters parameter) {
		return prefab;
	}
}
