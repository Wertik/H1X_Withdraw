package space.devport.wertik.withdraw.listeners;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import space.devport.wertik.withdraw.Main;

public class NoteListener implements Listener {

    private Main plugin;

    public NoteListener() {
        plugin = Main.inst;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.PHYSICAL) && !e.getAction().equals(Action.LEFT_CLICK_AIR) && !e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (e.getItem() == null)
                return;

            if (plugin.getNoteHandler().isNote(e.getItem())) {
                Player player = e.getPlayer();

                double value = plugin.getNoteHandler().getValue(e.getItem());

                if (value > 0) {

                    // Permission check
                    if (!player.hasPermission("withdraw.redeem")) {
                       player.sendMessage(plugin.getCfg().getColored("redeem-fail-perm").replace("%prefix%", plugin.cO.getPrefix()));
                       return;
                    }

                    // Add balance
                    EconomyResponse r = Main.getEconomy().depositPlayer(player, value);

                    if (r.transactionSuccess()) {
                        // Add balance & destroy note
                        if (e.getItem().getAmount() > 1)
                            e.getItem().setAmount(e.getItem().getAmount() - 1);
                        else
                            player.getInventory().remove(player.getInventory().getItemInHand());

                        player.sendMessage(plugin.getCfg().getColored("redeem").replace("%prefix%", plugin.cO.getPrefix())
                                .replace("%value%", String.valueOf(value)));
                    } else {
                        player.sendMessage(plugin.getCfg().getColored("redeem-fail").replace("%prefix%", plugin.cO.getPrefix()));
                    }
                }
            }
        }
    }
}
