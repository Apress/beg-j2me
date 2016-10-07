import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class QuatschCanvas
    extends GameCanvas
    implements Runnable {
  private boolean mTrucking;
  
  private LayerManager mLayerManager;
  
  private TiledLayer mAtmosphere;
  private TiledLayer mBackground;
  private int mAnimatedIndex;
  
  private Sprite mQuatsch;
  private int mState, mDirection;
  
  private static final int kStanding = 1;
  private static final int kRunning = 2;
  
  private static final int kLeft = 1;
  private static final int kRight = 2;
  
  private static final int[] kRunningSequence = { 0, 1, 2 };
  private static final int[] kStandingSequence = { 3 };
  
   public QuatschCanvas(String quatschImageName,
      String atmosphereImageName, String backgroundImageName)
      throws IOException {
    super(true);
    
    // Create a LayerManager.
    mLayerManager = new LayerManager();
    int w = getWidth();
    int h = getHeight();
    mLayerManager.setViewWindow(96, 0, w, h);
    
    createBackground(backgroundImageName);
    createAtmosphere(atmosphereImageName);
    createQuatsch(quatschImageName);
  }
  
  private void createBackground(String backgroundImageName)
      throws IOException {
    // Create the tiled layer.
    Image backgroundImage = Image.createImage(backgroundImageName);
    int[] map = {
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      1, 2, 0, 0, 0, 0, 0, 0,
      3, 3, 2, 0, 0, 0, 5, 0,
      3, 3, 3, 2, 4, 1, 3, 2,
      6, 6, 6, 6, 6, 6, 6, 6
    };
    mBackground = new TiledLayer(8, 6, backgroundImage, 48, 48);
    mBackground.setPosition(12, 0);
    for (int i = 0; i < map.length; i++) {
      int column = i % 8;
      int row = (i - column) / 8;
      mBackground.setCell(column, row, map[i]);
    }
    mAnimatedIndex = mBackground.createAnimatedTile(8);
    mBackground.setCell(3, 0, mAnimatedIndex);
    mBackground.setCell(5, 0, mAnimatedIndex);
    mLayerManager.append(mBackground);
  }
    
  private void createAtmosphere(String atmosphereImageName)
      throws IOException {
    // Create the atmosphere layer
    Image atmosphereImage = Image.createImage(atmosphereImageName);
    mAtmosphere = new TiledLayer(8, 1, atmosphereImage,
        atmosphereImage.getWidth(), atmosphereImage.getHeight());
    mAtmosphere.fillCells(0, 0, 8, 1, 1);
    mAtmosphere.setPosition(0,192 );
    
    mLayerManager.insert(mAtmosphere, 0);
  }

  private void createQuatsch(String quatschImageName)
      throws IOException {
    // Create the sprite.
    Image quatschImage = Image.createImage(quatschImageName);
    mQuatsch = new Sprite(quatschImage, 48, 48);
    mQuatsch.setPosition(96 + (getWidth() - 48) / 2, 192);
    mQuatsch.defineReferencePixel(24, 24);
    setDirection(kLeft);
    setState(kStanding);
    mLayerManager.insert(mQuatsch, 1);
  }

  public void start() {
    mTrucking = true;
    Thread t = new Thread(this);
    t.start();
  }
  
  public void run() {
    int w = getWidth();
    int h = getHeight();
    Graphics g = getGraphics();
    int frameCount = 0;
    int factor = 2;
    int animatedDelta = 0;
    
    while (mTrucking) {
      if (isShown()) {
        int keyStates = getKeyStates();
        if ((keyStates & LEFT_PRESSED) != 0) {
          setDirection(kLeft);
          setState(kRunning);
          mBackground.move(3, 0);
          mAtmosphere.move(3, 0);
          mQuatsch.nextFrame();
        }
        else if ((keyStates & RIGHT_PRESSED) != 0) {
          setDirection(kRight);
          setState(kRunning);
          mBackground.move(-3, 0);
          mAtmosphere.move(-3, 0);
          mQuatsch.nextFrame();
        }
        else {
          setState(kStanding);
        }
        
        frameCount++;
        if (frameCount % factor == 0) {
          int delta = 1;
          if (frameCount / factor < 10) delta = -1;
          mAtmosphere.move(delta, 0);
          if (frameCount / factor == 20) frameCount = 0;

          mBackground.setAnimatedTile(mAnimatedIndex,
              8 + animatedDelta++);
          if (animatedDelta == 3) animatedDelta = 0;
        }

        g.setColor(0x5b1793);
        g.fillRect(0, 0, w, h);
        
        mLayerManager.paint(g, 0, 0);
        
        flushGraphics();
      }
      
      try { Thread.sleep(80); }
      catch (InterruptedException ie) {}
    }
  }
  
  public void stop() {
    mTrucking = false;
  }
  
  public void setVisible(int layerIndex, boolean show) {
    Layer layer = mLayerManager.getLayerAt(layerIndex);
    layer.setVisible(show);
  }
  
  public boolean isVisible(int layerIndex) {
    Layer layer = mLayerManager.getLayerAt(layerIndex);
    return layer.isVisible();
  }
  
  private void setDirection(int newDirection) {
    if (newDirection == mDirection) return;
    if (mDirection == kLeft)
      mQuatsch.setTransform(Sprite.TRANS_MIRROR);
    else if (mDirection == kRight)
      mQuatsch.setTransform(Sprite.TRANS_NONE);
    mDirection = newDirection;
  }
  
  private void setState(int newState) {
    if (newState == mState) return;
    switch (newState) {
      case kStanding:
        mQuatsch.setFrameSequence(kStandingSequence);
        mQuatsch.setFrame(0);
        break;
      case kRunning:
        mQuatsch.setFrameSequence(kRunningSequence);
        break;
      default:
        break;
    }
    mState = newState;
  }
}

