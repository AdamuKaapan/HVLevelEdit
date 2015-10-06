package com.hvleveledit;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;

public class Main {

	public static void main(String[] args){
		HVLevelEditMainForm form = new HVLevelEditMainForm(60, 1300, 600, "HVLevelEdit", new HvlDisplayModeResizable());
		if(args.length > 0) form.loadFile(args[0]);
	}	
}
