import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

public class WidgetColorpicker extends Widget {
	
	String label;
	JFrame parentFrame;
	JButton colorButton;
	Consumer<Color> handler;
	Color defaultColor;
	
	/**
	 * A widget that lets the user specify a color.
	 * 
	 * @param textLabel    Label to show at the left of the color button.
	 * @param defaultColor Default color value.
	 * @param eventHandler Will be notified when the color changes.
	 */
	public WidgetColorpicker(String textLabel, Color defaultColor, Consumer<Color> eventHandler) {

		super();
		
		label = textLabel;
		handler = eventHandler;
		this.defaultColor = defaultColor;
		
		// create a button for the user to specify a color
		colorButton = new JButton("Set Color");
		
		// add the button
		widgets.put(new JLabel(label + ": "), "");
		widgets.put(colorButton, "span 3, growx");

		// get the parent
		parentFrame = (JFrame) SwingUtilities.windowForComponent(colorButton);
		colorButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				Color colorValue = JColorChooser.showDialog(parentFrame, "Pick a color", colorButton.getBackground());
				setColor(colorValue);
			}
		});
		
		setColor(defaultColor);

	}
	
	/**
	 * Sets the color, then notifies the handlers.
	 */
	public void setColor(Color colorValue) {
		
		try {
			
			colorButton.setBackground(colorValue);
			colorButton.setForeground(colorValue);
			handler.accept(colorValue);
			
		} catch (Exception e) {

			colorButton.setBackground(this.defaultColor);
			colorButton.setForeground(this.defaultColor);
			handler.accept(this.defaultColor);
			
		}
		
	}
	
	
	/**
	 * Updates the widget and chart based on settings from a layout file.
	 * 
	 * @param lines    A queue of remaining lines from the layout file.
	 */
	@Override public void importState(CommunicationController.QueueOfLines lines) {

		// parse the text
		Color value = ChartUtils.parseColor(lines.remove(), label.trim().toLowerCase() + " = 0x%s");
		
		// update the widget
		setColor(value);
		
	}
	
	/**
	 * Saves the current state to one or more lines of text.
	 * 
	 * @return    A String[] where each element is a line of text.
	 */
	@Override public String[] exportState() {
		
		return new String[] { 
			label.trim().toLowerCase() + " = "
				+ String.format("0x%02X%02X%02X%02X"
					, colorButton.getBackground().getRed()
					, colorButton.getBackground().getGreen()
					, colorButton.getBackground().getBlue()
					, colorButton.getBackground().getAlpha()) 
		};

	}

}