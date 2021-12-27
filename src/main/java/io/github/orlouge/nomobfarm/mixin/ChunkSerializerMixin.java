package io.github.orlouge.nomobfarm.mixin;

import io.github.orlouge.nomobfarm.TrackedMobOrigin;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "deserialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/poi/PointOfInterestStorage;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/world/chunk/ProtoChunk;",
            at = @At("RETURN"))
    private static void onDeserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        ProtoChunk protoChunk = cir.getReturnValue();
        Chunk chunk;

        if (protoChunk instanceof ReadOnlyChunk) {
            chunk = ((ReadOnlyChunk) protoChunk).getWrappedChunk();
        } else {
            chunk = (Chunk) protoChunk;
        }

        if (chunk instanceof TrackedMobOrigin) {
            ((TrackedMobOrigin) chunk).getMobDeathScoreAlgorithm().readNbt(nbt);
        }
    }

    @ModifyVariable(method = "serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/NbtCompound;",
                    at = @At("STORE"),
                    ordinal = 0)
    private static NbtCompound onSerialize(NbtCompound nbt, ServerWorld world, Chunk chunk) {
        if (chunk instanceof TrackedMobOrigin) {
            ((TrackedMobOrigin) chunk).getMobDeathScoreAlgorithm().writeNbt(nbt);
        }
        return nbt;
    }
}
