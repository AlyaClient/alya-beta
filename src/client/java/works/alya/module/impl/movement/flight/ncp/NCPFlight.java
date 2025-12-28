/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.module.impl.movement.flight.ncp;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.SubModule;
import works.alya.utilities.player.MoveUtility;

public final class NCPFlight extends SubModule {
    static boolean messageSent = false;
    static boolean clipped = false;

    public NCPFlight(Module parent) {
        super("NCP", parent);
    }

    @SuppressWarnings("unused")
    private final IEventListener<MotionEvent> motionEvent = event -> {
        if(mc.player == null) return;
        if(mc.world == null) return;
        if(!event.isPre()) return;

        Vec3d pos = mc.player.getPos();
        BlockPos blockAbovePlayer = new BlockPos((int)pos.x, (int)(pos.y + mc.player.getHeight() + 0.5), (int)pos.z);
        boolean isBlockAbovePlayer = !mc.world.getBlockState(blockAbovePlayer).isAir();

        if(!clipped) {
            mc.player.setPosition(pos.x, pos.y + 0.1, pos.z);
            MoveUtility.setMotionY(0.2);
            clipped = true;
        }

        MoveUtility.setSpeed(2.0f, true);
        MoveUtility.setMotionY(-0.2);
    };

    @Override
    public void reset() {
        super.reset();

        messageSent = false;
        clipped = false;
    }
}
