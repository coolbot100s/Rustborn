package cool.bot.rustborn;

import com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.SensorHasHostileTargetMemory;
import com.hypixel.hytale.builtin.npccombatactionevaluator.evaluator.CombatActionEvaluator;
import com.hypixel.hytale.builtin.npccombatactionevaluator.memory.TargetMemory;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.role.Role;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cool.bot.rustborn.Rustborn.LOGGER;

public class RustbornHeadSpawnSystem extends RefChangeSystem<EntityStore, DeathComponent> {




    @NotNull
    @Override
    public ComponentType<EntityStore, DeathComponent> componentType() {
        return DeathComponent.getComponentType();
    }

    @Override
    public void onComponentAdded(@NotNull Ref<EntityStore> ref, @NotNull DeathComponent deathComponent,
                                 @NotNull Store<EntityStore> entityStore, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        ref.validate();
        //LOGGER.atInfo().log("DeathComponent Added for %s", ref);

        // Use this to determine entity
        PersistentModel persistentModel = entityStore.getComponent(ref, PersistentModel.getComponentType());
        String modelName = "";
        if (persistentModel != null) {
            modelName = persistentModel.getModelReference().getModelAssetId();
        }

        // Filter to Rustborn Skeletons that aren't floating heads.
        if (modelName.contains("Skeleton_Rustborn_") && !modelName.contains("Skeleton_Rustborn_Head")) {

            // Get the transform from the dying entity so we can spawn the head at the same location.
            TransformComponent transform = entityStore.getComponent(ref, TransformComponent.getComponentType());
            if (transform == null) {
                return;
            }

            // Use flock membership to determine if this is a summoned Rustborn.
            World world = entityStore.getExternalData().getWorld();
            FlockMembership flockMembership;
            if (entityStore.getComponent(ref, FlockMembership.getComponentType()) != null) {
                flockMembership = entityStore.getComponent(ref, FlockMembership.getComponentType());
            } else {
                flockMembership = null;
            }

            //LOGGER.atInfo().log("Archetype for %s: %s", ref, entityStore.getArchetype(ref));

            // Spawn the NPC on the world thread
            world.execute(() -> {
                // Don't spawn a head if the Rustborn is a summoned one.
                if (flockMembership == null) {
                    // Head
                    Pair<Ref<EntityStore>, INonPlayerCharacter> head = NPCPlugin.get().spawnNPC(
                            entityStore,
                            "Skeleton_Rustborn_Head",
                            null,
                            transform.getPosition(),
                            transform.getRotation()
                    );
                }
            });
        }

    }

    @Override
    public void onComponentSet(@NotNull Ref<EntityStore> var1, @Nullable DeathComponent var2, @NotNull DeathComponent var3, @NotNull Store<EntityStore> var4, @NotNull CommandBuffer<EntityStore> var5) {
        //var1.validate();
        //LOGGER.atInfo().log("DeathComponent Set for %s", var1);
    }

    @Override
    public void onComponentRemoved(@NotNull Ref<EntityStore> var1, @NotNull DeathComponent var2, @NotNull Store<EntityStore> var3, @NotNull CommandBuffer<EntityStore> var4) {
        //var1.validate();
        //LOGGER.atInfo().log("DeathComponent Removed for %s", var1);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
