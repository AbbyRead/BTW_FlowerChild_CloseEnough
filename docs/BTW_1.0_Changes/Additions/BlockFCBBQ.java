package net.minecraft.src;

public class BlockFCBBQ extends Block {
	public BlockFCBBQ(int iBlockID) {
		super(iBlockID, 22, Material.iron);
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		boolean bPowered = world.isBlockGettingPowered(i, j, k);
		if(bPowered) {
			this.BBQIgnite(world, i, j, k);
		} else {
			this.ClearBBQLitFlag(world, i, j, k);
		}

	}

	public int getBlockTextureFromSide(int side) {
		return side == 1 ? 103 : this.blockIndexInTexture;
	}

	public boolean IsBBQLit(World world, int i, int j, int k) {
		int iMetaData = world.getBlockMetadata(i, j, k);
		return (iMetaData & 4) > 0;
	}

	private void SetBBQLitFlag(World world, int i, int j, int k) {
		int iMetaData = world.getBlockMetadata(i, j, k);
		world.setBlockMetadataWithNotify(i, j, k, iMetaData | 4);
	}

	private void ClearBBQLitFlag(World world, int i, int j, int k) {
		int iMetaData = world.getBlockMetadata(i, j, k);
		world.setBlockMetadataWithNotify(i, j, k, iMetaData & -5);
	}

	private void ToggleBBQLitFlag(World world, int i, int j, int k) {
		int iMetaData = world.getBlockMetadata(i, j, k);
		world.setBlockMetadataWithNotify(i, j, k, iMetaData ^ 4);
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		boolean bPowered = world.isBlockGettingPowered(i, j, k);
		if(bPowered) {
			if(!this.IsBBQLit(world, i, j, k)) {
				this.BBQIgnite(world, i, j, k);
				return;
			}
		} else if(this.IsBBQLit(world, i, j, k)) {
			this.BBQExtinguish(world, i, j, k);
			return;
		}

		if(this.IsBBQLit(world, i, j, k)) {
			if(!world.isBlockOpaqueCube(i, j + 1, k)) {
				boolean isFireAbove = world.getBlockId(i, j + 1, k) == Block.fire.blockID;
				if(!isFireAbove) {
					this.BBQIgnite(world, i, j, k);
				}
			} else if(Block.fire.canBlockCatchFire(world, i, j + 1, k)) {
				world.setBlockWithNotify(i, j + 1, k, Block.fire.blockID);
			}
		}

	}

	private void BBQIgnite(World world, int i, int j, int k) {
		world.playSoundEffect((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
		if(!world.isBlockOpaqueCube(i, j + 1, k)) {
			world.setBlockWithNotify(i, j + 1, k, Block.fire.blockID);
		} else if(Block.fire.canBlockCatchFire(world, i, j + 1, k)) {
			world.setBlockWithNotify(i, j + 1, k, Block.fire.blockID);
		}

		this.SetBBQLitFlag(world, i, j, k);
	}

	private void BBQExtinguish(World world, int i, int j, int k) {
		world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		boolean isFireAbove = world.getBlockId(i, j + 1, k) == Block.fire.blockID;
		if(isFireAbove) {
			world.setBlockWithNotify(i, j + 1, k, 0);
		}

		this.ClearBBQLitFlag(world, i, j, k);
	}
}
