import javax.microedition.lcdui.*;

public class BoxTextCanvas
    extends Canvas {
  private Font mFont;
  
  public BoxTextCanvas() {
    mFont = Font.getFont(Font.FACE_PROPORTIONAL,
        Font.STYLE_PLAIN, Font.SIZE_LARGE);
  }
    
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    
    g.setColor(0xffffff);
    g.fillRect(0, 0, w, h);
    g.setColor(0x000000);

    String s = "dolce";
    int stringWidth = mFont.stringWidth(s);
    int stringHeight = mFont.getHeight();
    int x = (w - stringWidth) / 2;
    int y = h / 2;
    
    g.setFont(mFont);
    g.drawString(s, x, y, Graphics.TOP | Graphics.LEFT);
    g.drawRect(x, y, stringWidth, stringHeight);
  }
}

