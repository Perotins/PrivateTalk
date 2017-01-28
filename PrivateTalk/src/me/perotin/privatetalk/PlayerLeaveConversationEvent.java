package me.perotin.privatetalk;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveConversationEvent extends Event implements Cancellable{

	Boolean isCancelled = false;
	private static final HandlerList handlers = new HandlerList();

	Player leaving;
	Conversation leavingFrom;
	
	public PlayerLeaveConversationEvent(Player p, Conversation c){
		leaving = p;
		leavingFrom = c;
	}
	@Override
	public HandlerList getHandlers() {
				return handlers;
	}
	
	public static HandlerList getHandlerList() {
				return handlers;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;		
	}
	
	public Player getPlayer(){
		return leaving;
	}
	public Conversation getConversation(){
		return leavingFrom;
	}

}
