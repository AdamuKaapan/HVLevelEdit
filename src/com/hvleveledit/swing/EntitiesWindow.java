package com.hvleveledit.swing;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EntitiesWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JTextField entityClassTextBox;
	private JTextField newArgTextField;
	private JList<String> constructorArgsList;

	public DefaultListModel<String> constructorArgs;

	/**
	 * Create the frame.
	 */
	public EntitiesWindow() {
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[114.00][grow][]", "[][grow][][]"));

		JLabel lblEntityClass = new JLabel("Entity Class");
		lblEntityClass.setFont(new Font("Tahoma", Font.BOLD, 11));
		contentPane.add(lblEntityClass, "cell 0 0,alignx right");

		entityClassTextBox = new JTextField();
		contentPane.add(entityClassTextBox, "cell 1 0 2 1,growx");
		entityClassTextBox.setColumns(10);

		constructorArgsList = new JList<String>();
		constructorArgs = new DefaultListModel<String>();
		constructorArgsList.setModel(constructorArgs);
		contentPane.add(constructorArgsList, "cell 0 1 3 1,grow");

		newArgTextField = new JTextField();
		contentPane.add(newArgTextField, "cell 0 2 2 1,growx");
		newArgTextField.setColumns(10);

		JButton addArgButton = new JButton("Add Arg");
		addArgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addArgButton_ActionPerformed(e);
			}
		});
		contentPane.add(addArgButton, "cell 2 2,growx");

		JButton removeArgButton = new JButton("Remove Selected Arg");
		removeArgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeArgButton_ActionPerformed(e);
			}
		});
		contentPane.add(removeArgButton, "cell 1 3,alignx right");

		JButton editSelectedArg = new JButton("Edit Selected Arg");
		editSelectedArg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelectedArg_ActionPerformed(e);
			}
		});
		contentPane.add(editSelectedArg, "cell 2 3,growx");
	}

	protected void addArgButton_ActionPerformed(ActionEvent e) {
		if (newArgTextField.getText().isEmpty())
			return;

		constructorArgs.addElement(newArgTextField.getText());
		newArgTextField.setText("");
	}

	protected void removeArgButton_ActionPerformed(ActionEvent e) {
		if (constructorArgsList.isSelectionEmpty())
			return;

		constructorArgs.remove(constructorArgsList.getSelectedIndex());
		constructorArgsList.setSelectedIndex(-1);
	}

	protected void editSelectedArg_ActionPerformed(ActionEvent e) {
		if (constructorArgsList.isSelectionEmpty())
			return;

		String returned = (String) JOptionPane.showInputDialog(this, "What should the new value of this argument be?",
				"New Argument Value", JOptionPane.QUESTION_MESSAGE);

		if (returned == null)
			return;

		constructorArgs.set(constructorArgsList.getSelectedIndex(), returned);
	}
}
