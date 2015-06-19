package com.osreboot.ridhvl.hvleveledit;

import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl.display.HvlDisplayMode;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureButton;
import com.osreboot.ridhvl.painter.painter2d.HvlExpandingRectangle;
import com.osreboot.ridhvl.template.HvlTemplateInteg2DBasic;

public class HVLevelEditMainForm extends HvlTemplateInteg2DBasic {

	private HvlMenu mainMenu;
	private HvlArrangerBox menuBar;
	private HvlButton newButton, openButton, saveButton;

	private HvlExpandingRectangle menuBarBackground;

	public HVLevelEditMainForm(int frameRateArg, int width, int height,
			String title, HvlDisplayMode displayModeArg) {
		super(frameRateArg, width, height, title, 25, 5, displayModeArg);
	}

	@Override
	public void initialize() {
		getTextureLoader().loadResource("White");
		getTextureLoader().loadResource("ButtonOff");
		getTextureLoader().loadResource("ButtonHover");
		getTextureLoader().loadResource("ButtonOn");
		getTextureLoader().loadResource("MenuBar");

		menuBar = new HvlArrangerBox(0, 0, Display.getWidth(), 96, Display.getHeight(), ArrangementStyle.HORIZONTAL);
		menuBar.setAlign(0.5f);
		menuBar.setBorderL(16f);

		menuBarBackground = new HvlExpandingRectangle(getTextureLoader().getResource(4),
				0.0625f, 0.9375f, 0.0625f, 0.9375f, 0, 0, 0, menuBar.getHeight(), 8, 8);
		
		newButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(), getTextureLoader().getResource(1), getTextureLoader().getResource(2), getTextureLoader().getResource(3)) {

		};
		openButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(), getTextureLoader().getResource(1), getTextureLoader().getResource(2), getTextureLoader().getResource(3)) {

		};
		saveButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(), getTextureLoader().getResource(1), getTextureLoader().getResource(2), getTextureLoader().getResource(3)) {

		};

		mainMenu = new HvlMenu() {

		};
		mainMenu.add(menuBar);
		menuBar.add(newButton);
		menuBar.add(openButton);
		menuBar.add(saveButton);

		HvlMenu.setCurrent(mainMenu);
	}

	@Override
	public void update(float delta) {
		menuBar.setY(Display.getHeight() - menuBar.getHeight());
		menuBar.setWidth(Display.getWidth());
		menuBar.setHeightInversion(Display.getHeight());
		menuBarBackground.setY(menuBar.getY());
		menuBarBackground.setTotalWidth(menuBar.getWidth());
		
		draw(delta);
	}
	
	private void draw(float delta)
	{
		menuBarBackground.draw();
		HvlMenu.updateMenus(delta);
	}
}
