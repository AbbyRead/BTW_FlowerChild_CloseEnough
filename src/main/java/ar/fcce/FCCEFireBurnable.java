package ar.fcce;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.FireBurnableRegisterEvent;

/**
 * Registers which blocks are flammable and how they interact with fire.
 * The hibachi acts as a valid fuel source when lit.
 */
@SuppressWarnings("unused")
public class FCCEFireBurnable {

    @EventListener
    public void registerBurnableBlocks(FireBurnableRegisterEvent event) {
        // Register hibachi as burnable (acts as fuel source)
        // burnChance = 0: fire won't burn the hibachi away
        // spreadChance = 0: fire won't spread from hibachi to adjacent blocks
        event.addBurnable(FCCEBlocks.HIBACHI.id, 0, 0);
    }
}
