package ca.blarg.gdx.tilemap3d;

public interface TileRawDataContainer {
	Tile[] getData();

	int getWidth();
	int getHeight();
	int getDepth();

	Tile get(int x, int y, int z);
	Tile getSafe(int x, int y, int z);
}
