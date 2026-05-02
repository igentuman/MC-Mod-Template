package igentuman.mod_template.registration;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static igentuman.mod_template.setup.Registers.BLOCKS;
import static igentuman.mod_template.setup.Registers.ITEMS;

public class MaterialEntry {

    public final int color;
    public final String name;

    private Supplier<? extends Block> oreBlockSupplier;
    private Supplier<? extends Block> storageBlockSupplier;
    private Supplier<? extends Item> ingotSupplier;
    private Supplier<? extends Item> gemSupplier;
    private Supplier<? extends Item> rawOreSupplier;
    private Supplier<? extends BlockItem> oreItemSupplier;
    private Supplier<? extends BlockItem> storageItemSupplier;
    private Supplier<? extends Item> dustSupplier;
    private Supplier<? extends Item> plateSupplier;
    private Supplier<? extends Item> nuggetSupplier;
    private Supplier<? extends Item> fluid;

    private RegisteredEntry entry;

    public MaterialEntry(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public FluidDefinition fluidDefinition = null;

    private MaterialEntry(int color, String name) {
        this.color = color;
        this.name = name;
    }

    public static MaterialEntry of(int color, String name) {
        return new MaterialEntry(color, name);
    }

    public MaterialEntry setIngotSupplier(Supplier<? extends Item> ingotSupplier) {
        this.ingotSupplier = ingotSupplier;
        return this;
    }

    public MaterialEntry setGemSupplier(Supplier<? extends Item> gemSupplier) {
        this.gemSupplier = gemSupplier;
        return this;
    }

    public MaterialEntry setBlock(Supplier<? extends Block> storageBlock, Supplier<? extends BlockItem> storageItem) {
        this.storageBlockSupplier = storageBlock;
        this.storageItemSupplier = storageItem;
        return this;
    }

    public MaterialEntry setOre(Supplier<? extends Block> oreBlock, Supplier<? extends BlockItem> oreItem) {
        this.oreBlockSupplier = oreBlock;
        this.oreItemSupplier = oreItem;
        return this;
    }

    public MaterialEntry setDustSupplier(Supplier<? extends Item> dustSupplier) {
        this.dustSupplier = dustSupplier;
        return this;
    }

    public MaterialEntry setNuggetSupplier(Supplier<? extends Item> nuggetSupplier) {
        this.nuggetSupplier = nuggetSupplier;
        return this;
    }

    public MaterialEntry setRawOreSupplier(Supplier<? extends Item> rawOreSupplier) {
        this.rawOreSupplier = rawOreSupplier;
        return this;
    }

    public MaterialEntry setPlateSupplier(Supplier<? extends Item> plateSupplier) {
        this.plateSupplier = plateSupplier;
        return this;
    }

    public MaterialEntry setFluid(Supplier<? extends Item> fluid) {
        this.fluid = fluid;
        return this;
    }

    public MaterialEntry setFluidDefinition(FluidDefinition fluidDefinition) {
        this.fluidDefinition = fluidDefinition;
        return this;
    }

    public boolean hasIngot() { return ingotSupplier != null; }
    public boolean hasGem() { return gemSupplier != null; }
    public boolean hasBlock() { return storageBlockSupplier != null; }
    public boolean hasOre() { return oreBlockSupplier != null; }
    public boolean hasDust() { return dustSupplier != null; }
    public boolean hasNugget() { return nuggetSupplier != null; }
    public boolean hasRawOre() { return rawOreSupplier != null; }
    public boolean hasPlate() { return plateSupplier != null; }
    public boolean hasFluid() { return fluid != null; }

    public DeferredBlock<Block> oreBlock() { return entry.oreBlock(); }
    public DeferredBlock<Block> storageBlock() { return entry.storageBlock(); }
    public DeferredItem<Item> ingot() { return entry.ingot(); }
    public DeferredItem<Item> gem() { return entry.gem(); }
    public DeferredItem<Item> rawOre() { return entry.rawOre(); }
    public DeferredItem<BlockItem> oreItem() { return entry.oreItem(); }
    public DeferredItem<BlockItem> storageItem() { return entry.storageItem(); }
    public DeferredItem<Item> dust() { return entry.dust(); }
    public DeferredItem<Item> plate() { return entry.plate(); }
    public DeferredItem<Item> nugget() { return entry.nugget(); }
    public DeferredItem<Item> fluid() { return entry.fluid(); }

    public MaterialEntry metalOre() {
        BlockBehaviour.Properties oreProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
        BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);

        this.setOre(() -> new Block(oreProps),() -> new BlockItem(new Block(oreProps), new Item.Properties()));

        this.setIngotSupplier(() -> new Item(new Item.Properties()));
        this.setDustSupplier(() -> new Item(new Item.Properties()));
        this.setPlateSupplier(() -> new Item(new Item.Properties()));
        this.setNuggetSupplier(() -> new Item(new Item.Properties()));
        this.setRawOreSupplier(() -> new Item(new Item.Properties()));

        this.setBlock(() -> new Block(blockProps), () -> new BlockItem(new Block(blockProps), new Item.Properties()));

        return this;
    }

