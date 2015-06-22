package com.hvleveledit;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

import com.osreboot.ridhvl.HvlFontUtil;
import com.osreboot.ridhvl.display.HvlDisplayMode;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.HvlLabel;
import com.osreboot.ridhvl.menu.component.collection.HvlExpandableRectTextBox;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureButton;
import com.osreboot.ridhvl.painter.painter2d.HvlExpandingRectangle;
import com.osreboot.ridhvl.painter.painter2d.HvlFontPainter2D;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2DBasic;
import com.osreboot.ridhvl.tile.HvlLayeredTileMap;
import com.osreboot.ridhvl.tile.HvlTileMap;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;

public class HVLevelEditMainForm extends HvlTemplateInteg2DBasic {
	private HvlMenu mainMenu;
	private HvlArrangerBox menuBar;
	private HvlArrangerBox tileArr, layerArr;
	private HvlButton newButton, openButton, saveButton, resizeButton;
	private HvlLabel tileLabel, layerLabel;
	private HvlExpandableRectTextBox tileTextBox, layerTextBox;

	private HvlExpandingRectangle menuBarBackground, tilemapBackground;

	private HvlLayeredTileMap tilemap;

	private int tileSize;

	public HVLevelEditMainForm(int frameRateArg, int width, int height,
			String title, HvlDisplayMode displayModeArg) {
		super(frameRateArg, width, height, title, 25, 5, displayModeArg);
	}

