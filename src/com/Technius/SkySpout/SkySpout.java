package com.Technius.SkySpout;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.getspout.spout.Spout;

public class SkySpout 
{
	private SkyrimRPG p;
	public SkySpout(SkyrimRPG p)
	{
		this.p = p;
	}
	private boolean useSpout = false;
	Plugin spoutraw = null;
	public boolean checkSpout(PluginManager pm)
	{
		spoutraw = pm.getPlugin("Spout");
		if(spoutraw!= null)
		{
			useSpout = true;
			return true;
		}
		else return false;
	}
	public boolean useSpout()
	{
		return useSpout;
	}
	public void init()
	{
		SkyRecipieManager srm = new SkyRecipieManager((Spout)spoutraw, p);
		srm.setupRecipies();
	}
}