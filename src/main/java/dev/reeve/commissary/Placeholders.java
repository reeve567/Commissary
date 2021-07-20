package dev.reeve.commissary;

import dev.reeve.commissary.save.Tickets;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {
	private CommissaryManager manager;
	private final Tickets tickets;
	
	public Placeholders(CommissaryManager manager, Tickets tickets) {
		this.manager = manager;
		this.tickets = tickets;
	}
	
	@Override
	public String getIdentifier() {
		return "commissary";
	}
	
	@Override
	public String getAuthor() {
		return "Xwy";
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public boolean persist() {
		return true;
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if (player == null) return "";
		
		if (identifier.equals("tickets")) {
			return tickets.getOrDefault(player.getUniqueId(), 0).toString();
		} else if (identifier.equals("time")) {
			for (int i = 1; i <= CommissaryManager.COMMISSARIES; i++) {
				if (manager.players.get(i).containsKey(player.getUniqueId())) {
					return String.valueOf(manager.players.get(i).get(player.getUniqueId()));
				}
			}
			return "0";
		}
		
		return null;
	}
}
