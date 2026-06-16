package yungando.strippingtoggle.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yungando.strippingtoggle.StrippingToggle;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelBlockInteraction(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
    if (!StrippingToggle.strippingEnabled) {
      Item heldItem = player.getItemInHand(hand).getItem();
      Block interactionBlock = player.level().getBlockState(blockHit.getBlockPos()).getBlock();

      if (heldItem instanceof AxeItem && StrippingToggle.canBeAxeStripped(interactionBlock)) {
        cir.setReturnValue(InteractionResult.PASS);
      }

      if (heldItem instanceof ShovelItem && StrippingToggle.canBeShovelPathed(interactionBlock)) {
        cir.setReturnValue(InteractionResult.PASS);
      }
    }
  }

  @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelEntityInteraction(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (!StrippingToggle.strippingEnabled && entity instanceof CopperGolem copperGolem) {
      Item heldItem = player.getItemInHand(hand).getItem();
      WeatheringCopper.WeatherState oxidationLevel = copperGolem.getWeatherState();

      if (heldItem instanceof AxeItem && oxidationLevel != WeatheringCopper.WeatherState.UNAFFECTED) {
        cir.setReturnValue(InteractionResult.PASS);
      }
    }
  }
}
