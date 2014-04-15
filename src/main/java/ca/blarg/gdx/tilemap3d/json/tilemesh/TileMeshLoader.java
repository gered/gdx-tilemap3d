package ca.blarg.gdx.tilemap3d.json.tilemesh;

import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.MaterialTileMapping;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class TileMeshLoader extends AsynchronousAssetLoader<TileMesh, TileMeshLoader.TileMeshParameter> {
	public static class TileMeshParameter extends AssetLoaderParameters<TileMesh> {
	}

	public TileMeshLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonTileMesh definition;
	TileMesh mesh;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TileMeshParameter parameter) {
		definition = TileMeshJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();

		if (definition.materials != null)
			deps.add(new AssetDescriptor(definition.materials, MaterialTileMapping.class));
		if (definition.textureAtlas != null)
			deps.add(new AssetDescriptor(definition.textureAtlas, TextureAtlas.class));

		if (!definition.cube) {
			if (definition.model != null)
				deps.add(new AssetDescriptor(definition.model, Model.class));
			else if (definition.models != null && definition.models.size() > 0) {
				for (int i = 0; i < definition.models.size(); ++i)
					deps.add(new AssetDescriptor(definition.models.get(i).submodel, Model.class));
			}

			if (definition.collisionModel != null)
				deps.add(new AssetDescriptor(definition.collisionModel, Model.class));
		}

		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TileMeshParameter parameter) {
		mesh = TileMeshJsonLoader.create(definition, manager);
	}

	@Override
	public TileMesh loadSync(AssetManager manager, String fileName, FileHandle file, TileMeshParameter parameter) {
		return mesh;
	}
}