package dev.reeve.commissary.listeners;

import dev.reeve.commissary.CommissaryManager;
import dev.reeve.commissary.save.SaveData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    private CommissaryManager manager;
    private SaveData saveData;

    public LeaveListener(CommissaryManager manager, SaveData saveData) {
        this.manager = manager;
        this.saveData = saveData;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        for (int i = 1; i <= CommissaryManager.COMMISSARIES; i++) {
            if (manager.players.get(i).containsKey(event.getPlayer().getUniqueId())) {
                manager.players.get(i).remove(event.getPlayer().getUniqueId());
                event.getPlayer().teleport(saveData.get(i).exitLocation);
            }
        }
    }
}
