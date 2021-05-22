import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JSpinner;


public class WidgetSpinner extends Widget {

	String label;
	JSpinner spinner;
	Consumer<Integer> handler;

	int defaultValue;
	int lowerLimit;
	int upperLimit;

	/**
	 * A widget that lets the user select a numeric value.
	 * 
	 * @param textLabel			Label to show at the left of the spinner.
	 * @param defaultValue		Default value.
	 * @param lowerLimit		Minimum allowed value.
	 * @param upperLimit		Maximum allowed value.
	 * @param eventHandler		Will be notified when the spinner changes.
	 */
	public WidgetSpinner(String textLabel, int defaultValue, int lowerLimit, int upperLimit,
			Consumer<Integer> eventHandler) {

		super();

		this.label = textLabel;
		this.handler = eventHandler;
		this.defaultValue = defaultValue;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;

		spinner = new JSpinner();
		spinner.addChangeListener(event -> sanityCheck());

		widgets.put(new JLabel(label + ": "), "");
		widgets.put(spinner, "span 3, growx");

		spinner.setValue(defaultValue);
		sanityCheck();
	}

	/**
	 * Ensures the widget is in a consistent state, then calls the event handler.
	 */
	public void sanityCheck() {

		try {

			int value = (int) spinner.getValue();
			if( value > upperLimit || value < lowerLimit)
				spinner.setValue(ChartUtils.clamp( value, lowerLimit,  upperLimit));
			handler.accept(value);

		} catch (Exception e) {

			spinner.setValue(defaultValue);
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
		int value = ChartUtils.parseInteger(lines.remove(), label.trim().toLowerCase() + " = %d");

		// update the widget
		if(value < lowerLimit || value > upperLimit)
			throw new AssertionError("Invalid value.");

		// update the chart
		spinner.setValue(value);
		
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
			label.trim().toLowerCase() + " = " + spinner.getValue()
		};

	}
	
}
