package ca.blarg.gdx.tilemap3d.json.tilemesh;

import java.util.ArrayList;

public class JsonMaterialMapping {
	public class Material {
		public String name;
		public int tile;
		public float minU;
		public float maxU;
		public float minV;
		public float maxV;
	}

	public String textureAtlas;
	public ArrayList<Material> materials;
}
