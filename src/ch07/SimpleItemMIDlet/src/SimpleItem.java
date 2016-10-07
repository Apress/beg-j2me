import javax.microedition.lcdui.*;

public class SimpleItem
    extends CustomItem {
  public SimpleItem(String title) { super(title); }
  
  // CustomItem abstract methods.
  
  public int getMinContentWidth() { return 100; }
  public int getMinContentHeight() { return 60; }
  
  public int getPrefContentWidth(int width) {
    return getMinContentWidth();
  }
  
  public int getPrefContentHeight(int height) {
    return getMinContentHeight();
  }
  
  public void paint(Graphics g, int w, int h) {
    g.drawRect(0, 0, w - 1, h - 1);
    g.setColor(0x000000ff);
    int offset = 0;
    for (int y = 4; y < h; y += 12) {
      offset = (offset + 12) % 24;
      for (int x = 4; x < w; x += 24) {
        g.fillTriangle(x + offset,     y,
                       x + offset - 3, y + 6,
                       x + offset + 3, y + 6);
      }
    }
  }
}
