package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.atlas.TextureAtlas;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public final class TileMeshCollectionLoader {
	public static TileMeshCollection load(FileHandle configFile, TextureAtlas atlas) {
		if (atlas == null)
			throw new IllegalArgumentException();

		Json json = new Json();
		JsonTileMeshCollection config = json.fromJson(JsonTileMeshCollection.class, configFile);

		TileMeshCollection collection = new TileMeshCollection(atlas);

		if (config.tiles == null)
			throw new RuntimeException("Missing tiles section.");

		MaterialTileMapping materialMapping = null;
		if (config.materials != null) {
			materialMapping = new MaterialTileMapping();
			for (int i = 0; i < config.materials.size(); ++i) {
				JsonMaterialMapping mapping = config.materials.get(i);
				materialMapping.add(
						mapping.name, atlas.get(mapping.tile),
						mapping.minU, mapping.maxU, mapping.minV, mapping.maxV
				);
			}
		}

		Map<String, Model> models = new HashMap<String, Model>();
		ModelLoader loader = new G3dModelLoader(new JsonReader());

		for (int i = 0; i < config.tiles.size(); ++i) {
			JsonTileDefinition tileDef = config.tiles.get(i);

			if (tileDef.cube && tileDef.model != null)
				throw new RuntimeException("cube and model cannot both be set.");
			if (tileDef.model != null && materialMapping == null)
				throw new RuntimeException("Missing materials section but using models to define tiles.");
			if (tileDef.collisionModel != null && tileDef.collisionShape != null)
				throw new RuntimeException("collisionModel and collisionShape cannot both be set.");

			boolean isCube = tileDef.cube;
			TextureRegion texture = null;
			TextureRegion topTexture = null;
			TextureRegion bottomTexture = null;
			TextureRegion frontTexture = null;
			TextureRegion backTexture = null;
			TextureRegion leftTexture = null;
			TextureRegion rightTexture = null;
			byte faces = 0;
			Model model;
			Model collisionModel;
			byte opaqueSides = 0;
			byte lightValue;
			boolean alpha;
			float translucency;
			Color color = new Color(Color.WHITE);
			Vector3 scaleToSize = new Vector3(1.0f, 1.0f, 1.0f);
			Vector3 positionOffset = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 collisionPositionOffset = new Vector3(0.0f, 0.0f, 0.0f);

			if (tileDef.opaqueSides != null) {
				if (tileDef.opaqueSides.contains("ALL"))
					opaqueSides = TileMesh.SIDE_ALL;
				else {
					if (tileDef.opaqueSides.contains("TOP"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_TOP, opaqueSides);
					if (tileDef.opaqueSides.contains("BOTTOM"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_BOTTOM, opaqueSides);
					if (tileDef.opaqueSides.contains("FRONT"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_FRONT, opaqueSides);
					if (tileDef.opaqueSides.contains("BACK"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_BACK, opaqueSides);
					if (tileDef.opaqueSides.contains("LEFT"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_LEFT, opaqueSides);
					if (tileDef.opaqueSides.contains("RIGHT"))
						opaqueSides = Bitfield.set(TileMesh.SIDE_RIGHT, opaqueSides);
				}
			}

			lightValue = (byte)tileDef.light;
			alpha = tileDef.alpha;
			translucency = tileDef.translucency;
			if (tileDef.color != null)
				color.set(tileDef.color);
			if (tileDef.scaleToSize != null)
				scaleToSize.set(tileDef.scaleToSize);
			if (tileDef.positionOffset != null)
				positionOffset.set(tileDef.positionOffset);
			if (tileDef.collisionPositionOffset != null)
				collisionPositionOffset.set(tileDef.collisionPositionOffset);

			if (isCube) {
				if (tileDef.textures != null) {
					if (tileDef.textures.top >= 0)
						topTexture = atlas.get(tileDef.textures.top);
					if (tileDef.textures.bottom >= 0)
						bottomTexture = atlas.get(tileDef.textures.bottom);
					if (tileDef.textures.front >= 0)
						frontTexture = atlas.get(tileDef.textures.front);
					if (tileDef.textures.back >= 0)
						backTexture = atlas.get(tileDef.textures.back);
					if (tileDef.textures.left >= 0)
						leftTexture = atlas.get(tileDef.textures.left);
					if (tileDef.textures.right >= 0)
						rightTexture = atlas.get(tileDef.textures.right);
				} else if (tileDef.texture >= 0) {
					texture = atlas.get(tileDef.texture);
					if (tileDef.faces != null) {
						if (tileDef.faces.contains("ALL"))
							faces = TileMesh.SIDE_ALL;
						else {
							if (tileDef.faces.contains("TOP"))
								faces = Bitfield.set(TileMesh.SIDE_TOP, opaqueSides);
							if (tileDef.faces.contains("BOTTOM"))
								faces = Bitfield.set(TileMesh.SIDE_BOTTOM, opaqueSides);
							if (tileDef.faces.contains("FRONT"))
								faces = Bitfield.set(TileMesh.SIDE_FRONT, opaqueSides);
							if (tileDef.faces.contains("BACK"))
								faces = Bitfield.set(TileMesh.SIDE_BACK, opaqueSides);
							if (tileDef.faces.contains("LEFT"))
								faces = Bitfield.set(TileMesh.SIDE_LEFT, opaqueSides);
							if (tileDef.faces.contains("RIGHT"))
								faces = Bitfield.set(TileMesh.SIDE_RIGHT, opaqueSides);
						}
					}
				} else
					throw new RuntimeException("No cube texture specified.");

				if (texture != null)
					collection.addCube(texture, faces, opaqueSides, lightValue, alpha, translucency, color);
				else
					collection.addCube(topTexture, bottomTexture, frontTexture, backTexture, leftTexture, rightTexture, opaqueSides, lightValue, alpha, translucency, color);

			} else {
				model = models.get(tileDef.model);
				if (model == null) {
					model = loader.loadModel(Gdx.files.internal(tileDef.model));
					models.put(tileDef.model, model);
				}

				collisionModel = model;  // default is to use the same model

				if (tileDef.collisionModel != null) {
					//override with a specific collision model
					collisionModel = models.get(tileDef.collisionModel);
					if (collisionModel == null) {
						collisionModel = loader.loadModel(Gdx.files.internal(tileDef.collisionModel));
						models.put(tileDef.collisionModel, collisionModel);
					}
				}
				if (tileDef.collisionShape != null) {
					collisionModel = null;   // using a shape instead!
					throw new NotImplementedException();
				}

				collection.add(model, collisionModel, materialMapping, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset, collisionPositionOffset);
			}
		}

		return collection;
	}
}
