package igentuman.mod_template.handler.item;

import net.neoforged.neoforge.items.ItemStackHandler;

public class CustomInventoryHandler extends ItemStackHandler {

    public CustomInventoryHandler(int size) {
        super(size);
    }

    public static CustomInventoryHandler init(int size, Runnable onChanged) {
        return new CustomInventoryHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                onChanged.run();
            }
        };
    }
}
