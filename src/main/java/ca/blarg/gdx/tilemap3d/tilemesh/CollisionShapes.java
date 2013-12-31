package ca.blarg.gdx.tilemap3d.tilemesh;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ObjectMap;

public class CollisionShapes {
	static final ModelBuilder modelBuilder = new ModelBuilder();
	static final ObjectMap<String, Model> shapes = new ObjectMap<String, Model>();

	static {
		shapes.put("cube-1x1x1", buildCube1x1x1());
	}

	public static Model get(String shapeName) {
		return shapes.get(shapeName);
	}

	private static Model buildCube1x1x1() {
		modelBuilder.begin();
		MeshPartBuilder partBuilder = modelBuilder.part("collisionShape", GL10.GL_TRIANGLES, VertexAttributes.Usage.Position, null);
		addBoxShape(partBuilder, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f);
		return modelBuilder.end();
	}

	private static void addBoxShape(MeshPartBuilder partBuilder, float ax, float ay, float az, float bx, float by, float bz) {
		// TODO: for some reason, ModelBuilder's createBox() creates a box model in a way that BaseModelTileMesh
		// collects the vertices from it incorrectly. for now, we just manually create a box's vertices/indices ourself

		partBuilder.vertex(ax, by, bz);  // 0
		partBuilder.vertex(bx, by, bz);  // 1
		partBuilder.vertex(ax, by, az);  // 2
		partBuilder.vertex(bx, by, az);  // 3
		partBuilder.vertex(bx, ay, bz);  // 4
		partBuilder.vertex(ax, ay, bz);  // 5
		partBuilder.vertex(bx, ay, az);  // 6
		partBuilder.vertex(ax, ay, az);  // 7

		// top
		partBuilder.index((short)0, (short)1, (short)2);
		partBuilder.index((short)1, (short)3, (short)2);

		// bottom
		partBuilder.index((short)4, (short)5, (short)6);
		partBuilder.index((short)5, (short)7, (short)6);

		// front
		partBuilder.index((short)6, (short)7, (short)3);
		partBuilder.index((short)7, (short)2, (short)3);

		// back
		partBuilder.index((short)5, (short)4, (short)0);
		partBuilder.index((short)4, (short)1, (short)0);

		// left
		partBuilder.index((short)7, (short)5, (short)2);
		partBuilder.index((short)5, (short)0, (short)2);

		// right
		partBuilder.index((short)4, (short)6, (short)1);
		partBuilder.index((short)6, (short)3, (short)1);
	}
}
