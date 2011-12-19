package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SRPGPL extends PlayerListener
{
	private SkyrimRPG plugin;
	public SRPGPL(SkyrimRPG p)
	{
		plugin = p;
	}
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getPlayer().getItemInHand().getType() != Material.BLAZE_ROD)return;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			plugin.st.chargeFireball(event.getPlayer());
			event.getPlayer().sendMessage("Charging fireball...");
		}
		else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			int m = plugin.st.unchargeFireball(event.getPlayer());
			if(m == -1) return;
			plugin.sm.shootFireball(event.getPlayer(), m);
			event.getPlayer().sendMessage("Fireball shot!");
		}
	}
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.loadSkills(event.getPlayer());
	}
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.saveSkills(event.getPlayer());
	}
}
