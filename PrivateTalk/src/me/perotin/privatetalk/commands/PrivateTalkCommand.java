package me.perotin.privatetalk.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.perotin.privatetalk.Conversation;
import me.perotin.privatetalk.PrivateTalk;
import me.perotin.privatetalk.events.PlayerLeaveConversationEvent;

public class PrivateTalkCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		String noPerm = ChatColor.RED + "You do not have permission to do this!";
		String inConvo = ChatColor.RED + "You must leave your conversation before doing this, type /pt leave";
		String noConvo = ChatColor.RED + "You must join a conversation before doing this!";

		String playerOnly = ChatColor.RED + "You must be a player to do this!";

		if (args.length == 0) {
			s.sendMessage(ChatColor.GREEN + "PrivateTalk Command Menu");
			s.sendMessage(ChatColor.GREEN + "<------------------------>");
			s.sendMessage(ChatColor.YELLOW + "/pt create <name>");
			s.sendMessage(ChatColor.YELLOW + "/pt leave");
			s.sendMessage(ChatColor.YELLOW + "/pt invite <player>");
			s.sendMessage(ChatColor.YELLOW + "/pt kick <player>");
			s.sendMessage(ChatColor.YELLOW + "/pt toggle");
			s.sendMessage(ChatColor.YELLOW + "/pt list");
			s.sendMessage(ChatColor.YELLOW + "/pt info <name>");
			s.sendMessage(ChatColor.YELLOW + "/pt join <name>");
			s.sendMessage(ChatColor.YELLOW + "/pt public <optional : true / false>");
			s.sendMessage(ChatColor.YELLOW + "/pt delete <optional :  name> ");
			s.sendMessage(ChatColor.YELLOW + "/pt promote <name>");
			s.sendMessage(ChatColor.GREEN + "Version 1.0 developed by Perotin");
			return true;

		}
		if (args[0].equalsIgnoreCase("create")) {
			// exceptions: no permission, name already taken, already in a
			// party, not a player, args
			if (args.length != 2 && args.length != 3) {
				s.sendMessage(ChatColor.RED + "/pt create <name>");
				return true;
			}
			if (!(s instanceof Player)) {
				s.sendMessage(playerOnly);
				return true;
			}
			Player p = (Player) s;
			if (!p.hasPermission("privatetalk.create")) {
				p.sendMessage(noPerm);
				return true;
			}
			if (Conversation.getConversation(p) != null) {
				p.sendMessage(inConvo);
				return true;
			} else if (args.length == 2) {
				String convoName = args[1];
				for (Conversation c : PrivateTalk.instance.convos) {
					if (c.getName().equalsIgnoreCase(convoName)) {
						p.sendMessage(ChatColor.YELLOW + "The name " + ChatColor.RED + convoName + ChatColor.YELLOW
								+ " is already taken!");
						return true;
					}
				}
				Conversation createdConvo = new Conversation(convoName, p.getUniqueId(), false);
				p.sendMessage("Name(" + ChatColor.GREEN + createdConvo.getName() + ChatColor.WHITE + ") Owner("
						+ ChatColor.GREEN + createdConvo.getOwner().getName() + ChatColor.WHITE + ") Public("
						+ ChatColor.GREEN + createdConvo.getPublic() + ChatColor.WHITE + ")");
				PrivateTalk.instance.convos.add(createdConvo);
				createdConvo.add(p);

			} else if (args.length == 3) {
				String convoName = args[1] + " " + args[2];
				for (Conversation c : PrivateTalk.instance.convos) {
					if (c.getName().equalsIgnoreCase(convoName)) {
						p.sendMessage(ChatColor.YELLOW + "The name " + ChatColor.RED + convoName + ChatColor.YELLOW
								+ " is already taken!");
						return true;
					}
				}
				Conversation createdConvo = new Conversation(convoName, p.getUniqueId(), false);
				p.sendMessage("Name(" + ChatColor.GREEN + createdConvo.getName() + ChatColor.WHITE + ") Owner("
						+ ChatColor.GREEN + createdConvo.getOwner().getName() + ChatColor.WHITE + ") Public("
						+ ChatColor.GREEN + createdConvo.getPublic() + ChatColor.WHITE + ")");
				createdConvo.add(p);
				PrivateTalk.instance.convos.add(createdConvo);
			}

		}
		if (args[0].equalsIgnoreCase("leave")) {
			// exceptions: not in a conversation, not a player,
			if (!(s instanceof Player)) {
				s.sendMessage(playerOnly);
				return true;
			}
			Player p = (Player) s;
			if (Conversation.getConversation(p) == null) {
				p.sendMessage(ChatColor.YELLOW + "You are not in a conversation!");
				return true;

			} else {
				PlayerLeaveConversationEvent event = new PlayerLeaveConversationEvent(p,
						Conversation.getConversation(p));
				p.sendMessage(ChatColor.YELLOW + "You have left " + ChatColor.RED+Conversation.getConversation(p).getName());

				Conversation.getConversation(p).remove(p);
				Bukkit.getPluginManager().callEvent(event);


			}
		}
		if (args[0].equalsIgnoreCase("invite")) {
			if (args.length != 2) {
				s.sendMessage(ChatColor.RED + "/pt invite <player>");
				return true;
			}
			// exceptions: not owner of a party, not a player, player inviting
			// is null, args
			if (!(s instanceof Player)) {
				s.sendMessage(playerOnly);
				return true;
			}

			Player p = (Player) s;
			if (!Conversation.playerInAnyConversation(p)) {
				p.sendMessage(noConvo);
				return true;
			} else {
				Conversation c = Conversation.getConversation(p);
				Player t = Bukkit.getPlayer(args[1]);
				if (!c.isOwner(p) && !p.hasPermission("privatetalk.invite")) {
					p.sendMessage(ChatColor.RED + "You cannot do this!");
				}
				if (t == null) {
					p.sendMessage(ChatColor.RED + args[1] + " is unknown!");
					return true;

				}
				t.sendMessage(ChatColor.YELLOW + "You have been invited to join " + ChatColor.GREEN + c.getName()
				+ ChatColor.YELLOW + "!");
				t.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GREEN + "/pt accept" + ChatColor.YELLOW + " or "
						+ ChatColor.RED + "/pt deny");
				p.sendMessage(ChatColor.GREEN + t.getName() + " has been invited to " + ChatColor.GREEN + c.getName());
				PrivateTalk.instance.invites.put(t.getUniqueId(), c);
			}
		}
		if (args[0].equalsIgnoreCase("kick")) {
			// exceptions: not in a convo, no perm, null player, args, player
			// not in convo
			if (!(s instanceof Player)) {
				s.sendMessage(playerOnly);
				return true;
			}
			Player p = (Player) s;
			if (args.length != 2) {
				p.sendMessage(ChatColor.RED + "/pt kick <player>");
				return true;
			}
			if (!Conversation.playerInAnyConversation(p)) {
				p.sendMessage(noConvo);
				return true;

			}
			Conversation con = Conversation.getConversation(p);
			if (!con.isOwner(p) && !p.hasPermission("privatetalk.kick")) {
				p.sendMessage(noPerm);
				return true;
			}
			Player t = Bukkit.getPlayer(args[1]);
			if (t == null) {
				p.sendMessage(ChatColor.RED + args[1] + " is not known!");
				return true;
			}
			if (!con.contains(t)) {
				p.sendMessage(ChatColor.RED + t.getName() + ChatColor.YELLOW + " is not in " + ChatColor.GREEN
						+ con.getName());
				return true;
			}else {
				Bukkit.getPluginManager().callEvent(new PlayerLeaveConversationEvent(t, con));
				p.sendMessage(ChatColor.YELLOW + "Removed " + ChatColor.RED + t.getName() + ChatColor.YELLOW + " from " + ChatColor.GREEN + con.getName());
				t.sendMessage(ChatColor.YELLOW + "You have been removed from "  +ChatColor.RED + con.getName());
				con.remove(t);


			}

		}
		if (args[0].equalsIgnoreCase("toggle")){
			//exceptions: not a player, not in a conversation, 
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;
			if(!Conversation.playerInAnyConversation(p)){
				p.sendMessage(noConvo);
				return true;
			}
			else{
				if(PrivateTalk.instance.toggle.containsKey(p.getUniqueId())) {
					p.sendMessage(ChatColor.YELLOW + "Toggle chat has been set to "+ChatColor.RED + "false");
					PrivateTalk.instance.toggle.remove(p.getUniqueId());
					return true;
				}else{
					p.sendMessage(ChatColor.YELLOW + "Toggle chat has been set to "  +ChatColor.GREEN + "true");
					PrivateTalk.instance.toggle.put(p.getUniqueId(), Conversation.getConversation(p));
					return true;
				}
			}




		}
		if(args[0].equalsIgnoreCase("list")){
			//exceptions: none
			if(PrivateTalk.instance.convos.size() == 0){
				s.sendMessage(ChatColor.WHITE + "Conversations ("+ChatColor.GREEN+0+ChatColor.WHITE+")");
				return true;
			}
			String msg="";
			for (int i=0; i<PrivateTalk.instance.convos.size(); i++) {
				msg += ChatColor.GREEN + PrivateTalk.instance.convos.get(i).getName() + ChatColor.WHITE+", ";
				msg.trim();

			
				s.sendMessage(ChatColor.WHITE + "Conversations ("+ChatColor.GREEN  + PrivateTalk.instance.convos.size()+ChatColor.WHITE+") : "+ msg);

			}
		}
		if(args[0].equalsIgnoreCase("info")){
			if(args.length != 2 && args.length !=3){
				s.sendMessage(ChatColor.RED +"/pt info <name>");
				return true;
			}
			if(args.length == 2){
				Conversation toInform = Conversation.getConversation(args[1]);
				if(toInform == null){
					s.sendMessage(ChatColor.RED + args[1] + " could not be found!");

				}else{
					s.sendMessage(ChatColor.GREEN + "--- " + ChatColor.YELLOW+toInform.getName() +ChatColor.GREEN + " ---");
					s.sendMessage(ChatColor.GREEN + "Public : " + ChatColor.WHITE + toInform.getPublic());
					s.sendMessage(ChatColor.GREEN + "Owner : " + ChatColor.WHITE + toInform.getOwner().getName());
					String msg = "";
					for(int i = 0; i < toInform.getMembers().size(); i++){
						msg += ChatColor.GREEN + toInform.getMembers().get(i).getName() + ChatColor.WHITE + ", ";

					}
					s.sendMessage(ChatColor.GREEN + "Members ("+ChatColor.WHITE + toInform.getMembers().size()+ChatColor.GREEN + ")"+ChatColor.WHITE+": " + msg);

				}
			}else if(args.length == 3){
				Conversation toInform = Conversation.getConversation(args[1] + " " + args[2]);
				if(toInform == null){
					s.sendMessage(ChatColor.RED + args[1] + " " + args[2] + " could not be found!");

				}else{
					s.sendMessage(ChatColor.GREEN + "--- " + ChatColor.YELLOW+toInform.getName() +ChatColor.GREEN + " ---");
					s.sendMessage(ChatColor.GREEN + "Public : " + ChatColor.WHITE + toInform.getPublic());
					s.sendMessage(ChatColor.GREEN + "Owner : " + ChatColor.WHITE + toInform.getOwner().getName());
					String msg = "";
					for(int i = 0; i < toInform.getMembers().size(); i++){
						msg += ChatColor.GREEN + toInform.getMembers().get(i).getName() + ChatColor.WHITE + ", ";

					}
					s.sendMessage(ChatColor.GREEN + "Members ("+ChatColor.WHITE + toInform.getMembers().size()+ChatColor.GREEN + ")"+ChatColor.WHITE+" : " + msg);

				}
			}


		return true;
	}
		if(args[0].equalsIgnoreCase("join")){
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;
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
				}if(toJoin.getPublic() || p.isOp()){
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
		if(args[0].equalsIgnoreCase("deny")){
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;
			if(PrivateTalk.instance.invites.containsKey(p.getUniqueId())){
				p.sendMessage(ChatColor.YELLOW + "Denied invite from " +PrivateTalk.instance.invites.get(p).getName());
				PrivateTalk.instance.invites.remove(p.getUniqueId());
				return true;
			}else{
				p.sendMessage(ChatColor.YELLOW + "No pending invites");
			}
		}
		if(args[0].equalsIgnoreCase("accept")){
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;

			if(PrivateTalk.instance.invites.containsKey(p.getUniqueId())){
				Conversation c = PrivateTalk.instance.invites.get(p.getUniqueId());
				if(c==null){
					p.sendMessage(ChatColor.RED + "Oh no! Something has gone wrong and we couldn't accept the invitation!");
					PrivateTalk.instance.invites.remove(p.getUniqueId());
					return true;
				}
				c.add(p);
				PrivateTalk.instance.invites.remove(p.getUniqueId());
				p.sendMessage(ChatColor.YELLOW + "You have joined " + ChatColor.GREEN + c.getName());
				for(Player t : c.getMembers()){
					t.sendMessage(ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has joined!");
					return true;
				}
			}else{
				p.sendMessage(ChatColor.YELLOW + "No pending invites!");
			}
		}

		if(args[0].equalsIgnoreCase("delete")){
			//exceptions: no permission, not a player, not in a conversation, specified conversation is null
			if(args.length!= 2 && args.length!=3 && args.length!=1){
				s.sendMessage(ChatColor.RED + "/pt delete <optional : name>");
				return true;
			}
			if(args.length == 1){
				if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
				Player p = (Player) s;
				if(!Conversation.playerInAnyConversation(p)){p.sendMessage(noConvo);return true;}
				Conversation convo = Conversation.getConversation(p);
				if(!convo.isOwner(p) && !p.hasPermission("privatetalk.delete")){
					p.sendMessage(noPerm);
					return true;
				}else{
					for (Player t : convo.getMembers()){
						t.sendMessage(ChatColor.RED + convo.getName() + ChatColor.YELLOW + " is closing down! Goodbye!");
						
					}
					p.sendMessage(ChatColor.YELLOW + "Deleted " + ChatColor.RED + convo.getName()+ChatColor.YELLOW+"!");

					convo.delete();
					convo=null;
				}
			}
			if(args.length == 2){
				Conversation convo = Conversation.getConversation(args[1]);
				if(convo==null){
					s.sendMessage(ChatColor.RED + args[1] + " is unkown!");
					return true;
				}
				if(s.isOp() || s.hasPermission("privatetalk.delete")){
					for (Player t : convo.getMembers()) {
						t.sendMessage(ChatColor.RED + convo.getName() + ChatColor.YELLOW + " is closing down! Goodbye!");

					}
					s.sendMessage(ChatColor.YELLOW + "Deleted " + ChatColor.RED + convo.getName()+ChatColor.YELLOW+"!");
					convo.delete();
					convo = null;
				}else s.sendMessage(noPerm);
			}if(args.length == 3){
				Conversation convo = Conversation.getConversation(args[1] + " " + args[2]);
				if(convo==null){
					s.sendMessage(ChatColor.RED + args[1] + " " + args[2] + " is unkown!");
					return true;
				}
				if(s.isOp() || s.hasPermission("privatetalk.delete")){
					for (Player t : convo.getMembers()) {
						t.sendMessage(ChatColor.RED + convo.getName() + ChatColor.YELLOW + " is closing down! Goodbye!");

					}
					s.sendMessage(ChatColor.YELLOW + "Deleted " + ChatColor.RED + convo.getName()+ChatColor.YELLOW+"!");

					convo.delete();
					convo = null;
				}
			}
		}
		if(args[0].equals("public")){
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;
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
					Boolean pub = toToggle.getPublic();
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
		if(args[0].equalsIgnoreCase("promote")) {
			if(args.length!=2){
				s.sendMessage(ChatColor.RED + "/pt promote <player>"); return true;
			}
			if(!(s instanceof Player)){s.sendMessage(playerOnly);return true;}
			Player p = (Player) s;
			if(!Conversation.playerInAnyConversation(p)){
				p.sendMessage(noConvo);
				return true;
			}
			Conversation con = Conversation.getConversation(p);
			if(!con.isOwner(p) && !p.hasPermission("privatetalk.promote")){
				p.sendMessage(noPerm);
				return true;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null){
				p.sendMessage(ChatColor.RED + args[1] + " is unknown!");
				return true;
			}
			else if(!con.contains(target)) {
				p.sendMessage(ChatColor.RED + target.getName() +ChatColor.YELLOW + " is not in your conversation!");
				return true;
			}else{
				for (Player t : con.getMembers()){
					t.sendMessage(ChatColor.RED + p.getName() + ChatColor.YELLOW + " has made " + ChatColor.GREEN + target.getName() + ChatColor.YELLOW + " the new owner of "+ChatColor.GREEN + con.getName());
				}
				con.setOwner(target);
			}
			
			
		}
		else if(!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("list")&&!args[0].equalsIgnoreCase("leave")
				&&!args[0].equalsIgnoreCase("kick")&&!args[0].equalsIgnoreCase("info")&&!args[0].equalsIgnoreCase("accept")&&!args[0].equalsIgnoreCase("deny")
				&&!args[0].equalsIgnoreCase("invite")&&!args[0].equalsIgnoreCase("public")&&!args[0].equalsIgnoreCase("join")&&!args[0].equalsIgnoreCase("toggle")
				&&!args[0].equalsIgnoreCase("delete") &&!args[0].equalsIgnoreCase("promote")){
			s.sendMessage(ChatColor.RED + "Unknown arguments, please do /pt to view all commands.");
		}
		

		

		return true;
	

	}
}

