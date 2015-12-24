package com.hvleveledit;

import com.osreboot.ridhvl.configold.HvlConfigUtil;

public class MainConfig {

	public static String recentTilemapPath;
	public static int recentTilemapWidth, recentTilemapHeight;
	public static int recentMapLayers;
	
	public MainConfig() {
		
	}
	
	public static void load() {
		recentTilemapPath = "";
		HvlConfigUtil.loadStaticConfig(MainConfig.class, "Conf");
		recentTilemapPath = recentTilemapPath.replaceAll(";", ":");
	}
	
	public static void save() {
		// Noone will eeeeeeever knoooowwwww... :P
		recentTilemapPath = recentTilemapPath.replaceAll(":", ";");
		HvlConfigUtil.saveStaticConfig(MainConfig.class, "Conf");
		recentTilemapPath = recentTilemapPath.replaceAll(";", ":");
	}
}
