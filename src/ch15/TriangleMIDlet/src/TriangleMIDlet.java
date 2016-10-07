import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.*;

public class TriangleMIDlet extends MIDlet implements CommandListener {
    private Display mDisplay = null;
    private TriangleCanvas mCanvas = null;
    private Command exitCommand = new Command("Exit", Command.ITEM, 1);
        
    
    public TriangleMIDlet() {
        super();
        mDisplay = Display.getDisplay(this);
        mCanvas = new TriangleCanvas();
        mCanvas.setCommandListener(this);
        mCanvas.addCommand(exitCommand);
    }
    
    public void startApp()  {
        mCanvas.init();
        mDisplay.setCurrent(mCanvas);
        mCanvas.start();
    }
        
    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
     mCanvas.stop();
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
