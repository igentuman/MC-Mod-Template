package igentuman.mod_template.datagen.loot;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Collections;

public class ModBlockLootTableProvider  extends BlockLootSubProvider {

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (entry.hasBlock()) {
                dropSelf(entry.block().get());
            }
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModEntries.ENTRIES.values().stream().filter(entry -> entry.hasBlock()).map(entry -> entry.block().get()).toList();
    }
}
