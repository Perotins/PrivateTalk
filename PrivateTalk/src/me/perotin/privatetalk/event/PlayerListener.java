package me.perotin.privatetalk.event;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.perotin.privatetalk.Conversation;
import me.perotin.privatetalk.PrivateTalk;

public class PlayerListener implements Listener {

	private PrivateTalk plugin;

	public PlayerListener(PrivateTalk plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onLeaveConvo(PlayerLeaveConversationEvent e) {
		Conversation c = e.getConversation();
		if (c == null) {
			return;
		}

		if (c.getMembers() == null) {
			c.delete();
			c = null;

			e.getPlayer().sendMessage("FIRED");
			return;
		} else {
			Player owner = e.getPlayer();
			owner.sendMessage("fired");
			// player leaving is owner, making a random player the
			// new owner
			if (c.getOwner().getName().equals(owner.getName()) && c.size() > 1) {
				int random = new Random().nextInt(c.getMembers().size());
				Player newOwner = c.getMembers().get(random);
				c.setOwner(newOwner);
				newOwner.sendMessage(ChatColor.GREEN + owner.getName() + ChatColor.YELLOW + " has left, giving you ownership of " + ChatColor.GREEN + c.getName());
			}
			return;

		}

	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = plugin.getConfig().getString("party-message-format");
		for (Conversation c : plugin.convos) {
			if (!plugin.toggle.containsKey(p.getUniqueId().toString())) {

				if (c.getMembers().contains(p)) {
					if (e.getMessage().startsWith("@")) {
						e.setCancelled(true);
						String m = msg.replaceAll("%CONVERSATION%", c.getName());
						String m2 = m.replaceAll("%PLAYER%", p.getName());
						String m3 = m2.replaceAll("%MESSAGE%", e.getMessage().substring(1));

						for (Player t : c.getMembers()) {
							t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));

						}
					}
				}
			} else if (plugin.toggle.containsKey(p.getUniqueId().toString())) {
				Conversation c2 = plugin.toggle.get(p.getUniqueId().toString());
				e.setCancelled(true);
				String m = msg.replaceAll("%CONVERSATION%", c2.getName());
				String m2 = m.replaceAll("%PLAYER%", p.getName());
				String m3 = m2.replaceAll("%MESSAGE%", e.getMessage());

				for (Player t : c2.getMembers()) {
					t.sendMessage(ChatColor.translateAlternateColorCodes('&', m3));
				}
			} else {
				return;
			}
		}
	}
}
