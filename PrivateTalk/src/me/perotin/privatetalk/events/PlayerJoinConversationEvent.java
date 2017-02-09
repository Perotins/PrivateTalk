package me.perotin.privatetalk.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.perotin.privatetalk.Conversation;

public class PlayerJoinConversationEvent extends Event {

	private Player player;
	private Conversation convo;
	private static final HandlerList handlers = new HandlerList();

	public PlayerJoinConversationEvent(Player player, Conversation convo) {
		this.player = player;
		this.convo = convo;
	}

	public Player getPlayer() {
		return player;
	}

	public Conversation getConversation() {
		return convo;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
