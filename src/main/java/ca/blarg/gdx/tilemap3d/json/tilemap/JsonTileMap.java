package ca.blarg.gdx.tilemap3d.json.tilemap;

import java.util.ArrayList;

public class JsonTileMap {
	public int chunkWidth;
	public int chunkHeight;
	public int chunkDepth;
	public int widthInChunks;
	public int heightInChunks;
	public int depthInChunks;
	public String lightingMode;
	public int ambientLightValue;
	public int skyLightValue;
	public String tileMeshes;
	public ArrayList<String> chunks;
}
