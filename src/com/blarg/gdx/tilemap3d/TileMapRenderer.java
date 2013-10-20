package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class TileMapRenderer {
	public void render(ModelBatch modelBatch, Camera camera, TileMap tileMap) {
		TileChunk[] chunks = tileMap.chunks;
		for (int i = 0; i < chunks.length; ++i) {
			TileChunk chunk = chunks[i];
			if (camera.frustum.boundsInFrustum(chunk.getMeshBounds()))
				modelBatch.render(chunk);
		}
	}
}
