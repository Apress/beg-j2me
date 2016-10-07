import java.io.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class ToneMIDlet
    extends MIDlet
    implements CommandListener {
  private final static String kSoundOfMusic = "Sound of Music";
  private final static String kQuandoMenVo = "Quando men vo";
  private final static String kTwinkle = "Twinkle number VII";
  
  private Display mDisplay;
  private List mMainScreen;
  
  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    if (mMainScreen == null) {
      mMainScreen = new List("AudioMIDlet", List.IMPLICIT);
  
      mMainScreen.append(kSoundOfMusic, null);
      mMainScreen.append(kQuandoMenVo, null);
      mMainScreen.append(kTwinkle, null);
      mMainScreen.addCommand(new Command("Exit", Command.EXIT, 0));
      mMainScreen.addCommand(new Command("Play", Command.SCREEN, 0));
      mMainScreen.setCommandListener(this);
    }

    mDisplay.setCurrent(mMainScreen);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) notifyDestroyed();
    else run();
  }

  public void run() {
    String selection = mMainScreen.getString(
        mMainScreen.getSelectedIndex());
    
    byte[] sequence = null;
    if (selection.equals(kSoundOfMusic)) {
      sequence = new byte[] {
        ToneControl.VERSION, 1,
        67, 16, // The
        69, 16, // hills
        67,  8, // are
        65,  8, // a -
        64, 48, // live
        62,  8, // with
        60,  8, // the
        59, 16, // sound
        57, 16, // of
        59, 32, // mu -
        59, 32  // sic
      };
    }
    else if (selection.equals(kQuandoMenVo)) {
      sequence = new byte[] {
        ToneControl.VERSION, 1,
        ToneControl.TEMPO, 22,
        ToneControl.RESOLUTION, 96,
        64, 48, ToneControl.SILENCE, 8, 52, 4, 56, 4, 59, 4, 64, 4,
        63, 48, ToneControl.SILENCE, 8, 52, 4, 56, 4, 59, 4, 63, 4,
        61, 72,
        ToneControl.SILENCE, 12, 61, 12,
            63, 12, 66, 2, 64, 10, 63, 12, 61, 12,
        64, 12, 57, 12, 57, 48,
        ToneControl.SILENCE, 12, 59, 12,
            61, 12, 64, 2, 63, 10, 61, 12, 59, 12,
        63, 12, 56, 12, 56, 48,
      };
    }
    else if (selection.equals(kTwinkle)) {
      sequence = new byte[] {
        ToneControl.VERSION, 1,
        ToneControl.TEMPO, 22,
        ToneControl.BLOCK_START, 0,
        60, 8,        62, 4, 64, 4, 65, 4, 67, 4, 69, 4, 71, 4,
        72, 4, 74, 4, 76, 4, 77, 4, 79, 4, 81, 4, 83, 4, 84, 4,
        83, 4, 81, 4, 80, 4, 81, 4, 86, 4, 84, 4, 83, 4, 81, 4,
        81, 4, 79, 4, 78, 4, 79, 4, 60, 4, 79, 4, 88, 4, 79, 4,
        57, 4, 77, 4, 88, 4, 77, 4, 59, 4, 77, 4, 86, 4, 77, 4,
        56, 4, 76, 4, 86, 4, 76, 4, 57, 4, 76, 4, 84, 4, 76, 4,
        53, 4, 74, 4, 84, 4, 74, 4, 55, 4, 74, 4, 83, 4, 74, 4,
        84, 16, ToneControl.SILENCE, 16,
        ToneControl.BLOCK_END, 0,
        ToneControl.BLOCK_START, 1,
        79, 4, 84, 4, 88, 4, 86, 4, 84, 4, 83, 4, 81, 4, 79, 4,
        77, 4, 76, 4, 74, 4, 72, 4, 71, 4, 69, 4, 67, 4, 65, 4,
        64, 8,        76, 8,        77, 8,        78, 8,
        79, 12,              76, 4, 74, 8, ToneControl.SILENCE, 8,
        ToneControl.BLOCK_END, 1,
        
        ToneControl.SET_VOLUME, 100, ToneControl.PLAY_BLOCK, 0,
        ToneControl.SET_VOLUME,  50, ToneControl.PLAY_BLOCK, 0,
        ToneControl.SET_VOLUME, 100, ToneControl.PLAY_BLOCK, 1,
        ToneControl.SET_VOLUME,  50, ToneControl.PLAY_BLOCK, 1,
        ToneControl.SET_VOLUME, 100, ToneControl.PLAY_BLOCK, 0,
      };
    }
    try {
      Player player = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
      player.realize();
      ToneControl tc = (ToneControl)player.getControl("ToneControl");
      tc.setSequence(sequence);
      player.start();
    }
    catch (Exception e) {
      Alert a = new Alert("Exception", e.toString(), null, null);
      a.setTimeout(Alert.FOREVER);
      mDisplay.setCurrent(a, mMainScreen);
    }
  }
}
