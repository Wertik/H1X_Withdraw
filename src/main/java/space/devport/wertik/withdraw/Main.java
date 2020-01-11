package space.devport.wertik.withdraw;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.wertik.withdraw.commands.WithdrawCommand;
import space.devport.wertik.withdraw.handlers.NoteHandler;
import space.devport.wertik.withdraw.listeners.NoteListener;

public class Main extends JavaPlugin {

    public static Main inst;
    public ConsoleOutput cO;

    private Configuration config;

    private static Economy econ = null;

    private NoteHandler noteHandler;

    public NoteHandler getNoteHandler() {
        return noteHandler;
    }

    @Override
    public void onEnable() {
        inst = this;

        cO = new ConsoleOutput(this);

        config = new Configuration(this, "config");

        cO.setDebug(config.getYaml().getBoolean("enable-debug"));
        cO.setPrefix(config.getColored("plugin-prefix"));

        if (!setupEconomy()) {
            cO.err("Vault not found, cannot function.. disabling.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        noteHandler = new NoteHandler();

        getCommand("withdraw").setExecutor(new WithdrawCommand());
        getServer().getPluginManager().registerEvents(new NoteListener(), this);
    }

    public void reload(CommandSender s) {
        long start = System.currentTimeMillis();

        cO.setReloadSender(s);

        config.reload();

        cO.setDebug(config.getYaml().getBoolean("enable-debug"));
        cO.setPrefix(config.getColored("plugin-prefix"));

        cO.setReloadSender(null);

        s.sendMessage("§aDone.. reload took " + (System.currentTimeMillis() - start) + " ms.");
    }

    @Override
    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public Configuration getCfg() {
        return config;
    }

    @Override
    public FileConfiguration getConfig() {
        return config.getYaml();
    }
}
