package com.hvleveledit;

public class SessionVars {
	public static String tilemapPath;
	public static int tilemapWidth, tilemapHeight;
	public static int mapLayers;
	
	public static void saveTilemapDataToMainConfig() {
		MainConfig.recentTilemapPath = tilemapPath;
		MainConfig.recentTilemapWidth = tilemapWidth;
		MainConfig.recentTilemapHeight = tilemapHeight;
		MainConfig.recentMapLayers = mapLayers;
	}
	
	public static void loadTilemapDataFromMainConfig() {
		System.out.println("MCfg " + MainConfig.recentTilemapPath);
		tilemapPath = MainConfig.recentTilemapPath == null ? "" : MainConfig.recentTilemapPath;
		tilemapWidth = Math.max(MainConfig.recentTilemapWidth, 1);
		tilemapHeight = Math.max(MainConfig.recentTilemapHeight, 1);
		mapLayers = Math.max(MainConfig.recentMapLayers, 1);
	}
}
