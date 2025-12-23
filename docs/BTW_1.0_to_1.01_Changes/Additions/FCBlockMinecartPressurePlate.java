package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class FCBlockMinecartPressurePlate extends Block {
	private static final int iMinecartPlateTickRate = 20;

	public FCBlockMinecartPressurePlate(int iBlockID, int iTextureIndex, Material material) {
		super(iBlockID, iTextureIndex, material);
		this.setTickOnLoad(true);
	}

	public int tickRate() {
		return 20;
	}

	public int getBlockTextureFromSideAndMetadata(int iSide, int iMetaData) {
		int iTexture = this.blockIndexInTexture;
		if(iSide == 1) {
			if(this.blockID == mod_FCBetterThanWolves.fcMinecartPressurePlateStone.blockID) {
				iTexture = 6;
			} else if(this.blockID == mod_FCBetterThanWolves.fcMinecartPressurePlateWood.blockID) {
				iTexture = 4;
			} else if(this.blockID == mod_FCBetterThanWolves.fcMinecartPressurePlateIron.blockID) {
				iTexture = 22;
			}
		}

		return iTexture;
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
		if(IsPlateOn(world, i, j, k)) {
			List list = null;
			list = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBoxFromPool((double)((float)i), (double)(j + 1), (double)k, (double)((float)(i + 1)), (double)(j + 2), (double)((float)(k + 1))));
			boolean bTriggeredByCart = false;
			if(list != null && list.size() > 0) {
				for(int listIndex = 0; listIndex < list.size(); ++listIndex) {
					EntityMinecart minecartEntity = (EntityMinecart)list.get(listIndex);
					if(ShouldPlateActivateBasedOnMinecart(world, i, j, k, minecartEntity.minecartType, minecartEntity.riddenByEntity)) {
						bTriggeredByCart = true;
					}
				}
			}

			if(bTriggeredByCart) {
				world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate());
			} else {
				PlateDeactivate(world, i, j, k);
			}
		}

	}

	public static boolean IsPlateOn(World world, int i, int j, int k) {
		return world.getBlockMetadata(i, j, k) > 0;
	}

	public static boolean ShouldPlateActivateBasedOnMinecart(World world, int i, int j, int k, int iMinecartType, Entity aD) {
		int iLocalBlockID = world.getBlockId(i, j, k);
		if(iLocalBlockID == mod_FCBetterThanWolves.fcMinecartPressurePlateWood.blockID) {
			return true;
		} else {
			if(iLocalBlockID == mod_FCBetterThanWolves.fcMinecartPressurePlateStone.blockID) {
				if(iMinecartType > 0 || aD != null) {
					return true;
				}
			} else if(iLocalBlockID == mod_FCBetterThanWolves.fcMinecartPressurePlateIron.blockID && aD != null && aD instanceof EntityPlayer) {
				return true;
			}

			return false;
		}
	}

	public static void OnMinecartOver(World world, int i, int j, int k, int iMinecartType, Entity aD) {
		int iLocalBlockID = world.getBlockId(i, j, k);
		if(ShouldPlateActivateBasedOnMinecart(world, i, j, k, iMinecartType, aD)) {
			if(!IsPlateOn(world, i, j, k)) {
				PlateActivate(world, i, j, k);
			}

			world.scheduleBlockUpdate(i, j, k, iLocalBlockID, 20);
		}

	}

	private static void PlateActivate(World world, int i, int j, int k) {
		int iLocalBlockID = world.getBlockId(i, j, k);
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.1D, (double)k + 0.5D, "random.click", 0.3F, 0.6F);
		world.setBlockMetadataWithNotify(i, j, k, 1);
		world.notifyBlocksOfNeighborChange(i, j, k, iLocalBlockID);
		world.notifyBlocksOfNeighborChange(i, j - 1, k, iLocalBlockID);
		world.markBlocksDirty(i, j, k, i, j, k);
	}

	private static void PlateDeactivate(World world, int i, int j, int k) {
		int iLocalBlockID = world.getBlockId(i, j, k);
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.1D, (double)k + 0.5D, "random.click", 0.3F, 0.5F);
		world.setBlockMetadataWithNotify(i, j, k, 0);
		world.notifyBlocksOfNeighborChange(i, j, k, iLocalBlockID);
		world.notifyBlocksOfNeighborChange(i, j - 1, k, iLocalBlockID);
		world.markBlocksDirty(i, j, k, i, j, k);
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
		world.getBlockMetadata(i, j, k);
		if(IsPlateOn(world, i, j, k)) {
			world.notifyBlocksOfNeighborChange(i, j, k, this.blockID);
			world.notifyBlocksOfNeighborChange(i, j - 1, k, this.blockID);
		}

		super.onBlockRemoval(world, i, j, k);
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getBlockMetadata(i, j, k) > 0;
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return IsPlateOn(world, i, j, k);
	}

	public boolean canProvidePower() {
		return true;
	}
}
