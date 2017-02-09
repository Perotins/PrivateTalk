package me.perotin.privatetalk.events;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.perotin.privatetalk.Conversation;
import me.perotin.privatetalk.PrivateTalk;

public class PlayerConversationEvents implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinConversationEvent e) {
		Player p = e.getPlayer();
		Conversation convo = e.getConversation();
		for (Player t : convo.getMembers()) {
			t.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has joined!");
		}
		for (Conversation c : PrivateTalk.instance.convos){
			if(c.getName().equalsIgnoreCase(convo.getName())) {
				PrivateTalk.instance.convos.remove(c);
				PrivateTalk.instance.convos.add(convo);
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerLeaveConversationEvent e) {
		Player p = e.getPlayer();
		Conversation convo = e.getConversation();
		if (convo.size() == 0) {
			// no one is in the conversation anymore, delete it
			p.sendMessage("size is 0, deleting convo");
			convo.delete();
			convo =null;
			return;
		}
		// player leaving is owner, got to make another player owner
		if (convo.isOwner(p)) {
			Player newOwner = convo.getMembers().get(new Random().nextInt(convo.size()));
			convo.setOwner(newOwner);
			newOwner.sendMessage(
					ChatColor.RED + p.getName() + ChatColor.YELLOW + " has left, making you the new owner of "
							+ ChatColor.GREEN + convo.getName() + ChatColor.YELLOW + "!");

		}
		for (Player t : convo.getMembers()) {
			t.sendMessage(ChatColor.RED + p.getName() + ChatColor.YELLOW + " has left!");
		}
	}

}
