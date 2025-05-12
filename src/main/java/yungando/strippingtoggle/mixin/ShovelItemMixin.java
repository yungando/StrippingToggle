package yungando.strippingtoggle.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yungando.strippingtoggle.StrippingToggle;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {
  @ModifyVariable(method = "useOnBlock", at = @At("STORE"), ordinal = 1)
  private BlockState setPathStateNull(BlockState originalValue) {
    if (!StrippingToggle.strippingEnabled)
      return null;
    return originalValue;
  }
}