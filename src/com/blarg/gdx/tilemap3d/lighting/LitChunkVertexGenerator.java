package com.blarg.gdx.tilemap3d.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.blarg.gdx.graphics.Vertices;
import com.blarg.gdx.tilemap3d.ChunkVertexGenerator;
import com.blarg.gdx.tilemap3d.Tile;
import com.blarg.gdx.tilemap3d.TileChunk;
import com.blarg.gdx.tilemap3d.TileCoord;
import com.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class LitChunkVertexGenerator extends ChunkVertexGenerator {
	final Vector3 tmpOffset = new Vector3();
	final Vector3 tmpLightSource = new Vector3();
	final Color finalLightingColor = new Color();

	@Override
	protected void addMesh(MeshBuilder builder, Tile tile, TileMesh sourceMesh, TileChunk chunk, TileCoord position, Matrix4 transform, Color color, int firstVertex, int numVertices) {
		// adjust position by the tilemesh offset. TileMesh's are modeled using the
		// origin (0,0,0) as the center and are 1 unit wide/deep/tall. So, their
		// max extents are from -0.5,-0.5,-0.5 to 0.5,0.5,0.5. For rendering
		// purposes in a chunk, we want the extents to be 0,0,0 to 1,1,1. This
		// adjustment fixes that
		tmpOffset.set(TileMesh.OFFSET);
		tmpOffset.x += (float)position.x;
		tmpOffset.y += (float)position.y;
		tmpOffset.z += (float)position.z;

		// figure out what the default lighting value is for this chunk
		byte defaultLightValue = chunk.tileMap.skyLightValue;
		if (chunk.tileMap.ambientLightValue > defaultLightValue)
			defaultLightValue = chunk.tileMap.ambientLightValue;

		Vertices sourceVertices = sourceMesh.getVertices();
		sourceVertices.moveTo(firstVertex);

		// copy vertices
		for (int i = 0; i < numVertices; ++i) {
			sourceVertices.getVertex(vertex);

			// transform if applicable... (this will probably just be per-tile rotation)
			if (transform != null) {
				vertex.position.mul(transform);
				vertex.normal.rot(transform);
			}

			// translate vertex into "world/tilemap space"
			vertex.position.add(tmpOffset);

			// the color we set to the chunk mesh determines the brightness (in other words: it is the lighting value)

			// use the tile that's adjacent to this one in the direction that
			// this vertex's normal is pointing as the light source
			tmpLightSource.set(vertex.position).add(vertex.normal);

			// if the light source position is off the bounds of the entire world
			// then use the default light value.
			// the below call to TileChunk.getWithinSelfOrNeighbour() actually does
			// do bounds checking, but we would need to cast from float to int
			// first. this causes some issues when the one or more of the
			// lightSource x/y/z values are between 0 and -1 (rounds up to 0 when
			// using a cast). rather then do some weird custom rounding, we just
			// check for negatives to ensure we catch them and handle it properly
			// NOTE: this is only a problem currently because world coords are
			//       always >= 0. this will need to be adjusted if that changes
			float brightness;
			if (tmpLightSource.x < 0.0f || tmpLightSource.y < 0.0f || tmpLightSource.z < 0.0f)
				brightness = Tile.getBrightness(defaultLightValue);
			else {
				// light source is within the boundaries of the world, get the
				// actual tile (may or may not be in a neighbouring chunk)
				int lightX = (int)tmpLightSource.x - chunk.getMinX();
				int lightY = (int)tmpLightSource.y - chunk.getMinY();
				int lightZ = (int)tmpLightSource.z - chunk.getMinZ();

				Tile lightSourceTile = chunk.getWithinSelfOrNeighbourSafe(lightX, lightY, lightZ);
				if (lightSourceTile == null)
					brightness = Tile.getBrightness(defaultLightValue);
				else
					brightness = lightSourceTile.getBrightness();
			}

			finalLightingColor.set(
					vertex.color.r * color.r * brightness,
					vertex.color.g * color.g * brightness,
					vertex.color.b * color.b * brightness,
					vertex.color.a * color.a
			);
			vertex.color.set(finalLightingColor);

			builder.vertex(vertex);
			sourceVertices.moveNext();
		}
	}
}
