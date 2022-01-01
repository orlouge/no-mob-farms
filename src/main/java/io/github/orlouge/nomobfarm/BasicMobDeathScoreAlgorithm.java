package io.github.orlouge.nomobfarm;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Date;

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
    private long offlinePersistence = 3 * 24 * 60 * 60;

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths, long offlinePersistence, BasicMobDeathScoreAlgorithmNotify notified) {
        this.slowdownRate = slowdownRate;
        this.maxWait = maxWait;
        this.recoveryRate = recoveryRate;
        this.minDeaths = (float) minDeaths;
        this.notified = notified;
        this.offlinePersistence = offlinePersistence;
    }

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths, long offlinePersistence) {
        this(slowdownRate, maxWait, recoveryRate, minDeaths, offlinePersistence, null);
    }

    @Override
    public void signalDeath() {
        mobDeathCount = Float.min(1000000, mobDeathCount + 1);
        if (!running && mobDeathCount >= minDeaths) {
            running = true;
            notified.notifyLargeScoreChange();
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
            if (data.contains("whenWritten")) {
                if (new Date(data.getLong("whenWritten") + offlinePersistence * 1000).before(new Date())) {
                    if (ticksUntilNextSpawn >= 1000 || mobDeathCount >= 1) {
                        notified.notifyLargeScoreChange();
                    }
                    return;
                }
            }
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
        data.putLong("whenWritten", (new Date()).getTime());

        if (!nbtCompound.contains("NoMobFarmsData")) {
            nbtCompound.put("NoMobFarmsData", (NbtElement) data);
        }
    }

    public interface BasicMobDeathScoreAlgorithmNotify {
        public void notifyLargeScoreChange();
    }
}
