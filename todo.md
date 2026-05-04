# TODO

## SlotsLayout Integration
- Add SlotsLayout integration to builder
- Menu and screen will use it for slot positions

## Materials Registration
- ~~New type `MaterialEntry` - single place to register a material (e.g. platinum)~~
- ~~Flag which components to register: ingot, block, ore, liquid, plate, etc.~~
- Include world gen settings if needed

## Networking Support
- Add networking support for `UniversalProcessor`

## UniversalProcessor Block Improvements
- ~~Make `UniversalProcessor` a horizontal directional block~~
- ~~Update blockstate generator accordingly~~

## Capabilities Integration
- ~~Integrate capabilities to `UniversalProcessor`~~

## Example Recipes
- ~~Add actual example recipes for `UniversalProcessor` for testing~~

## JEI/EMI Recipe Categories
- ~~Autopopulate recipe categories for JEI/EMI~~
- Only JEI added for now

## Multiblock Entry Builder
- Introduce multiblock entry builder
- Allow specifying blocks for structure
- Structure logic class
- Structure validation class

## Dynamic Config for Entries
- Allow players to disable items visibility via config
- If item/block disabled, exclude from creative tabs
- If entry with recipes disabled, don't populate JEI/EMI categories
