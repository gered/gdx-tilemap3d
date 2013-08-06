package com.blarg.gdx.tilemap3d;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.blarg.gdx.math.IntersectionTester;
import com.blarg.gdx.math.MathHelpers;
import com.blarg.gdx.tilemap3d.tilemesh.TileMesh;
import com.blarg.gdx.tilemap3d.tilemesh.TileMeshCollection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class TileContainer {
	static final Vector3 tmp1 = new Vector3();
	static final Vector3 tmpTMax = new Vector3();
	static final Vector3 tmpTDelta = new Vector3();
	static final TileCoord tmpCoords = new TileCoord();

	public abstract int getWidth();
	public abstract int getHeight();
	public abstract int getDepth();
	public abstract int getMinX();
	public abstract int getMinY();
	public abstract int getMinZ();
	public abstract int getMaxX();
	public abstract int getMaxY();
	public abstract int getMaxZ();

	public abstract Tile get(int x, int y, int z);
	public abstract Tile getSafe(int x, int y, int z);

	public abstract Vector3 getPosition();
	public abstract BoundingBox getBounds();

	public boolean isWithinBounds(int x, int y, int z) {
		if (x < getMinX() || x > getMaxX())
			return false;
		else if (y < getMinY() || y > getMaxY())
			return false;
		else if (z < getMinZ() || z > getMaxZ())
			return false;
		else
			return true;
	}

	public boolean isWithinLocalBounds(int x, int y, int z) {
		if (x < 0 || x >= getWidth())
			return false;
		else if (y < 0 || y >= getHeight())
			return false;
		else if (z < 0 || z >= getDepth())
			return false;
		else
			return true;
	}

	public void getBoundingBoxFor(int x, int y, int z, BoundingBox result) {
		// local "TileContainer space"
		result.min.set(x, y, z);
		result.max.set(x + 1.0f, y + 1.0f, z + 1.0f);   // 1.0f = tile width

		// move to "world/tilemap space"
		result.min.add(getBounds().min);
		result.max.add(getBounds().min);
	}

	public boolean getOverlappedTiles(BoundingBox box, TileCoord min, TileCoord max) {
		// make sure the given box actually intersects with this TileContainer in the first place
		if (!IntersectionTester.overlaps(getBounds(), box))
			return false;

		// convert to tile coords (these will be in "world/tilemap space")
		// HACK: ceil() calls and "-1"'s keep us from picking up too many/too few
		// tiles. these were arrived at through observation
		int minX = (int)box.min.x;
		int minY = (int)box.min.y;
		int minZ = (int)box.min.z;
		int maxX = MathUtils.ceil(box.max.x);
		int maxY = MathUtils.ceil(box.max.y - 1.0f);
		int maxZ = MathUtils.ceil(box.max.z);

		// trim off the excess bounds so that we end up with a min-to-max area
		// that is completely within the bounds of this TileContainer
		// HACK: "+1"'s ensure we pick up just the right amount of tiles. these were arrived
		// at through observation
		minX = MathUtils.clamp(minX, getMinX(), getMaxX() + 1);
		minY = MathUtils.clamp(minY, getMinY(), getMaxY());
		minZ = MathUtils.clamp(minZ, getMinZ(), getMaxZ() + 1);
		maxX = MathUtils.clamp(maxX, getMinX(), getMaxX() + 1);
		maxY = MathUtils.clamp(maxY, getMinY(), getMaxY());
		maxZ = MathUtils.clamp(maxZ, getMinZ(), getMaxZ() + 1);

		// return the leftover area, converted to the local coordinate space of the TileContainer
		min.x = minX - getMinX();
		min.y = minY - getMinY();
		min.z = minZ - getMinZ();
		max.x = maxX - getMinX();
		max.y = maxY - getMinY();
		max.z = maxZ - getMinZ();

		return true;
	}

	public boolean checkForCollision(Ray ray, TileCoord collisionCoords) {
		// make sure that the ray and this TileContainer can actually collide in the first place
		if (!Intersector.intersectRayBounds(ray, getBounds(), tmp1))
			return false;

		// convert initial collision point to tile coords (this is in "world/tilemap space")
		int currentX = (int)tmp1.x;
		int currentY = (int)tmp1.y;
		int currentZ = (int)tmp1.z;

		// make sure the coords are inrange of this container. due to some floating
		// point errors / decimal truncating from the above conversion we could
		// end up with one or more that are very slightly out of bounds.
		// this is still in "world/tilemap space"
		currentX = MathUtils.clamp(currentX, getMinX(), getMaxX());
		currentY = MathUtils.clamp(currentY, getMinY(), getMaxY());
		currentZ = MathUtils.clamp(currentZ, getMinZ(), getMaxZ());

		// convert to the local space of this TileContainer
		currentX -= getMinX();
		currentY -= getMinY();
		currentZ -= getMinZ();

		// is the start position colliding with a solid tile?
		Tile startTile = get(currentX, currentY, currentZ);
		if (startTile.isCollideable())
		{
			// collision found, set the tile coords of the collision
			if (collisionCoords != null) {
				collisionCoords.x = currentX;
				collisionCoords.y = currentY;
				collisionCoords.z = currentZ;
			}

			// and we're done
			return true;
		}

		// no collision initially, continue on with the rest ...

		// step increments in "TileContainer tile" units
		int stepX = (int)MathHelpers.sign(ray.direction.x);
		int stepY = (int)MathHelpers.sign(ray.direction.y);
		int stepZ = (int)MathHelpers.sign(ray.direction.z);

		// tile boundary (needs to be in "world/tilemap space")
		int tileBoundaryX = getMinX() + currentX + (stepX > 0 ? 1 : 0);
		int tileBoundaryY = getMinY() + currentY + (stepY > 0 ? 1 : 0);
		int tileBoundaryZ = getMinZ() + currentZ + (stepZ > 0 ? 1 : 0);

		// HACK: for the tMax and tDelta initial calculations below, if any of the
		//       components of ray.direction are zero, it will result in "inf"
		//       components in tMax or tDelta. This is fine, but it has to be
		//       *positive* "inf", not negative. What I found was that sometimes
		//       they would be negative, sometimes positive. So, we force them to be
		//       positive below. Additionally, "nan" components (which will happen
		//       if both sides of the divisions are zero) are bad, and we need to
		//       change that up for "inf" as well.

		// determine how far we can travel along the ray before we hit a tile boundary
		tmpTMax.set(
				(tileBoundaryX - ray.origin.x) / ray.direction.x,
				(tileBoundaryY - ray.origin.y) / ray.direction.y,
				(tileBoundaryZ - ray.origin.z) / ray.direction.z
		);
		if (tmpTMax.x == Float.NEGATIVE_INFINITY)
			tmpTMax.x = Float.POSITIVE_INFINITY;
		if (tmpTMax.y == Float.NEGATIVE_INFINITY)
			tmpTMax.y = Float.POSITIVE_INFINITY;
		if (tmpTMax.z == Float.NEGATIVE_INFINITY)
			tmpTMax.z = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTMax.x))
			tmpTMax.x = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTMax.y))
			tmpTMax.y = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTMax.z))
			tmpTMax.z = Float.POSITIVE_INFINITY;

		// determine how far we must travel along the ray before we cross a grid cell
		tmpTDelta.set(
				stepX / ray.direction.x,
				stepY / ray.direction.y,
				stepZ / ray.direction.z
		);
		if (tmpTDelta.x == Float.NEGATIVE_INFINITY)
			tmpTDelta.x = Float.POSITIVE_INFINITY;
		if (tmpTDelta.y == Float.NEGATIVE_INFINITY)
			tmpTDelta.y = Float.POSITIVE_INFINITY;
		if (tmpTDelta.z == Float.NEGATIVE_INFINITY)
			tmpTDelta.z = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTDelta.x))
			tmpTDelta.x = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTDelta.y))
			tmpTDelta.y = Float.POSITIVE_INFINITY;
		if (Float.isNaN(tmpTDelta.z))
			tmpTDelta.z = Float.POSITIVE_INFINITY;

		boolean collided = false;
		boolean outOfContainer = false;
		while (!outOfContainer)
		{
			// step up to the next tile using the lowest step value
			// (in other words, we figure out on which axis, X, Y, or Z, the next
			// tile that lies on the ray is closest, and use that axis step increment
			// to move us up to get to the next tile location)
			if (tmpTMax.x < tmpTMax.y && tmpTMax.x < tmpTMax.z)
			{
				// tMax.x is lowest, the YZ tile boundary plane is closest
				currentX += stepX;
				tmpTMax.x += tmpTDelta.x;
			}
			else if (tmpTMax.y < tmpTMax.z)
			{
				// tMax.y is lowest, the XZ tile boundary plane is closest
				currentY += stepY;
				tmpTMax.y += tmpTDelta.y;
			}
			else
			{
				// tMax.z is lowest, the XY tile boundary plane is closest
				currentZ += stepZ;
				tmpTMax.z += tmpTDelta.z;
			}

			// need to figure out if this new position is still inside the bounds of
			// the container before we can attempt to determine if the current tile is
			// solid
			// (remember, currentX/Y/Z is in the local "TileContainer space"
			if (
					currentX < 0 || currentX >= getWidth() ||
					currentY < 0 || currentY >= getHeight() ||
					currentZ < 0 || currentZ >= getDepth()
				)
				outOfContainer = true;
			else
			{
				// still inside and at the next position, test for a solid tile
				Tile tile = get(currentX, currentY, currentZ);
				if (tile.isCollideable())
				{
					collided = true;

					// set the tile coords of the collision
					if (collisionCoords != null) {
						collisionCoords.x = currentX;
						collisionCoords.y = currentY;
						collisionCoords.z = currentZ;
					}

					break;
				}
			}
		}

		return collided;
	}

	public boolean checkForCollision(Ray ray, TileCoord collisionCoords, TileMeshCollection tileMeshes, Vector3 tileMeshCollisionPoint) {
		// if the ray doesn't collide with any solid tiles in the first place, then
		// we can skip this more expensive triangle collision check...
		if (!checkForCollision(ray, tmpCoords))
			return false;

		if (collisionCoords != null)
			collisionCoords.set(tmpCoords);

		// now perform the per-triangle collision check against the tile position
		// where the ray ended up at the end of the above checkForCollision() call
		return checkForCollisionWithTileMesh(
				ray,
				tmpCoords.x, tmpCoords.y, tmpCoords.z,
				tileMeshes,
				tileMeshCollisionPoint
		);
	}

	static final Vector3 tileWorldPosition = new Vector3();
	static final Vector3 collisionPoint = new Vector3();
	static final Vector3 tmpA = new Vector3();
	static final Vector3 tmpB = new Vector3();
	static final Vector3 tmpC = new Vector3();
	public boolean checkForCollisionWithTileMesh(Ray ray, int x, int y, int z, TileMeshCollection tileMeshes, Vector3 outCollisionPoint) {
		Tile tile = get(x, y, z);
		TileMesh mesh = tileMeshes.get(tile);

		Vector3[] vertices = mesh.getCollisionVertices();

		// world position of this tile, will be used to move each
		// mesh triangle into world space
		tileWorldPosition.set((float)x, (float)y, (float)z);

		float closestSquaredDistance = Float.POSITIVE_INFINITY;
		boolean collided = false;
		collisionPoint.set(Vector3.Zero);

		for (int i = 0; i < vertices.length; i += 3) {
			// get the vertices making up this triangle (and move the vertices into world space)
			tmpA.set(vertices[i]).add(tileWorldPosition);
			tmpB.set(vertices[i + 1]).add(tileWorldPosition);
			tmpC.set(vertices[i + 2]).add(tileWorldPosition);

			if (Intersector.intersectRayTriangle(ray, tmpA, tmpB, tmpC, collisionPoint)) {
				collided = true;

				// if this is the closest collision yet, then keep the distance
				// and point of collision
				float squaredDistance = tmp1.set(collisionPoint).sub(ray.origin).len2();
				if (squaredDistance < closestSquaredDistance) {
					closestSquaredDistance = squaredDistance;
					outCollisionPoint.set(collisionPoint);
				}
			}
		}

		return collided;
	}
}
