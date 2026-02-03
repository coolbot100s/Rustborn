package cool.bot.rustborn;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
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

        // Filter to Rustborn Skeletons that aren't floating heads or soulless (summoned) Rustborn Knights.
        if (!modelName.contains("Rustborn_") || modelName.contains("Skeleton_Rustborn_Head") || modelName.contains("Risen_Rustborn_Knight")) {
            return;
        }

        // Get the transform from the dying entity so we can spawn the head at the same location.
        TransformComponent transform = entityStore.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return;
        }

        // pick between crawler or head, or use head if this is already a crawler.
        String npcType;
        if (modelName.contains("Rustborn_Crawler")) {
            npcType = "Skeleton_Rustborn_head";
        } else {
            npcType = Math.random() < 0.35 ? "Rustborn_Crawler" : "Skeleton_Rustborn_Head";
        }

        // Spawn the NPC on the world thread
        World world = entityStore.getExternalData().getWorld();
        world.execute(() -> {
                    Pair<Ref<EntityStore>, INonPlayerCharacter> scrapHeap = NPCPlugin.get().spawnNPC(
                            entityStore,
                            npcType,
                            null,
                            transform.getPosition(),
                            transform.getRotation()
                    );
                }
        );
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
