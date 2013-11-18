package ca.blarg.gdx.tilemap3d;

import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import ca.blarg.gdx.math.SweptSphere;
import ca.blarg.gdx.math.SweptSphereCollisionTester;
import ca.blarg.gdx.math.SweptSphereWorldCollisionChecker;
import ca.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class TileMapSweptSphereCollisionChecker implements SweptSphereWorldCollisionChecker {
	static final TileCoord min = new TileCoord();
	static final TileCoord max = new TileCoord();
	static final Vector3 tileWorldPosition = new Vector3();
	static final Vector3 a = new Vector3();
	static final Vector3 b = new Vector3();
	static final Vector3 c = new Vector3();

	public TileMap tileMap;

	public final TileCoord lastCollisionTilePosition = new TileCoord();

	@Override
	public boolean checkForCollisions(SweptSphere sphere, BoundingBox possibleCollisionArea) {
		if (tileMap == null)
			throw new UnsupportedOperationException("No TileMap object is set.");

		min.set(0, 0, 0);
		max.set(0, 0, 0);

		float lastCollisionDistance = Float.MAX_VALUE;
		boolean foundCollision = false;
		lastCollisionTilePosition.set(TileCoord.Zero);

		// TODO: I don't think we even need to check if the area overlaps ... ?
		//       (it probably always will, right?)
		boolean overlapping = tileMap.getOverlappedTiles(possibleCollisionArea, min, max);
		if (overlapping) {
			for (int y = min.y; y <= max.y; ++y) {
				for (int z = min.z; z < max.z; ++z) {
					for (int x = min.x; x < max.x; ++x) {
						Tile tile = tileMap.get(x, y, z);

						// only check solid tiles
						if (tile.isCollideable()) {
							// check each triangle in this tile's mesh
							TileMesh mesh = tileMap.tileMeshes.get(tile);
							Vector3[] vertices = mesh.getCollisionVertices();

							// world position of this tile, will be used to move each
							// mesh triangle into world space
							tileWorldPosition.set(x, y, z);

							// also add the global TileMesh offset so the mesh is within
							// 0,0,0 to 1,1,1 and not -0.5,-0.5,-0.5 to 0.5,0.5,0.5
							tileWorldPosition.add(TileMesh.OFFSET);

							Matrix4 transform = Tile.getTransformationFor(tile);

							for (int i = 0; i < vertices.length; i += 3) {
								// get the vertices making up this triangle
								a.set(vertices[i]);
								b.set(vertices[i + 1]);
								c.set(vertices[i + 2]);

								if (transform != null) {
									a.mul(transform);
									b.mul(transform);
									c.mul(transform);
								}

								// move these vertices into world space
								a.add(tileWorldPosition);
								b.add(tileWorldPosition);
								c.add(tileWorldPosition);

								// test against it. we don't actually care about the
								// return value, but just want the CollisionPacket
								// to be updated. at the end of all these tests, it
								// will contain info for the closest intersection
								// found
								boolean collided = SweptSphereCollisionTester.test(sphere, a, b, c);
								if (collided && sphere.nearestCollisionDistance < lastCollisionDistance) {
									foundCollision = true;
									lastCollisionDistance = sphere.nearestCollisionDistance;

									// this is the closest collision found so far
									// record the grid position ...
									lastCollisionTilePosition.set(x, y, z);
								}
							}
						}
					}
				}
			}
		}

		return foundCollision;
	}
}
