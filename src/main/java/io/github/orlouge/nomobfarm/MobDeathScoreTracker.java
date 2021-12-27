package io.github.orlouge.nomobfarm;

public interface MobDeathScoreTracker {
    void increaseMobDeathScore();

    void tickMobDeathScore();

    boolean acceptableMobDeathScore();
}
