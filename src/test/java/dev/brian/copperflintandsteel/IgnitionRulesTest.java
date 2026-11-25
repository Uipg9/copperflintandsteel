package dev.brian.copperflintandsteel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IgnitionRulesTest {
	@Test
	void durabilityIsFortyUses() {
		assertEquals(40, IgnitionRules.MAX_DURABILITY);
		assertTrue(IgnitionRules.MAX_DURABILITY < IgnitionRules.VANILLA_DURABILITY);
	}

	@Test
	void eachIgnitionCostsOneDurability() {
		assertEquals(1, IgnitionRules.DAMAGE_PER_USE);
	}

	@Test
	void primesTntOnlyWithCopperFlintAndSteelOnTnt() {
		assertTrue(IgnitionRules.shouldPrimeTnt(true, true, false));
		assertFalse(IgnitionRules.shouldPrimeTnt(false, true, false));
		assertFalse(IgnitionRules.shouldPrimeTnt(true, false, false));
	}

	@Test
	void spectatorsCannotPrimeTnt() {
		assertFalse(IgnitionRules.shouldPrimeTnt(true, true, true));
	}
}
