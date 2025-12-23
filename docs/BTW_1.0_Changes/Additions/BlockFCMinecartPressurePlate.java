package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockFCMinecartPressurePlate extends Block {
	public BlockFCMinecartPressurePlate(int iBlockID, int iTextureIndex) {
		super(iBlockID, iTextureIndex, Material.iron);
		this.setTickOnLoad(true);
	}

	public int tickRate() {
		return 20;
	}

	public int getBlockTextureFromSideAndMetadata(int iSide, int iMetaData) {
		return iSide == 1 ? (iMetaData > 0 ? 72 : 6) : this.blockIndexInTexture;
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if(this.IsPlateOn(world, i, j, k)) {
			List list = null;
			list = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBoxFromPool((double)((float)i), (double)(j + 1), (double)k, (double)((float)(i + 1)), (double)(j + 2), (double)((float)(k + 1))));
			if(list.size() == 0) {
				this.PlateDeactivate(world, i, j, k);
			} else {
				world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate());
			}
		}

	}

	public boolean IsPlateOn(World world, int i, int j, int k) {
		return world.getBlockMetadata(i, j, k) > 0;
	}

	public void OnMinecartOver(World world, int i, int j, int k) {
		if(!this.IsPlateOn(world, i, j, k)) {
			this.PlateActivate(world, i, j, k);
		}

		world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate());
	}

	private void PlateActivate(World world, int i, int j, int k) {
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.1D, (double)k + 0.5D, "random.click", 0.3F, 0.6F);
		world.setBlockMetadataWithNotify(i, j, k, 1);
		world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
		world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
		world.markBlocksDirty(i, j, k, i, j, k);
	}

	private void PlateDeactivate(World world, int i, int j, int k) {
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.1D, (double)k + 0.5D, "random.click", 0.3F, 0.5F);
		world.setBlockMetadataWithNotify(i, j, k, 0);
		world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
		world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
		world.markBlocksDirty(i, j, k, i, j, k);
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		world.getBlockMetadata(i, j, k);
		if(this.IsPlateOn(world, i, j, k)) {
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
		}

		super.onBlockRemoval(world, i, j, k);
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getBlockMetadata(i, j, k) > 0;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return this.IsPlateOn(world, i, j, k);
	}

	public boolean canProvidePower() {
		return true;
	}
}
