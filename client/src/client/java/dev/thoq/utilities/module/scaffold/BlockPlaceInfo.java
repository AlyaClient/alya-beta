/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.utilities.module.scaffold;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockPlaceInfo {
    public final BlockPos pos;
    public final Direction side;
    public final Vec3d hitVec;

    public BlockPlaceInfo(BlockPos pos, Direction side, Vec3d hitVec) {
        this.pos = pos;
        this.side = side;
        this.hitVec = hitVec;
    }
}
