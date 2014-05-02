package de.clemensloos.imagebrowser.gui.dialog;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import org.jdesktop.swingx.JXDatePicker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.clemensloos.imagebrowser.types.ImgEvent;


public class EventDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private ImgEvent imgEvent = null;

	private JTextField eventTextField;
	private JXDatePicker startDate;
	private JXDatePicker endDate;
	private JSpinner startTime;
	private JSpinner endTime;
	private JButton okayButton;
	private JButton cancelButton;


	public EventDialog(Frame owner) {
		super(owner, "Create an event", true);

		JPanel mainPanel = new JPanel();

		eventTextField = new JTextField(25);

		startDate = new JXDatePicker(new Date());
		endDate = new JXDatePicker(new Date());

		startTime = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTime, "HH:mm:ss");
		startTime.setEditor(startTimeEditor);
		Calendar c = Calendar.getInstance();
		c.set(1970, 0, 1, 0, 0, 0);
		startTime.setValue(c.getTime());

		endTime = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTime, "HH:mm:ss");
		endTime.setEditor(endTimeEditor);
		c.set(1970, 0, 1, 23, 59, 59);
		endTime.setValue(c.getTime());

		okayButton = new JButton("Okay");
		okayButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		String columnSpecs = "3dlu, 30dlu, pref, 3dlu, pref, 3dlu";
		String rowSpecs = "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		// layout.setColumnGroups(new int[][] { { 2, 6 } });
		// layout.setRowGroups(new int[][] { { 2, 6 } });
		mainPanel.setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, mainPanel);
		CellConstraints cc = new CellConstraints();

		builder.add(eventTextField, cc.xyw(2, 2, 4));
		builder.add(startDate, cc.xyw(2, 4, 2));
		builder.add(startTime, cc.xy(5, 4));
		builder.add(endDate, cc.xyw(2, 6, 2));
		builder.add(endTime, cc.xy(5, 6));
		builder.add(okayButton, cc.xy(3, 8));
		builder.add(cancelButton, cc.xy(5, 8));

		add(mainPanel);

		pack();

		setLocationRelativeTo(owner);

		setVisible(true);
	}


	public ImgEvent getImgEvent() {

		return imgEvent;

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okayButton)) {

			imgEvent = new ImgEvent(eventTextField.getText(),
					startDate.getDate().getTime() + ((Date) startTime.getValue()).getTime(),
					endDate.getDate().getTime() + ((Date) endTime.getValue()).getTime());
		}
		this.dispose();
	}

}
