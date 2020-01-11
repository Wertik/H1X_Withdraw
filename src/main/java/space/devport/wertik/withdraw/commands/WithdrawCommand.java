package space.devport.wertik.withdraw.commands;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import space.devport.wertik.withdraw.Main;

public class WithdrawCommand implements CommandExecutor {

    private Main plugin;

    public WithdrawCommand() {
        plugin = Main.inst;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getCfg().getColoredStringList("help"));
            return true;
        }

        if (args[0].equals("reload") && sender.hasPermission("withdraw.admin")) {
            plugin.reload(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getCfg().getColored("only-players").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("withdraw.withdraw")) {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail-perm").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        double value;

        try {
            value = Double.parseDouble(args[0]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail-num").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        if (value < 0) {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail-num").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        if (Main.getEconomy().getBalance(player) < value) {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail-eco").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail-full").replace("%prefix%", plugin.cO.getPrefix()));
            return true;
        }

        EconomyResponse r = Main.getEconomy().withdrawPlayer(player, value);

        if (r.transactionSuccess()) {
            // Create note
            ItemStack item = plugin.getNoteHandler().getNote(value, player.getName());
            player.getInventory().addItem(item);

            player.sendMessage(plugin.getCfg().getColored("withdraw").replace("%prefix%", plugin.cO.getPrefix()).replace("%value%", String.valueOf(value)));
        } else {
            player.sendMessage(plugin.getCfg().getColored("withdraw-fail").replace("%prefix%", plugin.cO.getPrefix()));
        }

        return false;
    }
}
