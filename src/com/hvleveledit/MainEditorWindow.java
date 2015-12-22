package com.hvleveledit;

import java.awt.Dialog.ModalityType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import com.hvleveledit.swing.NewFileDialog;
import com.hvleveledit.swing.OpenFileDialog;
import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.HvlTextureUtil;
import com.osreboot.ridhvl.action.HvlAction1;
import com.osreboot.ridhvl.action.HvlAction2;
import com.osreboot.ridhvl.display.collection.HvlDisplayModeResizable;
import com.osreboot.ridhvl.map.HvlMap;
import com.osreboot.ridhvl.menu.HvlComponentDefault;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlLabel;
import com.osreboot.ridhvl.menu.component.HvlSlider;
import com.osreboot.ridhvl.menu.component.HvlTextBox;
import com.osreboot.ridhvl.menu.component.collection.HvlLabeledButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureDrawable;
import com.osreboot.ridhvl.painter.HvlCursor;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2D;

public class MainEditorWindow extends HvlTemplateInteg2D {

	public static final int dragKey = Keyboard.KEY_LSHIFT;
	public static final int zoomKey = Keyboard.KEY_LCONTROL;

	public static float bottomBarHeight = 96f, sideBarWidth = 384f;

	private HvlMenu menu;

	private HvlArrangerBox bottomMenuArranger;
	private HvlArrangerBox sideMenuArranger;
	private HvlArrangerBox layersArranger;

	private HvlLabeledButton newButton, openButton, saveButton;

	private HvlTextBox layerTextBox;
	private HvlLabeledButton layerUpButton, layerDownButton;
	private HvlSlider layerOpacitySlider;

	private HvlLabeledButton entitiesButton;

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

		font = new HvlFontPainter2D(getTextureLoader().getResource(1),
				(String.copyValueOf(HvlFontUtil.DEFAULT) + "-+").toCharArray(), 112, 144, 0, 0.2f);

		menu = new HvlMenu();

		bottomMenuArranger = new HvlArrangerBox(0, 0, 512, bottomBarHeight, HvlArrangerBox.ArrangementStyle.HORIZONTAL);
		bottomMenuArranger.setBorderU(16);
		bottomMenuArranger.setBorderD(16);
		bottomMenuArranger.setBorderL(32);
		bottomMenuArranger.setxAlign(0.0f);
		bottomMenuArranger.setyAlign(0.5f);

		sideMenuArranger = new HvlArrangerBox(16, sideBarWidth, sideBarWidth - 16,
				Display.getHeight() - bottomBarHeight - sideBarWidth, HvlArrangerBox.ArrangementStyle.VERTICAL);
		sideMenuArranger.setBorderR(16);
		sideMenuArranger.setBorderU(16);
		sideMenuArranger.setxAlign(0.0f);
		sideMenuArranger.setyAlign(0.0f);

		layersArranger = new HvlArrangerBox(0, 0, sideBarWidth - 32, 32, HvlArrangerBox.ArrangementStyle.HORIZONTAL);
		layersArranger.setBorderR(16);
		layersArranger.setxAlign(0.0f);
		layersArranger.setyAlign(0.5f);

