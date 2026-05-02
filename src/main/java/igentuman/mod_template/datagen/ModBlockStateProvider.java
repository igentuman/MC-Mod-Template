package igentuman.mod_template.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;

import java.util.function.Function;

import static igentuman.mod_template.Main.MODID;

public class ModBlockStateProvider extends BlockStateProvider {
    private final ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public void registerStatesAndModels() {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (entry.hasBlock()) {
                blockWithItem(entry.block());
            }
        }
    }

    private void stateBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(modelFunc.apply(state)).build());
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        Block block = deferredBlock.get();
        ModelFile model = cubeAll(deferredBlock.get());
        simpleBlock(block, model);
        itemModels().getBuilder("item/" + BuiltInRegistries.BLOCK.getKey(block).getPath()).parent(model);
    }
}
