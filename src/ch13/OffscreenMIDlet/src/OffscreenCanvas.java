import javax.microedition.lcdui.*;

public class OffscreenCanvas
    extends Canvas {
  private Image mImage;
  
  public void paint(Graphics g) {
    if (mImage == null)
      initialize();
    g.drawImage(mImage, 0, 0, Graphics.TOP | Graphics.LEFT);
  }
  
  private void initialize() {
    int w = getWidth();
    int h = getHeight();
    
    mImage = Image.createImage(w, h);
    
    Graphics g = mImage.getGraphics();
    
    g.drawRect(0, 0, w - 1, h - 1);
    g.drawLine(0, 0, w - 1, h - 1);
    g.drawLine(w - 1, 0, 0, h - 1);
  }
}

