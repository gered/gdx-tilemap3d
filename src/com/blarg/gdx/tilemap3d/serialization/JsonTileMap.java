package com.blarg.gdx.tilemap3d.serialization;

import java.util.ArrayList;

public class JsonTileMap {
	public int chunkWidth;
	public int chunkHeight;
	public int chunkDepth;
	public int widthInChunks;
	public int heightInChunks;
	public int depthInChunks;
	public String lightingMode;
	public ArrayList<String> chunks;
}
