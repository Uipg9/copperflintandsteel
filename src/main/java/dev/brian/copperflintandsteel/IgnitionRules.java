package dev.brian.copperflintandsteel;

/** Pure rules for the copper flint and steel (no Minecraft types, unit-tested). */
public final class IgnitionRules {
	public static final int VANILLA_DURABILITY = 64;
	public static final int MAX_DURABILITY = 40;
	public static final int DAMAGE_PER_USE = 1;

	private IgnitionRules() {
	}

	/** Vanilla TNT only accepts the iron flint and steel by identity, so the mod primes it itself. */
	public static boolean shouldPrimeTnt(boolean heldCopperFlintAndSteel, boolean targetIsTnt, boolean spectator) {
		return heldCopperFlintAndSteel && targetIsTnt && !spectator;
	}
}
