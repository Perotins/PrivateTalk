package me.perotin.privatetalk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Conversation {

	public ArrayList<UUID> members = new ArrayList<>();
	private Player owner;
	private String name;
	private Boolean isPublic;

	public Conversation(Player owner, String name, Boolean publicC) {
		this.owner = owner;
		this.name = name;
		this.isPublic = publicC;
	}

	public Player getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public int size() {
		return members.size();
	}

	public Boolean isPublic() {
		return isPublic;
	}

	public List<Player> getMembers() {
		for (Player p : Bukkit.getOnlinePlayers()){
			if(members.contains(p.getUniqueId())){
				ArrayList<Player> m=new ArrayList<>();
				m.add(p);
				return m;
			}
		}
		return null;
		
	}

	public void delete() {
		if (getMembers() != null) {
			for (Player p : getMembers()) {
				getMembers().remove(p);
			}
		}
		PrivateTalk.instance.convos.remove(this);
	}

	public void setPublic(Boolean b) {
		isPublic = b;
	}

	public void setOwner(Player p) {
		owner = p;
	}

	public Boolean playerInConversation(Player p) {
		if (getMembers().contains(p)) {
			return true;
		} else {
			return false;
		}

	}
	
	public void add(Player p) {
		members.add(p.getUniqueId());
	}

	public void remove(Player p) {
		members.remove(p);
	}

	public static Conversation getConversation(Player player) {
		for (Conversation c : PrivateTalk.instance.convos) {
			if (c.playerInConversation(player)) {
				return c;
			} else {
				return null;
			}
		}
		return null;
	}

	public static Conversation getConversation(String name) {
		for (Conversation con : PrivateTalk.instance.convos) {
			if (con.getName().equalsIgnoreCase(name)) {
				return con;
			} else
				return null;
		}

		return null;
	}

}
