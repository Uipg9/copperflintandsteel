# Roadmap

## Done (verified by `.\scripts\verify.ps1`: build + unit tests + client gametest)
- 1.0.1: recipe unlock advancement so the recipe shows in the recipe book (unit test guards every recipe).
- Item `copperflintandsteel:copper_flint_and_steel`, a vanilla `FlintAndSteelItem` with durability 40.
- Lights fire on a block with a real right-click, 1 damage per use; breaks on the 40th use.
- TNT priming (vanilla `TntBlock#useItemOn` checks `Items.FLINT_AND_STEEL` by identity, so a
  `UseBlockCallback` primes it); 1 damage per use.
- Dispenser: `FlintAndSteelDispenseItemBehavior` registered for the copper item; lights fire.
- Tags: `minecraft:creeper_igniters`, `minecraft:enchantable/durability` (Unbreaking, Mending, and
  Vanishing via inheritance). Membership asserted in the gametest.
- Shapeless recipe, creative tab placement, lang, item model + texture (seen in screenshots).

## Untested in-game (expected to work through `FlintAndSteelItem`/tags, no dedicated assertion)
- Nether portal lighting, candles/candle cakes, campfires.
- Creeper ignition by a player (tag only), dispenser priming TNT.
- Anvil repair with copper ingots, enchanting table offering Unbreaking.

## Not done
- Nothing planned beyond the above.
