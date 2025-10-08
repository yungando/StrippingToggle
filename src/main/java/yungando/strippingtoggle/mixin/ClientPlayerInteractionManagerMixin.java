package yungando.strippingtoggle.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
  private void StrippingToggle$cancelBlockInteraction(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
    if (!StrippingToggle.strippingEnabled) {
      Item heldItem = player.getStackInHand(hand).getItem();
      Block interactionBlock = player.getEntityWorld().getBlockState(hitResult.getBlockPos()).getBlock();

      if (heldItem instanceof AxeItem && StrippingToggle.canBeAxeStripped(interactionBlock)) {
        cir.setReturnValue(ActionResult.PASS);
      }

      if (heldItem instanceof ShovelItem && StrippingToggle.canBeShovelPathed(interactionBlock)) {
        cir.setReturnValue(ActionResult.PASS);
      }
    }
  }

  @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelEntityInteraction(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
    if (!StrippingToggle.strippingEnabled && entity instanceof CopperGolemEntity copperGolem) {
      Item heldItem = player.getStackInHand(hand).getItem();
      Oxidizable.OxidationLevel oxidationLevel = copperGolem.getOxidationLevel();

      if (heldItem instanceof AxeItem && oxidationLevel != Oxidizable.OxidationLevel.UNAFFECTED) {
        cir.setReturnValue(ActionResult.PASS);
      }
    }
  }
}
