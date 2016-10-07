import java.util.Vector;

import javax.microedition.lcdui.*;

public class StationSign
    extends CustomItem
    implements Runnable {
  private Vector mValues;
  private int mSelection;
  private boolean mTrucking;
  
  private Display mDisplay;
  private Font mFont;
  private int mVisibleIndexTimesTen;
  private boolean mFocus;
  
  public StationSign(String title, Display display) {
    super(title);
    mDisplay = display;
    mValues = new Vector();
    mSelection = 0;
    mTrucking = true;
    mFont = Font.getFont(Font.FONT_STATIC_TEXT);
    mVisibleIndexTimesTen = mSelection * 10;
    
    Thread t = new Thread(this);
    t.start();
  }
  
  public void add(String value) {
    if (value == null) return;
    mValues.addElement(value);
  }
  
  public void remove(String value) {
    if (value == null) return;
    mValues.removeElement(value);
  }
  
  public String getSelection() {
    if (mValues.size() == 0) return "";
    return (String)mValues.elementAt(mSelection);
  }
  
  public void flip() {
    mSelection++;
    if (mSelection >= mValues.size()) mSelection = 0;
  }
  
  public void dispose() {
    mTrucking = false;
  }
  
  // Runnable interface.
  
  public void run() {
    while (mTrucking) {
      int target = mSelection * 10;
      if (mVisibleIndexTimesTen != target) {
        mVisibleIndexTimesTen++;
        if (mVisibleIndexTimesTen >= mValues.size() * 10)
          mVisibleIndexTimesTen = 0;
        repaint();
      }
      try { Thread.sleep(50); }
      catch (InterruptedException ie) {}
    }
  }
  
  // CustomItem abstract methods.
  
  public int getMinContentWidth() {
    // Loop through the values. Find the maximum width.
    int maxWidth = 0;
    for (int i = 0; i < mValues.size(); i++) {
      String value = (String)mValues.elementAt(i);
      int width = mFont.stringWidth(value);
      maxWidth = Math.max(maxWidth, width);
    }
    // Don't forget about the title, although we don't
    // really know what font is used for that.
    int width = mFont.stringWidth(getLabel()) + 20;
    maxWidth = Math.max(maxWidth, width);
    return maxWidth;
  }
  
  public int getMinContentHeight() {
    return mFont.getHeight();
  }
  
  public int getPrefContentWidth(int width) {
    return getMinContentWidth();
  }
  
  public int getPrefContentHeight(int height) {
    return getMinContentHeight();
  }
  
  public void paint(Graphics g, int w, int h) {
    int fraction = mVisibleIndexTimesTen % 10;
    int visibleIndex = (mVisibleIndexTimesTen - fraction) / 10;
    String value = (String)mValues.elementAt(visibleIndex);

    g.setFont(mFont);
    int bc = mDisplay.getColor(Display.COLOR_BACKGROUND);
    int fc = mDisplay.getColor(Display.COLOR_FOREGROUND);
    if (mFocus == true) {
      bc = mDisplay.getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND);
      fc = mDisplay.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND);
    }
    g.setColor(bc);
    g.fillRect(0, 0, w, h);
    g.setColor(fc);

    // Simple case: visibleIndex is aligned on a single item.
    if (fraction == 0) {
      g.drawString(value, 0, 0, Graphics.TOP | Graphics.LEFT);
      return;
    }
    
    // Complicated case: show two items and a line.
    int lineHeight = mFont.getHeight();
    int divider = lineHeight - lineHeight * fraction / 10;
    
    // Draw the piece of the visible value.
    g.drawString(value, 0, divider - lineHeight,
        Graphics.TOP | Graphics.LEFT);
    // Now get the next value.
    visibleIndex = (visibleIndex + 1) % mValues.size();
    value = (String)mValues.elementAt(visibleIndex);
    
    // Draw the line.
    g.setStrokeStyle(Graphics.DOTTED);
    g.drawLine(0, divider, w, divider);

    g.drawString(value, 0, divider,
        Graphics.TOP | Graphics.LEFT);
  }
  
  // CustomItem methods.
  
  protected void keyPressed(int keyCode) { flip (); }
  
  protected void pointerPressed(int x, int y) { flip(); }
  
  protected boolean traverse(int dir,
      int viewportWidth, int viewportHeight,
      int[] visRect_inout) {
    mFocus = true;
    repaint();
    return false;
  }
  
  protected void traverseOut() {
    mFocus = false;
    repaint();
  }
}