	@Override
	public void initialize() {
		tileSize = 64;

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
		getTextureLoader().loadResource("TileBox");
		getTextureLoader().loadResource("Font");
		getTextureLoader().loadResource("MathFont");
		getTextureLoader().loadResource("ResizeButton/Off");
		getTextureLoader().loadResource("ResizeButton/Hover");
		getTextureLoader().loadResource("ResizeButton/On");

		menuBar = new HvlArrangerBox(0, 0, Display.getWidth(), 96,
				Display.getHeight(), ArrangementStyle.HORIZONTAL);
		menuBar.setAlign(0.5f);
		menuBar.setBorderL(16f);

		menuBarBackground = new HvlExpandingRectangle(getTextureLoader()
				.getResource(1), 0.0625f, 0.9375f, 0.0625f, 0.9375f, 0, 0, 0,
				menuBar.getHeight(), 8, 8);
		tilemapBackground = new HvlExpandingRectangle(getTextureLoader()
				.getResource(1), 0.0625f, 0.9375f, 0.0625f, 0.9375f, 0, 0, 320,
				Display.getHeight() - menuBar.getHeight(), 8, 8);

		newButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(2), getTextureLoader()
						.getResource(3), getTextureLoader().getResource(4)) {
			@Override
			public void onTriggered() {

				int[] mapDims = getNumberPair("What are the map dimensions?");
				if (mapDims == null)
					return;

				Texture t = getTexture();
				if (t == null)
					return;

				int[] tileDims = getNumberPair("In tiles, what are the dimensions of this tilemap?");
				if (tileDims == null)
					return;

				Integer layerCount = getNumber("How many layers should this tilemap have?");
				if (layerCount == null)
					return;

				HvlTileMap[] layers = new HvlTileMap[layerCount];

				for (int i = 0; i < layers.length; i++) {
					layers[i] = new HvlTileMap(t, tileDims[0], tileDims[1],
							mapDims[0], mapDims[1], 0, 0, 64, 64);
				}

				tilemap = new HvlLayeredTileMap(0, 0, 64, 64, layers);
				tilemap.getLayer(0).fill(new HvlSimpleTile(0));
			}
		};
		openButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(5), getTextureLoader()
						.getResource(6), getTextureLoader().getResource(7)) {
			@Override
			public void onTriggered() {
				String fileText = getFileText();
				if (fileText == null)
					return;

				Texture t = getTexture();
				if (t == null)
					return;

				tilemap = HvlLayeredTileMap.load(fileText, t, 0, 0, 64, 64);
			}
		};
		saveButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(8), getTextureLoader()
						.getResource(9), getTextureLoader().getResource(10)) {
			@Override
			public void onTriggered() {
				if (tilemap == null)
					return;

				JFileChooser filePicker = new JFileChooser();
				filePicker.showSaveDialog(null);
				File f = filePicker.getSelectedFile();

				if (f == null)
					return;

				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(f));
					writer.write(HvlLayeredTileMap.save(tilemap));
					writer.close();
				} catch (IOException e) {

				}
			}
		};
		resizeButton = new HvlTextureButton(0, 0, 64, 64, Display.getHeight(),
				getTextureLoader().getResource(14), getTextureLoader()
						.getResource(15), getTextureLoader().getResource(16)) {
			@Override
			public void onTriggered() {
				if (tilemap != null) {
					int[] dims = getNumberPair("Please enter the new dimensions?");
					for (int i = 0; i < tilemap.getLayerCount(); i++) {
						tilemap.getLayer(i).resize(dims[0], dims[1]);
					}
				}
			}
		};

		tileArr = new HvlArrangerBox(0, 320, 256, 48, Display.getHeight(),
				ArrangementStyle.HORIZONTAL);
		tileArr.setBorderL(8);
		tileArr.setAlign(0.5f);

		tileLabel = new HvlLabel(0, 0, Display.getHeight(),
				new HvlFontPainter2D(getTextureLoader().getResource(12),
						HvlFontUtil.DEFAULT, 2048, 2048, 112, 144, 18), "tile",
				Color.black);
		tileLabel.setScale(0.25f);

		layerArr = new HvlArrangerBox(0, tileArr.getY() + tileArr.getHeight()
				+ 32, 256, 48, Display.getHeight(), ArrangementStyle.HORIZONTAL);
		layerArr.setBorderL(8);
		layerArr.setAlign(0.5f);

		layerLabel = new HvlLabel(0, 0, Display.getHeight(),
				new HvlFontPainter2D(getTextureLoader().getResource(12),
						HvlFontUtil.DEFAULT, 2048, 2048, 112, 144, 18),
				"layer", Color.black);
		layerLabel.setScale(0.25f);

		tileTextBox = new HvlExpandableRectTextBox(0, 0, 256, 48,
				Display.getHeight(), "-1", new HvlExpandingRectangle(
						getTextureLoader().getResource(1), 0.0625f, 0.9375f,
						0.0625f, 0.9375f, 0, 0, 0, 0, 4, 4),
				new HvlExpandingRectangle(getTextureLoader().getResource(1),
						0.0625f, 0.9375f, 0.0625f, 0.9375f, 0, 0, 0, 0, 4, 4),
				new HvlFontPainter2D(getTextureLoader().getResource(13),
						HvlFontUtil.MATHEMATICS, 256, 256, 32, 64, 8));
		tileTextBox.setTextScale(0.75f);
		tileTextBox.setOffsetX(6f);
		tileTextBox.setTextColor(Color.black);
		tileTextBox.setMaxCharacters(3);

		layerTextBox = new HvlExpandableRectTextBox(32, tileTextBox.getY()
				+ tileTextBox.getHeight() + 8, 256, 48, Display.getHeight(),
				"0", new HvlExpandingRectangle(getTextureLoader()
						.getResource(1), 0.0625f, 0.9375f, 0.0625f, 0.9375f, 0,
						0, 0, 0, 4, 4), new HvlExpandingRectangle(
						getTextureLoader().getResource(1), 0.0625f, 0.9375f,
						0.0625f, 0.9375f, 0, 0, 0, 0, 4, 4),
				new HvlFontPainter2D(getTextureLoader().getResource(13),
						HvlFontUtil.MATHEMATICS, 256, 256, 32, 64, 8));
		layerTextBox.setTextScale(0.75f);
		layerTextBox.setOffsetX(6f);
		layerTextBox.setTextColor(Color.black);
		layerTextBox.setMaxCharacters(2);

		mainMenu = new HvlMenu() {

		};
		mainMenu.add(menuBar);
		menuBar.add(newButton);
		menuBar.add(openButton);
		menuBar.add(saveButton);
		menuBar.add(resizeButton);
		mainMenu.add(tileArr);
		tileArr.add(tileLabel);
		tileArr.add(tileTextBox);
		mainMenu.add(layerArr);
		layerArr.add(layerLabel);
		layerArr.add(layerTextBox);

		HvlMenu.setCurrent(mainMenu);
	}

	@Override
	public void update(float delta) {
		dimensionUpdate();

		if (tilemap != null) {
			if (Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) {
				tilemap.setX(tilemap.getX() + Mouse.getDX());
				tilemap.setY(tilemap.getY() - Mouse.getDY());
			} else if (Mouse.isButtonDown(0)) {
				int tileX = getMouseTileX();
				int tileY = getMouseTileY();

				if (!layerTextBox.getText().trim().isEmpty()) {
					int layer = Integer.parseInt(layerTextBox.getText().trim());

					if (layer < tilemap.getLayerCount()) {
						if (tileX >= 0
								&& tileX < tilemap.getLayer(layer)
										.getMapWidth()
								&& tileY >= 0
								&& tileY < tilemap.getLayer(layer)
										.getMapHeight()) {
							if (!tileTextBox.getText().trim().isEmpty()) {
								int tileCoord = Integer.parseInt(tileTextBox
										.getText().trim());
								if (tileCoord < 0) {
									tilemap.getLayer(layer).setTile(tileX,
											tileY, null);
								} else if (tileCoord < tilemap.getLayer(layer)
										.getInfo().tileWidth
										* tilemap.getLayer(layer).getInfo().tileHeight) {
									tilemap.getLayer(layer)
											.setTile(
													tileX,
													tileY,
													new HvlSimpleTile(tileCoord));
								}
							}
						}
					}
				}
			}
		}

		if (tilemap != null) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
					|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
				tileSize += (Mouse.getDWheel() / 120) * 2;
				tileSize = Math.max(Math.min(256, tileSize), 4);
				tilemap.setTileWidth(tileSize);
				tilemap.setTileHeight(tileSize);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
					|| Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
					&& !layerTextBox.getText().trim().isEmpty()) {
				int currentLayer = Integer.parseInt(layerTextBox.getText()
						.trim());

				currentLayer += Mouse.getDWheel() / 120;
				if (currentLayer >= tilemap.getLayerCount())
					currentLayer = 0;
				if (currentLayer < 0)
					currentLayer = tilemap.getLayerCount() - 1;

				layerTextBox.setText(currentLayer + "");
			} else if (!tileTextBox.getText().trim().isEmpty()
					&& !layerTextBox.getText().trim().isEmpty()) {
				int currentTile = Integer
						.parseInt(tileTextBox.getText().trim());
				int currentLayer = Integer.parseInt(layerTextBox.getText()
						.trim());

				currentTile += Mouse.getDWheel() / 120;

				if (currentTile >= tilemap.getLayer(currentLayer).getInfo().tileWidth
						* tilemap.getLayer(currentLayer).getInfo().tileHeight)
					currentTile = -1;
				if (currentTile < -1)
					currentTile = (tilemap.getLayer(currentLayer).getInfo().tileWidth * tilemap
							.getLayer(currentLayer).getInfo().tileHeight) - 1;

				tileTextBox.setText(currentTile + "");
			}
		}

		draw(delta);
	}

	private void dimensionUpdate() {
		menuBar.setY(Display.getHeight() - menuBar.getHeight());
		menuBar.setWidth(Display.getWidth());
		menuBar.setHeightInversion(Display.getHeight());
		menuBarBackground.setY(menuBar.getY());
		menuBarBackground.setTotalWidth(menuBar.getWidth());
		tilemapBackground.setTotalHeight(Display.getHeight()
				- menuBar.getHeight());
		newButton.setHeightInversion(Display.getHeight());
		openButton.setHeightInversion(Display.getHeight());
		saveButton.setHeightInversion(Display.getHeight());
		resizeButton.setHeightInversion(Display.getHeight());
		tileTextBox.setWidth(320 - tileTextBox.getX() - 32);
		tileTextBox.setHeightInversion(Display.getHeight());
		layerTextBox.setWidth(320 - layerTextBox.getX() - 32);
		layerTextBox.setHeightInversion(Display.getHeight());
	}

	private void draw(float delta) {
		if (tilemap != null) {
			tilemap.draw(delta);

			if (Mouse.getX() > tilemapBackground.getTotalWidth()) {
				int tileX = getMouseTileX();
				int tileY = getMouseTileY();

				if (!layerTextBox.getText().trim().isEmpty()) {

					int layer = Integer.parseInt(layerTextBox.getText().trim());

					if (layer < tilemap.getLayerCount()) {

						if (tileX >= 0
								&& tileX < tilemap.getLayer(layer)
										.getMapWidth()
								&& tileY >= 0
								&& tileY < tilemap.getLayer(layer)
										.getMapHeight()) {
							HvlPainter2D.hvlDrawQuad(tilemap.getX()
									+ (tileX * tileSize), tilemap.getY()
									+ (tileY * tileSize), tileSize, tileSize,
									getTextureLoader().getResource(11),
									Color.white);
						}
					}
				}
			}
		}

		tilemapBackground.draw();
		if (tilemap != null) {
			if (!layerTextBox.getText().trim().isEmpty()) {
				int layer = Integer.parseInt(layerTextBox.getText().trim());
				if (layer < tilemap.getLayerCount()) {
					HvlPainter2D.hvlDrawQuad(32, 32, 256, 256, tilemap
							.getLayer(layer).getInfo().texture);
				}
			}
		}

		menuBarBackground.draw();
		HvlMenu.updateMenus(delta);
	}

	private Integer getNumber(String prompt) {
		boolean isValid = false;
		Integer toReturn = null;

		do {
			String input = JOptionPane.showInputDialog(prompt);

			if (input == null)
				return null;

			try {
				toReturn = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"That's not a valid number.");
				continue;
			}

			if (toReturn < 0) {
				JOptionPane.showMessageDialog(null,
						"Please enter a positive number.");
				continue;
			}

			isValid = true;
		} while (!isValid);

		return toReturn;
	}

	private String getFileText() {
		boolean isValid = false;
		StringBuilder toReturn = null;

		do {
			JFileChooser filePicker = new JFileChooser();
			filePicker.showOpenDialog(null);

			File f = filePicker.getSelectedFile();
			if (f == null)
				return null;

			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				toReturn = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					toReturn.append(line);
					toReturn.append(System.lineSeparator());
				}
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Something went wrong... derp?");
				continue;
			}

			isValid = true;
		} while (!isValid);

		return toReturn.toString();
	}

	private Texture getTexture() {
		Texture toReturn = null;
		boolean isValid = false;
		do {
			JFileChooser filePicker = new JFileChooser();
			filePicker.showOpenDialog(null);
			File f = filePicker.getSelectedFile();
			if (f == null)
				return null;

			try {
				BufferedImage img = ImageIO.read(f);
				toReturn = BufferedImageUtil.getTexture("tilemap", img);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Something went wrong... derp?");
				continue;
			}

			isValid = true;
		} while (!isValid);

		return toReturn;
	}

	private int[] getNumberPair(String prompt) {
		boolean isValid = false;
		int[] toReturn = null;

		do {

			String input = JOptionPane.showInputDialog(prompt);

			if (input == null)
				return null;

			String[] split = input.split(",");
			if (split.length != 2) {
				JOptionPane.showMessageDialog(null,
						"Enter two integers separated by a comma.");
				continue;
			}

			toReturn = new int[2];

			try {
				toReturn[0] = Integer.parseInt(split[0].trim());
				toReturn[1] = Integer.parseInt(split[1].trim());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"You've got an invalid number there somewhere.");
				continue;
			}

			if (toReturn[0] < 0 || toReturn[1] < 0) {
				JOptionPane.showMessageDialog(null,
						"Please enter positive integers...");
				continue;
			}

			isValid = true;
		} while (!isValid);
		return toReturn;
	}

	private int getMouseTileX() {
		return (int) ((Mouse.getX() - tilemap.getX()) / tileSize);
	}

	private int getMouseTileY() {
		return (int) (((Display.getHeight() - Mouse.getY()) - tilemap.getY()) / tileSize);
	}
}
