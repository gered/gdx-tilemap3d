package com.blarg.gdx.tilemap3d.lighting;

import com.blarg.gdx.Bitfield;
import com.blarg.gdx.tilemap3d.Tile;
import com.blarg.gdx.tilemap3d.TileMap;
import com.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import com.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class SimpleTileMapLighter extends BaseTileMapLighter {
	public SimpleTileMapLighter() {
	}

	@Override
	public void light(TileMap tileMap) {
		resetLightValues(tileMap);
		castSkyLightDown(tileMap);
	}
}
