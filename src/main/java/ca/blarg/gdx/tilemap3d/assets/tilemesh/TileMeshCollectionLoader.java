package ca.blarg.gdx.tilemap3d.assets.tilemesh;

import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class TileMeshCollectionLoader extends AsynchronousAssetLoader<TileMeshCollection, TileMeshCollectionLoader.TileMeshCollectionParameters> {
	public static class TileMeshCollectionParameters extends AssetLoaderParameters<TileMeshCollection> {
	}

	public TileMeshCollectionLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonTileMeshCollection definition;
	TileMeshCollection collection;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TileMeshCollectionParameters parameter) {
		definition = TileMeshCollectionJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();

		if (definition.textureAtlas != null)
			deps.add(new AssetDescriptor(definition.textureAtlas, TextureAtlas.class));

		TileMeshLoader.TileMeshParameters params = new TileMeshLoader.TileMeshParameters();
		params.defaultTextureAtlas = definition.textureAtlas;

		for (String tileMeshFile : definition.tiles) {
			deps.add(new AssetDescriptor(tileMeshFile, TileMesh.class, params));
		}

		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TileMeshCollectionParameters parameter) {
		collection = TileMeshCollectionJsonLoader.create(file, definition, manager);
	}

	@Override
	public TileMeshCollection loadSync(AssetManager manager, String fileName, FileHandle file, TileMeshCollectionParameters parameter) {
		return collection;
	}
}
