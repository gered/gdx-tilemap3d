package com.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.math.MathHelpers;

public class ModelTileMesh extends BaseModelTileMesh {
	static final Vector3 tmpPosition = new Vector3();
	static final Vector3 tmpNormal = new Vector3();
	static final BoundingBox tmpModelBounds = new BoundingBox();
	static final Vector3 tmpScaleFactor = new Vector3();

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

	public ModelTileMesh(
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
		super(opaqueSides, alpha, translucency, lightValue, color);
		initScalingAndOffsetParams(scaleToSize, positionOffset, collisionPositionOffset);
		setupMesh(model, textures);
		setupCollisionVertices(collisionModel);
	}

	private void initScalingAndOffsetParams(Vector3 scaleToSize, Vector3 positionOffset, Vector3 collisionPositionOffset) {
		this.scaleToSize = (scaleToSize == null ? null : new Vector3(scaleToSize));
		this.positionOffset = (positionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(positionOffset));
		this.collisionPositionOffset = (collisionPositionOffset == null ? new Vector3(Vector3.Zero) : new Vector3(collisionPositionOffset));
	}

	private void setupMesh(Model model, MaterialTileMapping textures) {
		int numVertices = countModelVertices(model);
		vertices = new Vertices(
				numVertices,
				VertexAttribute.Position(),
				VertexAttribute.ColorUnpacked(),
				VertexAttribute.Normal(),
				VertexAttribute.TexCoords(0)
		);

		model.calculateBoundingBox(tmpModelBounds);
		if (scaleToSize != null) {
			MathHelpers.getScaleFactor(tmpModelBounds.getDimensions(), scaleToSize, tmpScaleFactor);
			bounds = new BoundingBox().set(Vector3.Zero, scaleToSize);
		} else {
			bounds = new BoundingBox().set(Vector3.Zero, tmpModelBounds.getDimensions());
			tmpScaleFactor.set(1.0f, 1.0f, 1.0f);
		}

		for (int i = 0; i < model.nodes.size; ++i)
			collectModelNodeVertices(model.nodes.get(i), vertices, textures, color, tmpScaleFactor, positionOffset);
	}

	private void setupCollisionVertices(Model collisionModel) {
		if (scaleToSize != null) {
			collisionModel.calculateBoundingBox(tmpModelBounds);
			MathHelpers.getScaleFactor(tmpModelBounds.getDimensions(), scaleToSize, tmpScaleFactor);
		} else
			tmpScaleFactor.set(1.0f, 1.0f, 1.0f);

		int numVertices = countModelVertices(collisionModel);
		collisionVertices = new Array<Vector3>(true, numVertices, Vector3.class);
		for (int i = 0; i < collisionModel.nodes.size; ++i)
			collectModelNodeVertexPositions(collisionModel.nodes.get(i), collisionVertices, tmpScaleFactor, collisionPositionOffset);
	}

	@Override
	public void dispose() {
	}
}
