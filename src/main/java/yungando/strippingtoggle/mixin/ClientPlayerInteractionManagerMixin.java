package yungando.strippingtoggle.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yungando.strippingtoggle.StrippingToggle;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
  @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelInteraction(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
    if (!StrippingToggle.strippingEnabled) {
      Item heldItem = player.getStackInHand(hand).getItem();
      Block interactionBlock = player.getWorld().getBlockState(hitResult.getBlockPos()).getBlock();

      if (heldItem instanceof AxeItem && StrippingToggle.canBeAxeStripped(interactionBlock)) {
        cir.setReturnValue(ActionResult.FAIL);
      }

      if (heldItem instanceof ShovelItem && StrippingToggle.canBeShovelPathed(interactionBlock)) {
        cir.setReturnValue(ActionResult.FAIL);
      }
    }
  }
}
