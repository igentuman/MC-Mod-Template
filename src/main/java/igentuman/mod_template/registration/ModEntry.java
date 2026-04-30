package igentuman.mod_template.registration;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
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
        DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> blockEntity
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
