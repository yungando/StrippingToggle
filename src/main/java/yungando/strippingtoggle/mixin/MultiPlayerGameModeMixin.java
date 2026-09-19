package yungando.strippingtoggle.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yungando.strippingtoggle.StrippingToggle;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
  @Unique
  private static boolean hasTransformer(
    ItemStack stack,
    ResourceKey<BlockTransformer> transformerKey
  ) {
    var transformer = stack.get(DataComponents.BLOCK_TRANSFORMER);

    return transformer != null && transformer.is(transformerKey);
  }

  @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelBlockInteraction(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
    if (!StrippingToggle.strippingEnabled) {
      ItemStack heldItem = player.getItemInHand(hand);
      Block interactionBlock = player.level().getBlockState(blockHit.getBlockPos()).getBlock();

      boolean blockStripping = hasTransformer(heldItem, BlockTransformers.AXE) && StrippingToggle.canBeAxeStripped(interactionBlock);
      boolean blockPathing = hasTransformer(heldItem, BlockTransformers.SHOVEL) && StrippingToggle.canBeShovelPathed(interactionBlock);

      if (blockStripping || blockPathing) {
        cir.setReturnValue(InteractionResult.PASS);
      }
    }
  }

  @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
  private void StrippingToggle$cancelEntityInteraction(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (!StrippingToggle.strippingEnabled && entity instanceof CopperGolem copperGolem) {
      ItemStack heldItem = player.getItemInHand(hand);
      WeatheringCopper.WeatherState oxidationLevel = copperGolem.getWeatherState();

      if (hasTransformer(heldItem, BlockTransformers.AXE) && oxidationLevel != WeatheringCopper.WeatherState.UNAFFECTED) {
        cir.setReturnValue(InteractionResult.PASS);
      }
    }
  }
}
