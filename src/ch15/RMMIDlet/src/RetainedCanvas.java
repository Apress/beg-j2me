import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.*;

class RetainedCanvas  extends GameCanvas implements Runnable {
    private boolean mRunning = false;
    private Thread mPaintThrd = null;
    
    private Graphics3D mGraphics3D = Graphics3D.getInstance();
    private World mWorld = null;
    private Camera mCam = null;
    private long mWorldStartTime = 0;
    
    public RetainedCanvas() {
        super(true);
    }
    
    public  void init() {
        try {
            mWorld = (World) Loader.load("/pogoroo.m3g")[0];
            mCam = mWorld.getActiveCamera();
            mCam.translate(0, 0, -1.5f);
            mCam.setOrientation(180, 0, 1,  0);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        mWorldStartTime = System.currentTimeMillis();
        
    }
    
    public void start() {
        mRunning = true;
        mPaintThrd = new Thread(this);
        mPaintThrd.start();
    }
    
    public void stop() {
        mRunning = false;
        try{
            mPaintThrd.join();}
        catch (InterruptedException ex){}
    }
    
    public void run() {
        Graphics g = getGraphics();
        long startTime;
        
        while(mRunning) {
            
            if (isShown()) {
                int keyStates = getKeyStates();
                if ((keyStates & UP_PRESSED) != 0) {
                    cameraForward();
                }
                else if ((keyStates & DOWN_PRESSED) != 0) {
                    cameraBackward();
                }
                else if ((keyStates & LEFT_PRESSED) != 0) {
                    cameraLeft();
                }
                else if ((keyStates & RIGHT_PRESSED) != 0) {
                    cameraRight();
                }
                startTime = System.currentTimeMillis() - mWorldStartTime;
                mWorld.animate((int)startTime);
                mGraphics3D.bindTarget(g);
                mGraphics3D.render(mWorld);
                mGraphics3D.releaseTarget();
                flushGraphics();
                try {Thread.sleep(100); }
                catch(InterruptedException ie){
                }
                
            }
        } // of while
    }
    
    
    private void cameraForward() {
        mCam.translate(0f, 0f, 0.2f);
    }
    private void cameraBackward() {
        mCam.translate(0f, 0f, -0.2f);
        
    }
    private void cameraLeft() {
        mCam.translate(-0.2f, 0f, 0f);
        
    }
    private void cameraRight() {
        mCam.translate(0.2f, 0f, 0f);
    }
    
    
}

