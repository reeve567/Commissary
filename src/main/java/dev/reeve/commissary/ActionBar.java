package dev.reeve.commissary;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar {
	private final PacketPlayOutChat packet;
	
	public ActionBar(String text) {
		packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
	}
	
	public void sendToPlayer(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
