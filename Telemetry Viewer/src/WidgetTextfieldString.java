import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class WidgetTextfieldString extends Widget {
	
	String label;
	JTextField textfield;
	Consumer<String> handler;
	String defaultValue;
	int lengthLimit;
	
	/**
	 * A widget that lets the user specify a string with a text field.
	 * 
	 * @param textLabel       Label to show at the left of the text field.
	 * @param defaultValue    Default value.
	 * @param lengthLimit     Minimum allowed length.
	 * @param eventHandler    Will be notified when the text field changes.
	 */
	public WidgetTextfieldString(String textLabel, String defaultValue, int lengthLimit, Consumer<String> eventHandler) {

		super();
		
		label = textLabel;
		handler = eventHandler;
		this.defaultValue = defaultValue;
		this.lengthLimit = lengthLimit;
		
		textfield = new JTextField(defaultValue);
		textfield.addFocusListener(new FocusListener() {
			@Override public void focusLost(FocusEvent fe)   { sanityCheck(); }
			@Override public void focusGained(FocusEvent fe) { textfield.selectAll(); }
		});
		
		widgets.put(new JLabel(label + ": "), "");
		widgets.put(textfield, "span 3, growx");

		sanityCheck();
		
	}
	
	/**
	 * Ensures the number is within the allowed length, then notifies the handlers.
	 */
	public void sanityCheck() {
		
		try {
			
			String value = textfield.getText().trim().substring(0, this.lengthLimit);
			textfield.setText(value);
			handler.accept(value);
			
		} catch(Exception e) {
			
			textfield.setText(this.defaultValue);
			handler.accept(defaultValue);
			
		}
		
	}
	
	/**
	 * Updates the widget and chart based on settings from a layout file.
	 * 
	 * @param lines    A queue of remaining lines from the layout file.
	 */
	@Override public void importState(CommunicationController.QueueOfLines lines) {

		// parse the text
		String value = ChartUtils.parseString(lines.remove(), label.trim().toLowerCase() + " = %s");
		
		// update the widget
		textfield.setText(value);
		
		// update the chart
		sanityCheck();
		
	}
	
	/**
	 * Saves the current state to one or more lines of text.
	 * 
	 * @return    A String[] where each element is a line of text.
	 */
	@Override public String[] exportState() {
		
		return new String[] {
			label.trim().toLowerCase() + " = " + textfield.getText()
		};

	}

}

