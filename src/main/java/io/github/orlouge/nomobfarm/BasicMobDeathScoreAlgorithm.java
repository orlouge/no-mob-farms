package io.github.orlouge.nomobfarm;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Date;
import java.util.function.Function;

public class BasicMobDeathScoreAlgorithm extends MobDeathScoreAlgorithm {
    private final Function<LivingEntity, Integer> slowdownFunction;
    private final int maxWait;
    private final float recoveryRate;
    private final float minDeaths;
    private final float maxDeaths;
    private final long offlinePersistence;
    private final BasicMobDeathScoreAlgorithmNotify notified;

    private float mobDeathCount = 0;
    private int ticksUntilNextSpawn = 0;
    private int lastTicksUntilNextSpawn = 0;
    private boolean running = false;

    public BasicMobDeathScoreAlgorithm(Function<LivingEntity, Integer> slowdownFunction, int maxWait, float recoveryRate, int minDeaths, int maxDeaths, long offlinePersistence, BasicMobDeathScoreAlgorithmNotify notified) {
        this.slowdownFunction = slowdownFunction;
        this.maxWait = maxWait;
        this.recoveryRate = recoveryRate;
        this.minDeaths = (float) minDeaths;
        this.maxDeaths = (float) maxDeaths;
        this.notified = notified;
        this.offlinePersistence = offlinePersistence;
    }

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths, int maxDeaths, long offlinePersistence, BasicMobDeathScoreAlgorithmNotify notified) {
        this((e) -> slowdownRate, maxWait, recoveryRate, minDeaths, maxDeaths, offlinePersistence, notified);
    }

    public BasicMobDeathScoreAlgorithm(int slowdownRate, int maxWait, float recoveryRate, int minDeaths, int maxDeaths, long offlinePersistence) {
        this(slowdownRate, maxWait, recoveryRate, minDeaths, maxDeaths, offlinePersistence, null);
    }

    @Override
    public void signalDeath(LivingEntity entity) {
        mobDeathCount = Float.min(1000000, mobDeathCount + 1);
        if (!running && mobDeathCount >= minDeaths) {
            running = true;
            notified.notifyLargeScoreChange();
        }
        if (running) {
            ticksUntilNextSpawn += slowdownFunction.apply(entity) * (int) Float.min(100, mobDeathCount);
            ticksUntilNextSpawn = Integer.min(maxWait, ticksUntilNextSpawn);
        }
        NoMobFarmMod.LOGGER.info(Float.toString(mobDeathCount) + "," + Integer.toString(ticksUntilNextSpawn));
    }

    @Override
    public void tick() {
        if (!running || (maxDeaths > 0 && mobDeathCount >= maxDeaths)) {
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
