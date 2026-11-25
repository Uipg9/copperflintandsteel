package dev.brian.copperflintandsteel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CopperflintandsteelTest {
	@Test
	void idUsesModNamespace() {
		var id = Copperflintandsteel.id("example");
		assertEquals(Copperflintandsteel.MOD_ID, id.getNamespace());
		assertEquals("example", id.getPath());
	}

	@Test
	void langKeyFollowsMinecraftConvention() {
		assertEquals("item.copperflintandsteel.example", Copperflintandsteel.langKey("item", "example"));
	}
}
