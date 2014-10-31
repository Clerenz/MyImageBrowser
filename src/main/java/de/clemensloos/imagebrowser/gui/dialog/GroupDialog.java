package de.clemensloos.imagebrowser.gui.dialog;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.types.ImgGroup;


public class GroupDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private ImgGroup imgGroup = null;

	private JTextField groupTextField;
	private JButton okayButton;
	private JButton cancelButton;


	public GroupDialog(Frame owner) {
		super(owner, "Create a group", true);

		JPanel mainPanel = new JPanel();

		groupTextField = new JTextField(25);

		okayButton = new JButton("Okay");
		okayButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		String columnSpecs = "3dlu, 30dlu, pref, 3dlu, pref, 3dlu";
		String rowSpecs = "3dlu, pref, 3dlu, pref, 3dlu";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		// layout.setColumnGroups(new int[][] { { 2, 6 } });
		// layout.setRowGroups(new int[][] { { 2, 6 } });
		mainPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, mainPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(groupTextField, cc.xyw(2, 2, 4));
		builder.add(okayButton, cc.xy(3, 4));
		builder.add(cancelButton, cc.xy(5, 4));

		add(mainPanel);

		pack();

		setLocationRelativeTo(owner);

		setVisible(true);
	}


	public ImgGroup getImgGroup() {

		return imgGroup;

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okayButton)) {

			imgGroup = new ImgGroup(groupTextField.getText());
		}
		this.dispose();
	}

}
