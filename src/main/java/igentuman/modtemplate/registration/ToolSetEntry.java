package igentuman.modtemplate.registration;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;

import static igentuman.modtemplate.setup.Registers.ITEMS;

public class ToolSetEntry {

    public final String name;
    private DeferredItem<Item> sword;
    private DeferredItem<Item> pickaxe;
    private DeferredItem<Item> axe;
    private DeferredItem<Item> shovel;
    private DeferredItem<Item> hoe;

    private ToolSetEntry(String name) {
        this.name = name;
    }

    public static ToolSetEntry build(String name, Tier tier) {
        ToolSetEntry e = new ToolSetEntry(name);
        e.sword   = ITEMS.register(name + "_sword",   () -> new SwordItem(tier,   new Item.Properties()));
        e.pickaxe = ITEMS.register(name + "_pickaxe", () -> new PickaxeItem(tier, new Item.Properties()));
        e.axe     = ITEMS.register(name + "_axe",     () -> new AxeItem(tier,     new Item.Properties()));
        e.shovel  = ITEMS.register(name + "_shovel",  () -> new ShovelItem(tier,  new Item.Properties()));
        e.hoe     = ITEMS.register(name + "_hoe",     () -> new HoeItem(tier,     new Item.Properties()));
        return e;
    }

    public DeferredItem<Item> sword()   { return sword; }
    public DeferredItem<Item> pickaxe() { return pickaxe; }
    public DeferredItem<Item> axe()     { return axe; }
    public DeferredItem<Item> shovel()  { return shovel; }
    public DeferredItem<Item> hoe()     { return hoe; }
}
