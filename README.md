# Mod Template for NeoForge 1.21.1

A universal template for building Minecraft mods on **NeoForge 1.21.1**. The core idea is a **single-point registration system** powered by `ModEntryBuilder` - define your items, blocks, machines, and processors in one place, and the mod automatically registers everything needed: blocks, block items, block entities, menus, screens, recipe types, recipe serializers, and creative tab entries.

## Features

- **One-liner registration** - add a full-featured processing machine with a single builder chain
- **Material registration system** - define a complete material (ore, ingot, dust, plate, nugget, raw ore, storage block, molten fluid, bucket) with one call via `addMetalOreMaterial`
- **Unified `ModEntry` record** - holds all deferred references (block, item, block entity, menu, recipe type, recipe serializer, material) in one object
- **Universal processor system** - reusable block, block entity, container, screen, and recipe classes that work for any machine
- **Auto creative tab placement** - machines go to Functional Blocks, plain blocks to Building Blocks, standalone items to Ingredients
- **Auto screen registration** - all entries with menus get their GUI screens registered on the client automatically
- **Datagen-ready** - includes providers for block states, item models, loot tables, recipes, tags (block, item, fluid), and language files
- **Recipe datagen builder** - `UniversalProcessorRecipeBuilder` with fluent API for item/fluid inputs/outputs, process time, and energy cost
- **Dynamic JEI integration** - automatically registers JEI recipe categories, catalysts, and recipes for every processor entry
- **World generation hooks** - preconfigured modules for configured features, placed features, biome modifiers, biomes, and dimensions

## Auto-Registered Components

When you use `ModEntryBuilder`, the following registries are populated automatically based on what you configure:

| Component | Registry | Registered when |
|---|---|---|
| **Block** | `BLOCKS` | `.block(...)` is called |
| **BlockItem** | `ITEMS` | A block is present but no custom item supplier is set |
| **Item** | `ITEMS` | `.item(...)` is called (standalone item) |
| **BlockEntityType** | `BLOCK_ENTITIES` | `.blockEntity(...)` is called |
| **MenuType** | `CONTAINERS` | `.menu(...)` is called |
| **RecipeType** | `RECIPE_TYPES` | `.withRecipes()` is called |
| **RecipeSerializer** | `RECIPE_SERIALIZERS` | `.withRecipes()` is called |
| **Creative Tab Entry** | - | Automatic based on entry type (machine / block / item) |
| **GUI Screen** | Client event | Automatic for every entry that has a menu |
| **Material (ore, ingot, etc.)** | `BLOCKS` / `ITEMS` | `addMetalOreMaterial(...)` is called |
| **Molten Fluid** | `FLUID_TYPES` / `FLUIDS` | Material has a `FluidDefinition` |
| **Bucket Item** | `ITEMS` | Material has a fluid |
| **JEI Category** | JEI plugin | Automatic for every entry with recipes |

## ModEntryBuilder API

### Available Methods

| Method | Description |
|---|---|
| `add(name)` | Start a blank entry |
| `addItem(name)` | Shortcut: register a simple item with default properties |
| `addItem(name, supplier)` | Shortcut: register a custom item |
| `addProcessor(name)` | Shortcut: register a full processor (block + block entity + menu + energy + recipes) |
| `.block(supplier)` | Set the block supplier |
| `.item(supplier)` | Set a custom item supplier (overrides auto BlockItem) |
| `.blockEntity(constructor)` | Set the block entity constructor |
| `.menu(factory)` | Set the container/menu factory |
| `.withEnergyInput(capacity)` | Define energy storage (input rate = capacity/2) |
| `.withRecipes()` | Register a `RecipeType` and `RecipeSerializer` for this processor |
| `.withRecipes(type, serializer)` | Register custom recipe type and serializer |
| `.fluidCap(in, out, default)` | Define fluid capability (input/output/default tanks) |
| `.itemCap(in, out)` | Define item capability (input/output slots) |
| `.material(color)` | Attach a `MaterialEntry` with the given color |
| `.build()` | Finalize and register everything, returns a `ModEntry` |
| `addMetalOreMaterial(name, color)` | Shortcut: register a full metal material (ore, block, ingot, dust, plate, nugget, raw ore, molten fluid, bucket) |

## Usage Examples

All entries are defined in `ModEntries.java`:

### Register a simple item

