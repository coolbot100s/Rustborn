package cool.bot.rustborn;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

// This essentially prevents the "double death" crash that occurs in the Vanilla game when a player dies in an instanced world. (go test for yourself, it's weird!)
// I do this by simply draining players out and unkilling the player, which makes it look like the players simply woke up at the portal entrance when someone died.
// A weird side effect is that the instanced world still breaks down pretty terribly, and portals become uninteractable... until a player dies again...?
// But hey, it's significantly better than the Vanilla behavior!

public class PlayerSaverSystem extends DeathSystems.OnDeathSystem {
    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and();
    }
    @Override
    public void onComponentAdded(@Nonnull Ref ref, @Nonnull DeathComponent component, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        if (store.getComponent(ref, Player.getComponentType()) == null) {
            return;
        }

        Player playerComponent = (Player) store.getComponent(ref, Player.getComponentType());
        assert playerComponent != null;

        World world = playerComponent.getWorld();

        if (world.getName().equals(World.DEFAULT)) {
            return;
        }

        //world.sendMessage(Message.raw("someone died so ya'll are getting sent back!"));
        world.execute(() -> world.drainPlayersTo(Universe.get().getWorld(World.DEFAULT)));

        commandBuffer.tryRemoveComponent(ref, DeathComponent.getComponentType());
    }
}

