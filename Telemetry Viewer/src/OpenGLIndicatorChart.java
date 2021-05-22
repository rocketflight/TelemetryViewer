import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.jogamp.opengl.GL2ES3;
import java.awt.Color;

 /**
 * Renders a label showing an on/off indicator based on
 * an integer value of the most recent sample.
 * 
 *     User settings: Dataset to visualize. 
 *     Colors of the button can be set for boolean on/off indicators 
 *     Sample count (this is used for autoscaling and for statistics.)
 *     Current reading label can be displayed. 
 *     Dataset label can be displayed.
 */
 
public class OpenGLIndicatorChart extends PositionedChart {
	
	Samples     samples;
	
	// plot region
	float xPlotLeft;
	float xPlotRight;
	float plotWidth;
	float yPlotTop;
	float yPlotBottom;
	float plotHeight;
	int precision;
	boolean sampleCountMode;
	
	// chart label and color
	boolean showChartLabel;
	String chartLabel;
	float[] chartBackColor = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	Color chartForeColor;
	float labelWidth;
	float xLabelLeft;
	float yLabelBaseline;
	
	// statistics
	boolean showStatistics;
	String meanText;
	String stdDevText;
	float statsTextWidth;
	float xMeanTextLeft;
	float xStdDevTextLeft;
	float yStatsTextBaseline;
	
	// min max labels
	boolean showMinMaxLabels;
	float yMinMaxLabelsBaseline;
	float yMinMaxLabelsTop;
	String minLabel;
	String maxLabel;
	float minLabelWidth;
	float maxLabelWidth;
	float xMinLabelLeft;
	float xMaxLabelLeft;
	
	// reading label and colors
	boolean showReadingLabel;
	String readingLabel;
	
	String onReadingLabel;
	float onReadingValue;
	Color onReadingBackColor;
	Color onReadingForeColor;
	
	String offReadingLabel;
	float offReadingValue;
	Color offReadingBackColor;
	Color offReadingForeColor;
	
	//float readingLabelWidth;
	//float xReadingLabelLeft;
	//float yReadingLabelBaseline;
	//float yReadingLabelTop;
	//float readingLabelRadius;
	
	// dataset label
	//boolean showDatasetLabel;
	//String datasetLabel;
	//float datasetLabelWidth;
	//float yDatasetLabelBaseline;
	//float yDatasetLabelTop;
	//float xDatasetLabelLeft;
	//float datasetLabelRadius;
	
	// constraints
	static final int SampleCountDefault = 1000;
	static final int SampleCountLowerLimit = 1;
	static final int SampleCountUpperLimit = Integer.MAX_VALUE;
	static final Color DefaultOnForegroundColor = Color.WHITE;
	static final Color DefaultOffForegroundColor = Color.WHITE;
	static final Color DefaultOnBackgroundColor = new Color(0, 204, 0);
	static final Color DefaultOffBackgroundColor = new Color(204, 0, 0);
	
	// control widgets
	WidgetDatasets datasetWidget;
	WidgetDuration durationWidget;
	WidgetTextfieldString chartLabelWidget;
	
	WidgetSpinner valueIfOnWidget;
	WidgetTextfieldString stringIfOnWidget;	
	WidgetColorpicker backColorIfOnWidget;
	WidgetColorpicker foreColorIfOnWidget;
	
	WidgetSpinner valueIfOffWidget;
	WidgetTextfieldString stringIfOffWidget;
	WidgetColorpicker backColorIfOffWidget;
	WidgetColorpicker foreColorIfOffWidget;
	
	WidgetCheckbox showChartLabelWidget;
	WidgetCheckbox showReadingLabelWidget;
	WidgetCheckbox showMinMaxLabelsWidget;
	WidgetCheckbox showStatisticsWidget;
	WidgetSpinner precisionWidget;
	
	@Override public String toString() {
		
		return "Indicator";
		
	}
	
