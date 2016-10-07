import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class Sweep
    extends MIDlet {
  public void startApp() {
    final SweepCanvas sweeper = new SweepCanvas();
    sweeper.start();
    
    sweeper.addCommand(new Command("Exit", Command.EXIT, 0));
    sweeper.setCommandListener(new CommandListener() {
      public void commandAction(Command c, Displayable s) {
        sweeper.stop();
        notifyDestroyed();
      }
    });
    
    Display.getDisplay(this).setCurrent(sweeper);
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
}

