package ar.fcce.block;

import ar.fcce.FCCEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

public class HibachiBlock extends TemplateBlock {

    private static final int LIT_FLAG = 4; // Bitwise flag for lit state in metadata

    public HibachiBlock(Identifier identifier) {
        super(identifier, 6, Material.METAL); // 6 is iron texture as fallback
        this.setHardness(0.4F);
        this.setSoundGroup(Block.STONE_SOUND_GROUP);
    }

    @Override
    public int getTexture(int side) {
        // Top surface (side 1) uses different texture than sides
        if (side == 1) {
            return FCCEBlocks.HIBACHI_TOP_TEXTURE;
        }
        return FCCEBlocks.HIBACHI_SIDE_TEXTURE;
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        this.updateLitState(world, x, y, z);
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int neighborId) {
        this.updateLitState(world, x, y, z);
    }

    private void updateLitState(World world, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }

        boolean isPowered = world.canTransferPower(x, y, z);
        boolean isCurrentlyLit = this.isLit(world, x, y, z);

        if (isPowered && !isCurrentlyLit) {
            this.ignite(world, x, y, z);
        } else if (!isPowered && isCurrentlyLit) {
            this.extinguish(world, x, y, z);
        }
    }

    public boolean isLit(World world, int x, int y, int z) {
        int metadata = world.getBlockMeta(x, y, z);
        return (metadata & LIT_FLAG) != 0;
    }

    private void ignite(World world, int x, int y, int z) {
        world.playSound(
                (double) x + 0.5D,
                (double) y + 0.5D,
                (double) z + 0.5D,
                "fire.ignite",
                1.0F,
                world.random.nextFloat() * 0.4F + 0.8F
        );

        // Place fire above if space is available
        if (!world.shouldSuffocate(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, Block.FIRE.id);
        }

        // Set lit flag in metadata
        int metadata = world.getBlockMeta(x, y, z);
        world.setBlockMeta(x, y, z, metadata | LIT_FLAG);
    }

    private void extinguish(World world, int x, int y, int z) {
        world.playSound(
                (double) x + 0.5F,
                (double) y + 0.5F,
                (double) z + 0.5F,
                "random.fizz",
                0.5F,
                2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
        );

        // Remove fire above if present
        if (world.getBlockId(x, y + 1, z) == Block.FIRE.id) {
            world.setBlock(x, y + 1, z, 0);
        }

        // Clear lit flag from metadata
        int metadata = world.getBlockMeta(x, y, z);
        world.setBlockMeta(x, y, z, metadata & ~LIT_FLAG);
    }
}
