package igentuman.mod_template.compat.jei;

import igentuman.mod_template.Main;
import igentuman.mod_template.recipe.UniversalProcessorRecipe;
import igentuman.mod_template.registration.ModEntry;
import igentuman.mod_template.setup.ModEntries;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    private final Map<String, RecipeType<UniversalProcessorRecipe>> recipeTypes = new HashMap<>();

    @Override
    public ResourceLocation getPluginUid() {
        return Main.rl("jei_plugin");
    }

    private RecipeType<UniversalProcessorRecipe> getOrCreateRecipeType(ModEntry entry) {
        return recipeTypes.computeIfAbsent(entry.name(), name ->
                RecipeType.create(Main.MODID, name, UniversalProcessorRecipe.class));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (!entry.hasRecipes()) continue;
            RecipeType<UniversalProcessorRecipe> jeiType = getOrCreateRecipeType(entry);
            registration.addRecipeCategories(new ProcessorRecipeCategory(guiHelper, entry, jeiType));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (!entry.hasRecipes() || !entry.hasItem()) continue;
            RecipeType<UniversalProcessorRecipe> jeiType = getOrCreateRecipeType(entry);
            registration.addRecipeCatalyst(new ItemStack(entry.item().get()), jeiType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        for (ModEntry entry : ModEntries.ENTRIES.values()) {
            if (!entry.hasRecipes()) continue;

            RecipeType<UniversalProcessorRecipe> jeiType = getOrCreateRecipeType(entry);
            net.minecraft.world.item.crafting.RecipeType<?> mcType = entry.recipeType().get();

            List<UniversalProcessorRecipe> recipes = recipeManager
                    .getAllRecipesFor((net.minecraft.world.item.crafting.RecipeType<UniversalProcessorRecipe>) mcType)
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();

            registration.addRecipes(jeiType, recipes);
        }
    }
}
