package com.hvleveledit;

import java.awt.Dialog.ModalityType;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.hvleveledit.swing.NewFileDialog;
import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.action.HvlAction2;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;
import com.osreboot.ridhvl.menu.HvlComponent;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlButton;
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
				button.setTextScale(0.09f);
			else
				button.setTextScale(0.10f);

			a.update(delta);
		}
	}

	public static float bottomBarHeight = 196f, sideBarWidth = 384f;

	HvlTiledRect bottomMenuBar;
	HvlTiledRect sideMenuBar;

	HvlMenu menu;

	HvlArrangerBox bottomMenuArranger;

	HvlLabeledButton newButton, openButton, saveButton;

	HvlFontPainter2D font;

	public MainEditorWindow() {
		super(60, 1366, 768, "HVLevelEdit", new HvlDisplayModeResizable());
	}

	@Override
	public void exit() {
		MainConfig.save();
		super.exit();
	}
	
	@Override
	public void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		MainConfig.load();
		MainConfig.save();
		SessionVars.loadTilemapDataFromMainConfig();

		getTextureLoader().loadResource("MenuBackground");
		getTextureLoader().loadResource("Font");

		font = new HvlFontPainter2D(getTextureLoader().getResource(1), HvlFontUtil.DEFAULT, 192, 256, 10, 1f);

		menu = new HvlMenu();

		bottomMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 64, 64);
		sideMenuBar = new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, sideBarWidth, 512, 64, 64);

		bottomMenuArranger = new HvlArrangerBox(0, 0, 512, 512, HvlArrangerBox.ArrangementStyle.HORIZONTAL);
		bottomMenuArranger.setBorderU(32);
		bottomMenuArranger.setBorderD(32);
		bottomMenuArranger.setBorderL(32);
		bottomMenuArranger.setBorderR(0);
		bottomMenuArranger.setxAlign(0.0f);
		bottomMenuArranger.setyAlign(0.5f);

		HvlComponentDefault
				.setDefault(
						new HvlLabeledButton.Builder()
								.setOffDrawable(new HvlTiledRectDrawable(new HvlTiledRect(getTexture(0), 0.25f, 0.75f,
										0, 0, 512, bottomBarHeight, 32, 32)))
				.setHoverDrawable(new HvlTiledRectDrawable(
						new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 32, 32)))
				.setOnDrawable(new HvlTiledRectDrawable(
						new HvlTiledRect(getTexture(0), 0.25f, 0.75f, 0, 0, 512, bottomBarHeight, 32, 32)))
				.setFont(font).setTextColor(Color.white).setWidth(128).setHeight(128)
				.setUpdateOverride(new ButtonCustomAnimationAction()).build());

		newButton = new HvlLabeledButton.Builder().setText("new").build();
		openButton = new HvlLabeledButton.Builder().setText("open").build();
		saveButton = new HvlLabeledButton.Builder().setText("save").build();

		newButton.setClickedCommand(new HvlAction1<HvlButton>() {

			@Override
			public void run(HvlButton a) {
				NewFileDialog dialog = new NewFileDialog();
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);
			}
		});

		bottomMenuArranger.add(newButton);
		bottomMenuArranger.add(openButton);
		bottomMenuArranger.add(saveButton);
		menu.add(bottomMenuArranger);

		HvlMenu.setCurrent(menu);
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
