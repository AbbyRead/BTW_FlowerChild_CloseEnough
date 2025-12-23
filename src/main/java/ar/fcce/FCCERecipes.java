package ar.fcce;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;

@SuppressWarnings("unused")
public class FCCERecipes {

    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        if (event.recipeId == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED.type()) {
            CraftingRegistry.addShapedRecipe(
                    new ItemStack(FCCEBlocks.HIBACHI, 1),
                    "#X#",
                    "#Z#",
                    "#Y#",
                    '#', Item.IRON_INGOT,
                    'X', Block.NETHERRACK,
                    'Y', Item.REDSTONE,
                    'Z', Item.FLINT
            );
        }
    }
}