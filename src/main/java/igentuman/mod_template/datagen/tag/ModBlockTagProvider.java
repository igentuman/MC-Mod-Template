package igentuman.mod_template.datagen.tag;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static igentuman.mod_template.Main.MODID;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (entry.hasBlock()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.block().get());
                tag(BlockTags.NEEDS_IRON_TOOL).add(entry.block().get());
            }
        }
    }
}