```java
public static final ModEntry EXAMPLE_ITEM = addItem("example_item").build();
```

This registers an `Item` with default properties and adds it to the Ingredients creative tab.

### Register a simple item with custom properties

```java
public static final ModEntry CUSTOM_ITEM = addItem("custom_item",
        () -> new Item(new Item.Properties().stacksTo(16))
).build();
```

### Register a full processing machine (one-liner)

```java
public static final ModEntry EXAMPLE_MACHINE = addProcessor("example_machine")
        .fluidCap(1, 1, 0)
        .itemCap(1, 0)
        .build();
```

This single call registers:
- A `UniversalProcessorBlock` (with right-click to open GUI, drops inventory on break)
- A `BlockItem` for the block
- A `UniversalProcessorBE` block entity (with inventory, progress tracking, NBT save/load)
- A `UniversalProcessorContainer` menu (syncs data to client)
- A `UniversalProcessorScreen` GUI (auto-registered on client)
- A `RecipeType` and `RecipeSerializer` for this machine
- Energy storage with 100,000 FE capacity
- Automatic placement in the Functional Blocks creative tab

### Register a custom block without a block entity

```java
public static final ModEntry DECORATIVE_BLOCK = add("decorative_block")
        .block(() -> new Block(BlockBehaviour.Properties.of().strength(2.0f)))
        .build();
```

This registers the block + auto-generated `BlockItem`, placed in the Building Blocks creative tab.

### Register a processor with custom classes

Instead of using the universal `UniversalProcessorBlock` / `UniversalProcessorBE` / `UniversalProcessorContainer`, you can pass your own classes via the low-level `add()` builder:

```java
public static final ModEntry ADVANCED_MACHINE = add("advanced_machine")
        .block(AdvancedMachineBlock::new)           // custom Block class
        .blockEntity(AdvancedMachineBE::new)        // custom BlockEntity class
        .menu(AdvancedMachineContainer::new)        // custom Container class
        .withEnergyInput(500000)
        .fluidCap(2, 1, 0)
        .itemCap(3, 2)
        .withRecipes()                              // uses default UniversalProcessorRecipe
        .build();
```

Your custom classes just need to follow the same constructor signatures as the universal ones:

- **Block:** `AdvancedMachineBlock(String name)` - receives the entry name
- **BlockEntity:** `AdvancedMachineBE(BlockPos pos, BlockState state, String name)` - the `TriFunction` passed to `.blockEntity(...)`
- **Container:** `AdvancedMachineContainer(int containerId, Inventory inv, RegistryFriendlyByteBuf data)` - the `IContainerFactory` passed to `.menu(...)`

You can also start from `addProcessor()` and override only the parts you want to customize:

```java
public static final ModEntry ADVANCED_MACHINE = addProcessor("advanced_machine")
        .block(AdvancedMachineBlock::new)           // override just the block
        .withEnergyInput(500000)
        .build();
```

### Register a processor with custom recipe classes

If you need a completely custom recipe format (different fields, different matching logic), use `.withRecipes(typeSupplier, serializerSupplier)` to supply your own `RecipeType` and `RecipeSerializer`:

```java
public static final ModEntry ADVANCED_MACHINE = add("advanced_machine")
        .block(AdvancedMachineBlock::new)
        .blockEntity(AdvancedMachineBE::new)
        .menu(AdvancedMachineContainer::new)
        .withEnergyInput(500000)
        .withRecipes(
                () -> RecipeType.simple(rl("advanced_machine")),     // custom RecipeType
                () -> new AdvancedMachineRecipeSerializer()          // custom RecipeSerializer
        )
        .build();
```

Where `AdvancedMachineRecipe` is your own `Recipe<ProcessorRecipeInput>` implementation and `AdvancedMachineRecipeSerializer` is its `RecipeSerializer`:

```java
// Custom recipe - add any fields you need (e.g. temperature, catalyst, tier)
public class AdvancedMachineRecipe implements Recipe<ProcessorRecipeInput> {

    private final String processorName;
    private final List<SizedIngredient> itemInputs;
    private final List<ItemStack> itemOutputs;
    private final int processTime;
    private final int requiredTemperature;   // custom field

    public AdvancedMachineRecipe(String processorName, List<SizedIngredient> itemInputs,
                                 List<ItemStack> itemOutputs, int processTime,
                                 int requiredTemperature) {
        this.processorName = processorName;
        this.itemInputs = itemInputs;
        this.itemOutputs = itemOutputs;
        this.processTime = processTime;
        this.requiredTemperature = requiredTemperature;
    }

    @Override
    public boolean matches(ProcessorRecipeInput input, Level level) {
        // Custom matching logic
        for (int i = 0; i < itemInputs.size(); i++) {
            if (i >= input.size()) return false;
            if (!itemInputs.get(i).test(input.getItem(i))) return false;
        }
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModEntries.get(processorName).recipeSerializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModEntries.get(processorName).recipeType().get();
    }

    // ... standard overrides: assemble(), getResultItem(), canCraftInDimensions(), etc.
}
```

```java
// Custom serializer - define your own codec with extra fields
public class AdvancedMachineRecipeSerializer implements RecipeSerializer<AdvancedMachineRecipe> {

    private final MapCodec<AdvancedMachineRecipe> codec = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SizedIngredient.FLAT_CODEC.listOf()
                    .fieldOf("item_inputs")
                    .forGetter(AdvancedMachineRecipe::getItemInputs),
            ItemStack.CODEC.listOf()
                    .fieldOf("item_outputs")
                    .forGetter(AdvancedMachineRecipe::getItemOutputs),
            Codec.INT
                    .optionalFieldOf("process_time", 200)
                    .forGetter(AdvancedMachineRecipe::getProcessTime),
            Codec.INT
                    .optionalFieldOf("required_temperature", 1000)   // custom field
                    .forGetter(AdvancedMachineRecipe::getRequiredTemperature)
    ).apply(inst, (inputs, outputs, time, temp) ->
            new AdvancedMachineRecipe("advanced_machine", inputs, outputs, time, temp)
    ));

    @Override
    public MapCodec<AdvancedMachineRecipe> codec() { return codec; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AdvancedMachineRecipe> streamCodec() {
        // Implement network serialization
        return ...;
    }
}
```

The corresponding recipe JSON would then include your custom field:

```json
{
  "type": "modtemplate:advanced_machine",
  "item_inputs": [
    { "ingredient": { "item": "minecraft:diamond" }, "count": 2 }
  ],
  "item_outputs": [
    { "id": "modtemplate:advanced_component", "count": 1 }
  ],
  "process_time": 400,
  "required_temperature": 1500
}
```

### Register a complete metal material (one-liner)

```java
public static final ModEntry SILVER = addMetalOreMaterial("silver", Color.LIGHT_GRAY.getRGB()).build();
```

This single call registers:
- A **silver ore** block + `BlockItem`
- A **silver storage block** + `BlockItem`
- An **ingot**, **dust**, **plate**, **nugget**, and **raw ore** item
- A **molten silver** fluid (source + flowing) with `FluidType`
- A **liquid block** for the molten fluid placed in world
- A **bucket** item for the molten fluid
- Automatic placement in creative tabs
- Full datagen support: block states, item models, language entries, block/item/fluid tags

The `MaterialEntry` API supports customization via chaining - you can remove components you don't need:

```java
public static final ModEntry COPPER_EXTRA = addMetalOreMaterial("copper_extra", 0xB87333)
        .noOre()      // skip ore block registration
        .noRawOre()   // skip raw ore item
        .noFluid()    // skip molten fluid
        .build();
```

### Recipe datagen with `UniversalProcessorRecipeBuilder`

Define processor recipes in datagen using a fluent builder:

```java
processor("example_machine")
        .itemInput(Items.SAND)
        .fluidInput(Fluids.WATER, 1000)
        .fluidOutput(Fluids.LAVA, 1000)
        .processTime(200)
        .energyPerTick(20)
        .save(recipeOutput, "sand_water_to_lava");
```

The builder supports:
- `.itemInput(item)` / `.itemInput(item, count)` / `.itemInput(tagKey, count)` - item inputs
- `.fluidInput(fluid, amount)` - fluid inputs
- `.itemOutput(item)` / `.itemOutput(item, count)` - item outputs
- `.fluidOutput(fluid, amount)` - fluid outputs
- `.processTime(ticks)` - processing duration (default 200)
- `.energyPerTick(fe)` - energy cost per tick (default 20)

Recipe JSON files are generated in `data/modtemplate/recipe/` with the processor name as subfolder.

