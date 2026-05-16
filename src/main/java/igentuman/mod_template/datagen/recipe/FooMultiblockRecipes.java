package igentuman.mod_template.datagen.recipe;

import igentuman.mod_template.setup.ModEntries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import static igentuman.mod_template.datagen.recipe.UniversalProcessorRecipeBuilder.controller;
import static igentuman.mod_template.datagen.recipe.UniversalProcessorRecipeBuilder.processor;

public class FooMultiblockRecipes {

    public static void generate(RecipeOutput recipeOutput) {

        controller("foo_controller")
                .itemInput(Items.DIRT)
                .itemOutput(Items.SAND)
                .processTime(2000)
                .energyPerTick(-200)
                .save(recipeOutput, "dirt_silver_to_water");
    }
}
