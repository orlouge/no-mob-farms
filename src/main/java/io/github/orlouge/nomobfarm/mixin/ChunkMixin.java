package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.BasicMobDeathScoreAlgorithm;
import io.github.orlouge.nomobfarm.MobDeathScoreAlgorithm;
import io.github.orlouge.nomobfarm.TrackedMobOrigin;
import io.github.orlouge.nomobfarm.NoMobFarmMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.world.chunk.Chunk.class)
public abstract class ChunkMixin implements TrackedMobOrigin, BasicMobDeathScoreAlgorithm.BasicMobDeathScoreAlgorithmNotify {
    @Shadow public abstract void setShouldSave(boolean shouldSave);

    protected MobDeathScoreAlgorithm deathScore = new BasicMobDeathScoreAlgorithm(NoMobFarmMod.NATURAL_SLOWDOWN_RATE,
                                                                                  NoMobFarmMod.NATURAL_MAX_WAIT,
                                                                                  NoMobFarmMod.NATURAL_RECOVERY_RATE,
                                                                                  NoMobFarmMod.NATURAL_MIN_DEATHS,
                                                                                  NoMobFarmMod.NATURAL_MAX_DEATHS,
                                                                                  NoMobFarmMod.NATURAL_OFFLINE_PERSISTENCE,
                                                                          this);
    @Override
    public MobDeathScoreAlgorithm getMobDeathScoreAlgorithm() {
        return deathScore;
    }

    @Override
    public void notifyLargeScoreChange() {
        this.setShouldSave(true);
    }
}
