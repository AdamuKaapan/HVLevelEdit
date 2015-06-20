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

import com.osreboot.ridhvl.display.HvlDisplayMode;
import com.osreboot.ridhvl.menu.HvlMenu;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox;
import com.osreboot.ridhvl.menu.component.HvlArrangerBox.ArrangementStyle;
import com.osreboot.ridhvl.menu.component.HvlButton;
import com.osreboot.ridhvl.menu.component.collection.HvlTextureButton;
import com.osreboot.ridhvl.painter.painter2d.HvlExpandingRectangle;
import com.osreboot.ridhvl.painter.painter2d.HvlPainter2D;
import com.osreboot.ridhvl.template.HvlTemplateInteg2DBasic;
import com.osreboot.ridhvl.tile.HvlTileMap;
import com.osreboot.ridhvl.tile.collection.HvlSimpleTile;

public class HVLevelEditMainForm extends HvlTemplateInteg2DBasic {
	private HvlMenu mainMenu;
	private HvlArrangerBox menuBar;
	private HvlButton newButton, openButton, saveButton;

	private HvlExpandingRectangle menuBarBackground;

	private HvlTileMap tilemap;

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

				tilemap = new HvlTileMap(t, tileDims[0], tileDims[1],
						mapDims[0], mapDims[1], 0, 0, 64, 64);
				tilemap.fill(new HvlSimpleTile(0));
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

				tilemap = HvlTileMap.load(fileText, t, 0, 0, 64, 64);
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
					writer.write(HvlTileMap.save(tilemap));
					writer.close();
				} catch (IOException e) {

				}
			}
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

		if (tilemap != null && Mouse.isButtonDown(1)) {
			tilemap.setX(tilemap.getX() + Mouse.getDX());
			tilemap.setY(tilemap.getY() - Mouse.getDY());
		}

		if (tilemap != null
				&& (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard
						.isKeyDown(Keyboard.KEY_RCONTROL))) {
			tileSize += (Mouse.getDWheel() / 120) * 2;
			tileSize = Math.max(Math.min(256, tileSize), 4);
			tilemap.setTileWidth(tileSize);
			tilemap.setTileHeight(tileSize);
		}

		draw(delta);
	}

	private void draw(float delta) {
		if (tilemap != null) {
			tilemap.draw(delta);

			float translatedX = Mouse.getX() - tilemap.getX();
			float translatedY = (Display.getHeight() - Mouse.getY())
					- tilemap.getY();
			int tileX = (int) (translatedX / tileSize);
			int tileY = (int) (translatedY / tileSize);

			if (tileX > 0 && tileX < tilemap.getMapWidth() && tileY > 0
					&& tileY < tilemap.getMapHeight()) {
				HvlPainter2D.hvlDrawQuad(tilemap.getX() + (tileX * tileSize),
						tilemap.getY() + (tileY * tileSize), tileSize,
						tileSize, getTextureLoader().getResource(11),
						Color.white);
			}
		}

		menuBarBackground.draw();
		HvlMenu.updateMenus(delta);
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
}
