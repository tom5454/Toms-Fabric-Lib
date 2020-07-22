package net.fabricmc.fabric.impl.client.indigo.renderer.render;

public class FabricLibsAccess {

	public static BlockRenderInfo blockInfo(Object in) {
		return ((AbstractQuadRenderer)in).blockInfo;
	}

	public static boolean defaultAo(BlockRenderInfo blockInfo) {
		return blockInfo.defaultAo;
	}

}
