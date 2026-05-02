package igentuman.mod_template.registration;

import igentuman.mod_template.util.caps.EnergyCapDefinition;
import igentuman.mod_template.block.UniversalProcessorBlock;
import igentuman.mod_template.block_entity.UniversalProcessorBE;
import igentuman.mod_template.container.UniversalProcessorContainer;
import igentuman.mod_template.recipe.UniversalProcessorRecipe;
import igentuman.mod_template.recipe.UniversalProcessorRecipeSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Supplier;
import java.util.function.Function;

import static igentuman.mod_template.Main.rl;
import static igentuman.mod_template.setup.ModEntries.ENTRIES;
import static igentuman.mod_template.setup.Registers.*;

public class ModEntryBuilder {

    private final String name;
    private Supplier<? extends Block> blockSupplier;
    private Supplier<? extends Item> itemSupplier;
    private Function<Block, Supplier<? extends BlockEntityType<?>>> entitySupplierFactory;
    private Supplier<MenuType<?>> menuType;
    private EnergyCapDefinition energy;
    private Supplier<RecipeType<?>> recipeTypeSupplier;
    private Supplier<RecipeSerializer<?>> recipeSerializerSupplier;

    private ModEntryBuilder(String name) {
        this.name = name;
    }

    public static ModEntryBuilder add(String name) {
        return new ModEntryBuilder(name);
    }

    public static ModEntryBuilder addItem(String name) {
        return add(name).item(() -> new Item(new Item.Properties()));
    }

    public static ModEntryBuilder addItem(String name, Supplier<? extends Item> itemSupplier) {
        return add(name).item(itemSupplier);
    }

    public static ModEntryBuilder addProcessor(String name) {
        return add(name)
                .block(UniversalProcessorBlock::new)
                .blockEntity(UniversalProcessorBE::new)
                .menu(UniversalProcessorContainer::new)
                .withEnergyInput(100000)
                .withRecipes();
    }

    public <B extends Block> ModEntryBuilder block(Supplier<B> blockSupplier) {
        this.blockSupplier = blockSupplier;
        return this;
    }

    public <B extends Block> ModEntryBuilder block(Function<String, B> blockFactory) {
        this.blockSupplier = () -> blockFactory.apply(name);
        return this;
    }

    public ModEntryBuilder item(Supplier<? extends Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    public <E extends BlockEntity> ModEntryBuilder blockEntity(TriFunction<BlockPos, BlockState, String, E> entityConstructor) {
        this.entitySupplierFactory = block -> () -> BlockEntityType.Builder.of(
                (pos, state) -> entityConstructor.apply(pos, state, name),
                block
        ).build(null);
        return this;
    }

    public <T extends AbstractContainerMenu> ModEntryBuilder menu(IContainerFactory<T> factory) {
        this.menuType = () -> IMenuTypeExtension.create(factory);
        return this;
    }


    public ModEntryBuilder withEnergyInput(int capacity) {
        this.energy = EnergyCapDefinition.processor(capacity);
        return this;
    }

    public ModEntryBuilder fluidCap(int inputTanks, int outputTanks, int defaultTanks) {
        return this;
    }

    public ModEntryBuilder itemCap(int inputSlots, int outputSlots) {
        return this;
    }

    public ModEntryBuilder withRecipes() {
        this.recipeTypeSupplier = () -> RecipeType.<UniversalProcessorRecipe>simple(rl(name));
        this.recipeSerializerSupplier = () -> new UniversalProcessorRecipeSerializer(name);
        return this;
    }

    public ModEntryBuilder withRecipes(
            Supplier<RecipeType<?>> recipeTypeSupplier,
            Supplier<RecipeSerializer<?>> recipeSerializerSupplier
    ) {
        this.recipeTypeSupplier = recipeTypeSupplier;
        this.recipeSerializerSupplier = recipeSerializerSupplier;
        return this;
    }

    public ModEntry build() {
        DeferredBlock<Block> block = null;
        DeferredItem<Item> item = null;
        DeferredHolder<MenuType<?>, MenuType<?>> menu = null;
        DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> blockEntity = null;
        DeferredHolder<RecipeType<?>, RecipeType<?>> recipeType = null;
        if (blockSupplier != null) {
            block = BLOCKS.register(name, blockSupplier);
        }

        final DeferredBlock<Block> finalBlock = block;

        // Register item - use provided supplier or create BlockItem
        if (itemSupplier != null) {
            item = ITEMS.register(name, itemSupplier);
        } else {
            item = ITEMS.register(name, () -> new BlockItem(finalBlock.get(), new Item.Properties()));
        }

        if (entitySupplierFactory != null && block != null) {
            @SuppressWarnings("unchecked")
            DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> entityCast =
                    (DeferredHolder<BlockEntityType<?>, BlockEntityType<?>>)
                            BLOCK_ENTITIES.register(name, () -> entitySupplierFactory.apply(finalBlock.get()).get());
            blockEntity = entityCast;
        }
        if (menuType != null) {
            @SuppressWarnings("unchecked")
            DeferredHolder<MenuType<?>, MenuType<?>> menuCast =
                    (DeferredHolder<MenuType<?>, MenuType<?>>)
                            (DeferredHolder<?, ?>) CONTAINERS.register(name, menuType);
            menu = menuCast;
        }
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> recipeSerializer = null;
        if (recipeTypeSupplier != null) {
            @SuppressWarnings("unchecked")
            DeferredHolder<RecipeType<?>, RecipeType<?>> recipeCast =
                    (DeferredHolder<RecipeType<?>, RecipeType<?>>)
                            (DeferredHolder<?, ?>) RECIPE_TYPES.register(name, recipeTypeSupplier);
            recipeType = recipeCast;
        }
        if (recipeSerializerSupplier != null) {
            @SuppressWarnings("unchecked")
            DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerCast =
                    (DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>>)
                            (DeferredHolder<?, ?>) RECIPE_SERIALIZERS.register(name, recipeSerializerSupplier);
            recipeSerializer = serializerCast;
        }

        ModEntry entry = new ModEntry(name, block, item, menu, blockEntity, recipeTypeSupplier != null, recipeType, recipeSerializer);
        ENTRIES.put(name, entry);
        return entry;

    }


}