	public OpenGLIndicatorChart(int x1, int y1, int x2, int y2) {
		
		super(x1, y1, x2, y2);
		
		samples = new Samples();
		
		datasetWidget = new WidgetDatasets(1,
				new String[] {"Dataset"},
				newDataset -> datasets = newDataset);

		chartLabelWidget = new WidgetTextfieldString("Chart label", "Data", 16,
				newStringChartLabel -> chartLabel = newStringChartLabel);

		valueIfOnWidget = new WidgetSpinner("Value if true", 1, -Integer.MAX_VALUE, Integer.MAX_VALUE,
				newValueIfOn -> onReadingValue = newValueIfOn);

		stringIfOnWidget = new WidgetTextfieldString("Label if on", "On", 16,
				newStringIfOn -> onReadingLabel = newStringIfOn);

		foreColorIfOnWidget = new WidgetColorpicker("Foreground color if on", DefaultOnForegroundColor,
				newForeColorIfOn -> onReadingForeColor = newForeColorIfOn);

		backColorIfOnWidget = new WidgetColorpicker("Background color if on", DefaultOnBackgroundColor,
				newBackColorIfOn -> onReadingBackColor = newBackColorIfOn);

		valueIfOffWidget = new WidgetSpinner("Value if off", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE,
				newValueIfOff -> offReadingValue = newValueIfOff);

		stringIfOffWidget = new WidgetTextfieldString("Label if off", "Off", 16,
				newStringIfOff -> offReadingLabel = newStringIfOff);

		foreColorIfOffWidget = new WidgetColorpicker("Foreground color if off", DefaultOffForegroundColor,
				newForeColorIfOff -> offReadingForeColor = newForeColorIfOff);

		backColorIfOffWidget = new WidgetColorpicker("Background color if off", DefaultOffBackgroundColor,
				newBackColorIfOff -> offReadingBackColor = newBackColorIfOff);

		showReadingLabelWidget = new WidgetCheckbox("Show Reading Label",
                true,
                newShowReadingLabel -> showReadingLabel = newShowReadingLabel);

		showChartLabelWidget = new WidgetCheckbox("Show Chart Label", true,
				newShowChartLabel -> showChartLabel = newShowChartLabel);

		showMinMaxLabelsWidget = new WidgetCheckbox("Show Min/Max Labels",
                true,
                newShowMinMaxLabels -> showMinMaxLabels = newShowMinMaxLabels);

		showStatisticsWidget = new WidgetCheckbox("Show Statistics",
              true,
              newShowStatistics -> showStatistics = newShowStatistics);

		durationWidget = new WidgetDuration(SampleCountDefault,
				SampleCountLowerLimit,
				SampleCountUpperLimit,
                (xAxisType) -> {
                	sampleCountMode  = xAxisType.equals("Sample Count");
                	//isTimestampsMode = xAxisType.equals("Timestamps");
                	//plot = sampleCountMode ? new PlotSampleCount() : new PlotMilliseconds();
                });

		precisionWidget = new WidgetSpinner("Decimal places", 6, 0, 6, newPrecision -> precision = newPrecision);

		widgets = new Widget[20];
		widgets[0] = datasetWidget;
		widgets[1] = chartLabelWidget;
		widgets[2] = null;
		widgets[3] = valueIfOnWidget;
		widgets[4] = stringIfOnWidget;
		widgets[5] = foreColorIfOnWidget;
		widgets[6] = backColorIfOnWidget;
		widgets[7] = null;
		widgets[8] = valueIfOffWidget;
		widgets[9] = stringIfOffWidget;
		widgets[10] = foreColorIfOffWidget;
		widgets[11] = backColorIfOffWidget;
		widgets[12] = null;
		widgets[13] = durationWidget;
		widgets[14] = null;
		widgets[15] = showChartLabelWidget;
		widgets[16] = showReadingLabelWidget;
		widgets[17] = showMinMaxLabelsWidget;
		widgets[18] = showStatisticsWidget;
		widgets[19] = precisionWidget;
		
	}
	
