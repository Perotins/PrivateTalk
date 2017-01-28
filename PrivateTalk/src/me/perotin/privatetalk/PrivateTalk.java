package me.perotin.privatetalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.perotin.privatetalk.commands.PrivateTalkCMD;

public class PrivateTalk extends JavaPlugin implements Listener{


	/*
	 * TODO list
	 */
	public static PrivateTalk priv;

	public HashMap<Player, Conversation>toggle = new HashMap<>();

	public ArrayList<Conversation>convos;

	public HashSet<Player>toRemove = new HashSet<>();
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		convos = new ArrayList<>();
		priv = this;
		getCommand("pt").setExecutor(new PrivateTalkCMD());
		getCommand("privatetalk").setExecutor(new PTCatcher());
		Bukkit.getPluginManager().registerEvents(new Speak(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitConvo(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		saveConfig();
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				for(Conversation c : convos){
					if(c.getMembers().size() == 0){
						Bukkit.broadcastMessage("deleted " + c.getName());
						c.delete();
					}
				}
			}
			
		}, 0, 20*20);


	}
	public void saveConfig(){
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
		}else{
			saveConfig();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

			@Override
			public void run() {
				if (toRemove.contains(p)) {
					toRemove.remove(p);
					p.sendMessage("joined in time");
				}else{
					p.sendMessage("fired1");
				}
				
			}
			
		}, 20*3);
				
			}
		
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player p = e.getPlayer();
		for(Conversation c : convos){
			if(!c.playerInConversation(p))return;
			else{
				toRemove.add(p);
				Bukkit.broadcastMessage(toRemove.contains(p) + " .");
				Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){

					@Override
					public void run() {
						if (toRemove.contains(p)) {
							if (new Conversation().getConversation(p).playerInConversation(p)) {
								toRemove.remove(p);
								Bukkit.getPluginManager().callEvent(new PlayerLeaveConversationEvent(p, new Conversation().getConversation(p)));
								new Conversation().getConversation(p).remove(p);
								Bukkit.broadcastMessage("removed offline player"); 
							}
						}
					}

				}, 20*60*5);
			}
		}
	}


}
