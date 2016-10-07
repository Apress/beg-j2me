import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class RMMIDlet extends MIDlet implements CommandListener {
    private Display myDisplay = null;
    private RetainedCanvas myCanvas = null;
    private Command exitCommand = new Command("Exit", Command.ITEM, 1);
    
    
    
    public RMMIDlet() {
        super();
        myDisplay = Display.getDisplay(this);
        myCanvas = new RetainedCanvas();
        myCanvas.setCommandListener(this);
        myCanvas.addCommand(exitCommand);
    }
    
    public void startApp()  {
        myCanvas.init();
        myDisplay.setCurrent(myCanvas);
        myCanvas.start();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
        myCanvas.stop();
    }
    
    
    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == exitCommand) {
            try {
                destroyApp(false);
                notifyDestroyed();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
}

