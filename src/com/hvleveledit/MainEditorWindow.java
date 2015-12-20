package com.hvleveledit;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import com.hvleveledit.swing.NewFileDialog;
import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.HvlTextureUtil;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;
import com.osreboot.ridhvl.map.HvlMap;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureDrawable;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class MainEditorWindow extends HvlTemplateInteg2D {

	public static float bottomBarHeight = 96f, sideBarWidth = 384f;

	private HvlMenu menu;

	private HvlArrangerBox bottomMenuArranger;

	private HvlLabeledButton newButton, openButton, saveButton;

	private HvlFontPainter2D font;

	private HvlMap map;

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
		getTextureLoader().loadResource("TileSquare");

		font = new HvlFontPainter2D(getTextureLoader().getResource(1), HvlFontUtil.DEFAULT, 112, 144, 0, 0.2f);

		menu = new HvlMenu();

		bottomMenuArranger = new HvlArrangerBox(0, 0, 512, bottomBarHeight, HvlArrangerBox.ArrangementStyle.HORIZONTAL);
		bottomMenuArranger.setBorderU(16);
		bottomMenuArranger.setBorderD(16);
		bottomMenuArranger.setBorderL(32);
		bottomMenuArranger.setBorderR(0);
		bottomMenuArranger.setxAlign(0.0f);
		bottomMenuArranger.setyAlign(0.5f);

		HvlComponentDefault
				.setDefault(new HvlLabeledButton.Builder()
						.setOffDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.darkGray)))
						.setHoverDrawable(
								new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.lightGray)))
				.setOnDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.darkGray)))
				.setFont(font).setTextColor(Color.white).setWidth(64).setHeight(64).setTextScale(0.7f).build());

		newButton = new HvlLabeledButton.Builder().setText("new").build();
		openButton = new HvlLabeledButton.Builder().setText("open").build();
		saveButton = new HvlLabeledButton.Builder().setText("save").build();

		newButton.setClickedCommand(new HvlAction1<HvlButton>() {

			@Override
			public void run(HvlButton a) {
				NewFileDialog dialog = new NewFileDialog();
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);

				try {
					BufferedImage loaded = ImageIO.read(new File(dialog.tilemapPathTextBox.getText()));
					Texture tmapTexture = BufferedImageUtil.getTexture("tilemap", loaded);

					map = new HvlMap(0, 0, 64, 64, (Integer) dialog.tilemapWidthSpinner.getValue(),
							(Integer) dialog.tilemapHeightSpinner.getValue(), (Integer) dialog.layersSpinner.getValue(),
							(Integer) dialog.mapWidthSpinner.getValue(), (Integer) dialog.mapHeightSpinner.getValue(),
							tmapTexture);

					map.setX(((Display.getWidth() - sideBarWidth) / 2) + sideBarWidth
							- ((map.getMapWidth() * map.getTileWidth()) / 2));
					map.setY(((Display.getHeight() - bottomBarHeight) / 2)
							- ((map.getMapHeight() * map.getTileHeight()) / 2));

					for (int x = 0; x < map.getMapWidth(); x++) {
						for (int y = 0; y < map.getMapHeight(); y++) {
							map.setTile(0, x, y, 0);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		bottomMenuArranger.setY(Display.getHeight() - bottomBarHeight);
		bottomMenuArranger.setWidth(Display.getWidth());

		if (map != null) {
			map.update(delta);
		}

		draw(delta);
	}

	public void draw(float delta) {
		if (map != null) {
			map.draw(delta);
			drawCellHighlight();
		}

		HvlPainter2D.hvlDrawQuad(0, Display.getHeight() - bottomBarHeight, Display.getWidth(), bottomBarHeight,
				Color.gray);
		HvlPainter2D.hvlDrawQuad(0, 0, sideBarWidth, Display.getHeight() - bottomBarHeight, Color.gray);

		HvlMenu.updateMenus(delta);
	}

	private void drawCellHighlight() {
		if (HvlCursor.getCursorX() < sideBarWidth || HvlCursor.getCursorY() > Display.getHeight() - bottomBarHeight) return;
		
		if (HvlCursor.getCursorX() < map.getX()
				|| HvlCursor.getCursorX() > map.getX() + (map.getMapWidth() * map.getTileWidth()))
			return;
		
		if (HvlCursor.getCursorY() < map.getY()
				|| HvlCursor.getCursorY() > map.getY() + (map.getMapHeight() * map.getTileHeight()))
			return;
		
		int tX = map.worldXToTile(HvlCursor.getCursorX());
		int tY = map.worldYToTile(HvlCursor.getCursorY());

		HvlPainter2D.hvlDrawQuad(map.getX() + (tX * map.getTileWidth()), map.getY() + (tY * map.getTileHeight()),
				map.getTileWidth(), map.getTileHeight(), getTextureLoader().getResource(2));
	}
}
