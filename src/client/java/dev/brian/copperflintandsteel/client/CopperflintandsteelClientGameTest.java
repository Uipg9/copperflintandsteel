package dev.brian.copperflintandsteel.client;

import dev.brian.copperflintandsteel.Copperflintandsteel;
import dev.brian.copperflintandsteel.ModItems;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/**
 * In-game test: real right-clicks with the copper flint and steel light fire and prime TNT,
 * a dispenser lights fire, and the item takes 1 damage per use.
 */
public class CopperflintandsteelClientGameTest implements FabricClientGameTest {
	private static final String ITEM = "copperflintandsteel:copper_flint_and_steel";
	private static final BlockPos TARGET = new BlockPos(3, 100, 5);
	private static final BlockPos ABOVE = TARGET.above();

	@Override
	public void runTest(ClientGameTestContext context) {
		expect(FabricLoader.getInstance().isModLoaded(Copperflintandsteel.MOD_ID), "mod not loaded");

		context.waitForScreen(TitleScreen.class);
		context.waitTicks(60);

		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(100);

			// registration, recipe, tags
			expect(BuiltInRegistries.ITEM.containsKey(Copperflintandsteel.id("copper_flint_and_steel")), "item not registered");
			expect(new ItemStack(ModItems.COPPER_FLINT_AND_STEEL).getMaxDamage() == 40, "max damage should be 40");
			boolean recipe = world.getServer().computeOnServer(server -> server.getRecipeManager()
				.byKey(ResourceKey.create(Registries.RECIPE, Copperflintandsteel.id("copper_flint_and_steel"))).isPresent());
			expect(recipe, "recipe missing");
			boolean tags = world.getServer().computeOnServer(server -> {
				ItemStack stack = new ItemStack(ModItems.COPPER_FLINT_AND_STEEL);
				return stack.is(ItemTags.CREEPER_IGNITERS) && stack.is(ItemTags.DURABILITY_ENCHANTABLE);
			});
			expect(tags, "item should be in creeper_igniters and enchantable/durability");
			pass("registration, recipe, tags");

			world.getServer().runCommand("gamemode survival @p");
			world.getServer().runCommand("fill 0 100 0 8 100 8 stone");
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			world.getServer().runCommand("tp @p 3.5 101 3.5 0 0");
			context.waitTicks(10);

			// light fire on top of a block: fire appears, 1 damage
			give(world, ITEM);
			context.getInput().lookAt(TARGET);
			context.waitTicks(2);
			rightClick(context);
			expect(world.getServer().computeOnServer(s -> s.overworld().getBlockState(ABOVE).is(Blocks.FIRE)), "fire should appear");
			expectHeld(world, ITEM, 1, "after lighting fire");
			context.takeScreenshot("fire_lit");
			pass("light fire");

			// TNT: primed entity appears, TNT block removed, 1 damage
			world.getServer().runCommand("setblock 3 101 5 air");
			world.getServer().runCommand("setblock 3 100 5 tnt");
			give(world, ITEM + "[minecraft:damage=5]");
			context.waitTicks(3);
			rightClick(context);
			boolean primed = world.getServer().computeOnServer(s -> !s.overworld().getBlockState(TARGET).is(Blocks.TNT)
				&& !s.overworld().getEntitiesOfClass(PrimedTnt.class, new AABB(TARGET).inflate(3)).isEmpty());
			expect(primed, "TNT should be primed");
			expectHeld(world, ITEM, 6, "after priming TNT");
			context.takeScreenshot("tnt_primed");
			pass("prime TNT");
			// let it blow up harmlessly (creative), then rebuild the platform
			world.getServer().runCommand("gamemode creative @p");
			context.waitTicks(100);
			world.getServer().runCommand("fill 0 90 0 8 100 8 stone");
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			world.getServer().runCommand("tp @p 3.5 101 3.5 0 0");
			world.getServer().runCommand("gamemode survival @p");
			context.waitTicks(10);
			context.getInput().lookAt(TARGET);

			// last durability point breaks it
			give(world, ITEM + "[minecraft:damage=39]");
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			context.waitTicks(3);
			rightClick(context);
			expectHeld(world, "minecraft:air", 0, "after the 40th use");
			pass("break on last use");

			// dispenser facing up lights fire above it
			world.getServer().runCommand("fill 0 101 0 8 104 8 air");
			world.getServer().runCommand("setblock 6 101 6 dispenser[facing=up]{Items:[{Slot:0b,id:\"" + ITEM + "\",count:1}]}");
			world.getServer().runCommand("setblock 7 101 6 redstone_block");
			context.waitTicks(10);
			boolean dispensed = world.getServer().computeOnServer(s -> s.overworld().getBlockState(new BlockPos(6, 102, 6)).is(Blocks.FIRE));
			expect(dispensed, "dispenser should light fire");
			pass("dispenser");

			world.getServer().runCommand("item replace entity @p hotbar.0 with " + ITEM);
			world.getServer().runCommand("item replace entity @p hotbar.1 with " + ITEM + "[minecraft:damage=20]");
			world.getServer().runCommand("item replace entity @p hotbar.2 with minecraft:flint_and_steel");
			world.getServer().runCommand("item replace entity @p hotbar.3 with " + ITEM + "[minecraft:enchantments={\"minecraft:unbreaking\":3}]");
			context.getInput().lookAt(0f, 20f);
			context.waitTicks(10);
			context.takeScreenshot("hotbar");
		}
	}

	private static void rightClick(ClientGameTestContext context) {
		context.getInput().pressKey(options -> options.keyUse);
		context.waitTicks(5);
	}

	private static void give(TestSingleplayerContext world, String stack) {
		world.getServer().runCommand("item replace entity @p weapon.mainhand with " + stack);
	}

	private static void expectHeld(TestSingleplayerContext world, String id, int damage, String when) {
		String actual = world.getServer().computeOnServer(server -> {
			ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
			ItemStack held = player.getMainHandItem();
			return BuiltInRegistries.ITEM.getKey(held.getItem()) + "#" + held.getDamageValue() + "x" + held.getCount();
		});
		String expected = id + "#" + damage + (id.equals("minecraft:air") ? "x0" : "x1");
		expect(actual.equals(expected), when + ": expected " + expected + " but held " + actual);
	}

	private static void pass(String step) {
		System.out.println("[copperflintandsteel-gametest] PASS " + step);
	}

	private static void expect(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
