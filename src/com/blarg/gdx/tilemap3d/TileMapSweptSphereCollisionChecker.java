package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.blarg.gdx.Bitfield;
import com.blarg.gdx.math.SweptSphere;
import com.blarg.gdx.math.SweptSphereCollisionTester;
import com.blarg.gdx.math.SweptSphereWorldCollisionChecker;
import com.blarg.gdx.tilemap3d.tilemesh.TileMesh;

public class TileMapSweptSphereCollisionChecker implements SweptSphereWorldCollisionChecker {
	static final TileCoord min = new TileCoord();
	static final TileCoord max = new TileCoord();
	static final Vector3 tileWorldPosition = new Vector3();
	static final Vector3 a = new Vector3();
	static final Vector3 b = new Vector3();
	static final Vector3 c = new Vector3();

	TileMap tileMap;

	public TileMapSweptSphereCollisionChecker(TileMap tileMap) {
		if (tileMap == null)
			throw new IllegalArgumentException("tileMap can not be null.");

		this.tileMap = tileMap;
	}

	@Override
	public void checkForCollisions(SweptSphere sphere, BoundingBox possibleCollisionArea) {
		min.set(0, 0, 0);
		max.set(0, 0, 0);

		float lastCollisionDistance = Float.MAX_VALUE;

		// TODO: I don't think we even need to check if the area overlaps ... ?
		//       (it probably always will, right?)
		boolean overlapping = tileMap.getOverlappedTiles(possibleCollisionArea, min, max);
		if (overlapping) {
			for (int y = min.y; y <= max.y; ++y) {
				for (int z = min.z; z < max.z; ++z) {
					for (int x = min.x; x < max.x; ++x) {
						Tile tile = tileMap.get(x, y, z);

						// only check solid tiles
						if (Bitfield.isSet(Tile.FLAG_COLLIDEABLE, tile.flags)) {
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
								if (collided && sphere.nearestCollisionDistance < lastCollisionDistance)
									lastCollisionDistance = sphere.nearestCollisionDistance;
							}
						}
					}
				}
			}
		}
	}
}
