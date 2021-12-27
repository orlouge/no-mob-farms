package io.github.orlouge.nomobfarm;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class BasicMobDeathScoreAlgorithm extends MobDeathScoreAlgorithm {
    private final int slowdownRate;
    private final int maxWait;
    private final float recoveryRate;
    private final float minDeaths;
    private BasicMobDeathScoreAlgorithmNotify notified;
    private float mobDeathCount = 0;
    private int ticksUntilNextSpawn = 0;
    private int lastTicksUntilNextSpawn = 0;
    private boolean running = false;

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths, BasicMobDeathScoreAlgorithmNotify notified) {
        this.slowdownRate = slowdownRate;
        this.maxWait = maxWait;
        this.recoveryRate = recoveryRate;
        this.minDeaths = (float) minDeaths;
        this.notified = notified;
    }

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths) {
        this(slowdownRate, maxWait, recoveryRate, minDeaths,null);
    }

    @Override
    public void signalDeath() {
        mobDeathCount = Float.min(1000000, mobDeathCount + 1);
        if (!running && mobDeathCount >= minDeaths) {
            running = true;
        }
        if (running) {
            ticksUntilNextSpawn += slowdownRate * (int) Float.min(100, mobDeathCount);
            ticksUntilNextSpawn = Integer.min(maxWait, ticksUntilNextSpawn);
        }
        NoMobFarmMod.LOGGER.info(Float.toString(mobDeathCount) + "," + Integer.toString(ticksUntilNextSpawn));
    }

    @Override
    public void tick() {
        if (!running) {
            return;
        }

        ticksUntilNextSpawn = Integer.max(0, ticksUntilNextSpawn - 1);
        mobDeathCount = Float.max(0, mobDeathCount - recoveryRate);

        if (notified != null && Math.abs(ticksUntilNextSpawn - lastTicksUntilNextSpawn) > 1000) {
            notified.notifyLargeScoreChange();
            lastTicksUntilNextSpawn = ticksUntilNextSpawn;
        }
    }

    @Override
    public boolean acceptableScore() {
        return ticksUntilNextSpawn < 10;
    }

    @Override
    public void readNbt(NbtCompound nbtCompound) {
        if (nbtCompound.contains("NoMobFarmsData")) {
            NbtCompound data = nbtCompound.getCompound("NoMobFarmsData");
            if (data.contains("ticksUntilNextSpawn")) {
                ticksUntilNextSpawn = data.getInt("ticksUntilNextSpawn");
                lastTicksUntilNextSpawn = ticksUntilNextSpawn;
            }
            if (data.contains("mobDeathCount")) {
                mobDeathCount = data.getFloat("mobDeathCount");

                if (mobDeathCount > 0 || lastTicksUntilNextSpawn > 0) {
                    running = true;
                }
            }
        }
        //NoMobFarmMod.LOGGER.info("read: " + Float.toString(mobDeathCount) + "," + Integer.toString(ticksUntilNextSpawn));
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound) {
        //NoMobFarmMod.LOGGER.info("write: " + Float.toString(mobDeathCount) + "," + Integer.toString(ticksUntilNextSpawn));
        NbtCompound data = nbtCompound.getCompound("NoMobFarmsData");

        data.putInt("ticksUntilNextSpawn", ticksUntilNextSpawn);
        data.putFloat("mobDeathCount", mobDeathCount);

        if (!nbtCompound.contains("NoMobFarmsData")) {
            nbtCompound.put("NoMobFarmsData", (NbtElement) data);
        }
    }

    public interface BasicMobDeathScoreAlgorithmNotify {
        public void notifyLargeScoreChange();
    }
}
