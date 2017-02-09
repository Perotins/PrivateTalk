package me.perotin.privatetalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.perotin.privatetalk.commands.PrivateTalkCommand;
import me.perotin.privatetalk.events.PlayerConversationEvents;
import me.perotin.privatetalk.events.PlayerEvents;

public class PrivateTalk extends JavaPlugin {
	/*
	 * TODO
	 * Fix convos not accepting new people
	 */
	public HashMap<UUID, Conversation> invites;
	public ArrayList<Conversation> convos;
	public HashMap<UUID, Conversation> toggle;
	public static PrivateTalk instance;

	public void onEnable() {
		saveConfig();
		toggle = new HashMap<>();
		invites = new HashMap<>();
		convos = new ArrayList<>();
		instance = this;
		Bukkit.getPluginManager().registerEvents(new PlayerConversationEvents(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
		getCommand("privatetalk").setExecutor(new PrivateTalkCommand());
	}
	
	public void saveConfig(){
		File file = new File(getDataFolder(), "config.yml");
		if(!file.exists()){
			saveDefaultConfig();
			
		} 
	}
}
