package me.perotin.privatetalk.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.perotin.privatetalk.Conversation;
import me.perotin.privatetalk.PrivateTalk;

public class PlayerEvents implements Listener {

	// if player leaves server for 'x' amount of minutes, they will be removed
	// from any conversations they are apart of
	int minutesToExpire = PrivateTalk.instance.getConfig().getInt("time-to-kick");

	@EventHandler
	public void onLeaveConvo(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Conversation convo = Conversation.getConversation(p);
		if (convo == null)
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateTalk.instance, new Runnable() {

			@Override
			public void run() {
				convo.remove(p);
				PlayerLeaveConversationEvent event = new PlayerLeaveConversationEvent(p, convo);
				Bukkit.getPluginManager().callEvent(event);

			}

		}, 20 * 60 * minutesToExpire);

	}
	
	@EventHandler
	public void talkInConvo(AsyncPlayerChatEvent e){
		FileConfiguration config = PrivateTalk.instance.getConfig();
		Player p = e.getPlayer();
		String msg = e.getMessage();
		if(!Conversation.playerInAnyConversation(p)) return;
		else{
			Conversation convo = Conversation.getConversation(p);
			String format = config.getString("party-chat-format");
			if(msg.startsWith("@") && !PrivateTalk.instance.toggle.containsKey(p.getUniqueId())){
				msg = msg.substring(1);
				e.setCancelled(true);
				String f1 = format.replace("%CONVERSATION%", convo.getName()).replace("%PLAYER%", p.getName()).replace("%MESSAGE%", msg);
				for (Player t : convo.getMembers()){
					t.sendMessage(ChatColor.translateAlternateColorCodes('&', f1));
				}
			}else if(PrivateTalk.instance.toggle.containsKey(p.getUniqueId())){
				Conversation c = PrivateTalk.instance.toggle.get(p.getUniqueId());
				if(c==null){
					PrivateTalk.instance.toggle.remove(p.getUniqueId());
					return;
				}else{
					e.setCancelled(true);
					String f1 = format.replace("%CONVERSATION%", c.getName()).replace("%PLAYER%", p.getName()).replace("%MESSAGE%", msg);

					for (Player t : c.getMembers()) {
						t.sendMessage(ChatColor.translateAlternateColorCodes('&', f1));

					}
				}
			}
		}
	}

}
