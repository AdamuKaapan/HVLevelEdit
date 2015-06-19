package com.hvleveledit;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;

public class Main {

	public static void main(String[] args) {
		new HVLevelEditMainForm(60, 1300, 400, "HVLevelEdit", new HvlDisplayModeResizable()).start();
	}
}
