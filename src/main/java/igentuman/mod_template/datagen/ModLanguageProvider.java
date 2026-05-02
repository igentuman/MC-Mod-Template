package igentuman.mod_template.datagen;

import igentuman.mod_template.registration.MaterialEntry;
import igentuman.mod_template.setup.ModEntries;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.LanguageProvider;

import static igentuman.mod_template.Main.MODID;
import static igentuman.mod_template.util.TextUtils.convertToName;

public class ModLanguageProvider  extends LanguageProvider {
    public ModLanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), MODID, locale);
    }

    @Override
    protected void addTranslations() {
        for (String name : ModEntries.ENTRIES.keySet()) {
            if(ModEntries.get(name).hasBlock()) {
                add(ModEntries.get(name).block().get(), convertToName(name));
                continue;
            }
            if(ModEntries.get(name).hasItem()) {
                add(ModEntries.get(name).item().get(), convertToName(name));
                continue;
            }
            if (ModEntries.get(name).materialEntry() instanceof MaterialEntry materialEntry) {
                if (materialEntry.hasOre()) {
                    add(materialEntry.oreBlock().get(), convertToName(materialEntry.name + "_ore"));
                }
                if (materialEntry.hasBlock()) {
                    add(materialEntry.storageBlock().get(), convertToName(materialEntry.name + "_block"));
                }
                if (materialEntry.hasIngot()) {
                    add(materialEntry.ingot().get(), convertToName(materialEntry.name + "_ingot"));
                }
                if (materialEntry.hasGem()) {
                    add(materialEntry.gem().get(), convertToName(materialEntry.name + "_gem"));
                }
                if (materialEntry.hasRawOre()) {
                    add(materialEntry.rawOre().get(), convertToName("raw_" + materialEntry.name));
                }
                if (materialEntry.hasDust()) {
                    add(materialEntry.dust().get(), convertToName(materialEntry.name + "_dust"));
                }
                if (materialEntry.hasPlate()) {
                    add(materialEntry.plate().get(), convertToName(materialEntry.name + "_plate"));
                }
                if (materialEntry.hasNugget()) {
                    add(materialEntry.nugget().get(), convertToName(materialEntry.name + "_nugget"));
                }
                if (materialEntry.hasFluid()) {
                    var fluid = materialEntry.materialFluid();
                    String fluidName = materialEntry.fluidDefinition.isMolten
                            ? "molten_" + materialEntry.name
                            : materialEntry.name + "_fluid";
                    add(fluid.bucket().get(), convertToName(fluidName + "_bucket"));
                    add("fluid_type.modtemplate." + fluidName, convertToName(fluidName));
                }
            }
        }
    }
}
