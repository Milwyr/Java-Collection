package hms.command;

import javax.swing.JPanel;

import hms.gui.TableView;

public interface Command {
	
	/**
	 * Gets the command panel.
	 * 
	 * @param view
	 *            the view
	 * @return the panel
	 */
	public JPanel getPanel(TableView view);
}