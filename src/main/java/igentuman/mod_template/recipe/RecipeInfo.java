package igentuman.mod_template.recipe;

import igentuman.mod_template.block_entity.GlobalBlockEntity;
import igentuman.mod_template.util.ClientUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import igentuman.mod_template.util.caps.ItemCapDefinition;
import igentuman.mod_template.util.caps.FluidCapDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import static igentuman.mod_template.Main.rlFromString;

public class RecipeInfo {

    public boolean stuck = false;
    public boolean changed = false;
    public int ticks = 0;
    public int ticksNeeded = 0;
    public int energyPerTick = 0;
    public final GlobalBlockEntity be;
    private String recipeId;
    public int multiplier = 1;
    public Recipe<?> recipe;
    public Recipe<?> lastRecipe;
    private final HashMap<String, Recipe<?>> allRecipes = new HashMap<>();

    public RecipeInfo(GlobalBlockEntity be) {
        this.be = be;
    }

    public boolean isDone() {
        return ticks >= ticksNeeded;
    }

    public void tick() {
        changed = false;
        if (!be.supportRecipes()) {
            return;
        }

        if (stuck) {
            if (!produceOutputs()) {
                return;
            }
            changed = true;
            stuck = false;
        }

        if (!hasRecipe()) {
            findRecipe();
        }

        if (!hasRecipe()) {
            return;
        }

        // If recipe requires energy, check and consume it before progressing
        if (energyPerTick > 0) {
            if (be.energyStorage == null) {
                return; // no energy storage, cannot process
            }
            int required = energyPerTick * multiplier;
            int extracted = be.energyStorage.getEnergyStored() >= required ? required : 0;
            if (extracted < required) {
                return; // not enough energy, stall
            }
            be.energyStorage.drainEnergy(required);
        }

        ticks+=multiplier;
        changed = true;

        if (isDone()) {
            lastRecipe = recipe;
            recipe = null;

            if (!produceOutputs()) {
                stuck = true;
            }
        }
    }

    public boolean produceOutputs() {
        if (lastRecipe == null) return true;
        if (!(lastRecipe instanceof UniversalProcessorRecipe upr)) return true;

        ModEntry entry = ModEntries.get(be.name);
        if (entry == null) return true;

        ItemCapDefinition itemCap = entry.itemCap();
        FluidCapDefinition fluidCap = entry.fluidCap();

        List<ItemStack> itemOutputs = upr.getItemOutputs();
        List<FluidStack> fluidOutputs = upr.getFluidOutputs();

        // Determine output slot range: output slots start right after input slots
        int outputSlotStart = (itemCap != null) ? itemCap.inputSlots : 0;
        int outputSlotCount = (itemCap != null) ? itemCap.outputSlots : 0;

        // Determine output tank range: output tanks start right after input tanks
        int outputTankStart = (fluidCap != null) ? fluidCap.inputTanks.size() : 0;
        int outputTankCount = (fluidCap != null) ? fluidCap.outputTanks.size() : 0;

        // Check if all item outputs can fit
        if (be.inventory != null) {
            for (int i = 0; i < itemOutputs.size(); i++) {
                if (i >= outputSlotCount) return false;
                ItemStack output = itemOutputs.get(i);
                int slot = outputSlotStart + i;
                ItemStack existing = be.inventory.getStackInSlot(slot);
                if (!existing.isEmpty()) {
                    if (!ItemStack.isSameItemSameComponents(existing, output)) return false;
                    if (existing.getCount() + output.getCount() > existing.getMaxStackSize()) return false;
                }
            }
        } else if (!itemOutputs.isEmpty()) {
            return false;
        }

        // Check if all fluid outputs can fit
        if (be.fluidTanks != null) {
            for (int i = 0; i < fluidOutputs.size(); i++) {
                if (i >= outputTankCount) return false;
                int tankIdx = outputTankStart + i;
                FluidStack output = fluidOutputs.get(i);
                int filled = be.fluidTanks[tankIdx].fill(output, IFluidHandler.FluidAction.SIMULATE);
                if (filled < output.getAmount()) return false;
            }
        } else if (!fluidOutputs.isEmpty()) {
            return false;
        }

        // All checks passed - place outputs
        if (be.inventory != null) {
            for (int i = 0; i < itemOutputs.size(); i++) {
                int slot = outputSlotStart + i;
                ItemStack existing = be.inventory.getStackInSlot(slot);
                if (existing.isEmpty()) {
                    be.inventory.setStackInSlot(slot, itemOutputs.get(i).copy());
                } else {
                    existing.grow(itemOutputs.get(i).getCount());
                }
            }
        }

        // Place fluid outputs
        if (be.fluidTanks != null) {
            for (int i = 0; i < fluidOutputs.size(); i++) {
                int tankIdx = outputTankStart + i;
                be.fluidTanks[tankIdx].fill(fluidOutputs.get(i).copy(), IFluidHandler.FluidAction.EXECUTE);
            }
        }

        return true;
    }

