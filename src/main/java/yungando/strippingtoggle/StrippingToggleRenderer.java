package yungando.strippingtoggle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class StrippingToggleRenderer {
  private static final Identifier STRIP_TEXTURE = Identifier.of("strippingtoggle", "textures/gui/strip.png");

  public static void renderTexture(DrawContext drawContext, int screenWidth, int screenHeight) {
    MinecraftClient client = MinecraftClient.getInstance();

    if (client.options.hudHidden)
      return;

    client.getTextureManager().bindTexture(STRIP_TEXTURE);

    int textureWidth = 32;
    int textureHeight = 16;
    int x = (screenWidth / 2) - (textureWidth / 2);
    int y = (screenHeight / 2) - (textureHeight / 2);

    MatrixStack matrixStack = drawContext.getMatrices();

    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    matrixStack.push();
    matrixStack.translate(0, -15, 0);

    drawContext.drawTexture(STRIP_TEXTURE, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

    matrixStack.pop();

    RenderSystem.disableBlend();
  }
}