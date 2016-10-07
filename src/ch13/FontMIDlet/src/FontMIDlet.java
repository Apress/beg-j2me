import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class FontMIDlet
    extends MIDlet
    implements CommandListener {
  private FontCanvas mFontCanvas;
  private Command mBoldCommand, mItalicCommand, mUnderlineCommand;
  
  public FontMIDlet() {
    mFontCanvas = new FontCanvas();

    mBoldCommand = new Command("Bold", Command.SCREEN, 0);
    mItalicCommand = new Command("Italic", Command.SCREEN, 0);
    mUnderlineCommand = new Command("Underline", Command.SCREEN, 0);
    Command exitCommand = new Command("Exit", Command.EXIT, 0);
    
    mFontCanvas.addCommand(mBoldCommand);
    mFontCanvas.addCommand(mItalicCommand);
    mFontCanvas.addCommand(mUnderlineCommand);
    mFontCanvas.addCommand(exitCommand);
    mFontCanvas.setCommandListener(this);
  }
  
  public void startApp() {
    Display.getDisplay(this).setCurrent(mFontCanvas);
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) {
      notifyDestroyed();
      return;
    }
    
    boolean isBold = mFontCanvas.isBold() ^ (c == mBoldCommand);
    boolean isItalic = mFontCanvas.isItalic() ^ (c == mItalicCommand);
    boolean isUnderline = mFontCanvas.isUnderline() ^
        (c == mUnderlineCommand);
    
    int style = 
        (isBold ? Font.STYLE_BOLD : 0) |
        (isItalic ? Font.STYLE_ITALIC : 0) |
        (isUnderline ? Font.STYLE_UNDERLINED : 0);
    
    mFontCanvas.setStyle(style);
    mFontCanvas.repaint();
  }
}

