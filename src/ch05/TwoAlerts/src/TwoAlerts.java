import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class TwoAlerts
    extends MIDlet
    implements CommandListener {
  private Display mDisplay;
  
  private TextBox mTextBox;
  private Alert mTimedAlert;
  private Alert mModalAlert;
  
  private Command mAboutCommand, mGoCommand, mExitCommand;
  
  public TwoAlerts() {
    mAboutCommand = new Command("About", Command.SCREEN, 1);
    mGoCommand = new Command("Go", Command.SCREEN, 1);
    mExitCommand = new Command("Exit", Command.EXIT, 2);

    mTextBox = new TextBox("TwoAlerts", "", 32, TextField.ANY);
    mTextBox.addCommand(mAboutCommand);
    mTextBox.addCommand(mGoCommand);
    mTextBox.addCommand(mExitCommand);
    mTextBox.setCommandListener(this);

    mTimedAlert = new Alert("Network error",
        "A network error occurred. Please try again.",
        null,
        AlertType.INFO);
    mModalAlert = new Alert("About TwoAlerts",
        "TwoAlerts is a simple MIDlet that demonstrates the use of Alerts.",
        null,
        AlertType.INFO);
    mModalAlert.setTimeout(Alert.FOREVER);
  }

  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    mDisplay.setCurrent(mTextBox);
  }

  public void pauseApp() {
  }

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c == mAboutCommand)
      mDisplay.setCurrent(mModalAlert);
    else if (c == mGoCommand)
      mDisplay.setCurrent(mTimedAlert, mTextBox);
    else if (c == mExitCommand)
      notifyDestroyed();
  }
}

