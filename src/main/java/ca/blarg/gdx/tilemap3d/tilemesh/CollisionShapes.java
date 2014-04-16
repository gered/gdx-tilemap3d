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

	public static void init() {
		if (shapes.size > 0)
			throw new IllegalStateException("Collision shapes have already been initialized.");

		shapes.put("cube-1x1x1", buildBox(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f));
		shapes.put("cube-2x2x2", buildBox(-0.5f, -0.5f, -0.5f, 1.5f, 1.5f, 1.5f));
		shapes.put("cube-3x3x3", buildBox(-0.5f, -0.5f, -0.5f, 2.5f, 2.5f, 2.5f));

		shapes.put("cube-0.5x0.5x0.5", buildBox(-0.25f, -0.25f, -0.25f, 0.25f, 0.25f, 0.25f));

		shapes.put("box-0.3x1x0.3", buildBox(-0.15f, -0.5f, -0.15f, 0.15f, 0.5f, 0.15f));
		shapes.put("box-0.5x1x0.5", buildBox(-0.25f, -0.5f, -0.25f, 0.25f, 0.5f, 0.25f));
		shapes.put("box-0.7x1x0.7", buildBox(-0.35f, -0.5f, -0.35f, 0.35f, 0.5f, 0.35f));

		shapes.put("box-0.3x1x1", buildBox(-0.15f, -0.5f, -0.5f, 0.15f, 0.5f, 0.5f));
		shapes.put("box-0.5x1x1", buildBox(-0.25f, -0.5f, -0.5f, 0.25f, 0.5f, 0.5f));
		shapes.put("box-0.7x1x1", buildBox(-0.35f, -0.5f, -0.5f, 0.35f, 0.5f, 0.5f));

		shapes.put("box-1x1x0.3", buildBox(-0.5f, -0.5f, -0.15f, 0.5f, 0.5f, 0.15f));
		shapes.put("box-1x1x0.5", buildBox(-0.5f, -0.5f, -0.25f, 0.5f, 0.5f, 0.25f));
		shapes.put("box-1x1x0.7", buildBox(-0.5f, -0.5f, -0.35f, 0.5f, 0.5f, 0.35f));

		shapes.put("box-1x2x1", buildBox(-0.5f, -0.5f, -0.5f, 0.5f, 1.5f, 0.5f));
		shapes.put("box-0.7x2x0.7", buildBox(-0.35f, -0.5f, -0.35f, 0.35f, 1.5f, 0.35f));
		shapes.put("box-2x1x2", buildBox(-0.5f, -0.5f, -0.5f, 1.5f, 0.5f, 1.5f));

		shapes.put("ramp-1x1x1", buildRamp(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f));
		shapes.put("ramp-2x2x2", buildRamp(-0.5f, -0.5f, -0.5f, 1.5f, 1.5f, 1.5f));

		shapes.put("ramp-2x1x2", buildRamp(-0.5f, -0.5f, -0.5f, 1.5f, 0.5f, 1.5f));
		shapes.put("ramp-1x0.5x1", buildRamp(-0.5f, -0.25f, -0.5f, 0.5f, 0.25f, 0.5f));
	}

	public static Model get(String shapeName) {
		return shapes.get(shapeName);
	}

	private static Model buildBox(float ax, float ay, float az, float bx, float by, float bz) {
		modelBuilder.begin();
		MeshPartBuilder partBuilder = modelBuilder.part("collisionShape", GL10.GL_TRIANGLES, VertexAttributes.Usage.Position, null);
		addBoxShape(partBuilder, ax, ay, az, bx, by, bz);
		return modelBuilder.end();
	}

	private static Model buildRamp(float ax, float ay, float az, float bx, float by, float bz) {
		modelBuilder.begin();
		MeshPartBuilder partBuilder = modelBuilder.part("collisionShape", GL10.GL_TRIANGLES, VertexAttributes.Usage.Position, null);
		addRampShape(partBuilder, ax, ay, az, bx, by, bz);
		return modelBuilder.end();
	}

	private static void addBoxShape(MeshPartBuilder partBuilder, float ax, float ay, float az, float bx, float by, float bz) {
		// TODO: for some reason, ModelBuilder's createBox() creates a box model in a way that BaseModelTileMesh
		// collects the vertices from it incorrectly. for now, we just manually create a box's vertices/indices ourself

		partBuilder.vertex(ax, by, bz);
		partBuilder.vertex(bx, by, bz);
		partBuilder.vertex(ax, by, az);
		partBuilder.vertex(bx, by, az);
		partBuilder.vertex(bx, ay, bz);
		partBuilder.vertex(ax, ay, bz);
		partBuilder.vertex(bx, ay, az);
		partBuilder.vertex(ax, ay, az);

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

	private static void addRampShape(MeshPartBuilder partBuilder, float ax, float ay, float az, float bx, float by, float bz) {
		partBuilder.vertex(ax, by, az);
		partBuilder.vertex(bx, by, az);
		partBuilder.vertex(bx, ay, bz);
		partBuilder.vertex(ax, ay, bz);
		partBuilder.vertex(bx, ay, az);
		partBuilder.vertex(ax, ay, az);

		// top (ramp)
		partBuilder.index((short)3, (short)2, (short)0);
		partBuilder.index((short)2, (short)1, (short)0);

		// bottom
		partBuilder.index((short)2, (short)3, (short)4);
		partBuilder.index((short)3, (short)5, (short)4);

		// front
		partBuilder.index((short)4, (short)5, (short)1);
		partBuilder.index((short)5, (short)0, (short)1);

		// left
		partBuilder.index((short)5, (short)3, (short)0);

		// right
		partBuilder.index((short)4, (short)1, (short)2);

	}
}
