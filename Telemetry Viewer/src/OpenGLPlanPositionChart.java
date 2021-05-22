import com.jogamp.opengl.GL2ES3;

/**
 * 
 */
public class OpenGLPlanPositionChart extends PositionedChart {

	// image region on screen
	float xDisplayLeft;
	float xDisplayRight;
	float yDisplayTop;
	float yDisplayBottom;
	float displayWidth;
	float displayHeight;
	
	// label
	boolean showLabel;
	float labelWidth;
	float xLabelLeft;
	float xLabelRight;
	float yLabelBaseline;
	float yLabelTop;
	
	// control widgets
	WidgetCheckbox labelWidget;
	
	@Override public String toString() {
		
		return "Plan Position";
		
	}
	
	public OpenGLPlanPositionChart(int x1, int y1, int x2, int y2) {
		
		super(x1, y1, x2, y2);

		labelWidget = new WidgetCheckbox("Show Label",
		                                 true,
		                                 newShowLabel -> showLabel = newShowLabel);
		widgets = new Widget[1];
		widgets[0] = labelWidget;
		
	}

	@Override public EventHandler drawChart(GL2ES3 gl, float[] chartMatrix, int width, int height, int lastSampleNumber, double zoomLevel, int mouseX, int mouseY) {
		
		// calculate x and y positions of everything
		xDisplayLeft = Theme.tilePadding;
		xDisplayRight = width - Theme.tilePadding;
		displayWidth = xDisplayRight - xDisplayLeft;
		yDisplayBottom = Theme.tilePadding;
		yDisplayTop = height - Theme.tilePadding;
		displayHeight = yDisplayTop - yDisplayBottom;

		if(showLabel) {
			labelWidth = OpenGL.largeTextWidth(gl, this.toString());
			yLabelBaseline = Theme.tilePadding;
			yLabelTop = yLabelBaseline + OpenGL.largeTextHeight;
			xLabelLeft = (width / 2f) - (labelWidth / 2f);
			xLabelRight = xLabelLeft + labelWidth;
		
			yDisplayBottom = yLabelTop + Theme.tickTextPadding + Theme.tilePadding;
			displayHeight = yDisplayTop - yDisplayBottom;
		}

		// draw the label, on top of a background quad, if there is room
		if(showLabel && labelWidth < width - Theme.tilePadding * 2) {
			OpenGL.drawLargeText(gl, this.toString(), (int) xLabelLeft, (int) yLabelBaseline, 0);
		}
		
		return null;
		
	}

}
