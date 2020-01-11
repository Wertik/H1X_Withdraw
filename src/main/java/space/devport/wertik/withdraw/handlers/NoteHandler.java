package space.devport.wertik.withdraw.handlers;

import org.bukkit.inventory.ItemStack;
import space.devport.wertik.withdraw.Main;
import space.devport.wertik.withdraw.util.ItemBuilder;
import space.devport.wertik.withdraw.util.NBTEditor;

public class NoteHandler {

    private Main plugin;

    public NoteHandler() {
        plugin = Main.inst;
    }

    public boolean isNote(ItemStack item) {
        return NBTEditor.hasNBT(item) && NBTEditor.hasNBTTag(item, "withdraw_note");
    }

    public double getValue(ItemStack note) {
        if (NBTEditor.hasNBTTag(note, "withdraw_note")) {
            try {
                return Double.parseDouble(NBTEditor.getNBT(note, "withdraw_note"));
            } catch (IllegalArgumentException | NullPointerException e) {
                plugin.cO.err("Could not parse NBT correctly.. contact the dev.");
            }
        }

        return -1;
    }

    public ItemStack getNote(double value, String playerName) {
        ItemBuilder noteB = ItemBuilder.loadBuilder(plugin.getConfig(), "withdraw-item")
                .parse("%player%", playerName).parse("%value%", String.valueOf(value))
                .addNBT("withdraw_note", String.valueOf(value));

        return noteB.build();
    }
}
