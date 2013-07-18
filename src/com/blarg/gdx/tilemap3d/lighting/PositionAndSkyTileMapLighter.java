package com.blarg.gdx.tilemap3d.lighting;

import com.blarg.gdx.tilemap3d.Tile;
import com.blarg.gdx.tilemap3d.TileMap;
import com.blarg.gdx.tilemap3d.lighting.SimpleTileMapLighter;
import com.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class PositionAndSkyTileMapLighter extends SimpleTileMapLighter {
	public PositionAndSkyTileMapLighter() {
	}

	private void applyLighting(TileMap tileMap) {
		// for each light source (sky or not), recursively go through and set
		// appropriate lighting for each adjacent tile
		for (int y = 0; y < tileMap.getHeight(); ++y)
		{
			for (int z = 0; z < tileMap.getDepth(); ++z)
			{
				for (int x = 0; x < tileMap.getWidth(); ++x)
				{
					Tile tile = tileMap.get(x, y, z);
					if (tile.isEmptySpace())
					{
						if (tile.isSkyLit())
							spreadSkyLight(x, y, z, tile, tile.skyLight, tileMap);
					}
					else
					{
						TileMesh mesh = tileMap.tileMeshes.get(tile);
						if (mesh.isLightSource())
							spreadTileLight(x, y, z, tile, mesh.lightValue, tileMap);
					}
				}
			}
		}	}

	private void spreadSkyLight(int x, int y, int z, Tile tile, byte light, TileMap tileMap) {
		if (light > 0)
		{
			tile.skyLight = light;
			--light;

			Tile left = tileMap.getSafe(x - 1, y, z);
			Tile right = tileMap.getSafe(x + 1, y, z);
			Tile forward = tileMap.getSafe(x, y, z - 1);
			Tile backward = tileMap.getSafe(x, y, z + 1);
			Tile up = tileMap.getSafe(x, y + 1, z);
			Tile down = tileMap.getSafe(x, y - 1, z);

			if (left != null && (left.isEmptySpace() || !tileMap.tileMeshes.get(left).isOpaque(TileMesh.SIDE_RIGHT)) && left.skyLight < light)
			{
				byte spreadLight = light;
				if (!left.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(left).translucency);
				spreadSkyLight(x - 1, y, z, left, spreadLight, tileMap);
			}
			if (right != null && (right.isEmptySpace() || !tileMap.tileMeshes.get(right).isOpaque(TileMesh.SIDE_LEFT)) && right.skyLight < light)
			{
				byte spreadLight = light;
				if (!right.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(right).translucency);
				spreadSkyLight(x + 1, y, z, right, spreadLight, tileMap);
			}
			if (forward != null && (forward.isEmptySpace() || !tileMap.tileMeshes.get(forward).isOpaque(TileMesh.SIDE_BACK)) && forward.skyLight < light)
			{
				byte spreadLight = light;
				if (!forward.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(forward).translucency);
				spreadSkyLight(x, y, z - 1, forward, spreadLight, tileMap);
			}
			if (backward != null && (backward.isEmptySpace() || !tileMap.tileMeshes.get(backward).isOpaque(TileMesh.SIDE_FRONT)) && backward.skyLight < light)
			{
				byte spreadLight = light;
				if (!backward.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(backward).translucency);
				spreadSkyLight(x, y, z + 1, backward, spreadLight, tileMap);
			}
			if (up != null && (up.isEmptySpace() || !tileMap.tileMeshes.get(up).isOpaque(TileMesh.SIDE_BOTTOM)) && up.skyLight < light)
			{
				byte spreadLight = light;
				if (!up.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(up).translucency);
				spreadSkyLight(x, y + 1, z, up, spreadLight, tileMap);
			}
			if (down != null && (down.isEmptySpace() || !tileMap.tileMeshes.get(down).isOpaque(TileMesh.SIDE_TOP)) && down.skyLight < light)
			{
				byte spreadLight = light;
				if (!down.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(down).translucency);
				spreadSkyLight(x, y - 1, z, down, spreadLight, tileMap);
			}
		}
	}

	private void spreadTileLight(int x, int y, int z, Tile tile, byte light, TileMap tileMap) {
		if (light > 0)
		{
			tile.tileLight = light;
			--light;

			Tile left = tileMap.getSafe(x - 1, y, z);
			Tile right = tileMap.getSafe(x + 1, y, z);
			Tile forward = tileMap.getSafe(x, y, z - 1);
			Tile backward = tileMap.getSafe(x, y, z + 1);
			Tile up = tileMap.getSafe(x, y + 1, z);
			Tile down = tileMap.getSafe(x, y - 1, z);

			if (left != null && (left.isEmptySpace() || !tileMap.tileMeshes.get(left).isOpaque(TileMesh.SIDE_RIGHT)) && left.tileLight < light)
			{
				byte spreadLight = light;
				if (!left.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(left).translucency);
				spreadTileLight(x - 1, y, z, left, spreadLight, tileMap);
			}
			if (right != null && (right.isEmptySpace() || !tileMap.tileMeshes.get(right).isOpaque(TileMesh.SIDE_LEFT)) && right.tileLight < light)
			{
				byte spreadLight = light;
				if (!right.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(right).translucency);
				spreadTileLight(x + 1, y, z, right, spreadLight, tileMap);
			}
			if (forward != null && (forward.isEmptySpace() || !tileMap.tileMeshes.get(forward).isOpaque(TileMesh.SIDE_BACK)) && forward.tileLight < light)
			{
				byte spreadLight = light;
				if (!forward.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(forward).translucency);
				spreadTileLight(x, y, z - 1, forward, spreadLight, tileMap);
			}
			if (backward != null && (backward.isEmptySpace() || !tileMap.tileMeshes.get(backward).isOpaque(TileMesh.SIDE_FRONT)) && backward.tileLight < light)
			{
				byte spreadLight = light;
				if (!backward.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(backward).translucency);
				spreadTileLight(x, y, z + 1, backward, spreadLight, tileMap);
			}
			if (up != null && (up.isEmptySpace() || !tileMap.tileMeshes.get(up).isOpaque(TileMesh.SIDE_BOTTOM)) && up.tileLight < light)
			{
				byte spreadLight = light;
				if (!up.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(up).translucency);
				spreadTileLight(x, y + 1, z, up, spreadLight, tileMap);
			}
			if (down != null && (down.isEmptySpace() || !tileMap.tileMeshes.get(down).isOpaque(TileMesh.SIDE_TOP)) && down.tileLight < light)
			{
				byte spreadLight = light;
				if (!down.isEmptySpace())
					spreadLight = Tile.adjustLightForTranslucency(spreadLight, tileMap.tileMeshes.get(down).translucency);
				spreadTileLight(x, y - 1, z, down, spreadLight, tileMap);
			}
		}
	}

	@Override
	public void light(TileMap tileMap) {
		resetLightValues(tileMap);
		applySkyLight(tileMap);
		applyLighting(tileMap);
	}
}
