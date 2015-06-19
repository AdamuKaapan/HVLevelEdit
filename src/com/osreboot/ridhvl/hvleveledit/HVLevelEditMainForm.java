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
		getTextureLoader().loadResource("MenuBar");
		getTextureLoader().loadResource("NewButton/Off");
		getTextureLoader().loadResource("NewButton/Hover");
		getTextureLoader().loadResource("NewButton/On");
		getTextureLoader().loadResource("OpenButton/Off");
		getTextureLoader().loadResource("OpenButton/Hover");
		getTextureLoader().loadResource("OpenButton/On");
		getTextureLoader().loadResource("SaveButton/Off");
		getTextureLoader().loadResource("SaveButton/Hover");
		getTextureLoader().loadResource("SaveButton/On");

		menuBar = new HvlArrangerBox(0, 0, Display.getWidth(), 96,
				Display.getHeight(), ArrangementStyle.HORIZONTAL);
		menuBar.setAlign(0.5f);
		menuBar.setBorderL(16f);

		menuBarBackground = new HvlExpandingRectangle(getTextureLoader()
				.getResource(1), 0.0625f, 0.9375f, 0.0625f, 0.9375f, 0, 0, 0,
				menuBar.getHeight(), 8, 8);

		newButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(2), getTextureLoader()
						.getResource(3), getTextureLoader().getResource(4)) {

		};
		openButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(5), getTextureLoader()
						.getResource(6), getTextureLoader().getResource(7)) {

		};
		saveButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(8), getTextureLoader()
						.getResource(9), getTextureLoader().getResource(10)) {

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
		newButton.setHeightInversion(Display.getHeight());
		openButton.setHeightInversion(Display.getHeight());
		saveButton.setHeightInversion(Display.getHeight());

		draw(delta);
	}

	private void draw(float delta) {
		menuBarBackground.draw();
		HvlMenu.updateMenus(delta);
	}
}
