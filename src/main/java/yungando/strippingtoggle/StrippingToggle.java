package yungando.strippingtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class StrippingToggle implements ClientModInitializer {
	public static KeyBinding toggleStripping;
	public static boolean strippingEnabled = false;

	private static final Identifier STRIP_TEXTURE = Identifier.of("strippingtoggle", "textures/gui/strip.png");

	@Override
	public void onInitializeClient() {
		toggleStripping = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.strippingtoggle.toggleStripping", GLFW.GLFW_KEY_B, "category.strippingtoggle"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (toggleStripping.wasPressed())
				StrippingToggle.toggleStripping();
		});

		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> {
			layeredDrawer.attachLayerAfter(IdentifiedLayer.MISC_OVERLAYS, STRIP_TEXTURE, this::renderTexture);
		});
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

		drawContext.drawTexture(
				texture -> RenderLayer.getGuiTextured(STRIP_TEXTURE),
				STRIP_TEXTURE,
				x, y,
				0.0f, 0.0f,
				textureWidth, textureHeight,
				textureWidth, textureHeight);
	}
}