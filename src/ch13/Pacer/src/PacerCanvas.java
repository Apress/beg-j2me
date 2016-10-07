import javax.microedition.lcdui.*;

public class PacerCanvas
    extends Canvas { 
  public void paint(Graphics g) { 
    int w = getWidth();
    int h = getHeight();
    
    g.setColor(0xffffff);
    g.fillRect(0, 0, w, h);
    g.setColor(0x000000);

    for (int x = 0; x < w; x += 10)
      g.drawLine(0, w - x, x, 0);
    
    int z = 50;
    g.drawRect(z, z, 20, 20);
    z += 20;
    g.fillRoundRect(z, z, 20, 20, 5, 5);
    z += 20;
    g.drawArc(z, z, 20, 20, 0, 360);
  } 
} 

