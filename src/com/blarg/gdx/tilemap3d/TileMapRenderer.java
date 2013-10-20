package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.Lights;

public class TileMapRenderer {
	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera) {
		render(modelBatch, tileMap, camera, null, null);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Shader shader) {
		render(modelBatch, tileMap, camera, shader, null);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Lights lights) {
		render(modelBatch, tileMap, camera, null, lights);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Shader shader, Lights lights) {
		TileChunk[] chunks = tileMap.chunks;
		for (int i = 0; i < chunks.length; ++i)
			render(modelBatch, chunks[i], camera, shader, lights);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera) {
		render(modelBatch, chunk, camera, null, null);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Shader shader) {
		render(modelBatch, chunk, camera, shader, null);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Lights lights) {
		render(modelBatch, chunk, camera, null, lights);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Shader shader, Lights lights) {
		if (camera.frustum.boundsInFrustum(chunk.getMeshBounds()))
			modelBatch.render(chunk, lights, shader);
	}
}
