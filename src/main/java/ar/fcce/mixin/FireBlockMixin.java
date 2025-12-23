package ar.fcce.mixin;

import ar.fcce.block.HibachiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Mixin to handle fire behavior above hibachi blocks.
 * When fire ticks and there's a hibachi below:
 * - If hibachi is LIT: fire persists normally
 * - If hibachi is NOT LIT: fire is extinguished
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(
            method = "onTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onFireTickAboveHibachi(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        // Check if there's a hibachi directly below the fire
        Block blockBelow = Block.BLOCKS[world.getBlockId(x, y - 1, z)];

        if (blockBelow instanceof HibachiBlock) {
            HibachiBlock hibachi = (HibachiBlock) blockBelow;

            // If hibachi is not lit, extinguish the fire immediately
            if (!hibachi.isLit(world, x, y - 1, z)) {
                world.setBlock(x, y, z, 0);
                ci.cancel(); // Don't run normal fire tick logic
            }
        }
    }
}
