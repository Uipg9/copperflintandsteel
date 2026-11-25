package dev.brian.copperflintandsteel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.BlockHitResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copperflintandsteel implements ModInitializer {
	public static final String MOD_ID = "copperflintandsteel";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		UseBlockCallback.EVENT.register(Copperflintandsteel::primeTnt);
	}

	/** Mirrors TntBlock#useItemOn, which only checks Items.FLINT_AND_STEEL by identity. */
	private static InteractionResult primeTnt(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		BlockPos pos = hit.getBlockPos();
		if (!IgnitionRules.shouldPrimeTnt(held.is(ModItems.COPPER_FLINT_AND_STEEL),
				level.getBlockState(pos).getBlock() instanceof TntBlock, player.isSpectator())) {
			return InteractionResult.PASS;
		}
		if (TntBlock.prime(level, pos, player, held)) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
			held.hurtAndBreak(IgnitionRules.DAMAGE_PER_USE, player, hand.asEquipmentSlot());
			player.awardStat(Stats.ITEM_USED.get(ModItems.COPPER_FLINT_AND_STEEL));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/** Translation key following the vanilla convention: {@code category.modid.path}. */
	public static String langKey(String category, String path) {
		return category + "." + MOD_ID + "." + path;
	}
}
