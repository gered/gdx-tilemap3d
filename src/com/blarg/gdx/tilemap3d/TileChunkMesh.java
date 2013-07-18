package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.graphics.TextureAtlas;

public class TileChunkMesh extends Renderable implements Disposable {
	public final BoundingBox bounds;

	public TileChunkMesh(TileChunk chunk, boolean blending) {
		meshPartOffset = 0;
		meshPartSize = 0;
		primitiveType = GL20.GL_TRIANGLES;
		bones = null;
		lights = null;
		shader = null;
		userData = null;
		material = new Material();

		TextureAtlas tileMapAtlas = chunk.tileMap.tileMeshes.atlas;
		material.set(TextureAttribute.createDiffuse(tileMapAtlas.texture));

		if (blending)
			material.set(new BlendingAttribute());

		bounds = new BoundingBox();
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
		this.meshPartSize = mesh.getNumVertices();
		if (this.meshPartSize > 0)
			mesh.calculateBoundingBox(bounds);
		else
			bounds.clr();
	}

	@Override
	public void dispose() {
		if (mesh != null)
			mesh.dispose();
	}
}
