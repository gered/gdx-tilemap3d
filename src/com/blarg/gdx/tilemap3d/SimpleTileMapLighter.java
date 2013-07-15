package com.blarg.gdx.tilemap3d;

import com.blarg.gdx.Bitfield;

public class SimpleTileMapLighter implements TileMapLighter {
	public SimpleTileMapLighter() {
	}

	protected void resetLightValues(TileMap tileMap) {
		for (int y = 0; y < tileMap.getHeight(); ++y)
		{
			for (int z = 0; z < tileMap.getDepth(); ++z)
			{
				for (int x = 0; x < tileMap.getWidth(); ++x)
				{
					Tile tile = tileMap.get(x, y, z);

					// sky lighting will be recalculated, and other types of light sources
					// info stays as they were
					tile.flags = Bitfield.clear(Tile.FLAG_LIGHT_SKY, tile.flags);
					tile.skyLight = 0;
					tile.tileLight = tileMap.ambientLightValue;
				}
			}
		}
	}

	protected void applySkyLight(TileMap tileMap) {
		// NOTE: resetLightValues() clears sky light data in such a way that
		//       doesn't require us to flood-fill 0 light values here for everything
		//       that isn't sky-lit
		// go through each vertical column one at a time from top to bottom
		for (int x = 0; x < tileMap.getWidth(); ++x)
		{
			for (int z = 0; z < tileMap.getDepth(); ++z)
			{
				boolean stillSkyLit = true;
				byte currentSkyLightValue = tileMap.skyLightValue;

				for (int y = tileMap.getHeight() - 1; y >= 0 && stillSkyLit; --y)
				{
					Tile tile = tileMap.get(x, y, z);
					TileMesh mesh = tileMap.tileMeshes.get(tile);
					if (mesh == null || (mesh != null && !mesh.isOpaque(TileMesh.SIDE_TOP) && !mesh.isOpaque(TileMesh.SIDE_BOTTOM)))
					{
						// tile is partially transparent or this tile is empty space
						tile.flags = Bitfield.set(Tile.FLAG_LIGHT_SKY, tile.flags);

						if (mesh != null)
							currentSkyLightValue = Tile.adjustLightForTranslucency(currentSkyLightValue, mesh.translucency);

						tile.skyLight = currentSkyLightValue;
					}
					else
					{
						// tile is present and is fully solid, sky lighting stops
						// at the tile above this one
						stillSkyLit = false;
					}
				}
			}
		}
	}

	@Override
	public void light(TileMap tileMap) {
		resetLightValues(tileMap);
		applySkyLight(tileMap);
	}
}
