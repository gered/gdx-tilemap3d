package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.blarg.gdx.graphics.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

public class MaterialTileMapping {
	public class TileTexture {
		public TextureRegion region;
		public float materialMinU = 0.0f;
		public float materialMaxU = 1.0f;
		public float materialMinV = 0.0f;
		public float materialMaxV = 1.0f;
	}

	private Map<String, TileTexture> mappings;

	public MaterialTileMapping() {
		mappings = new HashMap<String, TileTexture>();
	}

	public MaterialTileMapping add(String materialName, TextureRegion region) {
		TileTexture tileTexture = new TileTexture();
		tileTexture.region = region;
		mappings.put(materialName, tileTexture);
		return this;
	}

	public MaterialTileMapping add(String materialName, TextureRegion region, float materialMinU, float materialMaxU, float materialMinV, float materialMaxV) {
		TileTexture tileTexture = new TileTexture();
		tileTexture.region = region;
		tileTexture.materialMinU = materialMinU;
		tileTexture.materialMaxU = materialMaxU;
		tileTexture.materialMinV = materialMinV;
		tileTexture.materialMaxV = materialMaxV;
		mappings.put(materialName, tileTexture);
		return this;
	}

	public TileTexture get(String materialName) {
		return mappings.get(materialName);
	}

	public void scaleUV(String materialName, Vector2 srcTexCoord, Vector2 out) {
		TileTexture tileTexture = mappings.get(materialName);
		if (tileTexture == null)
			throw new IllegalArgumentException("No matching material.");

		out.x = TextureAtlas.scaleTexCoordU(srcTexCoord.x, tileTexture.materialMinU, tileTexture.materialMaxU, tileTexture.region);
		out.y = TextureAtlas.scaleTexCoordV(srcTexCoord.y, tileTexture.materialMinV, tileTexture.materialMaxV, tileTexture.region);
	}

	public void scaleUV(String materialName, float srcU, float srcV, Vector2 out) {
		TileTexture tileTexture = mappings.get(materialName);
		if (tileTexture == null)
			throw new IllegalArgumentException("No matching material.");

		out.x = TextureAtlas.scaleTexCoordU(srcU, tileTexture.materialMinU, tileTexture.materialMaxU, tileTexture.region);
		out.y = TextureAtlas.scaleTexCoordV(srcV, tileTexture.materialMinV, tileTexture.materialMaxV, tileTexture.region);
	}

	public void clear() {
		mappings.clear();
	}
}
