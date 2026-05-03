package igentuman.mod_template.block_entity;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import igentuman.mod_template.util.NBTField;
import igentuman.mod_template.util.caps.FluidCapDefinition;
import igentuman.mod_template.util.caps.ItemCapDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GlobalBlockEntity extends BlockEntity {

    public String name;

    /** Item inventory capability - null if no item slots are defined in the ModEntry. */
    @Nullable
    public final ItemStackHandler inventory;

    /** Total number of item slots (0 if no item cap). */
    public final int slotCount;

    /** Fluid tanks - null if no fluid tanks are defined in the ModEntry. */
    @Nullable
    public final FluidTank[] fluidTanks;

    /** Total number of fluid tanks (0 if no fluid cap). */
    public final int tankCount;

    private final List<Field> booleanFields;
    private final List<Field> intFields;
    private final List<Field> intArrayFields;
    private final List<Field> doubleFields;
    private final List<Field> stringFields;
    private final List<Field> stringArrayFields;
    private final List<Field> floatFields;
    private final List<Field> byteFields;
    private final List<Field> longFields;
    private final List<Field> blockPosFields;
    private final List<Field> directionFields;

    /** Fields annotated with @NBTField(syncToClient = true), used for ContainerData sync. */
    private final List<Field> syncFields;

    /**
     * Dynamically built ContainerData that syncs all @NBTField(syncToClient = true) fields.
     * Supports int, boolean, byte, and short field types.
     * Adding a new annotated field to any subclass will automatically include it.
     */
    public final ContainerData containerData;

    @NBTField(syncToClient = true)
    public int progress = 0;
    @NBTField(syncToClient = true)
    public int maxProgress = 100;

    public boolean hasInventory() {
        return inventory != null;
    }

    public boolean hasFluidTanks() {
        return fluidTanks != null && fluidTanks.length > 0;
    }

    /**
     * Returns the IItemHandler for the given side.
     * Currently exposes the full inventory from all sides.
     */
    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return inventory;
    }

    /**
     * Returns the IFluidHandler for the given side.
     * Currently exposes all tanks from all sides via a CombinedFluidHandler.
     */
    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (fluidTanks == null || fluidTanks.length == 0) return null;
        if (fluidTanks.length == 1) return fluidTanks[0];
        return new igentuman.mod_template.util.caps.CombinedFluidHandler(fluidTanks);
    }

    public GlobalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, String name) {
        super(type, pos, blockState);
        this.name = name;

        // Initialize item inventory from ModEntry's itemCap definition
        int slots = 0;
        ItemStackHandler handler = null;
        ItemCapDefinition capDef = null;
        if (name != null) {
            ModEntry entry = ModEntries.get(name);
            if (entry != null && entry.itemCap() != null) {
                capDef = entry.itemCap();
                slots = capDef.inputSlots + capDef.outputSlots + capDef.globalSlots + capDef.catalystSlots + capDef.hiddenSlots;
            }
        }
        if (slots > 0) {
            handler = new ItemStackHandler(slots) {
                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();
                }
            };
        }
        this.slotCount = slots;
        this.inventory = handler;

        // Initialize fluid tanks from ModEntry's fluidCap definition
        FluidCapDefinition fluidCapDef = null;
        if (name != null) {
            ModEntry entry = ModEntries.get(name);
            if (entry != null && entry.fluidCap() != null) {
                fluidCapDef = entry.fluidCap();
            }
        }
        if (fluidCapDef != null) {
            int totalTanks = fluidCapDef.inputTanks.size() + fluidCapDef.outputTanks.size() + fluidCapDef.globalTanks.size();
            FluidTank[] tanks = new FluidTank[totalTanks];
            int idx = 0;
            for (FluidCapDefinition.Tank t : fluidCapDef.inputTanks) {
                tanks[idx++] = new FluidTank(t.capacity) {
                    @Override
                    protected void onContentsChanged() {
                        setChanged();
                    }
                };
            }
            for (FluidCapDefinition.Tank t : fluidCapDef.outputTanks) {
                tanks[idx++] = new FluidTank(t.capacity) {
                    @Override
                    protected void onContentsChanged() {
                        setChanged();
                    }
                };
            }
            for (FluidCapDefinition.Tank t : fluidCapDef.globalTanks) {
                tanks[idx++] = new FluidTank(t.capacity) {
                    @Override
                    protected void onContentsChanged() {
                        setChanged();
                    }
                };
            }
            this.fluidTanks = tanks;
            this.tankCount = totalTanks;
        } else {
            this.fluidTanks = null;
            this.tankCount = 0;
        }

        directionFields = initFields(Direction.class);
        booleanFields = initFields(boolean.class);
        intFields = initFields(int.class);
        intArrayFields = initFields(int[].class);
        doubleFields = initFields(double.class);
        stringFields = initFields(String.class);
        stringArrayFields = initFields(String[].class);
        blockPosFields = initFields(BlockPos.class);
        floatFields = initFields(float.class);
        byteFields = initFields(byte.class);
        longFields = initFields(long.class);

        syncFields = initSyncFields();

        containerData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index < 0 || index >= syncFields.size()) return 0;
                Field field = syncFields.get(index);
                try {
                    Class<?> type = field.getType();
                    if (type == int.class) return field.getInt(GlobalBlockEntity.this);
                    if (type == boolean.class) return field.getBoolean(GlobalBlockEntity.this) ? 1 : 0;
                    if (type == byte.class) return field.getByte(GlobalBlockEntity.this);
                    if (type == short.class) return field.getShort(GlobalBlockEntity.this);
                } catch (IllegalAccessException e) {
                    return 0;
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index < 0 || index >= syncFields.size()) return;
                Field field = syncFields.get(index);
                try {
                    Class<?> type = field.getType();
                    if (type == int.class) field.setInt(GlobalBlockEntity.this, value);
                    else if (type == boolean.class) field.setBoolean(GlobalBlockEntity.this, value != 0);
                    else if (type == byte.class) field.setByte(GlobalBlockEntity.this, (byte) value);
                    else if (type == short.class) field.setShort(GlobalBlockEntity.this, (short) value);
                } catch (IllegalAccessException ignored) { }
            }

            @Override
            public int getCount() {
                return syncFields.size();
            }
        };
    }

    /**
     * Collects all @NBTField-annotated fields from the entire class hierarchy.
     * Walks from the concrete subclass up to (but not including) GlobalBlockEntity's parent.
     */
    private List<Field> collectAllNBTFields() {
        List<Field> all = new ArrayList<>();
        Class<?> clazz = getClass();
        while (clazz != null && BlockEntity.class.isAssignableFrom(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(NBTField.class)) {
                    field.setAccessible(true);
                    all.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return all;
    }

    private List<Field> initFields(Class<?> fieldClass) {
        List<Field> fields = new ArrayList<>();
        for (Field field : collectAllNBTFields()) {
            if (field.getType().equals(fieldClass)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Collects fields annotated with @NBTField(syncToClient = true)
     * that can be represented as int for ContainerData sync.
     * Supported types: int, boolean, byte, short.
     */
    private List<Field> initSyncFields() {
        List<Field> fields = new ArrayList<>();
        for (Field field : collectAllNBTFields()) {
            NBTField annotation = field.getAnnotation(NBTField.class);
            if (annotation.syncToClient() && isSyncableType(field.getType())) {
                fields.add(field);
            }
        }
        return fields;
    }

    private static boolean isSyncableType(Class<?> type) {
        return type == int.class || type == boolean.class
                || type == byte.class || type == short.class;
    }

    /** Returns the number of ContainerData slots used for client sync. */
    public int getSyncFieldCount() {
        return syncFields.size();
    }

    /** Returns the ContainerData index for the given field name, or -1 if not found. */
    public int getSyncFieldIndex(String fieldName) {
        for (int i = 0; i < syncFields.size(); i++) {
            if (syncFields.get(i).getName().equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    public void readTagData(CompoundTag tag) {
        try {
            for(Field f: directionFields) {
                if (tag.contains(f.getName())) {
                    f.set(this, Direction.byName(tag.getString(f.getName())));
                }
            }
            for(Field f: blockPosFields) {
                if (tag.contains(f.getName())) {
                    f.set(this, BlockPos.of(tag.getLong(f.getName())));
                }
            }
            for(Field f: booleanFields) {
                if (tag.contains(f.getName())) {
                    f.setBoolean(this, tag.getBoolean(f.getName()));
                }
            }
            for(Field f: intFields) {
                if (tag.contains(f.getName())) {
                    f.setInt(this, tag.getInt(f.getName()));
                }
            }
            for(Field f: stringFields) {
                if (tag.contains(f.getName())) {
                    f.set(this, tag.getString(f.getName()));
                }
            }
            for(Field f: doubleFields) {
                if (tag.contains(f.getName())) {
                    f.setDouble(this, tag.getDouble(f.getName()));
                }
            }
            for(Field f: floatFields) {
                if (tag.contains(f.getName())) {
                    f.setFloat(this, tag.getFloat(f.getName()));
                }
            }
            for(Field f: byteFields) {
                if (tag.contains(f.getName())) {
                    f.setByte(this, tag.getByte(f.getName()));
                }
            }
            for(Field f: longFields) {
                if (tag.contains(f.getName())) {
                    f.setLong(this, tag.getLong(f.getName()));
                }
            }
            for(Field f: intArrayFields) {
                if (tag.contains(f.getName())) {
                    f.set(this, tag.getIntArray(f.getName()));
                }
            }
            for(Field f: stringArrayFields) {
                if (tag.contains(f.getName())) {
                    ListTag tagList = tag.getList(f.getName(), 8);
                    String[] stringArray = new String[tagList.size()];
                    for (int i = 0; i < tagList.size(); i++) {
                        stringArray[i] = tagList.getString(i);
                    }
                    f.set(this, stringArray);
                }
            }
        } catch (IllegalAccessException ignore) { }
    }

    public void saveFullTagData(CompoundTag tag) {
        try {
            for (Field f : blockPosFields) {
                if((f.get(this)) != null) {
                    tag.putLong(f.getName(), ((BlockPos) f.get(this)).asLong());
                }
            }
            for (Field f : directionFields) {
                Direction direction = (Direction) f.get(this);
                if (direction != null) {
                    tag.putString(f.getName(), direction.getName());
                }
            }
            for (Field f : booleanFields) {
                tag.putBoolean(f.getName(), f.getBoolean(this));
            }
            for (Field f : intFields) {
                tag.putInt(f.getName(), f.getInt(this));
            }
            for (Field f : stringFields) {
                String value = (String) f.get(this);
                if (value != null) {
                    tag.putString(f.getName(), value);
                }
            }
            for (Field f : doubleFields) {
                tag.putDouble(f.getName(), f.getDouble(this));
            }
            for (Field f : floatFields) {
                tag.putFloat(f.getName(), f.getFloat(this));
            }
            for (Field f : byteFields) {
                tag.putByte(f.getName(), f.getByte(this));
            }
            for (Field f : longFields) {
                tag.putLong(f.getName(), f.getLong(this));
            }
            for (Field f : intArrayFields) {
                int[] array = (int[]) f.get(this);
                if (array != null) {
                    tag.putIntArray(f.getName(), array);
                }
            }
            for (Field f : stringArrayFields) {
                String[] stringArray = (String[]) f.get(this);
                if (stringArray != null) {
                    ListTag tagList = new ListTag();
                    for (String string : stringArray) {
                        if (string != null) {
                            tagList.add(StringTag.valueOf(string));
                        }
                    }
                    tag.put(f.getName(), tagList);
                }
            }
        } catch (IllegalAccessException ignore) { }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveFullTagData(tag);
        if (inventory != null) {
            tag.put("Inventory", inventory.serializeNBT(registries));
        }
        if (fluidTanks != null) {
            ListTag tankList = new ListTag();
            for (FluidTank tank : fluidTanks) {
                tankList.add(tank.writeToNBT(registries, new CompoundTag()));
            }
            tag.put("FluidTanks", tankList);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readTagData(tag);
        if (inventory != null && tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        if (fluidTanks != null && tag.contains("FluidTanks")) {
            ListTag tankList = tag.getList("FluidTanks", 10);
            for (int i = 0; i < Math.min(tankList.size(), fluidTanks.length); i++) {
                fluidTanks[i].readFromNBT(registries, tankList.getCompound(i));
            }
        }
    }

    /** Drops all items from the inventory into the world. Call on block break. */
    public void drops() {
        if (level == null || inventory == null) return;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                        worldPosition.getY(), worldPosition.getZ(), stack);
            }
        }
    }
}

