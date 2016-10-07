import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

public class QuatschMIDlet
    extends MIDlet
    implements CommandListener {
  private Display mDisplay;
  
  private QuatschCanvas mQuatschCanvas;
  private Form mShowForm;
  private Command mExitCommand, mShowCommand, mOkCommand;
  
  public void startApp() {
    if (mQuatschCanvas == null) {
      try {
        mQuatschCanvas = new QuatschCanvas("/quatsch.png",
            "/atmosphere.png", "/background_tiles.png");
        mQuatschCanvas.start();
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mShowCommand = new Command("Show/Hide", Command.SCREEN, 0);
        mOkCommand = new Command("OK", Command.OK, 0);
        mQuatschCanvas.addCommand(mExitCommand);
        mQuatschCanvas.addCommand(mShowCommand);
        mQuatschCanvas.setCommandListener(this);
      }
      catch (IOException ioe) {
        System.out.println(ioe);
      }
    }
    
    mDisplay = Display.getDisplay(this);
    mDisplay.setCurrent(mQuatschCanvas);
  }
  
  public void pauseApp() {}
  
  public void destroyApp(boolean unconditional) {
    mQuatschCanvas.stop();
  }
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) {
      destroyApp(true);
      notifyDestroyed();
    }
    else if (c == mShowCommand) {
      if (mShowForm == null) {
        mShowForm = new Form("Show/Hide");
        ChoiceGroup cg = new ChoiceGroup("Layers", Choice.MULTIPLE);
        cg.append("Fog", null);
        cg.append("Dr. Quatsch", null);
        cg.append("Background", null);
        mShowForm.append(cg);
        mShowForm.addCommand(mOkCommand);
        mShowForm.setCommandListener(this);
      }
      ChoiceGroup cg = (ChoiceGroup)mShowForm.get(0);
      cg.setSelectedIndex(0, mQuatschCanvas.isVisible(0));
      cg.setSelectedIndex(1, mQuatschCanvas.isVisible(1));
      cg.setSelectedIndex(2, mQuatschCanvas.isVisible(2));
      mDisplay.setCurrent(mShowForm);
    }
    else if (c == mOkCommand) {
      ChoiceGroup cg = (ChoiceGroup)mShowForm.get(0);
      mQuatschCanvas.setVisible(0, cg.isSelected(0));
      mQuatschCanvas.setVisible(1, cg.isSelected(1));
      mQuatschCanvas.setVisible(2, cg.isSelected(2));
      mDisplay.setCurrent(mQuatschCanvas);
    }
  }
}
