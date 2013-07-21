package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.atlas.TextureAtlas;
import com.blarg.gdx.tilemap3d.Tile;

public class TileMeshCollection {
	public final TextureAtlas atlas;
	Array<TileMesh> meshes;

	public TileMeshCollection(TextureAtlas atlas) {
		if (atlas == null)
			throw new IllegalArgumentException();

		this.atlas = atlas;
		this.meshes = new Array<TileMesh>(TileMesh.class);

		// the first mesh (index = 0) should always be a null one as this has special meaning
		// in other TileMap-related objects (basically, representing empty space)
		addMesh(null);
	}

	private int addMesh(TileMesh mesh) {
		meshes.add(mesh);
		return meshes.size - 1;
	}

	public int add(
			Model model,
			Model collisionModel,
			MaterialTileMapping textures,
			byte opaqueSides,
			byte lightValue,
			boolean alpha,
			float translucency,
			Color color,
	        Vector3 scaleToSize,
	        Vector3 positionOffset,
	        Vector3 collisionPositionOffset
	) {
		ModelTileMesh tileMesh = new ModelTileMesh(
				model, collisionModel, textures, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset, collisionPositionOffset
		);
		return addMesh(tileMesh);
	}

	public int addCube(
			TextureRegion topTexture,
			TextureRegion bottomTexture,
			TextureRegion frontTexture,
			TextureRegion backTexture,
			TextureRegion leftTexture,
			TextureRegion rightTexture,
			byte opaqueSides, byte lightValue, boolean alpha, float translucency, Color color) {
		byte faces = 0;
		if (topTexture != null) faces = Bitfield.set(TileMesh.SIDE_TOP, faces);
		if (bottomTexture != null) faces = Bitfield.set(TileMesh.SIDE_BOTTOM, faces);
		if (frontTexture != null) faces = Bitfield.set(TileMesh.SIDE_FRONT, faces);
		if (backTexture != null) faces = Bitfield.set(TileMesh.SIDE_BACK, faces);
		if (leftTexture != null) faces = Bitfield.set(TileMesh.SIDE_LEFT, faces);
		if (rightTexture != null) faces = Bitfield.set(TileMesh.SIDE_RIGHT, faces);

		CubeTileMesh tileMesh = new CubeTileMesh(
				topTexture, bottomTexture, frontTexture, backTexture, leftTexture, rightTexture,
				faces, opaqueSides, lightValue, alpha, translucency, color
		);
		return addMesh(tileMesh);
	}

	public int addCube(TextureRegion texture, byte faces, byte opaqueSides, byte lightValue, boolean alpha, float translucency, Color color) {
		CubeTileMesh tileMesh = new CubeTileMesh(
				texture, texture, texture, texture, texture, texture,
	            faces, opaqueSides, lightValue, alpha, translucency, color
		);
		return addMesh(tileMesh);
	}

	public TileMesh get(Tile tile) {
		return get(tile.tile);
	}

	public TileMesh get(int index) {
		return meshes.items[index];
	}
}
