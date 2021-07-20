package dev.reeve.commissary.listeners;

import dev.reeve.commissary.CommissaryManager;
import dev.reeve.commissary.ItemUtility;
import dev.reeve.commissary.save.SaveData;
import dev.reeve.commissary.save.Tickets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
	public static String name = "Commissary";
	private final CommissaryManager manager;
	private final Tickets tickets;
	private final SaveData saveData;
	
	public GUIListener(CommissaryManager manager, Tickets tickets, SaveData saveData) {
		this.manager = manager;
		this.tickets = tickets;
		this.saveData = saveData;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getName().equals(name)) {
			e.setCancelled(true);
			
			if (e.getRawSlot() % 9 == 0 && e.getRawSlot() >= 0 && e.getRawSlot() <= 27) {
				Player player = (Player) e.getWhoClicked();
				int commissary = (e.getRawSlot() / 9) + 1;
				
				if (player.getInventory().firstEmpty() != -1) {
					if (tickets.get(player.getUniqueId()) >= saveData.get(commissary).entrancePrice) {
						tickets.put(player.getUniqueId(), tickets.get(player.getUniqueId()) - saveData.get(commissary).entrancePrice);
						player.getInventory().addItem(ItemUtility.getTicket(commissary));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought a C" + commissary + " ticket!"));
						player.closeInventory();
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough tickets to buy this!"));
					}
					
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have a full inventory"));
				}
			} else if (e.getRawSlot() % 9 == 8) {
				e.getWhoClicked().closeInventory();
			}
		}
	}
}
