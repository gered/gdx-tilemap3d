package ca.blarg.gdx.tilemap3d.lighting;

import ca.blarg.gdx.Bitfield;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.Tile;
import ca.blarg.gdx.tilemap3d.TileMap;
import ca.blarg.gdx.tilemap3d.lighting.TileMapLighter;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class SimpleTileMapLighter extends BaseTileMapLighter {
	public SimpleTileMapLighter() {
	}

	@Override
	public void light(TileMap tileMap) {
		resetLightValues(tileMap);
		castSkyLightDown(tileMap);
	}
}
