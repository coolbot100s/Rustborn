package cool.bot.rustborn;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class Rustborn extends JavaPlugin {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Rustborn(JavaPluginInit init) {
        super(init);
    //    LOGGER.atInfo().log("Heyoooo from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        this.getEntityStoreRegistry().registerSystem(new RustbornHeadSpawnSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerSaverSystem());

    }
}