    public void consumeInputs() {
        if (recipe == null) return;
        if (!(recipe instanceof UniversalProcessorRecipe upr)) return;

        // Consume item inputs
        List<SizedIngredient> itemInputs = upr.getItemInputs();
        if (be.inventory != null) {
            for (int i = 0; i < itemInputs.size(); i++) {
                int count = itemInputs.get(i).count();
                be.inventory.extractItem(i, count, false);
            }
        }

        // Consume fluid inputs
        List<SizedFluidIngredient> fluidInputs = upr.getFluidInputs();
        if (be.fluidTanks != null) {
            for (int i = 0; i < fluidInputs.size(); i++) {
                int amount = fluidInputs.get(i).amount();
                be.fluidTanks[i].drain(amount, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        changed = true;
    }

    private void findRecipe() {
        if(lastRecipe != null) {
            if (isValidRecipe(lastRecipe)) {
                setRecipe(lastRecipe);
                return;
            }
        }
        for (Recipe<?> r : getRecipes().values()) {
            if (isValidRecipe(r)) {
                setRecipe(r);
                return;
            }
        }
    }

    public void setRecipe(Recipe<?> recipe) {
        this.recipe = recipe;
        this.lastRecipe = null;
        // Resolve recipeId from the recipes map
        this.recipeId = null;
        for (var entry : getRecipes().entrySet()) {
            if (entry.getValue() == recipe) {
                this.recipeId = entry.getKey();
                break;
            }
        }
        clear();
        if (recipe instanceof UniversalProcessorRecipe upr) {
            this.ticksNeeded = upr.getProcessTime();
            this.energyPerTick = upr.getEnergyPerTick();
        }
        consumeInputs();
    }

    @SuppressWarnings("unchecked")
    public boolean isValidRecipe(Recipe<?> recipe) {
        if (!(recipe instanceof UniversalProcessorRecipe)) return false;
        return ((Recipe<ProcessorRecipeInput>) recipe).matches(be.inputs(), getLevel());
    }

    private boolean hasRecipe() {
        return recipe != null;
    }

    public int getProgress() {
        return (int) ((double)ticks / ticksNeeded * 100);
    }

    public void clear() {
        ticks = 0;
        ticksNeeded = 0;
        energyPerTick = 0;
    }

    public Recipe<?> recipe() {
        if(recipe == null && recipeId != null && !recipeId.isEmpty()) {
            recipe = getRecipeFromTag(recipeId);
        }
        return recipe;
    }

    public HashMap<String, Recipe<?>> getRecipes() {
        if(allRecipes.isEmpty()) {
            if(getLevel() != null) {
                getLevel().getRecipeManager().getRecipes().forEach(r -> {
                    if (r.value() instanceof UniversalProcessorRecipe) {
                        allRecipes.put(r.id().toString(), r.value());
                    }
                });
            }
        }
        return allRecipes;
    }

    private Recipe<?> getRecipeFromTag(String recipe) {
        Recipe<?> cachedRecipe = getRecipes().getOrDefault(recipe, null);
        if(cachedRecipe != null) {
            return cachedRecipe;
        }
        ResourceLocation id = rlFromString(recipe);
        if(getLevel() == null) return null;
        try {
            return getLevel().getRecipeManager().byKey(id).get().value();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Level getLevel()
    {
        if(be != null) return be.getLevel();
        return switch (FMLEnvironment.dist) {
            case CLIENT -> ClientUtil.tryGetClientWorld();
            case DEDICATED_SERVER -> ServerLifecycleHooks.getCurrentServer().overworld();
        };
    }

    public Tag save() {
        CompoundTag data = new CompoundTag();
        data.putInt("ticks", ticks);
        data.putInt("ticksNeeded", ticksNeeded);
        data.putInt("energyPerTick", energyPerTick);
        if(recipe != null && recipeId != null) {
            data.putString("recipe", recipeId);
        }
        return data;
    }

    public void load(Tag nbt) {
        if(nbt instanceof CompoundTag) {
            ticks = ((CompoundTag) nbt).getInt("ticks");
            ticksNeeded = ((CompoundTag) nbt).getInt("ticksNeeded");
            energyPerTick = ((CompoundTag) nbt).getInt("energyPerTick");
            recipeId = ((CompoundTag) nbt).getString("recipe");
            recipe = null;
            if(!recipeId.isEmpty()) {
                recipe = getRecipeFromTag(recipeId);
            }
        }
    }
}
