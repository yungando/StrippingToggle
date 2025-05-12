package yungando.strippingtoggle.mixin;

import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yungando.strippingtoggle.StrippingToggle;

@Mixin(AxeItem.class)
public class AxeItemMixin {
  @Inject(method = "shouldCancelStripAttempt", at = @At("HEAD"), cancellable = true)
  private static void shouldCancelStripAttempt(ItemUsageContext context, CallbackInfoReturnable<Boolean> cir) {
    if (!StrippingToggle.strippingEnabled)
      cir.setReturnValue(true);
  }
}