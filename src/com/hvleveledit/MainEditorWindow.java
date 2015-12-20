package com.hvleveledit;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

	public static final int dragKey = Keyboard.KEY_LSHIFT;

	public static float bottomBarHeight = 96f, sideBarWidth = 384f;

	private HvlMenu menu;

	private HvlArrangerBox bottomMenuArranger;

	private HvlLabeledButton newButton, openButton, saveButton;

	private HvlFontPainter2D font;

	private HvlMap map;

	private int selectedTile;

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

		updateInput();

		if (map != null) {
			map.update(delta);
		}

		draw(delta);
	}

	private void updateInput() {
		// Dragging the map
		if (map != null && Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(0)) {
			map.setX(map.getX() + Mouse.getDX());
			map.setY(map.getY() - Mouse.getDY());

			// Clamp map inside screen
			map.setX(Math.max(Math.min(map.getX(), Display.getWidth() - map.getTileWidth()),
					sideBarWidth - ((map.getMapWidth() - 1) * map.getTileWidth())));
			map.setY(Math.max(Math.min(map.getY(), Display.getHeight() - bottomBarHeight - map.getTileHeight()),
					-((map.getMapHeight() - 1) * map.getTileHeight())));
		}

		// Tile scroll-select
		if (map != null) {
			selectedTile += (Mouse.getDWheel() / 120);
			if (selectedTile < 0)
				selectedTile = (map.getTilesAcross() * map.getTilesTall()) - 1;
			if (selectedTile >= map.getTilesAcross() * map.getTilesTall())
				selectedTile = 0;
		}

		// Tile setting
		if (map != null && cursorInMap() && !Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(0)) {
			map.setTile(0, map.worldXToTile(HvlCursor.getCursorX()), map.worldYToTile(HvlCursor.getCursorY()),
					selectedTile);
		}
		
		// Tile erasing
		if (map != null && cursorInMap() && !Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(1)) {
			map.setTile(0, map.worldXToTile(HvlCursor.getCursorX()), map.worldYToTile(HvlCursor.getCursorY()),
					-1);
		}
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

		drawTileSelect();
	}

	private void drawCellHighlight() {
		if (Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(0))
			return;

		if (HvlCursor.getCursorX() < sideBarWidth || HvlCursor.getCursorY() > Display.getHeight() - bottomBarHeight)
			return;

		if (!cursorInMap())
			return;

		int tX = map.worldXToTile(HvlCursor.getCursorX());
		int tY = map.worldYToTile(HvlCursor.getCursorY());

		HvlPainter2D.hvlDrawQuad(map.getX() + (tX * map.getTileWidth()), map.getY() + (tY * map.getTileHeight()),
				map.getTileWidth(), map.getTileHeight(), getTextureLoader().getResource(2));
	}

	private void drawTileSelect() {
		if (map == null)
			return;

		HvlPainter2D.hvlDrawQuad(16, 16, sideBarWidth - 32, sideBarWidth - 32, map.getTexture());

		drawTileSelectSquare();
	}

	private void drawTileSelectSquare() {
		if (map == null)
			return;

		int tX = selectedTile % map.getTilesAcross();
		int tY = selectedTile / map.getTilesAcross();

		float drawWidth = sideBarWidth - 32;
		HvlPainter2D.hvlDrawQuad(16 + ((drawWidth / map.getTilesAcross()) * tX),
				16 + ((drawWidth / map.getTilesTall()) * tY), drawWidth / map.getTilesAcross(),
				drawWidth / map.getTilesTall(), getTextureLoader().getResource(2));
	}

	private boolean cursorInMap() {
		return HvlCursor.getCursorX() > map.getX()
				&& HvlCursor.getCursorX() < map.getX() + (map.getMapWidth() * map.getTileWidth())
				&& HvlCursor.getCursorY() > map.getY()
				&& HvlCursor.getCursorY() < map.getY() + (map.getMapHeight() * map.getTileHeight());
	}
}
