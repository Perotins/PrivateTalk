package me.perotin.privatetalk.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.perotin.privatetalk.Conversation;
import me.perotin.privatetalk.PlayerLeaveConversationEvent;
import me.perotin.privatetalk.PrivateTalk;

public class PrivateTalkCMD implements CommandExecutor {

	HashMap<Player, Conversation>invites = new HashMap<>();
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(!(s instanceof Player)){
			s.sendMessage(ChatColor.RED + "Sorry, players only.");
			return true;
		}
		Player p = (Player) s;
		if(args.length == 0){
			p.sendMessage(ChatColor.GREEN + "Private Talk command menu!");
			p.sendMessage(ChatColor.GREEN + "<------------------------>");
			p.sendMessage(ChatColor.YELLOW + "/pt create <name>");
			p.sendMessage(ChatColor.YELLOW + "/pt leave");
			p.sendMessage(ChatColor.YELLOW + "/pt invite <player>");
			p.sendMessage(ChatColor.YELLOW + "/pt kick <player>");
			p.sendMessage(ChatColor.YELLOW + "/pt toggle");
			p.sendMessage(ChatColor.YELLOW + "/pt list");
			p.sendMessage(ChatColor.YELLOW + "/pt info <name>");
			p.sendMessage(ChatColor.YELLOW + "/pt join <name>");
			p.sendMessage(ChatColor.YELLOW + "/pt public <optional : true / false>");
			p.sendMessage(ChatColor.YELLOW + "/pt delete <optional :  name> ");
			p.sendMessage(ChatColor.YELLOW + "/pt promote <name>");
			p.sendMessage(ChatColor.GREEN + "Version 1.0 developed by Perotin");

			return true;
		}
		if(args[0].equalsIgnoreCase("list")){
			String msg = "";
			if(PrivateTalk.priv.convos.size() >= 1){
				for(int i = 0; i < PrivateTalk.priv.convos.size(); i++){
					msg += ChatColor.GREEN + PrivateTalk.priv.convos.get(i).getName() + ChatColor.WHITE+", ";
					msg.trim();

				}
				p.sendMessage(ChatColor.WHITE + "Conversations ("+ChatColor.GREEN  + PrivateTalk.priv.convos.size()+ChatColor.WHITE+") : "+ msg);

			}else{
				p.sendMessage(ChatColor.WHITE + "Conversations : " + ChatColor.GREEN + "No current conversations :(");
			}
		}
		if(args[0].equalsIgnoreCase("deny")){
			if(invites.containsKey(p)){
				p.sendMessage(ChatColor.YELLOW + "Denied invite from " +invites.get(p).getName());
				invites.remove(p);
				return true;
			}else{
				p.sendMessage(ChatColor.YELLOW + "No pending invites");
			}
		}
		if(args[0].equalsIgnoreCase("accept")){
			if(invites.containsKey(p)){
				Conversation c = invites.get(p);
				c.add(p);
				invites.remove(p);
				p.sendMessage(ChatColor.YELLOW + "You have joined " + ChatColor.GREEN + c.getName());
				for(Player t : c.getMembers()){
					t.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has joined!");
					return true;
				}
			}else{
				p.sendMessage(ChatColor.YELLOW + "No pending invites!");
			}
		}
		if(args[0].equalsIgnoreCase("promote")){
			if(args.length != 2){
				p.sendMessage(ChatColor.RED + "/pt promote <name>");
				return true;
			}else{
				Conversation c = Conversation.getConversation(p);
				if(c==null){
					p.sendMessage(ChatColor.RED + "You are not in a conversation! Please join one before promoting someone!");
					return true;
				}
				@SuppressWarnings("deprecation")
				Player t = Bukkit.getPlayer(args[1]);
				if(t==null){
					p.sendMessage(ChatColor.RED + args[1] + " is not known!");
					return true;
				}
				if(!c.getOwner().getName().equalsIgnoreCase(p.getName()) && !p.hasPermission("pt.promote")){
					p.sendMessage(ChatColor.RED + "No permission to do this!");
					return true;
				}else{
					if(p.getName().equalsIgnoreCase(t.getName())){
						p.sendMessage(ChatColor.RED + "You cannot promote yourself!");
						return true;
					}
					c.setOwner(t);
					for(Player r : c.getMembers()){
						r.sendMessage(ChatColor.RED + p.getName() + ChatColor.YELLOW + " has made " + ChatColor.GREEN + t.getName() + ChatColor.YELLOW + " the new owner of " + ChatColor.GREEN + c.getName()+ChatColor.YELLOW + "!");
					}
				}
			}
		}
		if(args[0].equalsIgnoreCase("leave")){
			for(Conversation c : PrivateTalk.priv.convos){
				if(!c.playerInConversation(p)){
					p.sendMessage(ChatColor.RED + "You are not in any conversations!");
					return true;
				}else{
					c.remove(p);

					PlayerLeaveConversationEvent e = new PlayerLeaveConversationEvent(p, c);
					Bukkit.getServer().getPluginManager().callEvent(e);

					p.sendMessage(ChatColor.YELLOW + "Successfully left " + ChatColor.RED + c.getName() + ChatColor.YELLOW + "!");
					return true;
				}
			}
		}else if(args[0].equalsIgnoreCase("toggle")){
			for(Conversation c : PrivateTalk.priv.convos){
				if(!c.playerInConversation(p)){
					p.sendMessage(ChatColor.YELLOW + "You are not in any chats!");
					return true;
				}else{
					if(PrivateTalk.priv.toggle.containsKey(p.getUniqueId().toString())){
						PrivateTalk.priv.toggle.remove(p.getUniqueId().toString());
						p.sendMessage(ChatColor.YELLOW + "Toggle chat set to " + ChatColor.RED + "off");
						return true;
					}else{
						PrivateTalk.priv.toggle.put(p.getUniqueId().toString(), c);
						p.sendMessage(ChatColor.YELLOW + "Toggle chat set to " + ChatColor.GREEN + "on");
						return true;

					}
				}
			}
		}

