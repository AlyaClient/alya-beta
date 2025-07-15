package works.alya.module.impl.movement.step;

import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class StepModule extends Module {

    private static final NumberSetting<Integer> stepHeight = new NumberSetting<>("Height", "Maximum step height", 2, 1, 10);

    public StepModule() {
        super("Step", "Allows you to step up blocks higher than normal", ModuleCategory.MOVEMENT);
        addSetting(stepHeight);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null || !event.isPre() || mc.world == null) return;

        if(mc.player.horizontalCollision && mc.player.isOnGround()) {
            Vec3d playerPos = mc.player.getPos();
            Vec3d lookDirection = mc.player.getRotationVec(1.0f);

            BlockPos frontBlock = new BlockPos((int)(playerPos.x + lookDirection.x), (int)playerPos.y, (int)(playerPos.z + lookDirection.z));

            if(!mc.world.getBlockState(frontBlock).isAir()) {
                int targetHeight = findStepHeight(frontBlock);

                if(targetHeight > 0 && targetHeight <= stepHeight.getValue()) {
                    double newY = frontBlock.getY() + targetHeight;
                    if(newY - playerPos.y <= stepHeight.getValue()) {
                        mc.player.setPosition(playerPos.x, newY, playerPos.z);
                    }
                }
            }
        }
    };

    private int findStepHeight(BlockPos startPos) {
        if(mc.world == null || mc.player == null) return 0;

        for(int height = 1; height <= stepHeight.getValue(); height++) {
            BlockPos checkPos = startPos.up(height);
            BlockPos abovePos = checkPos.up();

            if(mc.world.getBlockState(checkPos).isAir() && mc.world.getBlockState(abovePos).isAir()) {
                Box playerBox = mc.player.getBoundingBox().offset(0, height, 0);
                if(mc.world.isSpaceEmpty(mc.player, playerBox)) {
                    return height;
                }
            }
        }
        return 0;
    }
}