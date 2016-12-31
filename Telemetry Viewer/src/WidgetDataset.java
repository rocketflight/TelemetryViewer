import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A widget that lets the user pick one dataset from a drop-down list.
 */
@SuppressWarnings("serial")
public class WidgetDataset extends JPanel {
	
	JLabel label;
	JComboBox<Dataset> combobox;

	public WidgetDataset() {
		
		super();
		
		setLayout(new GridLayout(1, 2, 10, 10));
		
		label = new JLabel("Dataset: ");
		add(label);
		
		combobox = new JComboBox<Dataset>(Controller.getAllDatasets());
		add(combobox);
		
	}
	
	/**
	 * @return    The selected dataset.
	 */
	public Dataset getDataset() {
		
		return (Dataset) combobox.getSelectedItem();
		
	}

}