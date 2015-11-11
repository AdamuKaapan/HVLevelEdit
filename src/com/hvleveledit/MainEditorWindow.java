package com.hvleveledit;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.action.HvlAction2;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;
import com.osreboot.ridhvl.menu.HvlComponent;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTiledRectDrawable;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlTiledRect;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class MainEditorWindow extends HvlTemplateInteg2D {

	public class ButtonCustomAnimationAction extends HvlAction2<HvlComponent, Float> {
		@Override
		public void run(HvlComponent a, Float delta) {
			HvlLabeledButton button = (HvlLabeledButton) a;
					
			if (button.isBeingPressed(0))
				button.setTextColor(Color.orange);
			else
				button.setTextColor(Color.white);
			
			if (button.isHovering())
				button.setTextScale(0.14f);
			else
				button.setTextScale(0.15f);
			
			a.update(delta);
		}
	}

	public static float bottomBarHeight = 256f, sideBarWidth = 384f;

	HvlTiledRect bottomMenuBar;
	HvlTiledRect sideMenuBar;

	HvlMenu menu;

	HvlArrangerBox bottomMenuArranger;

	HvlLabeledButton newButton;

	HvlFontPainter2D font;

	public MainEditorWindow() {
		super(60, 1366, 768, "HVLevelEdit", new HvlDisplayModeResizable());
	}

	@Override
	public void initialize() {
		getTextureLoader().loadResource("MenuBackground");
		getTextureLoader().loadResource("Font");

		font = new HvlFontPainter2D(getTextureLoader().getResource(1), HvlFontUtil.DEFAULT, 2048, 2048, 192, 256, 10);

		menu = new HvlMenu();

		bottomMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 64, 64);
		sideMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, sideBarWidth, 512, 64, 64);

		bottomMenuArranger = new HvlArrangerBox(0, 0, 512, 512, HvlArrangerBox.ArrangementStyle.HORIZONTAL);
		bottomMenuArranger.setBorderU(32);
		bottomMenuArranger.setBorderD(32);
		bottomMenuArranger.setBorderL(32);
		bottomMenuArranger.setBorderR(32);
		bottomMenuArranger.setxAlign(0.0f);
		bottomMenuArranger.setyAlign(0.5f);

		HvlComponentDefault
				.setDefault(
						new HvlLabeledButton.Builder()
								.setOffDrawable(new HvlTiledRectDrawable(new HvlTiledRect(getTexture(0), 0.25f, 0.75f,
										0, 0, 512, bottomBarHeight, 64, 64)))
				.setHoverDrawable(new HvlTiledRectDrawable(
						new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 64, 64)))
				.setOnDrawable(new HvlTiledRectDrawable(
						new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 64, 64)))
				.setFont(font).setTextColor(Color.white).setUpdateOverride(new ButtonCustomAnimationAction()).build());
				
		newButton = new HvlLabeledButton.Builder().setText("new").setWidth(256).setHeight(128).build();
		
		bottomMenuArranger.add(newButton);
		menu.add(bottomMenuArranger);

		HvlMenu.setCurrent(menu);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void update(float delta) {

		bottomMenuBar.setY(Display.getHeight() - bottomBarHeight);
		bottomMenuBar.setTotalWidth(Display.getWidth());

		bottomMenuArranger.setX(bottomMenuBar.getX());
		bottomMenuArranger.setY(bottomMenuBar.getY());
		bottomMenuArranger.setWidth(bottomMenuBar.getTotalWidth());
		bottomMenuArranger.setHeight(bottomMenuBar.getTotalHeight());

		sideMenuBar.setTotalHeight(Display.getHeight() - bottomBarHeight);

		draw(delta);
	}

	public void draw(float delta) {
		bottomMenuBar.draw();
		sideMenuBar.draw();

		HvlMenu.updateMenus(delta);
	}
}
