package io.github.orlouge.nomobfarm;

import net.minecraft.nbt.NbtCompound;

public abstract class MobDeathScoreAlgorithm {
    public abstract void signalDeath();
    public abstract void tick();
    public abstract boolean acceptableScore();

    public abstract void readNbt(NbtCompound nbtCompound);
    public abstract void writeNbt(NbtCompound nbtCompound);
}
