import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * A helper class for font rendering. Example usage:
 * 
 * 1. Call FontUtils.setOffsets() to set the bottom-left corner of where the
 * chart is located in the GLcanvas. 2. The chart can then queue font rendering
 * with calls to the FontUtils.draw*text() methods. 3. After the chart has been
 * drawn with OpenGL, call FontUtils.drawQueuedText() to render text on top of
 * the chart.
 * 
 * The *TextHeight fields and *TextWidth() methods can be used to get the size
 * of text in pixels.
 */
public class OpenGLText {

	private static int xOffset = 0;
	private static int yOffset = 0;
	private static int canvasWidth = 0;
	private static int canvasHeight = 0;
	private static boolean displayScalingChanged = true;
	private static int framesSinceFlush = 0;

	// Small font
	private static final Queue<PositionedText> smallFontQueue = new LinkedList<PositionedText>();
	private static TextRenderer smallFontRenderer;
	public static float smallTextHeight;
	public static float smallTextWidth(String text) {
		return (float) Theme.smallFont.getStringBounds(text, smallFontRenderer.getFontRenderContext()).getWidth();
	}
	
	// Medium font
	private static final Queue<PositionedText> mediumFontQueue = new LinkedList<PositionedText>();
	private static TextRenderer mediumFontRenderer;
	public static float mediumTextHeight;
	public static float mediumTextWidth(String text) {
		return (float) Theme.smallFont.getStringBounds(text, smallFontRenderer.getFontRenderContext()).getWidth();
	}
	
	// Large font
	private static final Queue<PositionedText> largeFontQueue = new LinkedList<PositionedText>();
	private static TextRenderer largeFontRenderer;
	public static float largeTextHeight;
	public static float largeTextWidth(String text) {
		return (float) Theme.largeFont.getStringBounds(text, largeFontRenderer.getFontRenderContext()).getWidth();
	}
	
	OpenGLText ()
	{}
	
	/**
	 * Called by the Controller when the display scaling factor changes.
	 * 
	 * @param newFactor The new display scaling factor.
	 */
	public static void scalingFactorChanged(float newFactor) {

		displayScalingChanged = true;

	}
	
	/**
	 * Recalculate the display scale
	 */
	public static void initialise(GL2ES3 gl) {
		
		// Small font
		OpenGLText.smallFontRenderer = new TextRenderer(Theme.smallFont, true, true);
		OpenGLText.smallTextHeight = Theme.smallFont
				.createGlyphVector(smallFontRenderer.getFontRenderContext(), "Test")
				.getPixelBounds(smallFontRenderer.getFontRenderContext(), 0, 0).height;
		
		// Medium font
		OpenGLText.mediumFontRenderer = new TextRenderer(Theme.mediumFont, true, true);
		OpenGLText.mediumTextHeight = Theme.mediumFont
				.createGlyphVector(mediumFontRenderer.getFontRenderContext(), "Test")
				.getPixelBounds(mediumFontRenderer.getFontRenderContext(), 0, 0).height;
		
		// Large font
		OpenGLText.largeFontRenderer = new TextRenderer(Theme.largeFont, true, true);
		OpenGLText.largeTextHeight = Theme.largeFont
				.createGlyphVector(largeFontRenderer.getFontRenderContext(), "Test")
				.getPixelBounds(largeFontRenderer.getFontRenderContext(), 0, 0).height;
		
	}

	/**
	 * Saves the location of the chart's lower-left corner in the GLcanvas, and the
	 * size of the canvas. This needs to be called before using any of the
	 * draw*text() methods.
	 * 
	 * @param xOffset The x location of the lower-left corner, in pixels.
	 * @param yOffset The y location of the lower-left corner, in pixels.
	 * @param width   The canvas width, in pixels.
	 * @param height  The canvas height, in pixels.
	 */
	public static void setOffsets(int xOffset, int yOffset, int width, int height) {
		OpenGLText.xOffset = xOffset;
		OpenGLText.yOffset = yOffset;
		OpenGLText.canvasWidth = width;
		OpenGLText.canvasHeight = height;
	}
	
	public static void drawSmallText(String text, int x, int y, Color color) {
		smallFontQueue.add(new PositionedText(text, x + xOffset, y + yOffset, color));
	}
	
	public static void drawMediumText(String text, int x, int y, Color color) {
		mediumFontQueue.add(new PositionedText(text, x + xOffset, y + yOffset, color));
	}
	
	public static void drawLargeText(String text, int x, int y, Color color) {
		largeFontQueue.add(new PositionedText(text, x + xOffset, y + yOffset, color));
	}

	public static void drawQueuedText(GL2ES3 gl) {

		// work around memory leak in TextRenderer by replacing them periodically
		framesSinceFlush++;
		if (displayScalingChanged || framesSinceFlush >= 18000) { // 5 minutes of 60hz
			
			OpenGLText.initialise(gl);
			displayScalingChanged = false;
			framesSinceFlush = 0;

		}
		
		smallFontRenderer.beginRendering(canvasWidth, canvasHeight);
		while (!smallFontQueue.isEmpty()) {
			PositionedText pt = smallFontQueue.remove();
			smallFontRenderer.setColor(pt.color);
			smallFontRenderer.draw(pt.text, pt.x, pt.y);
		}
		smallFontRenderer.endRendering();
		
		smallFontRenderer.beginRendering(canvasWidth, canvasHeight);
		while (!smallFontQueue.isEmpty()) {
			PositionedText pt = smallFontQueue.remove();
			smallFontRenderer.setColor(pt.color);
			smallFontRenderer.draw(pt.text, pt.x, pt.y);
		}
		smallFontRenderer.endRendering();
		
		smallFontRenderer.beginRendering(canvasWidth, canvasHeight);
		while (!smallFontQueue.isEmpty()) {
			PositionedText pt = smallFontQueue.remove();
			smallFontRenderer.setColor(pt.color);
			smallFontRenderer.draw(pt.text, pt.x, pt.y);
		}
		smallFontRenderer.endRendering();

	}

	private static class PositionedText {
		String text;
		int x;
		int y;
		Color color; 

		public PositionedText(String text, int x, int y, Color color) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}
}
