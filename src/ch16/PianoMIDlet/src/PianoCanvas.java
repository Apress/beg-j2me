import javax.microedition.lcdui.*;
import javax.microedition.media.*;

public class PianoCanvas
    extends Canvas {
  private static final int[] kNoteX = {
     0, 11, 16, 29, 32, 48, 59, 64, 76, 80, 93, 96
  };
  
  private static final int[] kNoteWidth = {
    16,  8, 16,  8, 16, 16,  8, 16,  8, 16,  8, 16
  };
  
  private static final int[] kNoteHeight = {
    96, 64, 96, 64, 96, 96, 64, 96, 64, 96, 64, 96
  };
  
  private static final boolean[] kBlack = {
    false, true, false, true, false,
        false, true, false, true, false, true, false
  };
  
  private int mMiddleCX, mMiddleCY;
  
  private int mCurrentNote;
  
  public PianoCanvas() {
    int w = getWidth();
    int h = getHeight();
    
    int fullWidth = kNoteWidth[0] * 8;
    mMiddleCX = (w - fullWidth) / 2;
    mMiddleCY = (h - kNoteHeight[0]) / 2;
    
    mCurrentNote = 60;
  }
  
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    
    g.setColor(0xffffff);
    g.fillRect(0, 0, w, h);
    g.setColor(0x000000);
    
    for (int i = 60; i <= 72; i++)
      drawNote(g, i);
    
    drawSelection(g, mCurrentNote);
  }
  
  private void drawNote(Graphics g, int note) {
    int n = note % 12;
    int octaveOffset = ((note - n) / 12 - 5) * 7 * kNoteWidth[0];
    int x = mMiddleCX + octaveOffset + kNoteX[n];
    int y = mMiddleCY;
    int w = kNoteWidth[n];
    int h = kNoteHeight[n];
    
    if (isBlack(n))
      g.fillRect(x, y, w, h);
    else
      g.drawRect(x, y, w, h);
  }
  
  private void drawSelection(Graphics g, int note) {
    int n = note % 12;
    int octaveOffset = ((note - n) / 12 - 5) * 7 * kNoteWidth[0];
    int x = mMiddleCX + octaveOffset + kNoteX[n];
    int y = mMiddleCY;
    int w = kNoteWidth[n];
    int h = kNoteHeight[n];
    
    int sw = 6;
    int sx = x + (w - sw) / 2;
    int sy = y + h - 8;
    g.setColor(0xffffff);
    g.fillRect(sx, sy, sw, sw);
    g.setColor(0x000000);
    g.drawRect(sx, sy, sw, sw);
    g.drawLine(sx, sy, sx + sw, sy + sw);
    g.drawLine(sx, sy + sw, sx + sw, sy);
  }
  
  private boolean isBlack(int note) {
    return kBlack[note];
  }
  
  public void keyPressed(int keyCode) {
    int action = getGameAction(keyCode);
    switch (action) {
      case LEFT:
        mCurrentNote--;
        if (mCurrentNote < 60)
          mCurrentNote = 60;
        repaint();
        break;
      case RIGHT:
        mCurrentNote++;
        if (mCurrentNote > 72)
          mCurrentNote = 72;
        repaint();
        break;
      case FIRE:
        try { Manager.playTone(mCurrentNote, 1000, 100); }
        catch (MediaException me) {}
        break;
      default:
        break;
    }
  }
}

