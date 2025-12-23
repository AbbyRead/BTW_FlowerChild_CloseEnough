package ar.fcce;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;

@SuppressWarnings("unused")
public class FCCETextures {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @EventListener
    public void registerTextures(TextureRegisterEvent event) {
        // Register per-side textures and store their indices
        FCCEBlocks.HIBACHI_TOP_TEXTURE = getTextureIndex("block/hibachi_top");
        FCCEBlocks.HIBACHI_SIDE_TEXTURE = getTextureIndex("block/hibachi_side");

        // Set the block's primary texture to the side texture
        FCCEBlocks.HIBACHI.textureId = FCCEBlocks.HIBACHI_SIDE_TEXTURE;
    }

    private int getTextureIndex(String path) {
        return Atlases.getTerrain().addTexture(NAMESPACE.id(path)).index;
    }
}
