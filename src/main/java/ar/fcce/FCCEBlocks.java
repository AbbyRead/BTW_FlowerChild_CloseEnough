package ar.fcce;

import ar.fcce.block.HibachiBlock;
import net.minecraft.block.Block;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

@SuppressWarnings("unused")
public class FCCEBlocks {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    // Block instances
    public static Block HIBACHI;

    // Texture indices for per-side rendering
    public static int HIBACHI_TOP_TEXTURE;
    public static int HIBACHI_SIDE_TEXTURE;

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        HIBACHI = new HibachiBlock(NAMESPACE.id("hibachi"))
                .setTranslationKey(NAMESPACE, "hibachi");
    }
}