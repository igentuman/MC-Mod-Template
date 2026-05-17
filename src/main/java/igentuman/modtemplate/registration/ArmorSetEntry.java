package igentuman.modtemplate.registration;

import net.minecraft.core.Holder;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;

import static igentuman.modtemplate.setup.Registers.ITEMS;

public class ArmorSetEntry {

    public final String name;
    private DeferredItem<Item> helmet;
    private DeferredItem<Item> chestplate;
    private DeferredItem<Item> leggings;
    private DeferredItem<Item> boots;

    private ArmorSetEntry(String name) {
        this.name = name;
    }

    public static ArmorSetEntry build(String name, Holder<ArmorMaterial> material) {
        ArmorSetEntry e = new ArmorSetEntry(name);
        e.helmet     = ITEMS.register(name + "_helmet",     () -> new ArmorItem(material, ArmorItem.Type.HELMET,     new Item.Properties()));
        e.chestplate = ITEMS.register(name + "_chestplate", () -> new ArmorItem(material, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
        e.leggings   = ITEMS.register(name + "_leggings",   () -> new ArmorItem(material, ArmorItem.Type.LEGGINGS,   new Item.Properties()));
        e.boots      = ITEMS.register(name + "_boots",      () -> new ArmorItem(material, ArmorItem.Type.BOOTS,      new Item.Properties()));
        return e;
    }

    public DeferredItem<Item> helmet()     { return helmet; }
    public DeferredItem<Item> chestplate() { return chestplate; }
    public DeferredItem<Item> leggings()   { return leggings; }
    public DeferredItem<Item> boots()      { return boots; }
}
