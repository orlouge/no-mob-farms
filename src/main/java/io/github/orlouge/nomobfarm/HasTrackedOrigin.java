package io.github.orlouge.nomobfarm;

public interface HasTrackedOrigin {
    TrackedMobOrigin getOrigin();
    void setOrigin(TrackedMobOrigin origin);
}
