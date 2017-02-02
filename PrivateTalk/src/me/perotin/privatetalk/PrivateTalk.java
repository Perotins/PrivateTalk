package me.perotin.privatetalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.perotin.privatetalk.commands.PrivateTalkCMD;

public class PrivateTalk extends JavaPlugin implements Listener {

	public static PrivateTalk instance;

	public HashMap<String, Conversation> toggle = new HashMap<>();
	private HashMap<String, Conversation> toRemove = new HashMap<>();
	public ArrayList<Conversation> convos;

	private int timeToKick = getConfig().getInt("time-to-join-back");

	@Override
	public void onEnable() {
		instance = this;

		if (timeToKick < 1) {
			getLogger().severe("Config is not set up properly! Make sure you insert a positive digit in (time-to-join-back)");
			timeToKick = 5;
		}

		convos = new ArrayList<>();
		getCommand("pt").setExecutor(new PrivateTalkCMD());
		getCommand("privatetalk").setExecutor(new PTCatcher());
		Bukkit.getPluginManager().registerEvents(new Speak(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitConvo(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		saveConfig();

		new BukkitRunnable() {

			@Override
			public void run() {
				for (Conversation c : convos) {
					if (c.getMembers().size() == 0) {
						c.delete();
					}
				}
			}

		}.runTaskTimerAsynchronously(this, 0, 20 * 60 * 10);

	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public void saveConfig() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		} else {
			return;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				if (toRemove.containsKey(p.getUniqueId().toString())) {
					toRemove.remove(p.getUniqueId().toString());
					p.sendMessage("joined in time");
				} else {
					p.sendMessage("fired1");
				}

			}

		}, 20 * 3);

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		for (Conversation c : convos) {
			if (!c.playerInConversation(p)) return;
			else {

				toRemove.put(p.getUniqueId().toString(), c);
				Bukkit.broadcastMessage(toRemove.containsKey(p) + ".");
				new BukkitRunnable() {

					@Override
					public void run() {
						if (toRemove.containsKey(p.getUniqueId().toString())) {
							if (Conversation.getConversation(p).playerInConversation(p)) {
								toRemove.remove(p);
								Bukkit.getPluginManager().callEvent(new PlayerLeaveConversationEvent(p, toRemove.get(p)));
								c.remove(p);
								Bukkit.broadcastMessage("removed offline player");
							}
						}
					}

				}.runTaskLater(this, 1);
			}
		}
	}

}
