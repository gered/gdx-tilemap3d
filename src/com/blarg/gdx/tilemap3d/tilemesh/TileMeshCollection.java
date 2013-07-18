package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.graphics.TextureAtlas;
import com.blarg.gdx.tilemap3d.Tile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class TileMeshCollection {
	public final TextureAtlas atlas;
	Array<TileMesh> meshes;

	public TileMeshCollection(TextureAtlas atlas) {
		this(atlas, null);
	}

	public TileMeshCollection(TextureAtlas atlas, FileHandle configFile) {
		if (atlas == null)
			throw new IllegalArgumentException();

		this.atlas = atlas;
		this.meshes = new Array<TileMesh>(TileMesh.class);

		// the first mesh (index = 0) should always be a null one as this has special meaning
		// in other TileMap-related objects (basically, representing empty space)
		addMesh(null);

		if (configFile != null) {
			JsonTileMeshCollection config;
			Json json = new Json();
			config = json.fromJson(JsonTileMeshCollection.class, configFile);
			setupFromConfig(config);
		}
	}

	private int addMesh(TileMesh mesh) {
		meshes.add(mesh);
		return meshes.size - 1;
	}

	public int add(
			Model model,
			MaterialTileMapping textures,
			byte opaqueSides,
			byte lightValue,
			boolean alpha,
			float translucency,
			Color color,
			Vector3 scaleToSize,
			Vector3 positionOffset
	) {
		ModelTileMesh tileMesh = new ModelTileMesh(
				model, textures, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset
		);
		return addMesh(tileMesh);
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

	private void setupFromConfig(JsonTileMeshCollection config) {
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

			if (tileDef.model != null && materialMapping == null)
				throw new RuntimeException("Missing materials section but using models to define tiles.");
			if (tileDef.collisionModel != null && tileDef.collisionShape != null)
				throw new RuntimeException("collisionModel and collisionShape cannot both be set.");

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

			add(model, collisionModel, materialMapping, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset, collisionPositionOffset);
		}
	}
}
