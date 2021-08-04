package dev.reeve.commissary;

import dev.reeve.commissary.save.SaveData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtility {
	private static SaveData saveData;
	
	public static ItemStack getCancel() {
		ItemStack stack = new ItemStack(Material.WOOL);
		stack.setDurability((short) 14);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(translate("&cCancel"));
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack getConfirm() {
		ItemStack stack = new ItemStack(Material.WOOL);
		stack.setDurability((short) 5);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(translate("&aConfirm"));
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack getTicket(int commissary) {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = stack.getItemMeta();
		
		meta.setDisplayName(translate("&5C" + commissary + " Shop"));
		ArrayList<String> lore = new ArrayList<>();
		lore.add(translate("&2Temporary access to C" + commissary + " shop."));
		lore.add(translate("&4Costs " + saveData.get(commissary).entrancePrice + " tickets."));
		
		meta.setLore(lore);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static void setSaveData(SaveData saveData) {
		ItemUtility.saveData = saveData;
	}
	
	private static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
