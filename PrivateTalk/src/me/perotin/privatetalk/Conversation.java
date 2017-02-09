package me.perotin.privatetalk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Conversation {

	private ArrayList<UUID> uuidsOfMembers = new ArrayList<>();
	private String name;
	private UUID owner;
	boolean isPublic;

	public Conversation(String name, UUID owner, boolean isPublic) {
		this.name = name;
		this.owner = owner;
		this.isPublic = isPublic;

	}

	public int size() {
		return uuidsOfMembers.size();
	}

	public String getName() {
		return name;
	}

	public void setOwner(Player p) {
		owner = p.getUniqueId();
	}

	public Player getOwner() {
		Player owner = Bukkit.getPlayer(this.owner);
		if (owner != null)
			return owner;
		else
			return null;

	}

	public List<Player> getMembers() {
		for (UUID u : uuidsOfMembers) {
			ArrayList<Player> members = new ArrayList<>();
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				members.add(p);
			}
			return members;

		}
		return null;
	}

	public boolean isOwner(Player p) {
		if (p.getUniqueId().equals(owner)) {
			return true;
		} else
			return false;
	}

	public boolean getPublic() {
		return this.isPublic;
	}

	public boolean contains(Player p) {
		if (getMembers().contains(p)) {
			return true;
		}
		return false;
	}

	public static boolean playerInAnyConversation(Player p) {
		if (getConversation(p) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static Conversation getConversation(Player p) {
		for (Conversation c : PrivateTalk.instance.convos) {
			if (c.contains(p)) {
				return c;
			}
		}
		return null;
	}

	public static Conversation getConversation(String name) {
		for (Conversation c : PrivateTalk.instance.convos) {
			if (c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}

	public void delete() {
	
		PrivateTalk.instance.convos.remove(this);
	}

	public void add(Player p) {
		uuidsOfMembers.add(p.getUniqueId());
	}

	public void remove(Player p) {
		uuidsOfMembers.remove(p.getUniqueId());
	}

	public void setPublic(boolean b) {
		isPublic=b;		
	}
}
