package yungando.strippingtoggle;

import com.google.common.collect.BiMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.HoneycombItem;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Environment(EnvType.CLIENT)
public class StrippingToggle implements ClientModInitializer {
	public static KeyBinding toggleStripping;
	public static boolean strippingEnabled = false;

	private static final Identifier STRIP_TEXTURE = Identifier.of("strippingtoggle", "textures/gui/strip.png");

	protected static final List<Block> AXE_BLOCKS = Arrays.asList(
		Blocks.OAK_WOOD,
		Blocks.OAK_LOG,
		Blocks.DARK_OAK_WOOD,
		Blocks.DARK_OAK_LOG,
//		Blocks.PALE_OAK_WOOD,
//		Blocks.PALE_OAK_LOG,
		Blocks.ACACIA_WOOD,
		Blocks.ACACIA_LOG,
		Blocks.CHERRY_WOOD,
		Blocks.CHERRY_LOG,
		Blocks.BIRCH_WOOD,
		Blocks.BIRCH_LOG,
		Blocks.JUNGLE_WOOD,
		Blocks.JUNGLE_LOG,
		Blocks.SPRUCE_WOOD,
		Blocks.SPRUCE_LOG,
		Blocks.WARPED_STEM,
		Blocks.WARPED_HYPHAE,
		Blocks.CRIMSON_STEM,
		Blocks.CRIMSON_HYPHAE,
		Blocks.MANGROVE_WOOD,
		Blocks.MANGROVE_LOG,
		Blocks.BAMBOO_BLOCK
	);

	protected static final List<Block> SHOVEL_BLOCKS = Arrays.asList(
		Blocks.DIRT,
		Blocks.GRASS_BLOCK,
		Blocks.COARSE_DIRT,
		Blocks.MYCELIUM,
		Blocks.PODZOL,
		Blocks.ROOTED_DIRT
	);

	@Override
	public void onInitializeClient() {
		toggleStripping = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.strippingtoggle.toggleStripping", GLFW.GLFW_KEY_B, "category.strippingtoggle"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (toggleStripping.wasPressed())
				StrippingToggle.toggleStripping();
		});

		HudRenderCallback.EVENT.register((this::renderTexture));
	}

	public static void toggleStripping() {
		strippingEnabled = !strippingEnabled;
	}

	public void renderTexture(DrawContext drawContext, RenderTickCounter tickCounter) {
		if (!strippingEnabled)
			return;

		MinecraftClient client = MinecraftClient.getInstance();

		if (client.options.hudHidden)
			return;

		int textureWidth = 32;
		int textureHeight = 16;
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		int x = (screenWidth / 2) - (textureWidth / 2);
		int y = (screenHeight / 2) - (textureHeight / 2) - 15;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		drawContext.drawTexture(STRIP_TEXTURE, x, y,0.0f, 0.0f, textureWidth, textureHeight, textureWidth, textureHeight);

		RenderSystem.disableBlend();
	}

	public static boolean canBeAxeStripped(Block block) {
		if (AXE_BLOCKS.contains(block)) return true;

		BlockState blockState = block.getDefaultState();
		Optional<BlockState> blockOxidisable = Oxidizable.getDecreasedOxidationState(blockState);
		if (blockOxidisable.isPresent()) return true;

		Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap) HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(block))
			.map(b -> b.getStateWithProperties(blockState));
		if (optional3.isPresent()) return true;

		return false;
	}

	public static boolean canBeShovelPathed(Block block) {
		return SHOVEL_BLOCKS.contains(block);
	}
}