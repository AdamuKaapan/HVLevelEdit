package com.hvleveledit;

public class SessionVars {
	public static String tilemapPath;
	public static int tilemapWidth, tilemapHeight;
	public static int mapLayers;
	public static String currentFile;
	
	public static void saveTilemapDataToMainConfig() {
		MainConfig.recentTilemapPath = tilemapPath;
		MainConfig.recentTilemapWidth = tilemapWidth;
		MainConfig.recentTilemapHeight = tilemapHeight;
		MainConfig.recentMapLayers = mapLayers;
	}
	
	public static void loadTilemapDataFromMainConfig() {
		tilemapPath = MainConfig.recentTilemapPath == null ? "" : MainConfig.recentTilemapPath;
		tilemapWidth = Math.max(MainConfig.recentTilemapWidth, 1);
		tilemapHeight = Math.max(MainConfig.recentTilemapHeight, 1);
		mapLayers = Math.max(MainConfig.recentMapLayers, 1);
	}
}
