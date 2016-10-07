import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class GaugeMIDlet
    extends MIDlet
    implements CommandListener {
  private Display mDisplay;
  
  private Form mGaugeForm;
  private Command mUpdateCommand, mIdleCommand;
  
  private Gauge mInteractiveGauge;
  private Gauge mIncrementalGauge;
  private Gauge mContinuousGauge;
    
  public GaugeMIDlet() {
    mGaugeForm = new Form("Gauges");
    mInteractiveGauge = new Gauge("Interactive", true, 5, 2);
    mInteractiveGauge.setLayout(Item.LAYOUT_2);
    mGaugeForm.append(mInteractiveGauge);
    mContinuousGauge = new Gauge("Non-I continuous", false,
        Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
    mContinuousGauge.setLayout(Item.LAYOUT_2);
    mGaugeForm.append(mContinuousGauge);
    mIncrementalGauge = new Gauge("Non-I incremental", false,
        Gauge.INDEFINITE, Gauge.INCREMENTAL_UPDATING);
    mIncrementalGauge.setLayout(Item.LAYOUT_2);
    mGaugeForm.append(mIncrementalGauge);

    mUpdateCommand = new Command("Update", Command.SCREEN, 0);
    mIdleCommand = new Command("Idle", Command.SCREEN, 0);
    Command exitCommand = new Command("Exit", Command.EXIT, 0);
    mGaugeForm.addCommand(mUpdateCommand);
    mGaugeForm.addCommand(mIdleCommand);
    mGaugeForm.addCommand(exitCommand);
    mGaugeForm.setCommandListener(this);
  }
  
  public void startApp() {
    if (mDisplay == null) mDisplay = Display.getDisplay(this);
    mDisplay.setCurrent(mGaugeForm);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT)
      notifyDestroyed();
    else if (c == mUpdateCommand) {
      mContinuousGauge.setValue(Gauge.CONTINUOUS_RUNNING);
      mIncrementalGauge.setValue(Gauge.INCREMENTAL_UPDATING);
    }
    else if (c == mIdleCommand) {
      mContinuousGauge.setValue(Gauge.CONTINUOUS_IDLE);
      mIncrementalGauge.setValue(Gauge.INCREMENTAL_IDLE);
    }
  }
}

