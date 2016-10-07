import javax.microedition.lcdui.*;

public class FontCanvas
    extends Canvas {
  private Font mSystemFont, mMonospaceFont, mProportionalFont;
  
  public FontCanvas() { this(Font.STYLE_PLAIN); }
  
  public FontCanvas(int style) { setStyle(style); }
  
  public void setStyle(int style) {
    mSystemFont = Font.getFont(Font.FACE_SYSTEM,
        style, Font.SIZE_MEDIUM);
    mMonospaceFont = Font.getFont(Font.FACE_MONOSPACE,
        style, Font.SIZE_MEDIUM);
    mProportionalFont = Font.getFont(Font.FACE_PROPORTIONAL,
        style, Font.SIZE_MEDIUM);
  }
  
  public boolean isBold() {
    return mSystemFont.isBold();
  }
  public boolean isItalic() {
    return mSystemFont.isItalic();
  }
  public boolean isUnderline() {
    return mSystemFont.isUnderlined();
  }
  
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    
    // Clear the Canvas.
    g.setGrayScale(255);
    g.fillRect(0, 0, w - 1, h - 1);
    g.setGrayScale(0);
    g.drawRect(0, 0, w - 1, h - 1);
    
    int x = w / 2;
    int y = 20;
    
    y += showFont(g, "System", x, y, mSystemFont);
    y += showFont(g, "Monospace", x, y, mMonospaceFont);
    y += showFont(g, "Proportional", x, y, mProportionalFont);
  }

  private int showFont(Graphics g, String s, int x, int y, Font f) {
    g.setFont(f);
    g.drawString(s, x, y, Graphics.TOP | Graphics.HCENTER);
    return f.getHeight();
  }
}