### JEI Integration

JEI recipe categories are registered **dynamically** for every `ModEntry` that has recipes. No manual JEI code needed - just define your processors and recipes, and JEI categories appear automatically:

- **Category** - titled with the block's translation name, using the machine block as icon
- **Catalyst** - the machine block item is registered as the recipe catalyst
- **Recipes** - all recipes of the machine's `RecipeType` are collected and displayed
- **Layout** - item and fluid input/output slots are arranged based on the processor's capability definitions

The JEI plugin lives in `compat/jei/ModJeiPlugin.java` and iterates over `ModEntries.ENTRIES` at registration time.

### Access registered entries at runtime

```java
// By static field
Block block = ModEntries.EXAMPLE_MACHINE.block().get();
MenuType<?> menu = ModEntries.EXAMPLE_MACHINE.menu().get();

// By name lookup
ModEntry entry = ModEntries.get("example_machine");
if (entry.hasBlockEntity()) {
    BlockEntityType<?> beType = entry.blockEntity().get();
}
```

## Project Structure

```
src/main/java/igentuman/mod_template/
  Main.java                          - Mod entry point (@Mod)
  registration/
    ModEntryBuilder.java             - Fluent builder for mod entries
    ModEntry.java                    - Record holding all deferred registrations
    MaterialEntry.java               - Material definition (ore, ingot, dust, plate, etc.)
    FluidDefinition.java             - Fluid properties for materials
    MaterialFluid.java               - Registered fluid references (source, flowing, block, bucket)
    MaterialFluidType.java           - Custom FluidType with rendering properties
  setup/
    Registers.java                   - All DeferredRegister instances
    ModEntries.java                  - Where you define your content
    Client.java                      - Client-side setup (screen + fluid rendering)
    Common.java                      - Common setup events
  block/
    UniversalProcessorBlock.java     - Reusable processor block
  block_entity/
    UniversalProcessorBE.java        - Reusable processor block entity
  container/
    UniversalProcessorContainer.java - Reusable container/menu
  screen/
    UniversalProcessorScreen.java    - Reusable GUI screen
  recipe/
    UniversalProcessorRecipe.java    - Universal recipe (items + fluids + energy)
    UniversalProcessorRecipeSerializer.java
    ProcessorRecipeInput.java
  compat/
    jei/
      ModJeiPlugin.java              - Dynamic JEI plugin (auto-registers categories)
      ProcessorRecipeCategory.java   - Universal JEI recipe category for processors
  config/
    Common.java                      - Mod configuration
  datagen/
    ModBlockStateProvider.java       - Block state & model datagen
    ModItemModelProvider.java        - Item model datagen
    ModLanguageProvider.java         - Language file datagen
    recipe/
      ModRecipeProvider.java         - Recipe datagen entry point
      ExampleMachineRecipes.java     - Example processor recipes
      UniversalProcessorRecipeBuilder.java - Fluent recipe builder
    tag/
      ModBlockTagProvider.java       - Block tag datagen (mining, common tags)
      ModItemTagProvider.java        - Item tag datagen (ingots, dusts, ores, etc.)
      ModFluidTagProvider.java       - Fluid tag datagen
  util/                              - Capabilities and slot definitions
```

## Build & Run

Requires **Java 21**.

```bash
./gradlew build          # Build the mod JAR
./gradlew runClient      # Run Minecraft client
./gradlew runServer      # Run dedicated server
./gradlew runData        # Run data generators
./gradlew clean          # Clean build artifacts
```

## Planned Features

### Ore World Generation via Builder

Extend material registration with automatic world-gen setup (configured features, placed features, biome modifiers), deepslate variant generation, and loot tables:

```java
public static final ModEntry SILVER = addMetalOreMaterial("silver", Color.LIGHT_GRAY.getRGB())
        .veinSize(8)
        .veinsPerChunk(4)
        .heightRange(-64, 32)
        .deepslateVariant()             // auto-registers deepslate_silver_ore block
        .build();
```

Planned auto-registered components (on top of existing material registration):
- Deepslate ore variant block (optional)
- `ConfiguredFeature` with `OreConfiguration` (stone + deepslate targets)
- `PlacedFeature` with height and count placement modifiers
- `BiomeModifier` for overworld ore generation
- Loot table with ore-specific drops (raw item or silk touch)

## License

MIT
