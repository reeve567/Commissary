package dev.reeve.commissary.listeners;

import dev.reeve.commissary.CommissaryManager;
import dev.reeve.commissary.ItemUtility;
import dev.reeve.commissary.save.SaveData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
	
	public static String line = ChatColor.translateAlternateColorCodes('&', "&5[Commissary]");
	private final CommissaryManager manager;
	private final SaveData saveData;
	
	public SignListener(CommissaryManager manager, SaveData saveData) {
		this.manager = manager;
		this.saveData = saveData;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WALL_SIGN) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(0).equals(line)) {
					String line2 = sign.getLine(1);
					int commissary = Integer.parseInt(line2.substring(1));
					
					if (event.getItem() != null && event.getItem().isSimilar(ItemUtility.getTicket(commissary))) {
						if (event.getItem().getAmount() == 1) {
							event.getPlayer().getInventory().remove(event.getItem());
						} else {
							event.getItem().setAmount(event.getItem().getAmount() - 1);
						}
						
						manager.addPlayer(commissary, event.getPlayer().getUniqueId());
						event.getPlayer().teleport(saveData.get(commissary).entranceLocation);
					} else {
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat's not a commissary ticket."));
					}
				}
			}
		}
	}
}
