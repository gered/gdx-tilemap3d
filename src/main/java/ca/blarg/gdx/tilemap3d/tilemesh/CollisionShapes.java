package ca.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ObjectMap;

public class CollisionShapes {
	static final ModelBuilder modelBuilder = new ModelBuilder();
	static final ObjectMap<String, Model> shapes = new ObjectMap<String, Model>();

	static {
		shapes.put("cube-1x1x1", buildCube1x1x1());
	}

	private static Model buildCube1x1x1() {
		return modelBuilder.createBox(1.0f, 1.0f, 1.0f, null, VertexAttributes.Usage.Position);
	}

	public static Model get(String shapeName) {
		return shapes.get(shapeName);
	}
}
