package grondag.facility.transport.model;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import grondag.facility.Facility;
import grondag.fermion.orientation.api.OrientationType;
import grondag.xm.api.connect.state.SimpleJoinState;
import grondag.xm.api.mesh.WritableMesh;
import grondag.xm.api.mesh.polygon.MutablePolygon;
import grondag.xm.api.mesh.polygon.PolyTransform;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.paint.SurfaceTopology;
import grondag.xm.api.primitive.SimplePrimitive;
import grondag.xm.api.primitive.surface.XmSurface;
import grondag.xm.api.primitive.surface.XmSurfaceList;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

public class ItemMoverModel extends BasePipeModel {
	private static final ItemMoverModel INSTANCE = new ItemMoverModel();

	protected static final XmSurfaceList ITEM_MOVER_SURFACES = XmSurfaceList.builder(SURFACES)
			.add("mover_face", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("mover_side", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.add("mover_back", SurfaceTopology.CUBIC, XmSurface.FLAG_NONE)
			.build();

	public static final XmSurface SURFACE_MOVER_FACE = ITEM_MOVER_SURFACES.get(4);
	public static final XmSurface SURFACE_MOVER_SIDE = ITEM_MOVER_SURFACES.get(5);
	public static final XmSurface SURFACE_MOVER_BACK = ITEM_MOVER_SURFACES.get(6);

	protected static final float FAT_CONNECTOR_DEPTH = 2f / 16f;
	protected static final float FAT_CONNECTOR_WIDTH = 12f / 16f;
	protected static final float FAT_CONNECTOR_MIN = 0.5f - FAT_CONNECTOR_WIDTH * 0.5f;
	protected static final float FAT_CONNECTOR_MAX = 1f - FAT_CONNECTOR_MIN;

	@Override
	protected boolean hasJoins(PrimitiveState modelState, SimpleJoinState joinState, Axis axis) {
		return FACES[modelState.orientationIndex()].getAxis() == axis || super.hasJoins(modelState, joinState, axis);
	}

	@Override
	protected boolean isJoined(PrimitiveState modelState, SimpleJoinState joinState, Direction face) {
		return modelState.orientationIndex() == face.ordinal() || super.isJoined(modelState, joinState, face);
	}

	@Override
	protected boolean needsConnector(PrimitiveState modelState, int connectorBits, Direction face) {
		return modelState.orientationIndex() == face.ordinal() || super.needsConnector(modelState, connectorBits, face);
	}

	@Override
	protected void emitConnector(PrimitiveState modelState, Direction face, WritableMesh mesh) {
		if(modelState.orientationIndex() == face.ordinal()) {
			emitFatConnector(modelState, face, mesh);
		} else {
			super.emitConnector(modelState, face, mesh);
		}
	}

	protected void emitFatConnector(PrimitiveState modelState, Direction face, WritableMesh mesh) {
		final MutablePolygon writer = mesh.writer();
		final PolyTransform transform = PolyTransform.get(face);
		writer.lockUV(0, true)
		.surface(SURFACE_MOVER_SIDE);
		writer.saveDefaults();

		writer.setupFaceQuad(EAST, FAT_CONNECTOR_MIN, 0, FAT_CONNECTOR_MAX, FAT_CONNECTOR_DEPTH, FAT_CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(WEST, FAT_CONNECTOR_MIN, 0, FAT_CONNECTOR_MAX, FAT_CONNECTOR_DEPTH, FAT_CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(NORTH, FAT_CONNECTOR_MIN, 0, FAT_CONNECTOR_MAX, FAT_CONNECTOR_DEPTH, FAT_CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(SOUTH, FAT_CONNECTOR_MIN, 0, FAT_CONNECTOR_MAX, FAT_CONNECTOR_DEPTH, FAT_CONNECTOR_MIN, UP);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(UP, FAT_CONNECTOR_MIN, FAT_CONNECTOR_MIN, FAT_CONNECTOR_MAX, FAT_CONNECTOR_MAX, 1 - FAT_CONNECTOR_DEPTH, NORTH);
		writer.surface(SURFACE_MOVER_BACK);
		transform.accept(writer);
		writer.append();

		writer.setupFaceQuad(DOWN, FAT_CONNECTOR_MIN, FAT_CONNECTOR_MIN, FAT_CONNECTOR_MAX, FAT_CONNECTOR_MAX, 0, NORTH);
		writer.surface(SURFACE_MOVER_FACE);
		transform.accept(writer);
		writer.append();
	}

	public static final SimplePrimitive PRIMITIVE = SimplePrimitive.builder()
			.surfaceList(ITEM_MOVER_SURFACES)
			.polyFactory(INSTANCE::polyFactory)
			.orientationType(OrientationType.FACE)
			.primitiveBitCount(PRIMITIVE_BIT_COUNT)
			.simpleJoin(true)
			.alternateJoinAffectsGeometry(true)
			.build(Facility.REG.id("export_pipe"));
}
