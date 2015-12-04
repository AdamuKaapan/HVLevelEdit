package com.hvleveledit;

import com.osreboot.ridhvl.config.HvlConfigUtil;

public class MainConfig {

	public MainConfig() {
		
	}
	
	public static void load() {
		HvlConfigUtil.loadStaticConfig(MainConfig.class, "Conf");
	}
	
	public static void save() {
		HvlConfigUtil.saveStaticConfig(MainConfig.class, "Conf");
	}
}
