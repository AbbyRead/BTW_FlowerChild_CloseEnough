package net.minecraft.src;

import java.util.Random;

public class BlockFCLightBulb extends Block {
	public BlockFCLightBulb(int iBlockID, int iTextureIndex) {
		super(iBlockID, iTextureIndex, Material.glass);
	}

	public int idDropped(int i, Random random) {
		return Block.fclightbulbOff.blockID;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
		return this.blockID == Block.fclightbulbOn.blockID ? 100.0F : iblockaccess.getLightBrightness(i, j, k);
	}

	public boolean IsLightOn(World world, int i, int j, int k) {
		return world.getBlockId(i, j, k) == Block.fclightbulbOn.blockID;
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		boolean bPowered = world.isBlockIndirectlyGettingPowered(i, j, k);
		if(bPowered) {
			if(!this.IsLightOn(world, i, j, k)) {
				this.LightBulbTurnOn(world, i, j, k);
				return;
			}
		} else if(this.IsLightOn(world, i, j, k)) {
			this.LightBulbTurnOff(world, i, j, k);
			return;
		}

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		boolean bPowered = world.isBlockIndirectlyGettingPowered(i, j, k);
		if(bPowered) {
			this.LightBulbTurnOn(world, i, j, k);
		}

	}

	private void LightBulbTurnOn(World world, int i, int j, int k) {
		world.setBlockWithNotify(i, j, k, Block.fclightbulbOn.blockID);
	}

	private void LightBulbTurnOff(World world, int i, int j, int k) {
		world.setBlockWithNotify(i, j, k, Block.fclightbulbOff.blockID);
	}
}
