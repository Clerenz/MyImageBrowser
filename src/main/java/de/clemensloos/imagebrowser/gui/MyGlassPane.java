package de.clemensloos.imagebrowser.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class MyGlassPane extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private Thread waiter;

	MyGlassPane() {
		
		super();
		
		String columnSpecs = "pref:GROW, pref, pref:GROW";
		String rowSpecs = "pref:GROW, pref:GROW, pref, pref:GROW";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 1, 3} });
		layout.setRowGroups(new int[][] { { 1, 2, 4} });
		setLayout(layout);
		
		PanelBuilder builder = new PanelBuilder(layout, this);
		CellConstraints cc = new CellConstraints();
		
		label = new JLabel();
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		label.setBorder(BorderFactory.createLineBorder(Color.black, 5, true));
		label.setOpaque(true);
		
		builder.add(label,  cc.xy(2, 3));
		
		
	}
	
	public void show(String message) {
		if(waiter != null) {
			waiter.interrupt();
		}
		label.setText(message);
		setVisible(true);
		waiter = new Thread(new Waiter());
		waiter.start();
	}
	
	void hideMe() {
		for (int i=100; i>0; i--) {
			label.setForeground(new Color(1, 1, 1, (float)i / 100f));
			label.setBackground(new Color(0, 0, 0, (float)i / 100f));
			label.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, (float)i / 100f), 5, true));
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				label.setForeground(Color.WHITE);
				label.setBackground(Color.BLACK);
				label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
				return;
			}
		}
		setVisible(false);
		label.setForeground(Color.WHITE);
		label.setBackground(Color.BLACK);
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
	}
	
	public void block() {
		addMouseListener(this);
		addKeyListener(this);
	}
	
	public void unblock() {
		removeMouseListener(this);
		removeKeyListener(this);
	}

	class Waiter implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				return;
			}
			hideMe();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
}