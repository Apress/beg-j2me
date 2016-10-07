import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class SweepGameCanvas
    extends GameCanvas
    implements Runnable {
  private boolean mTrucking;
  private int mTheta;
  private int mBorder;
  private int mDelay;
  
  public SweepGameCanvas() {
    super(true);
    mTheta = 0;
    mBorder = 10;
    mDelay = 50;
  }
  
  public void start() {
    mTrucking = true;
    Thread t = new Thread(this);
    t.start();
  }
  
  public void stop() {
    mTrucking = false;
  }
    
  public void render(Graphics g) {
    int width = getWidth();
    int height = getHeight();
    
    // Clear the Canvas.
    g.setGrayScale(255);
    g.fillRect(0, 0, width - 1, height - 1);
    
    int x = mBorder;
    int y = mBorder;
    int w = width - mBorder * 2;
    int h = height - mBorder * 2;
    for (int i = 0; i < 8; i++) {
      g.setGrayScale((8 - i) * 32 - 16);
      g.fillArc(x, y, w, h, mTheta + i * 10, 10);
      g.fillArc(x, y, w, h, (mTheta + 180) % 360 + i * 10, 10);
    }
  }
  
  public void run() {
    Graphics g = getGraphics();
    while (mTrucking) {
      mTheta = (mTheta + 1) % 360;
      render(g);
      flushGraphics();
      try { Thread.sleep(mDelay); }
      catch (InterruptedException ie) {}
    }
  }
}

