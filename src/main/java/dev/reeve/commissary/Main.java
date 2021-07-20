package dev.reeve.commissary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.reeve.commissary.listeners.GUIListener;
import dev.reeve.commissary.listeners.LeaveListener;
import dev.reeve.commissary.listeners.SignListener;
import dev.reeve.commissary.save.CommissaryInfo;
import dev.reeve.commissary.save.SaveData;
import dev.reeve.commissary.save.Tickets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {
	
	private final File configFile = new File(getDataFolder(), "config.json");
	private final File dataFile = new File(getDataFolder(), "data.json");
	private final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Location.class, GsonUtility.locationJsonSerializer)
			.registerTypeAdapter(Location.class, GsonUtility.locationJsonDeserializer)
			.disableHtmlEscaping()
			.create();
	private CommissaryManager manager;
	private SaveData saveData;
	private Tickets tickets;
	
	@Override
	public void onEnable() {
		createFiles();
		
		ItemUtility.setSaveData(saveData);
		
		manager = new CommissaryManager(this, saveData);
		
		new Placeholders(manager, tickets).register();
		
		Bukkit.getPluginManager().registerEvents(new LeaveListener(manager, saveData), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(manager, saveData), this);
		Bukkit.getPluginManager().registerEvents(new GUIListener(manager, tickets, saveData), this);
	}
	
	// teleport players out when server is closing/plugin reloads
	@Override
	public void onDisable() {
		for (int i = 1; i <= CommissaryManager.COMMISSARIES; i++) {
			for (UUID uuid : manager.players.get(i).keySet()) {
				Bukkit.getPlayer(uuid).teleport(saveData.get(i).exitLocation);
			}
		}
		
		try {
			FileWriter writer = new FileWriter(configFile);
			writer.write(gson.toJson(saveData, SaveData.class));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			FileWriter writer = new FileWriter(dataFile);
			writer.write(gson.toJson(tickets, Tickets.class));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getLabel().equals("tickets")) {
			if (args.length == 0) {
				sender.sendMessage("Please choose a valid sub-command: give/take/set/reset");
			} else if (args.length == 1) {
				sender.sendMessage("Please include the arguments for your subcommand");
				sender.sendMessage("/tickets give <player> <amount>");
				sender.sendMessage("/tickets take <player> <amount>");
				sender.sendMessage("/tickets set <player> <amount>");
				sender.sendMessage("/tickets reset <player>");
			} else if (args[0].equalsIgnoreCase("give")) {
				UUID uuid = getPlayerFromString(sender, args[1]);
				if (uuid != null) {
					int amount = tickets.getOrDefault(uuid, 0) + Integer.parseInt(args[2]);
					tickets.put(uuid, amount);
					sender.sendMessage(args[1] + "'s new balance: " + tickets.get(uuid));
					
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Tickets] You have &2gained &6" + args[2] + " &atickets!"));
					}
				}
			} else if (args[0].equalsIgnoreCase("take")) {
				UUID uuid = getPlayerFromString(sender, args[1]);
				if (uuid != null) {
					int amount = tickets.getOrDefault(uuid, 0) - Integer.parseInt(args[2]);
					tickets.put(uuid, amount);
					sender.sendMessage(args[1] + "'s new balance: " + tickets.get(uuid));
					
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Tickets] You have &clost &6" + args[2] + " &atickets!"));
					}
				}
			} else if (args[0].equalsIgnoreCase("set")) {
				UUID uuid = getPlayerFromString(sender, args[1]);
				if (uuid != null) {
					tickets.put(uuid, Integer.valueOf(args[2]));
					sender.sendMessage(args[1] + "'s new balance: " + tickets.get(uuid));
					
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Tickets] You now have &6" + args[2] + " &atickets!"));
					}
				}
			} else if (args[0].equalsIgnoreCase("reset")) {
				UUID uuid = getPlayerFromString(sender, args[1]);
				if (uuid != null) {
					tickets.put(uuid, 0);
					sender.sendMessage(args[1] + "'s new balance: " + tickets.get(uuid));
					
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Tickets] You now have &60 &atickets!"));
					}
				}
			} else {
				sender.sendMessage("Please choose a valid sub-command: give/take/set/reset");
			}
			
			return true;
		} else if (command.getLabel().equals("commissary")) {
			if (!(sender instanceof Player)) return false;
			
			Inventory inventory = Bukkit.createInventory(null, 27, GUIListener.name);
			
			for (int i = 0; i < 27; i++) {
				if (i % 9 == 0) {
					inventory.setItem(i, ItemUtility.getConfirm());
				} else if (i % 9 == 8) {
					inventory.setItem(i, ItemUtility.getCancel());
				} else if (i % 9 == 4) {
					inventory.setItem(i, ItemUtility.getTicket((i / 9) + 1));
				}
			}
			
			((Player) sender).openInventory(inventory);
		} else if (command.getLabel().equals("commissaryset")) {
			if (!(sender instanceof Player)) return false;
			
			if (args.length == 0) {
				sender.sendMessage("Please choose a valid sub-command: exit/entrance/sign/price");
			} else if (args.length == 1) {
				sender.sendMessage("Please include the arguments for your subcommand");
				sender.sendMessage("/commissaryset exit <commissary number>");
				sender.sendMessage("/commissaryset entrance <commissary number>");
				sender.sendMessage("/commissaryset sign <commissary number>");
				sender.sendMessage("/commissaryset price <commissary number> <amount>");
			} else if (args[0].equalsIgnoreCase("exit")) {
				saveData.get(Integer.parseInt(args[1])).exitLocation = ((Player) sender).getLocation();
				sender.sendMessage("Location set");
			} else if (args[0].equalsIgnoreCase("entrance")) {
				saveData.get(Integer.parseInt(args[1])).entranceLocation = ((Player) sender).getLocation();
				sender.sendMessage("Location set");
			} else if (args[0].equalsIgnoreCase("sign")) {
				Block block = ((Player) sender).getTargetBlock((Set<Material>) null, 5);
				
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign) block.getState();
					sign.setLine(0, SignListener.line);
					sign.setLine(1, "C" + args[1]);
					
					sign.update();
				} else {
					sender.sendMessage("This is not a sign");
				}
			} else if (args[0].equalsIgnoreCase("price")) {
				
				saveData.get(Integer.valueOf(args[1])).entrancePrice = Integer.parseInt(args[2]);
				sender.sendMessage("Price is now " + saveData.get(Integer.valueOf(args[1])).entrancePrice);
			} else {
				sender.sendMessage("Please choose a valid sub-command: exit/entrance/sign/price");
			}
		}
		return false;
	}
	
	private void createFiles() {
		if (!configFile.exists()) {
			try {
				getDataFolder().mkdirs();
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			saveData = new SaveData();
			setSaveData();
		} else {
			try {
				saveData = gson.fromJson(new FileReader(configFile), SaveData.class);
				if (saveData == null) saveData = new SaveData();
				setSaveData();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if (!dataFile.exists()) {
			try {
				getDataFolder().mkdirs();
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			tickets = new Tickets();
		} else {
			try {
				tickets = gson.fromJson(new FileReader(dataFile), Tickets.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setSaveData() {
		if (saveData.get(1) == null) {
			saveData.put(1, new CommissaryInfo());
			saveData.put(2, new CommissaryInfo());
			saveData.put(3, new CommissaryInfo());
		}
	}
	
	private UUID getPlayerFromString(CommandSender sender, String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null) {
			sender.sendMessage("Could not find that player");
			return null;
		} else return player.getUniqueId();
	}
}
