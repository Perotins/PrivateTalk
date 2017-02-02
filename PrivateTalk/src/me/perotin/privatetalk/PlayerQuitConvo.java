package me.perotin.privatetalk;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitConvo implements Listener {

	@EventHandler
	public void quitConvo(PlayerLeaveConversationEvent e) {
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
}
