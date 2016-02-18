package com.hvleveledit;

import com.osreboot.ridhvl.config.HvlConfigUtil;

public class MainConfig {

	public static String recentTilemapPath;
	public static int recentTilemapWidth, recentTilemapHeight;
	public static int recentMapLayers;
	
	public MainConfig() {
		
	}
	
	public static void load() {
		recentTilemapPath = "";
		HvlConfigUtil.load(MainConfig.class, "Conf", false, true);
		recentTilemapPath = recentTilemapPath.replaceAll(";", ":");
	}
	
	public static void save() {
		// Noone will eeeeeeever knoooowwwww... :P
		recentTilemapPath = recentTilemapPath.replaceAll(":", ";");
		HvlConfigUtil.save(null, "Conf", false, true);
		recentTilemapPath = recentTilemapPath.replaceAll(";", ":");
	}
}
