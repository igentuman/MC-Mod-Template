package igentuman.mod_template.datagen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

import static igentuman.mod_template.setup.ModEntries.EXAMPLE_ITEM;
import static net.minecraft.data.recipes.RecipeCategory.MISC;

public class ModRecipeProvider  extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {

        ExampleMachineRecipes.generate(recipeOutput);
/*        ShapedRecipeBuilder.shaped(MISC, EXAMPLE_ITEM.item())
                .pattern(" S ")
                .pattern("WEW")
                .pattern("TWT")
                .define('S', Items.STRING).define('W', ItemTags.WOOL)
                .define('E', Items.REDSTONE).define('T', ItemTags.ANVIL)
                .unlockedBy(getHasName(Items.ENDER_CHEST), has(Items.ANVIL)).save(recipeOutput);*/

    }
}
