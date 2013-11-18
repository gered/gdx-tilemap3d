package ca.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;

public class TileMapRenderer {
	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera) {
		render(modelBatch, tileMap, camera, null, null);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Shader shader) {
		render(modelBatch, tileMap, camera, shader, null);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Environment environment) {
		render(modelBatch, tileMap, camera, null, environment);
	}

	public void render(ModelBatch modelBatch, TileMap tileMap, Camera camera, Shader shader, Environment environment) {
		TileChunk[] chunks = tileMap.chunks;
		for (int i = 0; i < chunks.length; ++i)
			render(modelBatch, chunks[i], camera, shader, environment);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera) {
		render(modelBatch, chunk, camera, null, null);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Shader shader) {
		render(modelBatch, chunk, camera, shader, null);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Environment environment) {
		render(modelBatch, chunk, camera, null, environment);
	}

	public void render(ModelBatch modelBatch, TileChunk chunk, Camera camera, Shader shader, Environment environment) {
		if (camera.frustum.boundsInFrustum(chunk.getMeshBounds()))
			modelBatch.render(chunk, environment, shader);
	}
}
