package ca.blarg.gdx.tilemap3d.json.tilemesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class JsonTileMesh {
	public class CubeTextures {
		public int top;
		public int bottom;
		public int front;
		public int back;
		public int left;
		public int right;
	}

	public class SubModels {
		public String submodel;
		public Color color;
		public Vector3 scaleToSize;
		public Vector3 positionOffset;
	}

	public String materials;
	public boolean cube;
	public CubeTextures textures;
	public int texture;
	public ArrayList<String> faces;
	public String model;
	public ArrayList<SubModels> models;
	public String collisionModel;
	public String collisionShape;
	public ArrayList<String> opaqueSides;
	public int light;
	public boolean alpha;
	public float translucency;
	public Color color;
	public Vector3 scaleToSize;
	public Vector3 positionOffset;
	public Vector3 collisionPositionOffset;
}
