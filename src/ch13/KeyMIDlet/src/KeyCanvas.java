import javax.microedition.lcdui.*;

public class KeyCanvas
    extends Canvas {
  private Font mFont;
  private String mMessage = "[Press keys]";
  
  public KeyCanvas() {
    mFont = Font.getFont(Font.FACE_PROPORTIONAL,
        Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
  }
  
  public void paint(Graphics g) {
    int w = getWidth();
    int h = getHeight();
    
    // Clear the Canvas.
    g.setGrayScale(255);
    g.fillRect(0, 0, w - 1, h - 1);
    g.setGrayScale(0);
    g.drawRect(0, 0, w - 1, h - 1);
    
    g.setFont(mFont);
    
    int x = w / 2;
    int y = h / 2;
    
    g.drawString(mMessage, x, y, Graphics.BASELINE | Graphics.HCENTER);
  }
  
  protected void keyPressed(int keyCode) {
    int gameAction = getGameAction(keyCode);
    switch(gameAction) {
      case UP:     mMessage = "UP";             break;
      case DOWN:   mMessage = "DOWN";           break;
      case LEFT:   mMessage = "LEFT";           break;
      case RIGHT:  mMessage = "RIGHT";          break;
      case FIRE:   mMessage = "FIRE";           break;
      case GAME_A: mMessage = "GAME_A";         break;
      case GAME_B: mMessage = "GAME_B";         break;
      case GAME_C: mMessage = "GAME_C";         break;
      case GAME_D: mMessage = "GAME_D";         break;
      default:     mMessage = ""; break;
    }
    repaint();
  }
}

