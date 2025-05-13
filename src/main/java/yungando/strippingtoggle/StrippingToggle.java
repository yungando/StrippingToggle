package yungando.strippingtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class StrippingToggle implements ClientModInitializer {
  public static KeyBinding toggleStripping;
  public static boolean strippingEnabled = false;

  @Override
  public void onInitializeClient() {
    toggleStripping = KeyBindingHelper.registerKeyBinding(new KeyBinding(
      "key.strippingtoggle.toggleStripping", GLFW.GLFW_KEY_B, "category.strippingtoggle"));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      if (toggleStripping.wasPressed())
        StrippingToggle.toggleStripping();
    });

    HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {
      int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
      int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

      if (strippingEnabled)
        StrippingToggleRenderer.renderTexture(drawContext, screenWidth, screenHeight);
    }));
  }

  public static void toggleStripping() {
    strippingEnabled = !strippingEnabled;
  }
}