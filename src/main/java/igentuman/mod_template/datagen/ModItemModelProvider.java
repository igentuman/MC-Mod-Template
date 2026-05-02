package igentuman.mod_template.datagen;

import igentuman.mod_template.registration.MaterialEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;

import static igentuman.mod_template.Main.MODID;

public class ModItemModelProvider  extends ItemModelProvider {
    ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    public void registerModels() {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (entry.hasBlock()) {
                withExistingParent(entry.name(), modLoc("block/" + entry.name()));
            } else if (entry.hasItem()) {
                simpleItem(entry.item(), entry.name());
            }
            if (entry.materialEntry() instanceof MaterialEntry materialEntry) {
                if (materialEntry.hasBlock()) {
                    withExistingParent(materialEntry.name + "_block", modLoc("block/" + materialEntry.name + "_block"));
                }
                if (materialEntry.hasOre()) {
                    withExistingParent(materialEntry.name + "_ore", modLoc("block/" + materialEntry.name + "_ore"));
                }
                if (materialEntry.hasIngot()) {
                    simpleItem(materialEntry.ingot(), "material/ingot/" + materialEntry.name);
                }
                if (materialEntry.hasGem()) {
                    simpleItem(materialEntry.gem(), "material/gem/" + materialEntry.name);
                }
                if (materialEntry.hasRawOre()) {
                    simpleItem(materialEntry.rawOre(), "material/raw/" + materialEntry.name);
                }
                if (materialEntry.hasDust()) {
                    simpleItem(materialEntry.dust(), "material/dust/" + materialEntry.name);
                }
                if (materialEntry.hasPlate()) {
                    simpleItem(materialEntry.plate(), "material/plate/" + materialEntry.name);
                }
                if (materialEntry.hasNugget()) {
                    simpleItem(materialEntry.nugget(), "material/nugget/" + materialEntry.name);
                }
            }
        }
    }

    private void simpleItem(DeferredItem<Item> deferredItem, String name) {
        buildItem(deferredItem, name, "item/generated");
    }

    private void buildItem(DeferredItem<Item> deferredItem, String name, String parent) {
        ResourceLocation item = BuiltInRegistries.ITEM.getKey(deferredItem.asItem());
        getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile(parent))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + name));
    }
}
