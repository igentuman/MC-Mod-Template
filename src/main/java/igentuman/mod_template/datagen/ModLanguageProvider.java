package igentuman.mod_template.datagen;

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
        }
    }
}
