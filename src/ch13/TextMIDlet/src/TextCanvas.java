import javax.microedition.lcdui.*;

public class TextCanvas
    extends Canvas {
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    
    g.setColor(0xffffff);
    g.fillRect(0, 0, w, h);
    g.setColor(0x000000);

    // First label the four corners.
    g.drawString("corner", 0, 0,
        Graphics.TOP | Graphics.LEFT);
    g.drawString("corner", w, 0,
        Graphics.TOP | Graphics.RIGHT);
    g.drawString("corner", 0, h,
        Graphics.BOTTOM | Graphics.LEFT);
    g.drawString("corner", w, h,
        Graphics.BOTTOM | Graphics.RIGHT);

    // Now put something in the middle (more or less).
    g.drawString("Sin Wagon", w / 2, h / 2,
        Graphics.BASELINE | Graphics.HCENTER);
  }
}

