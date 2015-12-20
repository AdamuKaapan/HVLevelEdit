package com.hvleveledit.swing;

import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.hvleveledit.SessionVars;

import net.miginfocom.swing.MigLayout;

public class NewFileDialog extends JDialog {

	public boolean confirmed;
	
	private static final long serialVersionUID = 1L;
	public JSpinner tilemapWidthSpinner;
	public JSpinner tilemapHeightSpinner;
	public JSpinner mapWidthSpinner;
	public JSpinner mapHeightSpinner;
	public JSpinner layersSpinner;
	public JTextField tilemapPathTextBox;

	public NewFileDialog() {
		
		setTitle("New Map");
		setBounds(100, 100, 790, 223);
		getContentPane().setLayout(new MigLayout("", "[][grow,fill][]", "[][][][][grow][]"));
		
		JLabel lblTilemapTexturePath = new JLabel("Tilesheet Texture Path");
		lblTilemapTexturePath.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblTilemapTexturePath, "cell 0 0,alignx trailing");
		
		tilemapPathTextBox = new JTextField();
		getContentPane().add(tilemapPathTextBox, "cell 1 0,growx");
		tilemapPathTextBox.setColumns(10);
		
		JButton tilemapPathBrowseButton = new JButton("Browse");
		tilemapPathBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tilemapPathBrowseButton_ActionPerformed(e);
			}
		});
		getContentPane().add(tilemapPathBrowseButton, "cell 2 0");
		
		JLabel lblTilemapDimensions = new JLabel("Tilesheet Dimensions");
		lblTilemapDimensions.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblTilemapDimensions, "cell 0 1,alignx trailing");
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 1 1 2 1,grow");
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblWidth = new JLabel("Width");
		panel.add(lblWidth);
		
		tilemapWidthSpinner = new JSpinner();
		tilemapWidthSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel.add(tilemapWidthSpinner);
		
		JLabel lblHeight = new JLabel("Height");
		panel.add(lblHeight);
		
		tilemapHeightSpinner = new JSpinner();
		tilemapHeightSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel.add(tilemapHeightSpinner);
		
		JLabel lblMapDimensions = new JLabel("Map Dimensions");
		lblMapDimensions.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblMapDimensions, "cell 0 2,alignx trailing");
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, "cell 1 2 2 1,grow");
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel label = new JLabel("Width");
		panel_1.add(label);
		
		mapWidthSpinner = new JSpinner();
		mapWidthSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel_1.add(mapWidthSpinner);
		
		JLabel label_1 = new JLabel("Height");
		panel_1.add(label_1);
		
		mapHeightSpinner = new JSpinner();
		mapHeightSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel_1.add(mapHeightSpinner);
		
		JLabel lblLayers = new JLabel("Layers");
		lblLayers.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblLayers, "cell 0 3,alignx trailing");
		
		layersSpinner = new JSpinner();
		layersSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		getContentPane().add(layersSpinner, "cell 1 3");
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_ActionPerformed(e);
			}
		});
		rootPane.setDefaultButton(okButton);
		getContentPane().add(okButton, "cell 0 5 2 1,grow");
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_ActionPerformed(e);
			}
		});
		getContentPane().add(cancelButton, "cell 2 5");
		
		loadFieldsFromSession();
	}

	protected void okButton_ActionPerformed(ActionEvent e) {
		confirmed = true;
		setVisible(false);
		saveFieldsToSession();
	}
	
	protected void cancelButton_ActionPerformed(ActionEvent e) {
		setVisible(false);
	}
	
	protected void tilemapPathBrowseButton_ActionPerformed(ActionEvent e) {
		FileDialog fileChooser = new FileDialog(this);
		fileChooser.setVisible(true);
		
		if (fileChooser.getFile() == null) return;
		
		tilemapPathTextBox.setText(fileChooser.getDirectory() + fileChooser.getFile());
	}
	
	private void loadFieldsFromSession() {
		tilemapPathTextBox.setText(SessionVars.tilemapPath == null ? "" : SessionVars.tilemapPath);
		tilemapWidthSpinner.setValue(Math.max(SessionVars.tilemapWidth, 1));
		tilemapHeightSpinner.setValue(Math.max(SessionVars.tilemapHeight, 1));
		layersSpinner.setValue(Math.max(SessionVars.mapLayers, 1));
	}
	
	private void saveFieldsToSession() {
		SessionVars.tilemapPath = tilemapPathTextBox.getText();
		SessionVars.tilemapWidth = (int) tilemapWidthSpinner.getValue();
		SessionVars.tilemapHeight = (int) tilemapHeightSpinner.getValue();
		SessionVars.mapLayers = (int) layersSpinner.getValue();
		SessionVars.saveTilemapDataToMainConfig();
	}
}
