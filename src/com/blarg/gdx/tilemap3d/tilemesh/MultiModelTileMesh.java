package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.math.MathHelpers;

public class MultiModelTileMesh extends BaseModelTileMesh {
	BoundingBox bounds;
	Vertices vertices;
	Array<Vector3> collisionVertices;
	Vector3 scaleToSize;
	Vector3 positionOffset;
	Vector3 collisionPositionOffset;

	@Override
	public BoundingBox getBounds() {
		return bounds;
	}

	@Override
	public Vertices getVertices() {
		return vertices;
	}

	@Override
	public Vector3[] getCollisionVertices() {
		return collisionVertices.items;
	}

	public MultiModelTileMesh(
			Model[] models,
			Color[] modelColors,
			Vector3[] scaleModelsToSizes,
			Vector3[] modelPositionOffsets,
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
		super(opaqueSides, alpha, translucency, lightValue, color);
		if (modelColors.length != models.length || scaleModelsToSizes.length != models.length || modelPositionOffsets.length != models.length)
			throw new RuntimeException("Mismatching number of submodel data provided.");

		initScalingAndOffsetParams(scaleToSize, positionOffset, collisionPositionOffset);
		setupMesh(models, modelColors, scaleModelsToSizes, modelPositionOffsets, textures);
		setupCollisionVertices(collisionModel);
	}

	private void initScalingAndOffsetParams(Vector3 scaleToSize, Vector3 positionOffset, Vector3 collisionPositionOffset) {
		this.scaleToSize = (scaleToSize == null ? null : new Vector3(scaleToSize));
		this.positionOffset = (positionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(positionOffset));
		this.collisionPositionOffset = (collisionPositionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(collisionPositionOffset));
	}

	private void setupMesh(
			Model[] models,
			Color[] modelColors,
			Vector3[] scaleModelsToSizes,
			Vector3[] modelPositionOffsets,
			MaterialTileMapping textures
	) {
		// setup vertices collection size to the proper amount to hold all submodel vertices
		int numVertices = 0;
		for (int i = 0; i < models.length; ++i)
			numVertices += countModelVertices(models[i]);
		vertices = new Vertices(
				numVertices,
				VertexAttribute.Position(),
				VertexAttribute.ColorUnpacked(),
				VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0)
		);

		bounds = new BoundingBox();
		BoundingBox tmpBounds = new BoundingBox();

		// collect the vertices from each of the models provided
		for (int i = 0; i < models.length; ++i) {
			Model submodel = models[i];
			Color color = modelColors[i];
			Vector3 scaleToSize = scaleModelsToSizes[i];
			Vector3 positionOffset = modelPositionOffsets[i];
			if (positionOffset == null)
				positionOffset = Vector3.Zero;

			// find the bounds of this submodel (scaled if necessary) and extend the final tilemesh
			// bounds as needed by this submodel's bounds.
			// also find the scale factor to scale the model to the requested size at the same time
			BoundingBox submodelBounds = new BoundingBox();
			Vector3 scaleFactor = new Vector3();

			submodel.getBoundingBox(tmpBounds);
			if (scaleToSize != null) {
				MathHelpers.getScaleFactor(tmpBounds.getDimensions(), scaleToSize, scaleFactor);
				submodelBounds.set(Vector3.Zero, scaleToSize);
			} else {
				submodelBounds.set(Vector3.Zero, tmpBounds.getDimensions());
				scaleFactor.set(1.0f, 1.0f, 1.0f);
			}
			bounds.ext(submodelBounds);

			for (int j = 0; j < submodel.nodes.size; ++j)
				collectModelNodeVertices(submodel.nodes.get(j), vertices, textures, color, scaleFactor, positionOffset);
		}

		// final scaling and offsets by the overall model scale/offsets (if provided)
		// TODO
	}

	private void setupCollisionVertices(Model collisionModel) {
		if (collisionModel != null) {
			BoundingBox tmpBounds = new BoundingBox();
			Vector3 scaleFactor = new Vector3();

			if (scaleToSize != null) {
				collisionModel.getBoundingBox(tmpBounds);
				MathHelpers.getScaleFactor(tmpBounds.getDimensions(), scaleToSize, scaleFactor);
			} else
				scaleFactor.set(1.0f, 1.0f, 1.0f);

			int numVertices = countModelVertices(collisionModel);
			collisionVertices = new Array<Vector3>(true, numVertices, Vector3.class);
			for (int i = 0; i < collisionModel.nodes.size; ++i)
				collectModelNodeVertexPositions(collisionModel.nodes.get(i), collisionVertices, scaleFactor, collisionPositionOffset);

		} else {
			// if no collision model is provided, just use the mesh vertices we (should have) previously collected
			// from all the submodels. they will already be scaled/offset appropriately so this is just a simple copy
			collisionVertices = new Array<Vector3>(true, vertices.count(), Vector3.class);
			for (int i = 0; i < vertices.count(); ++i) {
				Vector3 v = new Vector3();
				vertices.getPos(i, v);
				collisionVertices.add(v);
			}
		}
	}

	@Override
	public void dispose() {
	}
}