    public MaterialEntry crystalOre() {
        BlockBehaviour.Properties oreProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
        BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIAMOND)
                .strength(5.0f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.AMETHYST_CLUSTER);

        this.setOre(() -> new Block(oreProps),() -> new BlockItem(new Block(oreProps), new Item.Properties()));

        this.setGemSupplier(() -> new Item(new Item.Properties()));
        this.setDustSupplier(() -> new Item(new Item.Properties()));
        this.setPlateSupplier(() -> new Item(new Item.Properties()));
        this.setNuggetSupplier(() -> new Item(new Item.Properties()));
        this.setRawOreSupplier(() -> new Item(new Item.Properties()));

        this.setBlock(() -> new Block(blockProps), () -> new BlockItem(new Block(blockProps), new Item.Properties()));

        return this;
    }

    public MaterialEntry build() {
        DeferredBlock<Block> regOreBlock = null;
        DeferredItem<BlockItem> regOreItem = null;
        DeferredBlock<Block> regStorageBlock = null;
        DeferredItem<BlockItem> regStorageItem = null;
        DeferredItem<Item> regIngot = null;
        DeferredItem<Item> regGem = null;
        DeferredItem<Item> regRawOre = null;
        DeferredItem<Item> regDust = null;
        DeferredItem<Item> regPlate = null;
        DeferredItem<Item> regNugget = null;
        DeferredItem<Item> regFluid = null;

        if (oreBlockSupplier != null) {
            regOreBlock = BLOCKS.register(name + "_ore", oreBlockSupplier);
            final DeferredBlock<Block> finalOreBlock = regOreBlock;
            regOreItem = ITEMS.register(name + "_ore", () -> new BlockItem(finalOreBlock.get(), new Item.Properties()));
        }
        if (storageBlockSupplier != null) {
            regStorageBlock = BLOCKS.register(name + "_block", storageBlockSupplier);
            final DeferredBlock<Block> finalStorageBlock = regStorageBlock;
            regStorageItem = ITEMS.register(name + "_block", () -> new BlockItem(finalStorageBlock.get(), new Item.Properties()));
        }
        if (ingotSupplier != null) {
            regIngot = ITEMS.register(name + "_ingot", ingotSupplier);
        }
        if (gemSupplier != null) {
            regGem = ITEMS.register(name + "_gem", gemSupplier);
        }
        if (rawOreSupplier != null) {
            regRawOre = ITEMS.register("raw_" + name, rawOreSupplier);
        }
        if (dustSupplier != null) {
            regDust = ITEMS.register(name + "_dust", dustSupplier);
        }
        if (plateSupplier != null) {
            regPlate = ITEMS.register(name + "_plate", plateSupplier);
        }
        if (nuggetSupplier != null) {
            regNugget = ITEMS.register(name + "_nugget", nuggetSupplier);
        }
        if (fluid != null) {
            regFluid = ITEMS.register(name + "_bucket", fluid);
        }

        entry = new RegisteredEntry(
                name, color, fluidDefinition,
                regOreBlock, regOreItem,
                regStorageBlock, regStorageItem,
                regIngot, regGem, regRawOre,
                regDust, regPlate, regNugget,
                regFluid
        );
        return this;
    }

    public record RegisteredEntry(
            String name,
            int color,
            FluidDefinition fluidDefinition,
            DeferredBlock<Block> oreBlock,
            DeferredItem<BlockItem> oreItem,
            DeferredBlock<Block> storageBlock,
            DeferredItem<BlockItem> storageItem,
            DeferredItem<Item> ingot,
            DeferredItem<Item> gem,
            DeferredItem<Item> rawOre,
            DeferredItem<Item> dust,
            DeferredItem<Item> plate,
            DeferredItem<Item> nugget,
            DeferredItem<Item> fluid
    ) {
        public boolean hasOre() { return oreBlock != null; }
        public boolean hasBlock() { return storageBlock != null; }
        public boolean hasIngot() { return ingot != null; }
        public boolean hasGem() { return gem != null; }
        public boolean hasRawOre() { return rawOre != null; }
        public boolean hasDust() { return dust != null; }
        public boolean hasPlate() { return plate != null; }
        public boolean hasNugget() { return nugget != null; }
        public boolean hasFluid() { return fluid != null; }
    }
}
