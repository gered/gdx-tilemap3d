package ca.blarg.gdx.tilemap3d.assets.tilemap;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.assets.AssetLoadingException;
import ca.blarg.gdx.assets.AssetLoadingUtils;
import ca.blarg.gdx.tilemap3d.ChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.TileChunk;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.lighting.LightSpreadingTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.LitChunkVertexGenerator;
import ca.blarg.gdx.tilemap3d.lighting.SimpleTileMapLighter;
import ca.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.nio.ByteBuffer;

class TileMapJsonLoader {
	public static JsonTileMap load(FileHandle file) {
		Json json = new Json();
		JsonTileMap definition = json.fromJson(JsonTileMap.class, file);

		definition.tileMeshes = AssetLoadingUtils.addPathIfNone(definition.tileMeshes, file.parent().path());

		return definition;
	}

	public static TileMap create(FileHandle file, JsonTileMap definition, AssetManager assetManager) {
		if (definition.chunks == null || definition.chunks.size() == 0)
			throw new AssetLoadingException(file.path(), "No chunks.");
		int numChunks = (definition.widthInChunks * definition.heightInChunks * definition.depthInChunks);
		if (definition.chunks.size() != numChunks)
			throw new AssetLoadingException(file.path(), "Inconsistent map dimensions and number of chunks.");
		if (definition.tileMeshes == null)
			throw new AssetLoadingException(file.path(), "No tile mesh collection specified.");

		TileMeshCollection tileMeshes = assetManager.get(definition.tileMeshes, TileMeshCollection.class);

		ChunkVertexGenerator chunkVertexGenerator = null;
		TileMapLighter lighter = null;

		if (Strings.isNullOrEmpty(definition.lightingMode)) {
			chunkVertexGenerator = new ChunkVertexGenerator();
			lighter = null;
		} else if (definition.lightingMode.equalsIgnoreCase("simple")) {
			chunkVertexGenerator = new LitChunkVertexGenerator();
			lighter = new SimpleTileMapLighter();
		} else if (definition.lightingMode.equalsIgnoreCase("skyAndSources")) {
			chunkVertexGenerator = new LitChunkVertexGenerator();
			lighter = new LightSpreadingTileMapLighter(true, true);
		} else
			throw new AssetLoadingException(file.path(), "Invalid lighting mode.");

		TileMap tileMap = new TileMap(
			definition.chunkWidth, definition.chunkHeight, definition.chunkDepth,
			definition.widthInChunks, definition.heightInChunks, definition.depthInChunks,
			tileMeshes,
			chunkVertexGenerator,
			lighter
		);

		tileMap.ambientLightValue = (byte)definition.ambientLightValue;
		tileMap.skyLightValue = (byte)definition.skyLightValue;

		for (int i = 0; i < definition.chunks.size(); ++i) {
			String encodedChunk = definition.chunks.get(i);
			TileChunk outputChunk = tileMap.getChunks()[i];

			byte[] chunkBytes = Base64Coder.decode(encodedChunk);
			ByteBuffer buffer = ByteBuffer.wrap(chunkBytes);

			TileDataSerializer.deserialize(buffer, outputChunk);
		}

		return tileMap;
	}
}
