package me.perotin.privatetalk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Conversation {
	
	private ArrayList<UUID>members = new ArrayList<>();
	private Player owner;
	private String name;
	private Boolean isPublic;
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
	public int size(){
		return members.size();
	}
	public Boolean isPublic(){
		return isPublic;
	}
	public List<Player> getMembers(){
		for(UUID s : members){
			ArrayList<Player>members = new ArrayList<>();
			members.add(Bukkit.getPlayer(s));
			return members;
		}
		return null;
	}
	public void delete(){
		if(getMembers() != null){
			for(Player p : getMembers()){
				getMembers().remove(p);
			}
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
	public void add(Player p){
		members.add(p.getUniqueId());
	}
	public void remove(Player p){
		members.remove(p.getUniqueId());
	}
	public static Conversation getConversation(Player player){
		for(Conversation c : PrivateTalk.priv.convos){
			if(c.playerInConversation(player)){
				return c;
			}else{
				return null;
			}
		}
		return null;
	}
	public static Conversation getConversation(String name){
		for(Conversation con : PrivateTalk.priv.convos){
			if(con.getName().equalsIgnoreCase(name)){
				return con;
			}else return null;
		}
		
		return null;
	}

}