		HvlComponentDefault
				.setDefault(new HvlLabeledButton.Builder()
						.setOffDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.darkGray)))
						.setHoverDrawable(
								new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.lightGray)))
				.setOnDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.darkGray)))
				.setFont(font).setTextColor(Color.white).setWidth(64).setHeight(64).setTextScale(0.7f).build());

		HvlComponentDefault
				.setDefault(new HvlTextBox.Builder()
						.setUnfocusedDrawable(
								new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.lightGray)))
						.setFocusedDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(64, 64, Color.white)))
						.setFont(font).setWidth(256).setHeight(32).setText("inserttexthere").build());

		HvlComponentDefault.setDefault(
				new HvlLabel.Builder().setFont(font).setColor(Color.white).setText("inserttexthere").build());

		HvlComponentDefault.setDefault(new HvlSlider.Builder().setWidth(256).setHeight(32)
				.setDirection(HvlSlider.Direction.HORIZONTAL).setHandleWidth(32).setHandleHeight(32)
				.setHandleStartOffset(16).setHandleEndOffset(16).setLiveSnap(false).setSnapInterval(0.01f)
				.setBackground(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(32, 32, Color.lightGray)))
				.setHandleDownDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(32, 32, Color.darkGray)))
				.setHandleUpDrawable(new HvlTextureDrawable(HvlTextureUtil.getColoredRect(32, 32, Color.darkGray))).build());

		layersArranger.add(new HvlLabel.Builder().setText("layer").build());

		layerTextBox = new HvlTextBox.Builder().setWidth(font.getFontWidth() * 3).setHeight(32).setNumbersOnly(true)
				.setBlacklistCharacters("-").setMaxCharacters(3).build();
		layerTextBox.setTextChangedCommand(new HvlAction2<HvlTextBox, String>() {

			@Override
			public void run(HvlTextBox a, String b) {
				updateOpacitySlider();
			}
		});
		layersArranger.add(layerTextBox);

		layerDownButton = new HvlLabeledButton.Builder().setText("-").setWidth(32f).setHeight(32f).build();
		layerDownButton.setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				if (map == null)
					return;

				if (layerTextBox.getText().isEmpty())
					return;

				int layer = Integer.parseInt(layerTextBox.getText());

				layerTextBox.setText(Math.min(Math.max(layer - 1, 0), map.getLayerCount() - 1) + "");
				updateOpacitySlider();
			}
		});
		layersArranger.add(layerDownButton);

		layerUpButton = new HvlLabeledButton.Builder().setText("+").setWidth(32f).setHeight(32f).build();
		layerUpButton.setClickedCommand(new HvlAction1<HvlButton>() {
			@Override
			public void run(HvlButton a) {
				if (map == null)
					return;

				if (layerTextBox.getText().isEmpty())
					return;

				int layer = Integer.parseInt(layerTextBox.getText());

				layerTextBox.setText(Math.min(Math.max(layer + 1, 0), map.getLayerCount() - 1) + "");
				updateOpacitySlider();
			}
		});
		layersArranger.add(layerUpButton);

		sideMenuArranger.add(layersArranger);

		layerOpacitySlider = new HvlSlider.Builder().setWidth(sideBarWidth - 32).setHeight(32).setValue(1.0f).build();
		layerOpacitySlider.setValueChangedCommand(new HvlAction2<HvlSlider, Float>() {
			@Override
			public void run(HvlSlider a, Float b) {
				if (map == null)
					return;
				if (layerTextBox.getText().isEmpty())
					return;

				map.setLayerOpacity(Integer.parseInt(layerTextBox.getText()), layerOpacitySlider.getValue());
			}
		});
		sideMenuArranger.add(layerOpacitySlider);
		
		entitiesButton = new HvlLabeledButton.Builder().setText("place entities").setWidth(sideBarWidth - 32).build();
		sideMenuArranger.add(entitiesButton);
		
		newButton = new HvlLabeledButton.Builder().setText("new").build();
		openButton = new HvlLabeledButton.Builder().setText("open").build();
		saveButton = new HvlLabeledButton.Builder().setText("save").build();

		newButton.setClickedCommand(new HvlAction1<HvlButton>() {

			@Override
			public void run(HvlButton a) {
				NewFileDialog dialog = new NewFileDialog();
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);

				if (!dialog.confirmed)
					return;

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

					SessionVars.currentFile = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		openButton.setClickedCommand(new HvlAction1<HvlButton>() {

			@Override
			public void run(HvlButton a) {
				OpenFileDialog dialog = new OpenFileDialog();
				dialog.setModalityType(ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);

				if (!dialog.confirmed)
					return;

				try {
					BufferedImage loaded = ImageIO.read(new File(dialog.tilemapPathTextBox.getText()));
					Texture tmapTexture = BufferedImageUtil.getTexture("tilemap", loaded);

					String path = dialog.pathTextBox.getText();
					if (path.endsWith(".hvlmap")) {
						path = path.substring(0, path.length() - 7);
					}

					map = HvlMap.load(path, 0, 0, 64, 64, tmapTexture, (Integer) dialog.tilemapWidthSpinner.getValue(),
							(Integer) dialog.tilemapHeightSpinner.getValue());
					map.setX(((Display.getWidth() - sideBarWidth) / 2) + sideBarWidth
							- ((map.getMapWidth() * map.getTileWidth()) / 2));
					map.setY(((Display.getHeight() - bottomBarHeight) / 2)
							- ((map.getMapHeight() * map.getTileHeight()) / 2));

					SessionVars.currentFile = path;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		saveButton.setClickedCommand(new HvlAction1<HvlButton>() {

			@Override
			public void run(HvlButton a) {
				if (map == null)
					return;

				if (SessionVars.currentFile == null) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setAcceptAllFileFilterUsed(false);
					fileChooser.setFileFilter(new FileFilter() {

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith(".hvlmap");
						}

						@Override
						public String getDescription() {
							return "HvlMap file (.hvlmap)";
						}
					});
					int result = fileChooser.showSaveDialog(null);

					if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION)
						return;

					String path = fileChooser.getSelectedFile().getAbsolutePath();
					if (path.endsWith(".hvlmap")) {
						path = path.substring(0, path.length() - 7);
					}

					SessionVars.currentFile = path;
				}
				map.save(SessionVars.currentFile);
			}
		});

		bottomMenuArranger.add(newButton);
		bottomMenuArranger.add(openButton);
		bottomMenuArranger.add(saveButton);
		menu.add(bottomMenuArranger);
		menu.add(sideMenuArranger);

		HvlMenu.setCurrent(menu);
	}

	@Override
	public void update(float delta) {
		bottomMenuArranger.setY(Display.getHeight() - bottomBarHeight);
		bottomMenuArranger.setWidth(Display.getWidth());
		sideMenuArranger.setHeight(Display.getHeight() - bottomBarHeight - sideBarWidth);

		updateInput();

		if (map != null) {
			map.update(delta);
		}

		draw(delta);
	}

	private void updateInput() {
		// Disable input if there's no map.
		if (map == null) {
			layerTextBox.setEnabled(false);
			layerTextBox.setText("");
		} else
			layerTextBox.setEnabled(true);

		// Layer text box default value (if empty and unfocused)
		if (map != null && !layerTextBox.isFocused() && layerTextBox.getText().isEmpty()) {
			layerTextBox.setText("0");
		}

		// Layer text box input fix (if unfocused and not empty
		if (map != null && !layerTextBox.isFocused() && !layerTextBox.getText().isEmpty()) {
			int result = Integer.parseInt(layerTextBox.getText());

			layerTextBox.setText(Math.min(Math.max(result, 0), map.getLayerCount() - 1) + "");
		}

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

		// Zooming the map
		if (map != null && Keyboard.isKeyDown(zoomKey)) {
			float dWheel = Mouse.getDWheel();
			map.setTileWidth(map.getTileWidth() + (dWheel / 120));
			map.setTileHeight(map.getTileHeight() + (dWheel / 120));
		}

		// Tile scroll-select
		if (map != null && !Keyboard.isKeyDown(zoomKey)) {
			selectedTile += (Mouse.getDWheel() / 120);
			if (selectedTile < 0)
				selectedTile = (map.getTilesAcross() * map.getTilesTall()) - 1;
			if (selectedTile >= map.getTilesAcross() * map.getTilesTall())
				selectedTile = 0;
		}

		// Tile mouse select
		if (map != null && HvlCursor.getCursorX() > 16 && HvlCursor.getCursorX() < sideBarWidth - 16
				&& HvlCursor.getCursorY() > 16 && HvlCursor.getCursorY() < sideBarWidth - 16) {

			float drawWidth = sideBarWidth - 32;
			float tileSize = drawWidth / map.getTilesAcross();

			float relativeX = HvlCursor.getCursorX() - 16;
			float relativeY = HvlCursor.getCursorY() - 16;

			int tX = (int) (relativeX / tileSize);
			int tY = (int) (relativeY / tileSize);

			if (Mouse.isButtonDown(0))
				selectedTile = (tY * map.getTilesAcross()) + tX;
		}

		// Tile setting
		if (map != null && cursorInMap() && !Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(0)
				&& !layerTextBox.getText().isEmpty()) {
			map.setTile(Integer.parseInt(layerTextBox.getText()), map.worldXToTile(HvlCursor.getCursorX()),
					map.worldYToTile(HvlCursor.getCursorY()), selectedTile);
		}

		// Tile erasing
		if (map != null && cursorInMap() && !Keyboard.isKeyDown(dragKey) && Mouse.isButtonDown(1)
				&& !layerTextBox.getText().isEmpty()) {
			map.setTile(Integer.parseInt(layerTextBox.getText()), map.worldXToTile(HvlCursor.getCursorX()),
					map.worldYToTile(HvlCursor.getCursorY()), -1);
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
				map.getTileWidth(), map.getTileHeight(), getTextureLoader().getResource(2),
				new Color(1f, 1f, 1f, 0.5f + (0.5f * (float) Math.abs(Math.sin(getTimer().getTotalTime() * 2)))));
	}

	private void drawTileSelect() {
		if (map == null)
			return;

		HvlPainter2D.hvlDrawQuad(16, 16, sideBarWidth - 32, sideBarWidth - 32, map.getTexture());

		drawTileSelectSquare();
		drawTileSelectMouseSquare();
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

	private void drawTileSelectMouseSquare() {
		if (map == null)
			return;

		if (HvlCursor.getCursorX() <= 16 || HvlCursor.getCursorX() >= sideBarWidth - 16 || HvlCursor.getCursorY() <= 16
				|| HvlCursor.getCursorY() >= sideBarWidth - 16)
			return;

		float drawWidth = sideBarWidth - 32;
		float tileSize = drawWidth / map.getTilesAcross();

		float relativeX = HvlCursor.getCursorX() - 16;
		float relativeY = HvlCursor.getCursorY() - 16;

		int tX = (int) (relativeX / tileSize);
		int tY = (int) (relativeY / tileSize);

		HvlPainter2D.hvlDrawQuad(16 + ((drawWidth / map.getTilesAcross()) * tX),
				16 + ((drawWidth / map.getTilesTall()) * tY), drawWidth / map.getTilesAcross(),
				drawWidth / map.getTilesTall(), getTextureLoader().getResource(2),
				new Color(1f, 1f, 1f, 0.5f + (0.5f * (float) Math.abs(Math.sin(getTimer().getTotalTime() * 2)))));
	}

	private boolean cursorInMap() {
		return HvlCursor.getCursorX() > map.getX()
				&& HvlCursor.getCursorX() < map.getX() + (map.getMapWidth() * map.getTileWidth())
				&& HvlCursor.getCursorY() > map.getY()
				&& HvlCursor.getCursorY() < map.getY() + (map.getMapHeight() * map.getTileHeight());
	}

	private void updateOpacitySlider() {
		if (map == null)
			return;
		if (layerTextBox.getText().isEmpty())
			return;

		layerOpacitySlider.setValue(map.getLayerOpacity(Integer.parseInt(layerTextBox.getText())));
	}
}
