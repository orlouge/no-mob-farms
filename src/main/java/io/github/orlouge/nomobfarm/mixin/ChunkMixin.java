package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.MobDeathScoreTracker;
import org.spongepowered.asm.mixin.Mixin;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import io.github.orlouge.nomobfarm.NoMobFarmMod;

@Mixin(net.minecraft.world.chunk.Chunk.class)
public class ChunkMixin implements MobDeathScoreTracker {

    private float mobDeathCount = 0;
    private int ticksUntilNextSpawn = 0;

    @Override
    public void increaseMobDeathScore() {
        mobDeathCount = Float.min(100, mobDeathCount + 1);
        ticksUntilNextSpawn += NoMobFarmMod.SLOWDOWN_RATE * (int) mobDeathCount;
        ticksUntilNextSpawn = Integer.min(NoMobFarmMod.MAX_WAIT, ticksUntilNextSpawn);
        NoMobFarmMod.LOGGER.info(Float.toString(mobDeathCount) + "," + Integer.toString(ticksUntilNextSpawn));
    }

    @Override
    public void tickMobDeathScore() {
        ticksUntilNextSpawn = Integer.max(0, ticksUntilNextSpawn - 1);
        mobDeathCount = Float.max(0, mobDeathCount - NoMobFarmMod.RECOVERY_RATE);
    }

    @Override
    public boolean acceptableMobDeathScore() {
        return ticksUntilNextSpawn < 10;
    }
}
