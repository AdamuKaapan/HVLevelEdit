package com.hvleveledit.swing;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.hvleveledit.MainEditorWindow;

import net.miginfocom.swing.MigLayout;

public class EntitiesWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField newArgTextField;
	private JList<String> constructorArgsList;
	public JComboBox<String> entityClassComboBox;

	public DefaultListModel<String> constructorArgs;
	public DefaultComboBoxModel<String> classSelect;

	private MainEditorWindow parent;

	/**
	 * Create the frame.
	 */
	public EntitiesWindow(MainEditorWindow parent) {
		this.parent = parent;
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 572, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[114.00][grow][]", "[][grow][][]"));

		JLabel lblEntityClass = new JLabel("Entity Class");
		lblEntityClass.setFont(new Font("Tahoma", Font.BOLD, 11));
		contentPane.add(lblEntityClass, "cell 0 0,alignx trailing");

		constructorArgsList = new JList<String>();
		constructorArgs = new DefaultListModel<String>();

		entityClassComboBox = new JComboBox<String>();
		entityClassComboBox.setEditable(true);
		classSelect = new DefaultComboBoxModel<String>();
		entityClassComboBox.setModel(classSelect);
		contentPane.add(entityClassComboBox, "cell 1 0,growx");

		JButton btnRefreshList = new JButton("Refresh List");
		btnRefreshList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnRefreshList_ActionPerformed(arg0);
			}
		});
		contentPane.add(btnRefreshList, "cell 2 0");
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

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			refreshComboBox();
		}
		super.setVisible(visible);
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

	protected void btnRefreshList_ActionPerformed(ActionEvent arg0) {
		refreshComboBox();
	}

	public void refreshComboBox() {
		classSelect.removeAllElements();
		List<String> classes = parent.getAllEntityTypes();
		for (String c : classes) {
			classSelect.addElement(c);
		}
	}
}
