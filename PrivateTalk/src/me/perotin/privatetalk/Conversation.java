package me.perotin.privatetalk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Conversation {
	
	ArrayList<Player>members = new ArrayList<>();
	Player owner;
	String name;
	Boolean isPublic;
	public Conversation(){
		
	}
	public Conversation(Player owner, String name, Boolean publicC){
		this.owner = owner;
		this.name = name;
		this.isPublic = publicC;
	}
	public Player getOwner(){
		return owner;
	}
	public String getName(){
		return name;
	}
	public Boolean isPublic(){
		return isPublic;
	}
	public List<Player> getMembers(){
		return members;
	}
	public void delete(){
		for(Player p : getMembers()){
			remove(p);
		}
		PrivateTalk.priv.convos.remove(this);
	}
	public void setPublic(Boolean b){
		isPublic = b;
	}
	public void setOwner(Player p){
		owner = p;
	}
	
	public Boolean playerInConversation(Player p){
			if(getMembers().contains(p)){
				return true;
			}else{
				return false;
			}
		
	}
	public Conversation getConversation(Player p){
		for(Conversation c : PrivateTalk.priv.convos){
			if(c.playerInConversation(p)){
				return c;
			}else{
				return null;
			}
		}
		return null;
	}
	public void add(Player p){
		getMembers().add(p);
	}
	public void remove(Player p){
		getMembers().remove(p);
	}
	public Conversation getConversation(String convo){
		for(Conversation con : PrivateTalk.priv.convos){
			if(con.getName().equalsIgnoreCase(convo)){
				return con;
			}else return null;
		}
		
		return null;
	}

}
