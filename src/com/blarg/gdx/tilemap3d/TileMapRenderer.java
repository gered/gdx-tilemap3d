package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class TileMapRenderer {
	public void render(ModelBatch modelBatch, Camera camera, TileMap tileMap) {
		for (int y = 0; y < tileMap.heightInChunks; ++y)
		{
			for (int z = 0; z < tileMap.depthInChunks; ++z)
			{
				for (int x = 0; x < tileMap.widthInChunks; ++x)
				{
					TileChunk chunk = tileMap.getChunk(x, y, z);
					if (chunk.mesh.mesh.getNumVertices() > 0 && camera.frustum.boundsInFrustum(chunk.mesh.bounds))
						modelBatch.render(chunk.mesh);
					if (chunk.alphaMesh.mesh.getNumVertices() > 0 && camera.frustum.boundsInFrustum(chunk.alphaMesh.bounds))
						modelBatch.render(chunk.alphaMesh);
				}
			}
		}
	}
}
