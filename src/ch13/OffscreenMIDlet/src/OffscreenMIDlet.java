import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class OffscreenMIDlet
    extends MIDlet {
  public void startApp() {
    Displayable d = new OffscreenCanvas();
    
    d.addCommand(new Command("Exit", Command.EXIT, 0));
    d.setCommandListener(new CommandListener() {
      public void commandAction(Command c, Displayable s) {
        notifyDestroyed();
      }
    });
    
    Display.getDisplay(this).setCurrent(d);
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
}

