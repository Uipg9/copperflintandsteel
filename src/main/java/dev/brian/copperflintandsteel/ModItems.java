package dev.brian.copperflintandsteel;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.FlintAndSteelDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;

public final class ModItems {
	public static final FlintAndSteelItem COPPER_FLINT_AND_STEEL = register("copper_flint_and_steel");

	private ModItems() {
	}

	private static FlintAndSteelItem register(String path) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Copperflintandsteel.id(path));
		Item.Properties properties = new Item.Properties()
			.setId(key)
			.durability(IgnitionRules.MAX_DURABILITY)
			.repairable(Items.COPPER_INGOT);
		return Registry.register(BuiltInRegistries.ITEM, key, new FlintAndSteelItem(properties));
	}

	public static void initialize() {
		ResourceKey<CreativeModeTab> tools = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
			Identifier.withDefaultNamespace("tools_and_utilities"));
		CreativeModeTabEvents.modifyOutputEvent(tools).register(output -> output.insertAfter(
			Items.FLINT_AND_STEEL, COPPER_FLINT_AND_STEEL));
		// Vanilla registers the dispenser behavior for Items.FLINT_AND_STEEL only.
		DispenserBlock.registerBehavior(COPPER_FLINT_AND_STEEL, new FlintAndSteelDispenseItemBehavior());
	}
}
