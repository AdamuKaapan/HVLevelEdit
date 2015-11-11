package com.hvleveledit;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;
import com.osreboot.ridhvl.painter.painter2d.HvlTiledRect;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class MainEditorWindow extends HvlTemplateInteg2D {

	public static float bottomBarHeight = 256f, sideBarWidth = 384f;
	
	HvlTiledRect bottomMenuBar;
	HvlTiledRect sideMenuBar;
	
	public MainEditorWindow() {
		super(60, 1366, 768, "HVLevelEdit", new HvlDisplayModeResizable());
	}

	@Override
	public void initialize() {
		getTextureLoader().loadResource("MenuBackground");
		
		bottomMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 64, 64);
		sideMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, sideBarWidth, 512, 64, 64);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void update(float delta) {
		bottomMenuBar.setY(Display.getHeight() - bottomBarHeight);
		bottomMenuBar.setTotalWidth(Display.getWidth());
		
		sideMenuBar.setTotalHeight(Display.getHeight() - bottomBarHeight);
		
		draw(delta);
	}
	
	public void draw(float delta) {
		bottomMenuBar.draw();
		sideMenuBar.draw();
	}
}