		if(args[0].equalsIgnoreCase("create")){

			if(args.length != 2 && args.length != 3){
				p.sendMessage(ChatColor.RED + "/pt create <name>");
			}
			for(Conversation c : PrivateTalk.priv.convos){
				if(c == null){
					return true;
				}
				if(c.playerInConversation(p)){
					p.sendMessage(ChatColor.RED + "You are in a conversation! Please leave before creating one by doing "+ChatColor.WHITE+"/pt leave");
					return true;
				}else{
					if(!p.hasPermission("pt.create")){
						p.sendMessage(ChatColor.RED + "No permission to do this!");
						return true;
					}
				}
			}if(args.length == 2){
				String convoName = args[1];
				if(Conversation.getConversation(convoName) != null){
					p.sendMessage(ChatColor.RED + "That name already exists! Please choose a different name.");
					return true;
				}
				p.sendMessage(ChatColor.WHITE + "Conversation " + ChatColor.GREEN + convoName + ChatColor.WHITE +" - "+ChatColor.GREEN+"OWNER ("+
						ChatColor.WHITE +p.getName()+ChatColor.GREEN +") PUBLIC ("+ChatColor.WHITE +"false"+ChatColor.GREEN + ")");
				Conversation convo =  new Conversation(p, convoName, false);
				PrivateTalk.priv.convos.add(convo);
				convo.add(p);

			}else if(args.length == 3){
				String convoName = args[1] + " " + args[2];
				if(Conversation.getConversation(convoName) != null){
					p.sendMessage(ChatColor.RED + "That name already exists! Please choose a different name.");
					return true;
				}
				Conversation convo =new Conversation(p, convoName, false);
				PrivateTalk.priv.convos.add(convo);
				convo.add(p);
				p.sendMessage(ChatColor.WHITE + "Conversation " + ChatColor.GREEN + convoName + ChatColor.WHITE +" - "+ChatColor.GREEN+"OWNER ("+
						ChatColor.WHITE +p.getName()+ChatColor.GREEN +") PUBLIC ("+ChatColor.WHITE +"false"+ChatColor.GREEN + ")");
				return true;
			}
		}
		else if(args[0].equalsIgnoreCase("join")){
			if(args.length != 2 && args.length != 3){
				p.sendMessage(ChatColor.RED + "/pt join <name>");
				return true;
			}
			else if(args.length == 2){
				Conversation toJoin = Conversation.getConversation(args[1]);
				if(toJoin == null){
					p.sendMessage(ChatColor.RED + args[1] +" is not known.");
					return true;
				}else if (Conversation.getConversation(p) != null){
					p.sendMessage(ChatColor.RED + "You are currently in a conversation! Please type /pt leave before trying to join one!");
					return true;
				}if(toJoin.isPublic() || p.isOp()){
					toJoin.add(p);
					p.sendMessage(ChatColor.YELLOW + "Joined " + ChatColor.GREEN + toJoin.getName());
					for(Player t : toJoin.getMembers()){
						t.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW+" joined!");
					}
					return true;
				}else{
					p.sendMessage(ChatColor.GREEN + toJoin.getName() + ChatColor.YELLOW + " is set on " + ChatColor.RED + "private");
					return true;

				}
			}

		}
		else if(args[0].equalsIgnoreCase("delete")){
			if(args.length != 2 && args.length != 3 && args.length != 1){
				p.sendMessage(ChatColor.RED + "/pt delete <optional : name>");
				return true;
			}
			Conversation c2 = Conversation.getConversation(p);

			if(p.hasPermission("pt.delete") || p.isOp() || c2!=null && c2.getOwner().getName().equals(p.getName())){
				if(args.length == 1){
					if(Conversation.getConversation(p) == null){
						p.sendMessage(ChatColor.RED + "Currently you are not in a conversation!");
						return true;
					}else if(Conversation.getConversation(p).getOwner().getName() != p.getName()){
						p.sendMessage(ChatColor.RED + "No permission to do this!");
						return true;

					}else{
						p.sendMessage(ChatColor.YELLOW + "Successfully deleted!");
						for(Player t : Conversation.getConversation(p).getMembers()){
							t.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " is closing shop. Come again!");
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(PrivateTalk.priv, new Runnable(){

							@Override
							public void run() {
							    Conversation.getConversation(p).delete();
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pt delete " + Conversation.getConversation(p).getName());							}

						}, 20);
					}
				}
				if(args.length == 2){
					Conversation c = Conversation.getConversation(args[1]);
					if(c == null){
						p.sendMessage(ChatColor.RED + args[1] + " is not known!");
						return true;
					}else{
						for(Player t: c.getMembers()){
							t.sendMessage(ChatColor.RED + "Your conversation is being deleted!");

						}
						p.sendMessage(ChatColor.YELLOW + "Successfully deleted " + ChatColor.RED + c.getName());
						c.delete();
					}
				}else if(args.length == 3){
					Conversation c = Conversation.getConversation(args[1] + " " + args[2]);
					if(c == null){
						p.sendMessage(ChatColor.RED + args[1] + " is not known!");
						return true;
					}else{
						for(Player t: c.getMembers()){
							t.sendMessage(ChatColor.RED + "Your conversation is being deleted!");
							c.remove(t);

						}
						p.sendMessage(ChatColor.YELLOW + "Successfully deleted " + ChatColor.RED + c.getName());
						c.delete();
					}
				}
			}else{
				p.sendMessage(ChatColor.RED + "You cannot perform this action!");
			}
		}
		else if(args[0].equals("public")){
			if(args.length != 1 && args.length != 2){
				p.sendMessage(ChatColor.RED + "/pt public <optional : true/false>");
				return true;
			}
			else if(Conversation.getConversation(p) == null){
				p.sendMessage(ChatColor.RED + "You are currently not in a conversation!");
				return true;
			}else{
				Conversation toToggle = Conversation.getConversation(p);
				if(toToggle.getOwner().getName().equalsIgnoreCase(p.getName())){
					Boolean pub = toToggle.isPublic();
					if(args.length == 1){
						if(pub){
							toToggle.setPublic(false);
							p.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GREEN + toToggle.getName() + ChatColor.YELLOW + " to " +
									ChatColor.RED + "private!");
							return true;
						}else{
							toToggle.setPublic(true);
							p.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GREEN + toToggle.getName() + ChatColor.YELLOW + " to " +
									ChatColor.GREEN + "public!");
							return true;
						}
					}else if(args.length == 2){
						String bol = args[1];
						if(bol.equalsIgnoreCase("true") ){
							toToggle.setPublic(true);
							p.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GREEN + toToggle.getName() + ChatColor.YELLOW + " to " +
									ChatColor.GREEN + "public!");
							return true;
						}if(bol.equalsIgnoreCase("false")){
							toToggle.setPublic(false);
							p.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GREEN + toToggle.getName() + ChatColor.YELLOW + " to " +
									ChatColor.RED + "private!");
							return true;
						}else{
							p.sendMessage(ChatColor.RED + "Please use either true or false!");
						}
					}
				}else{
					p.sendMessage(ChatColor.RED + "You must be the owner of " + ChatColor.WHITE + toToggle.getName() + ChatColor.RED + " to do this!");
				}
			}

		}

		if(args[0].equalsIgnoreCase("kick")){
			if(args.length != 2){
				p.sendMessage(ChatColor.RED + "/pt kick <player>");
				return true;
			}
			Conversation c = Conversation.getConversation(p);
			if(c == null){
				p.sendMessage(ChatColor.RED+ "You are not in any conversations!");
				return true;
			}else{
				if(c.getOwner().getName() != p.getName()){
					p.sendMessage(ChatColor.RED + "You are not allowed to do this!");
					return true;
				}else{
					@SuppressWarnings("deprecation")
					Player toKick = Bukkit.getPlayer(args[1]);
					if(toKick == null){
						p.sendMessage(ChatColor.RED + args[1] + " is not a known player!");
						return true;
					}
					else if(Conversation.getConversation(toKick).getName() != c.getName()){
						p.sendMessage(ChatColor.RED + toKick.getName() + " is not in your conversation!");
						return true;
					}
					else{
						c.getMembers().remove(toKick);
						toKick.sendMessage(ChatColor.YELLOW + "You have been removed from " + ChatColor.RED +c.getName() +ChatColor.YELLOW+ "!");
						p.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.RED + toKick.getName() + ChatColor.YELLOW+" from " + ChatColor.GREEN+c.getName());
						return true;
					}
				}
			}
		}
		else if(args[0].equalsIgnoreCase("invite")){
			if(args.length != 2){
				p.sendMessage(ChatColor.RED + "/pt invite <player>");
				return true;
			}
			if(Conversation.getConversation(p) == null){
				p.sendMessage(ChatColor.RED + "You're currently not in a conversation! Please join one before inviting someone.");
				return true;
			}else{
				if(Conversation.getConversation(p).getOwner().getName() != p.getName() && !p.isOp()){
					p.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to do this!");
				}else{
					@SuppressWarnings("deprecation")
					Player toInvite = Bukkit.getPlayer(args[1]);
					Conversation c= Conversation.getConversation(p);
					if(toInvite == null){
						p.sendMessage(ChatColor.RED + args[1] + " could not be found!");
						return true;
					}
					if(c.playerInConversation(toInvite)){
						p.sendMessage(ChatColor.RED + toInvite.getName() + " is already in your conversation!");
						return true;
					}else{
						invites.put(toInvite, c);
						p.sendMessage(ChatColor.YELLOW + "Invited " + ChatColor.GREEN + toInvite.getName() + ChatColor.YELLOW + 
								" to " + ChatColor.GREEN + c.getName());
						toInvite.sendMessage(ChatColor.YELLOW + "You have been invited to the conversation " + ChatColor.GREEN + c.getName());
						toInvite.sendMessage(ChatColor.YELLOW + "To join, type " + ChatColor.GREEN + "/pt accept " + ChatColor.YELLOW + "or " 
								+ ChatColor.RED + "/pt deny");
						return true;
					}
				}
			}
		}
		if(args[0].equalsIgnoreCase("info")){
			if(args.length != 2 && args.length !=3){
				p.sendMessage(ChatColor.RED +"/pt info <name>");
				return true;
			}
			if(args.length == 2){
				Conversation toInform = Conversation.getConversation(args[1]);
				if(toInform == null){
					p.sendMessage(ChatColor.RED + args[1] + " could not be found!");

				}else{
					p.sendMessage(ChatColor.GREEN + "--- " + ChatColor.YELLOW+toInform.getName() +ChatColor.GREEN + " ---");
					p.sendMessage(ChatColor.GREEN + "Public : " + ChatColor.WHITE + toInform.isPublic());
					p.sendMessage(ChatColor.GREEN + "Owner : " + ChatColor.WHITE + toInform.getOwner().getName());
					String msg = "";
					for(int i = 0; i < toInform.getMembers().size(); i++){
						msg += ChatColor.GREEN + toInform.getMembers().get(i).getName() + ChatColor.WHITE + ", ";

					}
					p.sendMessage(ChatColor.GREEN + "Members ("+ChatColor.WHITE + toInform.getMembers().size()+ChatColor.GREEN + ")"+ChatColor.WHITE+": " + msg);

				}
			}else if(args.length == 3){
				Conversation toInform = Conversation.getConversation(args[1] + " " + args[2]);
				if(toInform == null){
					p.sendMessage(ChatColor.RED + args[1] + " " + args[2] + " could not be found!");

				}else{
					p.sendMessage(ChatColor.GREEN + "--- " + ChatColor.YELLOW+toInform.getName() +ChatColor.GREEN + " ---");
					p.sendMessage(ChatColor.GREEN + "Public : " + ChatColor.WHITE + toInform.isPublic());
					p.sendMessage(ChatColor.GREEN + "Owner : " + ChatColor.WHITE + toInform.getOwner().getName());
					String msg = "";
					for(int i = 0; i < toInform.getMembers().size(); i++){
						msg += ChatColor.GREEN + toInform.getMembers().get(i).getName() + ChatColor.WHITE + ", ";

					}
					p.sendMessage(ChatColor.GREEN + "Members ("+ChatColor.WHITE + toInform.getMembers().size()+ChatColor.GREEN + ")"+ChatColor.WHITE+" : " + msg);

				}
			}
		}else if(!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("list")&&!args[0].equalsIgnoreCase("leave")
				&&!args[0].equalsIgnoreCase("kick")&&!args[0].equalsIgnoreCase("info")&&!args[0].equalsIgnoreCase("accept")&&!args[0].equalsIgnoreCase("deny")
				&&!args[0].equalsIgnoreCase("invite")&&!args[0].equalsIgnoreCase("public")&&!args[0].equalsIgnoreCase("join")&&!args[0].equalsIgnoreCase("toggle")
				&&!args[0].equalsIgnoreCase("delete") &&!args[0].equalsIgnoreCase("promote")){
			p.sendMessage(ChatColor.RED + "Unknown arguements, please do /pt to view all commands.");
		}


		return true;
	}

}
