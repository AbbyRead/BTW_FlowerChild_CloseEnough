package net.minecraft.src;

import java.util.Random;

public class BlockFCMinecartBooster extends Block {
	public BlockFCMinecartBooster(int iBlockID, int iTextureIndex) {
		super(iBlockID, iTextureIndex, Material.iron);
	}

	public int idDropped(int i, Random random) {
		return Block.fcminecartboosterIdle.blockID;
	}

	public int getBlockTextureFromSide(int side) {
		return side == 1 ? (this.blockID == Block.fcminecartboosterActive.blockID ? 104 : 104) : this.blockIndexInTexture;
	}

	public boolean IsBoosterOn(World world, int i, int j, int k) {
		return world.getBlockId(i, j, k) == Block.fcminecartboosterActive.blockID;
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		boolean bPowered = world.isBlockGettingPowered(i, j, k);
		if(bPowered) {
			if(!this.IsBoosterOn(world, i, j, k)) {
				this.BoosterTurnOn(world, i, j, k);
				return;
			}
		} else if(this.IsBoosterOn(world, i, j, k)) {
			this.BoosterTurnOff(world, i, j, k);
			return;
		}

	}

	public void onBlockAdded(World world, int i, int j, int k) {
		boolean bPowered = world.isBlockGettingPowered(i, j, k);
		if(bPowered) {
			this.BoosterTurnOn(world, i, j, k);
		}

	}

	public void OnMinecartOver(World world, int i, int j, int k) {
		if(this.IsBoosterOn(world, i, j, k) && world.rand.nextInt(15) == 0) {
			world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), "mob.ghast.moan", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		}

	}

	private void BoosterTurnOn(World world, int i, int j, int k) {
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "mob.ghast.scream", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
		world.setBlockWithNotify(i, j, k, Block.fcminecartboosterActive.blockID);
	}

	private void BoosterTurnOff(World world, int i, int j, int k) {
		world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), "mob.ghast.moan", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		world.setBlockWithNotify(i, j, k, Block.fcminecartboosterIdle.blockID);
	}

	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if(this.IsBoosterOn(world, i, j, k)) {
			double dParticleX = (double)i + (double)random.nextFloat();
			double dParticleY = (double)j + 1.0D + (double)random.nextFloat() * 0.2D;
			double dParticleZ = (double)k + (double)random.nextFloat();
			world.spawnParticle("reddust", dParticleX, dParticleY, dParticleZ, 0.0D, 0.0D, 0.0D);
		}

	}
}
