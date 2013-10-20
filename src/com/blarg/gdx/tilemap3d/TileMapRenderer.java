package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class TileMapRenderer {
	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera) {
		TileChunk[] chunks = tileMap.chunks;
		for (int i = 0; i < chunks.length; ++i)
			render(modelBatch, chunks[i], camera);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera) {
		if (camera.frustum.boundsInFrustum(chunk.getMeshBounds()))
			modelBatch.render(chunk);
	}
}
