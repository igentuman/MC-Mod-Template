package igentuman.mod_template.container;

import igentuman.mod_template.block_entity.UniversalProcessorBE;
import igentuman.mod_template.setup.ModEntries;
import igentuman.mod_template.setup.Registers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class UniversalProcessorContainer extends AbstractContainerMenu {

    private final UniversalProcessorBE blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public UniversalProcessorContainer(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                (UniversalProcessorBE) playerInventory.player.level().getBlockEntity(extraData.readBlockPos()),
                null);
    }


    // Server-side constructor
    public UniversalProcessorContainer(int containerId, Inventory playerInventory,
                                       UniversalProcessorBE blockEntity, ContainerData data) {
        super(ModEntries.get(blockEntity.name).menu().get(), containerId);
        this.blockEntity = blockEntity;
        // On the client side (data == null), create a SimpleContainerData matching the BE's sync field count.
        // On the server side, use the provided ContainerData (typically blockEntity.containerData).
        this.data = data != null ? data : new SimpleContainerData(blockEntity.getSyncFieldCount());
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addDataSlots(this.data);

        if (blockEntity.hasInventory()) {
            ItemStackHandler inv = blockEntity.inventory;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int index = row * 3 + col;
                    if (index < blockEntity.slotCount) {
                        addSlot(new SlotItemHandler(inv, index, 62 + col * 18, 17 + row * 18));
                    }
                }
            }
        }

        // Add player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 10 + col * 18, 96 + row * 18));
            }
        }

        // Add player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 10 + col * 18, 154));
        }
    }

    /**
     * Gets a synced field value by its index.
     * Indices match the order fields are discovered via reflection
     * (subclass fields first, then parent class fields, in declaration order).
     */
    public int getSyncedValue(int index) {
        return data.get(index);
    }

    public int getProgress() {
        return blockEntity.getSyncFieldIndex("progress") != -1
                ? data.get(blockEntity.getSyncFieldIndex("progress")) : 0;
    }

    public int getMaxProgress() {
        return blockEntity.getSyncFieldIndex("maxProgress") != -1
                ? data.get(blockEntity.getSyncFieldIndex("maxProgress")) : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int beSlots = blockEntity.slotCount;

        if (slotIndex < beSlots) {
            // Move from block entity to player inventory
            if (!moveItemStackTo(stack, beSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Move from player inventory to block entity
            if (!moveItemStackTo(stack, 0, beSlots, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModEntries.get(blockEntity.name).block().get());
    }
}
