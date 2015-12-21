package com.hvleveledit.swing;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import com.hvleveledit.SessionVars;

import net.miginfocom.swing.MigLayout;

public class OpenFileDialog extends JDialog {

	public boolean confirmed;
	
	private static final long serialVersionUID = 1L;
	public JSpinner tilemapWidthSpinner;
	public JSpinner tilemapHeightSpinner;
	public JTextField tilemapPathTextBox;
	public JTextField pathTextBox;

	public OpenFileDialog() {
		
		setTitle("New Map");
		setBounds(100, 100, 790, 223);
		getContentPane().setLayout(new MigLayout("", "[][grow,fill][]", "[][][][grow][]"));
		
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
		
		JLabel lblPath = new JLabel("Map Path");
		lblPath.setFont(new Font("Tahoma", Font.BOLD, 13));
		getContentPane().add(lblPath, "cell 0 2,alignx trailing");
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_ActionPerformed(e);
			}
		});
		
		pathTextBox = new JTextField();
		getContentPane().add(pathTextBox, "cell 1 2,growx");
		pathTextBox.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBrowse_ActionPerformed(e);
			}
		});
		getContentPane().add(btnBrowse, "cell 2 2");
		rootPane.setDefaultButton(okButton);
		getContentPane().add(okButton, "cell 0 4 2 1,grow");
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_ActionPerformed(e);
			}
		});
		getContentPane().add(cancelButton, "cell 2 4");
		
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
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) return;
		
		tilemapPathTextBox.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}
	
	protected void btnBrowse_ActionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".hvlmap");
			}

			@Override
			public String getDescription() {
				return "HvlMap file (.hvlmap)";
			}});
		int result = fileChooser.showOpenDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) return;
		
		pathTextBox.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}
	
	private void loadFieldsFromSession() {
		tilemapPathTextBox.setText(SessionVars.tilemapPath == null ? "" : SessionVars.tilemapPath);
		tilemapWidthSpinner.setValue(Math.max(SessionVars.tilemapWidth, 1));
		tilemapHeightSpinner.setValue(Math.max(SessionVars.tilemapHeight, 1));
	}
	
	private void saveFieldsToSession() {
		SessionVars.tilemapPath = tilemapPathTextBox.getText();
		SessionVars.tilemapWidth = (int) tilemapWidthSpinner.getValue();
		SessionVars.tilemapHeight = (int) tilemapHeightSpinner.getValue();
		SessionVars.saveTilemapDataToMainConfig();
	}
}
