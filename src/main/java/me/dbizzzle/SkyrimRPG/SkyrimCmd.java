package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.Spell;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyrimCmd implements CommandExecutor
{
	private SkyrimRPG plugin;
	private ConfigManager cm;
	private PlayerManager pm;
	public SkyrimCmd(SkyrimRPG plugin)
	{
		this.plugin = plugin;
		this.pm = plugin.getPlayerManager();
		this.cm = plugin.getConfigManager();
	}
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		if (command.getName().equalsIgnoreCase("bindspell")) {
			if (player == null) 
			{
				sender.sendMessage(ChatColor.RED + "Console can not use magic.");
			} 
			else if (player.hasPermission("skyrimrpg.bindspell")) 
			{
				if(args.length < 2)
				{
					player.sendMessage(ChatColor.RED + "Usage: /bindspell <left/right/both> <spell>");
				}
				else
				{
					int mode = 1;
					if(args[0].equalsIgnoreCase("left"))mode = 1;
					else if(args[0].equalsIgnoreCase("right"))mode = 2;
					else if(args[0].equalsIgnoreCase("both"))mode = 3;
					if(args[1].equalsIgnoreCase("none"))
					{
						if(mode == 1 ||mode == 3)plugin.getSpellManager().bindLeft(player, null);
						if(mode == 2 ||mode == 3)plugin.getSpellManager().bindRight(player, null);
						if(mode == 3)player.sendMessage(ChatColor.GREEN + "Spell bindings removed from both hands");
						else player.sendMessage(ChatColor.GREEN + "Spell bindings removed from " + (mode == 1 ? "left" : "right") + " hand");
						return true;
					}
					Spell s = null;
					StringBuffer sb = new StringBuffer();
					for(int i = 1; i < args.length; i ++)
					{
						if(sb.length() == 0)sb.append(args[i]);
						else sb.append("_" + args[i]);
					}
					try{s = Spell.valueOf(sb.toString().toUpperCase());}
					catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
					if(!pm.getData(player.getName()).hasSpell(s))
					{
						player.sendMessage(ChatColor.RED + "You have not yet learned this spell!");
						return true;
					}
					if(plugin.getConfigManager().useSpellPermissions && !player.hasPermission("skyrimrpg.spells.*"))
					{
						if(!player.hasPermission("skyrimrpg.spells." + s.getDisplayName()))
						{
							player.sendMessage(ChatColor.RED + "You are not allowed to use this spell!");return true;
						}
					}
					else if(s.getPartner() != null)
					{
						plugin.getSpellManager().bindLeft(player, s);
						plugin.getSpellManager().bindRight(player, s.getPartner());
					}
					else
					{
						if(mode == 1 ||mode == 3)plugin.getSpellManager().bindLeft(player, s);
						if(mode == 2 ||mode == 3)plugin.getSpellManager().bindRight(player, s);
					}
					if(mode != 3)player.sendMessage(ChatColor.GREEN + s.toString() + " bound to " + (mode == 1 ? "left" : "right") + " hand");
					else player.sendMessage(ChatColor.GREEN + s.toString() + " bound to both hands");
				}
			}
			else
			{
				player.sendMessage("You aren't allowed to bind spells.");
			}
		}
		else if (command.getName().equalsIgnoreCase("addspell")) 
		{
			if(!sender.hasPermission("skyrimrpg.addspell"))return msgret(sender, ChatColor.RED + "You do not have permission to do this!");
			if(args.length != 2 && args.length != 1)
			{
				if(player == null)sender.sendMessage(ChatColor.RED + "Usage: /addspell <player> <spell>");
				else sender.sendMessage(ChatColor.RED + "Usage: /addspell [player] <spell>");
				return true;
			}
			if(args.length == 1)
			{
				if(player == null)return noConsole(sender);
				Spell s = null;
				try{s = Spell.valueOf(args[0].toUpperCase());}catch(IllegalArgumentException iae){return msgret(sender, ChatColor.RED + "No such spell: " + args[0] + "!");}
				PlayerData pd = pm.getData(player.getName());
				if(pd.hasSpell(s))return msgret(sender, ChatColor.RED + "You already have the spell " + s.toString() + "!");
				pd.addSpell(s);
				player.sendMessage(ChatColor.GREEN + "You have given " + ChatColor.BLUE + s.toString() + ChatColor.GREEN + " to yourself");
			}
			else if(args.length == 2)
			{
				Player t = sender.getServer().getPlayer(args[0]);
				if(t == null)return msgret(sender, ChatColor.RED + "No such player: " + args[0] + "!");
				Spell s = null;
				try{s = Spell.valueOf(args[1].toUpperCase());}catch(IllegalArgumentException iae){return msgret(sender, ChatColor.RED + "No such spell: " + args[1] + "!");}
				PlayerData pd = pm.getData(t.getName());
				if(pd.hasSpell(s))return msgret(sender, ChatColor.RED + "\"" + t.getName() + "\" already has the spell " + s.toString() + "!");
				pd.addSpell(s);
				sender.sendMessage(ChatColor.GREEN + t.getName() + " has been given the spell " + s.toString());
				t.sendMessage(ChatColor.GREEN + "You have been given the spell " + s.toString());
			}
		}
		else if (command.getName().equalsIgnoreCase("removespell")) 
		{
			if(!sender.hasPermission("skyrimrpg.removespell"))return msgret(sender, ChatColor.RED + "You do not have permission to do this!");
			if(args.length != 2 && args.length != 1)
			{
				if(player == null)sender.sendMessage(ChatColor.RED + "Usage: /removespell <player> <spell>");
				else sender.sendMessage(ChatColor.RED + "Usage: /removespell [player] <spell>");
				return true;
			}
			if(args.length == 1)
			{
				if(player == null)return noConsole(sender);
				Spell s = null;
				try{s = Spell.valueOf(args[0].toUpperCase());}catch(IllegalArgumentException iae){return msgret(sender, ChatColor.RED + "No such spell: " + args[0] + "!");}
				PlayerData pd = pm.getData(player.getName());
				if(!pd.hasSpell(s))return msgret(sender, ChatColor.RED + "You don't have the spell " + s.toString() + "!");
				pd.removeSpell(s);
				player.sendMessage(ChatColor.GREEN + "You have removed " + ChatColor.BLUE + s.toString() + ChatColor.GREEN + " from yourself");
			}
			else if(args.length == 2)
			{
				Player t = sender.getServer().getPlayer(args[0]);
				if(t == null)return msgret(sender, ChatColor.RED + "No such player: " + args[0] + "!");
				Spell s = null;
				try{s = Spell.valueOf(args[1].toUpperCase());}catch(IllegalArgumentException iae){return msgret(sender, ChatColor.RED + "No such spell: " + args[1] + "!");}
				PlayerData pd = pm.getData(t.getName());
				if(!pd.hasSpell(s))return msgret(sender, ChatColor.RED + "Doesn't have the spell " + s.toString() + "!");
				pd.addSpell(s);
				sender.sendMessage(ChatColor.GREEN + t.getName() + " has had the spell " + s.toString() + " taken away");
				t.sendMessage(ChatColor.GREEN + "You have had the spell " + s.toString() + " removed from you");
			}
		}
		else if (command.getName().equalsIgnoreCase("listspells")) 
		{
			if(args.length != 0 && args.length != 1)
			{
				if(player == null)return msgret(sender, ChatColor.RED + "Usage: /listspells [player]");
				else return msgret(sender, ChatColor.RED + "Usage: /listspells <player>");
			}
			Player target = null;
			if(args.length == 0)
			{
				if(player != null)target = player;
				else return msgret(sender, ChatColor.RED + "Usage: /listspells <player>");
			}
			else if(args.length == 1)
			{
				target = sender.getServer().getPlayer(args[0]);
				if(target == null)msgret(sender, ChatColor.RED + "No such player: " + args[0]);
			}
			sender.sendMessage(ChatColor.BLUE + target.getName() + "'s spells");
			for(Spell s:pm.getData(target.getName()).getSpells())sender.sendMessage(s.getDisplayName());
		}
		else if(command.getName().equalsIgnoreCase("skyrimrpg") || label.equalsIgnoreCase("srpg"))
		{
			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.YELLOW + "SkyrimRPG version " + plugin.getDescription().getVersion());
				sender.sendMessage(ChatColor.GREEN + "Made by dbizzle and Technius");
				if(plugin.getLatestVersion() != null && plugin.getVersionManager().compareVersion(plugin.getLatestVersion(), plugin.getDescription().getVersion())&& sender.hasPermission("skyrimrpg.newversion"))
				{
					if(plugin.getVersionMessage().indexOf("DEV") > -1 && !cm.announceDevBuild);
					else if(sender.hasPermission("skyrimrpg.newversion"))sender.sendMessage(ChatColor.RED + "!!!!" + ChatColor.GOLD + plugin.getVersionMessage() +  ChatColor.RED + "!!!!");
				}
				sender.sendMessage("========================");
				sender.sendMessage(ChatColor.RED + "/skystats <page>" + ChatColor.YELLOW + " - displays your stats");
				if(sender.hasPermission("skyrimrpg.setlevel"))sender.sendMessage(ChatColor.RED + "/skyrimrpg setlevel <skill> <level> [player]" + ChatColor.YELLOW + " - sets the level of the specified skill");
				if(sender.hasPermission("skyrimrpg.reload"))sender.sendMessage(ChatColor.RED + "/skyrimrpg reload" + ChatColor.YELLOW + " - reloads the configuration file");
				if(sender.hasPermission("skyrimrpg.refresh"))sender.sendMessage(ChatColor.RED + "/skyrimrpg refresh" + ChatColor.YELLOW + " - refreshes the configuration file by adding new values, useful when updating");
				sender.sendMessage(ChatColor.RED + "/perk" + ChatColor.YELLOW + " - shows the perk menu");
			}
			else if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("setlevel"))
				{
					if(args.length != 3 && args.length != 4)return msgret(sender, ChatColor.RED + "Usage: /srpg setlevel <skill> <level> [player]");
					if(!sender.hasPermission("skyrimrpg.setlevel"))return noPerm(sender);
					Skill skill = Skill.getSkill(args[1]);
					if(skill == null)msgret(sender, ChatColor.RED + "No such skill: " + args[1]);
					int l;
					try{ l = Integer.parseInt(args[2]);}catch(NumberFormatException nfe){sender.sendMessage(ChatColor.RED + "That is not a valid number."); return true;}
					Player target = null;
					if(args.length == 3)
					{
						if(player ==  null)return msgret(sender, ChatColor.RED + "Usage: /srpg setlevel <skill> <level> <player>");
						else target = player;
					}
					else if(args.length == 4)
					{
						target = sender.getServer().getPlayer(args[3]);
						if(target == null)return msgret(sender, ChatColor.RED + "No such player: " + args[3]);
						if(target != player && !sender.hasPermission("skyrimrpg.setlevel.other"))return noPerm(sender);
					}
					pm.getData(target.getName()).setSkillLevel(skill, l);
					sender.sendMessage(ChatColor.GREEN + skill.getName() + " set to level " + l);
					return true;
				}
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(!sender.hasPermission("skyrimrpg.reload"))noPerm(sender);
					cm.loadConfig();
					sender.sendMessage(ChatColor.GREEN + "Configuration file reloaded successfully.");
				}
				else if(args[0].equalsIgnoreCase("refresh"))
				{
					if(!sender.hasPermission("skyrimrpg.refresh"))noPerm(sender);
					cm.refreshConfig();
					sender.sendMessage(ChatColor.GREEN + "Configuration file refreshed successfully.");
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("skystats"))
		{
			if(player == null)
			{
				sender.sendMessage(ChatColor.RED + "You don't have stats!");
			}
			else
			{
				int page = 1;
				if(args.length != 0 && args.length != 1)
				{
					player.sendMessage(ChatColor.RED + "Usage: /skystats <page>");
					return true;
				}
				if(args.length == 1)
				{
					try{page = Integer.parseInt(args[0]);}catch(NumberFormatException nfe){page = 1;}
				}
				PlayerData pd = pm.getData(player.getName());
				if(page <= 0)page = 1;
				switch(page)
				{
				case 1:
					player.sendMessage(ChatColor.GOLD + "Stats Page 1 of 2");
					player.sendMessage(ChatColor.RED + "Combat" + ChatColor.WHITE + "|" + ChatColor.BLUE + "Magic" + ChatColor.WHITE + "|" + ChatColor.GRAY + "Stealth");
					player.sendMessage(ChatColor.GREEN + "Level: " + pd.getLevel());
					player.sendMessage(ChatColor.BLUE + "Magicka: " + pd.getMagicka());
					player.sendMessage(ChatColor.RED + "Archery: Level " + pd.getSkillLevel(Skill.ARCHERY));
					player.sendMessage(ChatColor.RED + "Swordsmanship: Level " + pd.getSkillLevel(Skill.SWORDSMANSHIP));
					player.sendMessage(ChatColor.RED + "Axecraft: Level " + pd.getSkillLevel(Skill.AXECRAFT));
					player.sendMessage(ChatColor.RED + "Blocking: Level " + pd.getSkillLevel(Skill.BLOCKING));
					player.sendMessage(ChatColor.RED + "Armor: Level " + pd.getSkillLevel(Skill.ARMOR));
					player.sendMessage(ChatColor.BLUE + "Destruction: Level " + pd.getSkillLevel(Skill.DESTRUCTION));
					break;
				case 2:
					player.sendMessage(ChatColor.GOLD + "Stats Page 2 of 2");
					player.sendMessage(ChatColor.BLUE + "Conjuration: Level " + pd.getSkillLevel(Skill.CONJURATION));
					player.sendMessage(ChatColor.BLUE + "Restoration: Level " + pd.getSkillLevel(Skill.RESTORATION));
					player.sendMessage(ChatColor.GRAY + "Pickpocketing: Level " + pd.getSkillLevel(Skill.PICKPOCKETING));
					player.sendMessage(ChatColor.GRAY + "Lockpicking: Level " + pd.getSkillLevel(Skill.LOCKPICKING));
					player.sendMessage(ChatColor.GRAY + "Sneak: Level " + pd.getSkillLevel(Skill.SNEAK));
					break;
				default:
					player.sendMessage(ChatColor.GOLD + "Stats Page " + page + " of 2");
					break;
				}
			}
		}
		return true;
	}
	private boolean msgret(CommandSender sender, String message)
	{
		sender.sendMessage(message);
		return true;
	}
	private boolean noConsole(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED + "Console cannot use this command!");
		return true;
	}
	private boolean noPerm(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
		return true;
	}
}
