package ca.blarg.gdx.tilemap3d.assets.tilemesh;

import ca.blarg.gdx.Bitfield;
import ca.blarg.gdx.assets.AssetLoadingException;
import ca.blarg.gdx.assets.AssetLoadingUtils;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.tilemap3d.tilemesh.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

class TileMeshJsonLoader {
	public static JsonTileMesh load(FileHandle file) {
		Json json = new Json();
		JsonTileMesh definition = json.fromJson(JsonTileMesh.class, file);

		String path = file.parent().path();

		definition.textureAtlas = AssetLoadingUtils.addPathIfNone(definition.textureAtlas, path);
		definition.model = AssetLoadingUtils.addPathIfNone(definition.model, path);
		definition.collisionModel = AssetLoadingUtils.addPathIfNone(definition.collisionModel, path);
		if (definition.models != null) {
			for (int i = 0; i < definition.models.size(); ++i) {
				JsonTileMesh.SubModels subModel = definition.models.get(i);
				subModel.submodel = AssetLoadingUtils.addPathIfNone(subModel.submodel, path);
			}
		}

		return definition;
	}

	public static TileMesh create(FileHandle file, JsonTileMesh definition, AssetManager assetManager) {
		if (!definition.cube && definition.model == null && definition.models == null)
			throw new AssetLoadingException(file.path(), "One of cube, model or models must be specified for each tile.");
		if (definition.textureAtlas == null)
			throw new AssetLoadingException(file.path(), "A texture atlas must be specified.");
		if (definition.collisionModel != null && definition.collisionShape != null)
			throw new AssetLoadingException(file.path(), "collisionModel and collisionShape cannot both be set.");

		TextureAtlas atlas = null;
		if (definition.textureAtlas != null)
			atlas = assetManager.get(definition.textureAtlas, TextureAtlas.class);

		if (!atlas.materialTileMapping.hasMappings() && !definition.cube)
			throw new AssetLoadingException(file.path(), "No material mappings defined for non-cube tile mesh. Material mappings needed to map from source model(s) textures to texture atlas tiles.");

		boolean isCube = definition.cube;
		TextureRegion texture = null;
		TextureRegion topTexture = null;
		TextureRegion bottomTexture = null;
		TextureRegion frontTexture = null;
		TextureRegion backTexture = null;
		TextureRegion leftTexture = null;
		TextureRegion rightTexture = null;
		byte faces = 0;
		Model model = null;
		Model collisionModel;
		byte opaqueSides = 0;
		byte lightValue;
		boolean alpha;
		float translucency;
		Color color = new Color(Color.WHITE);
		Vector3 scaleToSize = null;
		Vector3 positionOffset = null;
		Vector3 collisionPositionOffset = null;

		if (definition.opaqueSides != null) {
			if (definition.opaqueSides.contains("ALL"))
				opaqueSides = TileMesh.SIDE_ALL;
			else {
				if (definition.opaqueSides.contains("TOP"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_TOP, opaqueSides);
				if (definition.opaqueSides.contains("BOTTOM"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_BOTTOM, opaqueSides);
				if (definition.opaqueSides.contains("FRONT"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_FRONT, opaqueSides);
				if (definition.opaqueSides.contains("BACK"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_BACK, opaqueSides);
				if (definition.opaqueSides.contains("LEFT"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_LEFT, opaqueSides);
				if (definition.opaqueSides.contains("RIGHT"))
					opaqueSides = Bitfield.set(TileMesh.SIDE_RIGHT, opaqueSides);
			}
		}

		lightValue = (byte)definition.light;
		alpha = definition.alpha;
		translucency = definition.translucency;
		if (definition.color != null)
			color.set(definition.color);
		if (definition.scaleToSize != null)
			scaleToSize = new Vector3(definition.scaleToSize);
		if (definition.positionOffset != null)
			positionOffset = new Vector3(definition.positionOffset);
		if (definition.collisionPositionOffset != null)
			collisionPositionOffset = new Vector3(definition.collisionPositionOffset);

		if (isCube) {
			if (definition.textures != null) {
				if (definition.textures.top >= 0)
					topTexture = atlas.get(definition.textures.top);
				if (definition.textures.bottom >= 0)
					bottomTexture = atlas.get(definition.textures.bottom);
				if (definition.textures.front >= 0)
					frontTexture = atlas.get(definition.textures.front);
				if (definition.textures.back >= 0)
					backTexture = atlas.get(definition.textures.back);
				if (definition.textures.left >= 0)
					leftTexture = atlas.get(definition.textures.left);
				if (definition.textures.right >= 0)
					rightTexture = atlas.get(definition.textures.right);
			} else if (definition.texture >= 0)
				texture = atlas.get(definition.texture);
			else
				throw new AssetLoadingException(file.path(), "No cube texture specified.");

			if (definition.faces != null) {
				if (definition.faces.contains("ALL"))
					faces = TileMesh.SIDE_ALL;
				else {
					if (definition.faces.contains("TOP"))
						faces = Bitfield.set(TileMesh.SIDE_TOP, opaqueSides);
					if (definition.faces.contains("BOTTOM"))
						faces = Bitfield.set(TileMesh.SIDE_BOTTOM, opaqueSides);
					if (definition.faces.contains("FRONT"))
						faces = Bitfield.set(TileMesh.SIDE_FRONT, opaqueSides);
					if (definition.faces.contains("BACK"))
						faces = Bitfield.set(TileMesh.SIDE_BACK, opaqueSides);
					if (definition.faces.contains("LEFT"))
						faces = Bitfield.set(TileMesh.SIDE_LEFT, opaqueSides);
					if (definition.faces.contains("RIGHT"))
						faces = Bitfield.set(TileMesh.SIDE_RIGHT, opaqueSides);
				}
			}

			if (texture != null)
				return new CubeTileMesh(texture, texture, texture, texture, texture, texture, faces, opaqueSides, lightValue, alpha, translucency, color);
			else
				return new CubeTileMesh(topTexture, bottomTexture, frontTexture, backTexture, leftTexture, rightTexture, faces, opaqueSides, lightValue, alpha, translucency, color);

		} else if (definition.model != null) {
			model = assetManager.get(definition.model, Model.class);
			collisionModel = model; // default is to use the same model

			if (definition.collisionModel != null) {
				// override with a specific collision model
				collisionModel = assetManager.get(definition.collisionModel, Model.class);

			} else if (definition.collisionShape != null) {
				collisionModel = CollisionShapes.get(definition.collisionShape);
				if (collisionModel == null)
					throw new AssetLoadingException(file.path(), "collisionShape not recognized.");
			}

			return new ModelTileMesh(model, collisionModel, atlas.materialTileMapping, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset, collisionPositionOffset);

		} else if (definition.models != null) {
			int numModels = definition.models.size();
			Model[] submodels = new Model[numModels];
			Color[] colors = new Color[numModels];
			Vector3[] scaleToSizes = new Vector3[numModels];
			Vector3[] positionOffsets = new Vector3[numModels];

			for (int j = 0; j < numModels; ++j) {
				JsonTileMesh.SubModels subModelDef = definition.models.get(j);
				submodels[j] = assetManager.get(subModelDef.submodel, Model.class);

				if (subModelDef.color == null)
					colors[j] = new Color(Color.WHITE);
				else
					colors[j] = new Color(subModelDef.color);

				scaleToSizes[j] = subModelDef.scaleToSize;
				positionOffsets[j] = subModelDef.positionOffset;
			}

			collisionModel = null; // TODO: a nice default would be all vertices for all submodels combined

			if (definition.collisionModel != null) {
				//override with a specific collision model
				collisionModel = assetManager.get(definition.collisionModel, Model.class);
			}
			if (definition.collisionShape != null) {
				collisionModel = CollisionShapes.get(definition.collisionShape);
				if (collisionModel == null)
					throw new AssetLoadingException(file.path(), "collisionShape not recognized.");
			}

			return new MultiModelTileMesh(submodels, colors, scaleToSizes, positionOffsets, collisionModel, atlas.materialTileMapping, opaqueSides, lightValue, alpha, translucency, color, scaleToSize, positionOffset, collisionPositionOffset);

		} else
			throw new AssetLoadingException(file.path(), "Unrecognized tile mesh type.");
	}
}
