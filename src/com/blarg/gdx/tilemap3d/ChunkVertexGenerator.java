package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.nio.FloatBuffer;

public class ChunkVertexGenerator {
	protected final MeshBuilder.VertexInfo vertex = new MeshPartBuilder.VertexInfo();

	final MeshBuilder builder = new MeshBuilder();
	final MeshBuilder alphaBuilder = new MeshBuilder();

	final TileCoord tmpPosition = new TileCoord();
	final Color tmpColor = new Color();
	final Vector3 tmpOffset = new Vector3();

	public void generate(TileChunk chunk) {
		TileMap tileMap = chunk.tileMap;

		builder.begin(
				VertexAttributes.Usage.Position |
				VertexAttributes.Usage.Color |
				VertexAttributes.Usage.Normal |
				VertexAttributes.Usage.TextureCoordinates
		);
		alphaBuilder.begin(
				VertexAttributes.Usage.Position |
				VertexAttributes.Usage.Color |
				VertexAttributes.Usage.Normal |
				VertexAttributes.Usage.TextureCoordinates
		);

		for (int y = 0; y < chunk.getHeight(); ++y) {
			for (int z = 0; z < chunk.getDepth(); ++z) {
				for (int x = 0; x < chunk.getWidth(); ++x) {
					Tile tile = chunk.get(x, y, z);
					if (tile.tile == Tile.NO_TILE)
						continue;

					TileMesh mesh = chunk.tileMap.tileMeshes.get(tile);

					// "world/tilemap space" position that this tile is at
					tmpPosition.x = x + (int)chunk.getPosition().x;
					tmpPosition.y = y + (int)chunk.getPosition().y;
					tmpPosition.z = z + (int)chunk.getPosition().z;

					Matrix4 transform = Tile.getTransformationFor(tile);

					// tile color
					if (tile.hasCustomColor())
						tmpColor.set(tile.color);
					else
						tmpColor.set(mesh.color);

					if (mesh instanceof CubeTileMesh) {
						CubeTileMesh cubeMesh = (CubeTileMesh)mesh;

						// determine what's next to each cube face
						Tile left = chunk.getWithinSelfOrNeighbourSafe(x - 1, y, z);
						Tile right = chunk.getWithinSelfOrNeighbourSafe(x + 1, y, z);
						Tile forward = chunk.getWithinSelfOrNeighbourSafe(x, y, z - 1);
						Tile backward = chunk.getWithinSelfOrNeighbourSafe(x, y, z + 1);
						Tile down = chunk.getWithinSelfOrNeighbourSafe(x, y - 1, z);
						Tile up = chunk.getWithinSelfOrNeighbourSafe(x, y + 1, z);

						// evaluate each face's visibility and add it's vertices if needed one at a time
						if ((left == null || left.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(left).isOpaque(TileMesh.SIDE_RIGHT)) && cubeMesh.hasFace(TileMesh.SIDE_LEFT)) {
							// left face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.leftFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.leftFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
						if ((right == null || right.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(right).isOpaque(TileMesh.SIDE_LEFT)) && cubeMesh.hasFace(TileMesh.SIDE_RIGHT)) {
							// right face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.rightFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.rightFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
						if ((forward == null || forward.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(forward).isOpaque(TileMesh.SIDE_BACK)) && cubeMesh.hasFace(TileMesh.SIDE_FRONT)) {
							// front face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.frontFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.frontFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
						if ((backward == null || backward.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(backward).isOpaque(TileMesh.SIDE_FRONT)) && cubeMesh.hasFace(TileMesh.SIDE_BACK)) {
							// back face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.backFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.backFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
						if ((down == null || down.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(down).isOpaque(TileMesh.SIDE_TOP)) && cubeMesh.hasFace(TileMesh.SIDE_BOTTOM)) {
							// bottom face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.bottomFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.bottomFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
						if ((up == null || up.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(up).isOpaque(TileMesh.SIDE_BOTTOM)) && cubeMesh.hasFace(TileMesh.SIDE_TOP)) {
							// top face is visible
							if (cubeMesh.alpha)
								addMesh(alphaBuilder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.topFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
							else
								addMesh(builder, cubeMesh, chunk, tmpPosition, transform, tmpColor, cubeMesh.topFaceVertexOffset, TileMesh.CUBE_VERTICES_PER_FACE);
						}
					} else {
						boolean visible = false;

						// visibility determination. we check for at least one
						// adjacent empty space / non-opaque tile
						Tile left = chunk.getWithinSelfOrNeighbourSafe(x - 1, y, z);
						Tile right = chunk.getWithinSelfOrNeighbourSafe(x + 1, y, z);
						Tile forward = chunk.getWithinSelfOrNeighbourSafe(x, y, z - 1);
						Tile backward = chunk.getWithinSelfOrNeighbourSafe(x, y, z + 1);
						Tile down = chunk.getWithinSelfOrNeighbourSafe(x, y - 1, z);
						Tile up = chunk.getWithinSelfOrNeighbourSafe(x, y + 1, z);

						// null == empty space (off the edge of the entire map)
						if (
								(left == null || left.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(left).isOpaque(TileMesh.SIDE_RIGHT)) ||
								(right == null || right.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(right).isOpaque(TileMesh.SIDE_LEFT)) ||
								(forward == null || forward.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(forward).isOpaque(TileMesh.SIDE_BACK)) ||
								(backward == null || backward.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(backward).isOpaque(TileMesh.SIDE_FRONT)) ||
								(up == null || up.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(up).isOpaque(TileMesh.SIDE_BOTTOM)) ||
								(down == null || down.tile == Tile.NO_TILE || !tileMap.tileMeshes.get(down).isOpaque(TileMesh.SIDE_TOP))
						)
							visible = true;

						if (visible) {
							if (mesh.alpha)
								addMesh(alphaBuilder, mesh, chunk, tmpPosition, transform, tmpColor, 0, mesh.getVertices().count());
							else
								addMesh(builder, mesh, chunk, tmpPosition, transform, tmpColor, 0, mesh.getVertices().count());
						}
					}
				}
			}
		}

		chunk.mesh.setMesh(builder.end());
		chunk.alphaMesh.setMesh(alphaBuilder.end());
	}

	private void addMesh(MeshBuilder builder, TileMesh sourceMesh, TileChunk chunk, TileCoord position, Matrix4 transform, Color color, int firstVertex, int numVertices) {
		// adjust position by the tilemesh offset. TileMesh's are modeled using the
		// origin (0,0,0) as the center and are 1 unit wide/deep/tall. So, their
		// max extents are from -0.5,-0.5,-0.5 to 0.5,0.5,0.5. For rendering
		// purposes in a chunk, we want the extents to be 0,0,0 to 1,1,1. This
		// adjustment fixes that
		tmpOffset.set(TileMesh.OFFSET);
		tmpOffset.x += (float)position.x;
		tmpOffset.y += (float)position.y;
		tmpOffset.z += (float)position.z;

		// copy vertices
		for (int i = 0, j = firstVertex; i < numVertices; ++i, ++j)
			copyVertex(builder, sourceMesh, j, tmpOffset, transform, color, chunk);
	}

	private void copyVertex(MeshBuilder builder, TileMesh sourceMesh, int sourceVertexIndex, Vector3 positionOffset, Matrix4 transform, Color color, TileChunk chunk) {
		sourceMesh.getVertices().getVertex(sourceVertexIndex, vertex);

		// transform if applicable... (this will probably just be per-tile rotation)
		if (transform != null) {
			vertex.position.mul(transform);
			vertex.normal.mul(transform);
		}

		// translate vertex into "world/tilemap space"
		vertex.position.add(positionOffset);

		builder.vertex(vertex);
	}
}
