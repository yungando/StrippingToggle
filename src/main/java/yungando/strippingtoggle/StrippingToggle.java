package yungando.strippingtoggle;

import com.google.common.collect.BiMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Environment(EnvType.CLIENT)
public class StrippingToggle implements ClientModInitializer {
	public static KeyMapping toggleStripping;
	public static boolean strippingEnabled = false;

	private static final Identifier STRIP_TEXTURE = Identifier.fromNamespaceAndPath("strippingtoggle", "textures/gui/strip.png");

	protected static final List<Block> AXE_BLOCKS = Arrays.asList(
		Blocks.OAK_WOOD,
		Blocks.OAK_LOG,
		Blocks.DARK_OAK_WOOD,
		Blocks.DARK_OAK_LOG,
		Blocks.PALE_OAK_WOOD,
		Blocks.PALE_OAK_LOG,
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
		final KeyMapping.Category StrippingToggleKeyCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("yungando","strippingtoggle"));
		toggleStripping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.yungando.strippingtoggle.toggleStripping", GLFW.GLFW_KEY_B, StrippingToggleKeyCategory));

		ClientTickEvents.END_CLIENT_TICK.register(_ -> {
			if (toggleStripping.consumeClick())
				StrippingToggle.toggleStripping();
		});

		HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, STRIP_TEXTURE, this::renderTexture);
	}

	public static void toggleStripping() {
		strippingEnabled = !strippingEnabled;
	}

	public void renderTexture(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
		if (!strippingEnabled)
			return;

		Minecraft client = Minecraft.getInstance();

		if (client.gui.hud.isHidden())
			return;

		int textureWidth = 32;
		int textureHeight = 16;
		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();

		int x = (screenWidth / 2) - (textureWidth / 2);
		int y = (screenHeight / 2) - (textureHeight / 2) - 15;

		drawContext.blit(
			RenderPipelines.GUI_TEXTURED,
			STRIP_TEXTURE,
			x, y,
			0.0f, 0.0f,
			textureWidth, textureHeight,
			textureWidth, textureHeight);
	}

	public static boolean canBeAxeStripped(Block block) {
		if (AXE_BLOCKS.contains(block)) return true;

		if (block instanceof CopperGolemStatueBlock) return true;

		BlockState blockState = block.defaultBlockState();
		Optional<BlockState> blockOxidisable = WeatheringCopper.getPrevious(blockState);
		if (blockOxidisable.isPresent()) return true;

		Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap<?, ?>) HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(block))
			.map(b -> b.withPropertiesOf(blockState));

    return optional3.isPresent();
  }

	public static boolean canBeShovelPathed(Block block) {
		return SHOVEL_BLOCKS.contains(block);
	}
}