package com.github.touhoumaidaffection.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidBegTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(MaidBegTask.class)
public class MaidBegTaskMixin {
    private static final Logger TMA_LOGGER = LoggerFactory.getLogger("touhou_maid_affection/mixin");
    private static final double BEG_RANGE = 6.0D;

    @Inject(method = "checkExtraStartConditions", at = @At("RETURN"), cancellable = true, remap = false)
    private void onCheckExtraStartConditions(ServerLevel level, EntityMaid owner, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }

        boolean offhandMatched = owner.level().getEntitiesOfClass(Player.class, owner.getBoundingBox().inflate(BEG_RANGE),
                player -> owner.hasLineOfSight(player) && owner.getTemptationItem().test(player.getOffhandItem()))
                .stream()
                .findAny()
                .isPresent();

        if (offhandMatched) {
            TMA_LOGGER.info("Offhand temptation fallback matched for MaidBegTask; enabling beg behavior.");
            cir.setReturnValue(true);
        }
    }
}