	@Override public EventHandler drawChart(GL2ES3 gl, float[] chartMatrix, int width, int height, int lastSampleNumber, double zoomLevel, int mouseX, int mouseY) {
		
		OpenGLText.initialise(gl);
		EventHandler handler = null;
		
		// get the samples
		int endIndex = lastSampleNumber;
		int startIndex = endIndex - (int) (sampleCount * zoomLevel) + 1;
		int minDomain = SampleCountLowerLimit - 1;
		if(endIndex - startIndex < minDomain) startIndex = endIndex - minDomain;
		if(startIndex < 0) startIndex = 0;
		
		if(endIndex - startIndex < minDomain)
			return handler;
		
		// get the samples
		if (sampleCountMode)
			datasets.get(0).getSamples(lastSampleNumber, durationWidget.getSampleCount(), zoomLevel, samples);
		else
			datasets.get(0).getMilliseconds(lastSampleNumber, durationWidget.getMilliseconds(), zoomLevel, samples);

		datasets.get(0).getSamples(startIndex, endIndex, samples);
		
		// calculate precision
		int digiCount = (precision == 0 ? 0 : precision + 1);
		
		// calculate x and y positions of everything
		xPlotLeft = Theme.tilePadding;
		xPlotRight = width - Theme.tilePadding;
		plotWidth = xPlotRight - xPlotLeft;
		yPlotTop = height - Theme.tilePadding;
		yPlotBottom = Theme.tilePadding;
		plotHeight = yPlotTop - yPlotBottom;

		// what to display?
		float lastSample = samples.buffer[samples.buffer.length - 1];
		if (lastSample == onReadingValue) {
			readingLabel = onReadingLabel;
			chartForeColor = onReadingForeColor;
			chartBackColor[0] = onReadingBackColor.getRed() / 255f;
			chartBackColor[1] = onReadingBackColor.getGreen() / 255f;
			chartBackColor[2] = onReadingBackColor.getBlue() / 255f;
		} else {
			readingLabel = offReadingLabel;
			chartForeColor = offReadingForeColor;
			chartBackColor[0] = offReadingBackColor.getRed() / 255f;
			chartBackColor[1] = offReadingBackColor.getGreen() / 255f;
			chartBackColor[2] = offReadingBackColor.getBlue() / 255f;
		}
		
		if(showStatistics) {
			double[] doubles = new double[samples.buffer.length];
			for(int i = 0; i < samples.buffer.length; i++)
				doubles[i] = (double) samples.buffer[i];
			DescriptiveStatistics stats = new DescriptiveStatistics(doubles);
			
			meanText    = "Mean: " +    ChartUtils.formattedNumber(stats.getMean(), 6);
			stdDevText  = "Std Dev: " + ChartUtils.formattedNumber(stats.getStandardDeviation(), 6);
			
			statsTextWidth = OpenGLText.smallTextWidth(meanText) 
					+ Theme.tickTextPadding 
					+ OpenGLText.smallTextWidth(stdDevText);
			xMeanTextLeft = xPlotLeft;
			xStdDevTextLeft = xPlotRight - OpenGLText.smallTextWidth(stdDevText);
			yStatsTextBaseline = yPlotTop - OpenGLText.smallTextHeight;
			
			if(statsTextWidth < plotWidth) {
				OpenGL.drawSmallText(gl, meanText,   (int) xMeanTextLeft,   (int) yStatsTextBaseline, 0);
				OpenGL.drawSmallText(gl, stdDevText, (int) xStdDevTextLeft, (int) yStatsTextBaseline, 0);
			}
			
			yPlotTop = yStatsTextBaseline - Theme.tickTextPadding;
			plotHeight = yPlotTop - yPlotBottom;
		}
		
		if(showMinMaxLabels) {
			
			// calculate range
			float minSample = samples.min;
			float maxSample = samples.max;
			
			yMinMaxLabelsBaseline = Theme.tilePadding;
			yMinMaxLabelsTop = yMinMaxLabelsBaseline + OpenGLText.smallTextHeight;
			minLabel = ChartUtils.formattedNumber(minSample, digiCount);
			maxLabel = ChartUtils.formattedNumber(maxSample, digiCount);
			minLabelWidth = OpenGLText.smallTextWidth(minLabel);
			maxLabelWidth = OpenGLText.smallTextWidth(maxLabel);
			
			yPlotBottom = yMinMaxLabelsTop + Theme.tickTextPadding;
			plotHeight = yPlotTop - yPlotBottom;
		}
		
		// get the chart label
		String label;
		if (showChartLabel && showReadingLabel)
			label = chartLabel + " " + readingLabel;
		else if (showChartLabel)
			label = chartLabel;
		else if (showReadingLabel)
			label = readingLabel;
		else
			label = " ";
		
		// get the center of the plot area
		float xPlotCenter = plotWidth / 2.0f + Theme.tilePadding;
		float yPlotCenter = yPlotBottom + (plotHeight - OpenGLText.largeTextHeight) / 2.0f;

		// draw the chart label
		labelWidth = OpenGLText.largeTextWidth(label);
		xLabelLeft = xPlotCenter - (labelWidth / 2);
		yLabelBaseline = yPlotCenter;
		OpenGLText.drawLargeText(label, (int) xLabelLeft, (int) yLabelBaseline, chartForeColor);

		// draw the background color
		OpenGL.drawBox(gl, chartBackColor, xPlotLeft, yPlotBottom, plotWidth, plotHeight);

		return handler;
		
	}

}
