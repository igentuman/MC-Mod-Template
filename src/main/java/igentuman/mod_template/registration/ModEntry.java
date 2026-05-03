package igentuman.mod_template.registration;

import igentuman.mod_template.util.caps.EnergyCapDefinition;
import igentuman.mod_template.util.caps.FluidCapDefinition;
import igentuman.mod_template.util.caps.ItemCapDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public record ModEntry (
        String name,
        DeferredBlock<Block> block,
        DeferredItem<Item> item,
        DeferredHolder<MenuType<?>, MenuType<?>> menu,
        DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> blockEntity,
        boolean hasRecipes,
        DeferredHolder<RecipeType<?>, RecipeType<?>> recipeType,
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> recipeSerializer,
        MaterialEntry materialEntry,
        ItemCapDefinition itemCap,
        FluidCapDefinition fluidCap,
        EnergyCapDefinition energyCap
) {

    public boolean hasBlockEntity() {
        return blockEntity != null;
    }

    public boolean hasMenu() {
        return menu != null;
    }

    public boolean hasBlock() {
        return block != null;
    }

    public boolean hasRecipes() {
        return hasRecipes;
    }

    public boolean hasItem() {
        return item != null;
    }

    public DeferredBlock<Block> block() {
        return block;
    }

    public DeferredItem<Item> item() {
        return item;
    }
}
