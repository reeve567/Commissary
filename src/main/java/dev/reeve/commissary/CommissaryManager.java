package dev.reeve.commissary;

import dev.reeve.commissary.save.SaveData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CommissaryManager {
	public static int COMMISSARIES = 3;
	private final Main main;
	private final SaveData saveData;
	public HashMap<Integer, HashMap<UUID, Integer>> players = new HashMap<>();
	
	public CommissaryManager(Main main, SaveData saveData) {
		this.main = main;
		this.saveData = saveData;
		
		players.put(1, new HashMap<>());
		players.put(2, new HashMap<>());
		players.put(3, new HashMap<>());
	}
	
	public void addPlayer(int commissary, UUID player) {
		if (commissary != 1 && commissary != 2 && commissary != 3) {
			System.err.println("Wrong commissary number, " + commissary);
			return;
		}
		
		players.get(commissary).put(player, 120);
		
		new ActionBar(ChatColor.translateAlternateColorCodes('&', "&aC" + commissary + " timer: &c" + 120 + "&as")).sendToPlayer(Bukkit.getPlayer(player));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (players.get(commissary).containsKey(player)) {
					int value = players.get(commissary).get(player);
					players.get(commissary).put(player, Math.max(value - 1, 0));
					value = players.get(commissary).get(player);
					
					new ActionBar(ChatColor.translateAlternateColorCodes('&', "&aC" + commissary + " timer: &c" + value + "&as")).sendToPlayer(Bukkit.getPlayer(player));
					
					if (value == 0) {
						players.get(commissary).remove(player);
						Bukkit.getPlayer(player).teleport(saveData.get(commissary).exitLocation);
						this.cancel();
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(main, 20, 20);
	}
}
