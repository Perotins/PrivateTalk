package me.perotin.privatetalk;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Speak implements Listener {
	/*
	 * @EventHandler
	public void chatInParty(AsyncPlayerChatEvent e){
		String msg = PrivateTalk.priv.getConfig().getString("party-message-format");


		Player p = e.getPlayer();
		for(Conversation c : PrivateTalk.priv.convos){
			if(c == null){
				p.sendMessage("convo is null!");
				return;
			}
			if(c.getMembers().contains(p)){
				if(!PrivateTalk.priv.toggle.containsKey(p)) {


					else if(e.getMessage().startsWith("@")){
						e.setCancelled(true);
						String m = msg.replaceAll("%CONVERSATION%", c.getName());
						String m2 = m.replaceAll("%PLAYER%", p.getName());
						String m3 =m2.replaceAll("%MESSAGE%", e.getMessage().substring(1));

						for(Player t : c.getMembers()){
							t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));

						}


					}

				}

			}
			if(PrivateTalk.priv.toggle.containsKey(p)){
				Conversation c2 = PrivateTalk.priv.toggle.get(p);
				e.setCancelled(true);
				String m =msg.replaceAll("%CONVERSATION%", c2.getName());
				String m2 =m.replaceAll("%PLAYER%", p.getName());
				String m3=m2.replaceAll("%MESSAGE%", e.getMessage());

				for(Player t : c2.getMembers()){
					t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));
				}
			}


	 */
	@EventHandler
	public void chatInParty(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		String msg = PrivateTalk.priv.getConfig().getString("party-message-format");
		for (Conversation c : PrivateTalk.priv.convos) {
			if(!PrivateTalk.priv.toggle.containsKey(p)){

			if(c.getMembers().contains(p)){
					if(e.getMessage().startsWith("@")){
						e.setCancelled(true);
						String m = msg.replaceAll("%CONVERSATION%", c.getName());
						String m2 = m.replaceAll("%PLAYER%", p.getName());
						String m3 =m2.replaceAll("%MESSAGE%", e.getMessage().substring(1));

						for(Player t : c.getMembers()){
							t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));

						}
					}
				}
			}
			else if(PrivateTalk.priv.toggle.containsKey(p)) {
				Conversation c2 = PrivateTalk.priv.toggle.get(p);
				e.setCancelled(true);
				String m =msg.replaceAll("%CONVERSATION%", c2.getName());
				String m2 =m.replaceAll("%PLAYER%", p.getName());
				String m3=m2.replaceAll("%MESSAGE%", e.getMessage());

				for(Player t : c2.getMembers()){
					t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));
				}
			} else {
				return;
			}
		}
	}


}
