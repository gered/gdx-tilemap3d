package ca.blarg.gdx.tilemap3d.assets;

import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.assets.prefabs.TilePrefabLoader;
import ca.blarg.gdx.tilemap3d.assets.tilemap.TileMapLoader;
import ca.blarg.gdx.tilemap3d.assets.tilemesh.TileMeshCollectionLoader;
import ca.blarg.gdx.tilemap3d.assets.tilemesh.TileMeshLoader;
import ca.blarg.gdx.tilemap3d.prefabs.TilePrefab;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class TileAssetUtils {
	public static void registerLoaders(AssetManager assetManager) {
		assetManager.setLoader(TileMesh.class, new TileMeshLoader(new InternalFileHandleResolver()));
		assetManager.setLoader(TileMeshCollection.class, new TileMeshCollectionLoader(new InternalFileHandleResolver()));
		assetManager.setLoader(TilePrefab.class, new TilePrefabLoader(new InternalFileHandleResolver()));
		assetManager.setLoader(TileMap.class, new TileMapLoader(new InternalFileHandleResolver()));
	}
}
