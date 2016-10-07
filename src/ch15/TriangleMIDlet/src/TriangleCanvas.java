import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.*;
import javax.microedition.m3g.*;


public class TriangleCanvas extends GameCanvas implements Runnable {
    private boolean mRunning = false;
    private Thread mPaintThrd = null;
    
    private Graphics3D mGraphics3D = Graphics3D.getInstance();
    private Camera mCamera = new Camera();;
    private Light mLight = new Light();
    private float           mAngle = 0.0f;
    private Transform       mTransform = new Transform();
    private Background      mBackground = new Background();
    private VertexBuffer    mVertexBuffer;
    private IndexBuffer     mIndexBuffer;
    private Appearance      mAppearance = new Appearance();
    private Material        mMaterial = new Material();
    public TriangleCanvas() {
        super(true);
    }
    
    public void init()  {
        
        short[] vertices = { 0, 0, 0,  3, 0, 0,   0, 3, 0 }; // ,  3,3,0  };
        VertexArray vertexArray = new VertexArray(vertices.length / 3, 3, 2);
        vertexArray.set(0, vertices.length/3, vertices);
        
        byte[] normals = { 0, 0, 127,    0, 0, 127,    0, 0, 127 }; // , 0,0, 127 };
        
        VertexArray normalsArray = new VertexArray(normals.length / 3, 3, 1);
        normalsArray.set(0, normals.length/3, normals);
        
        
        VertexBuffer verbuf = mVertexBuffer = new VertexBuffer();
        verbuf.setPositions(vertexArray, 1.0f, null);
        verbuf.setNormals(normalsArray);
        
        int[] stripLength = { 3 }; // 4};
        mIndexBuffer = new TriangleStripArray( 0, stripLength );
                 /*        
                  PolygonMode tPoly = new PolygonMode();
                  tPoly.setCulling(PolygonMode.CULL_NONE);
                   mAppearance.setPolygonMode(tPoly);
                   */    
        
        mMaterial.setColor(Material.DIFFUSE, 0xFF0000);
        mMaterial.setColor(Material.SPECULAR, 0xFF0000);
        mMaterial.setShininess(100.0f);
        mAppearance.setMaterial(mMaterial);
        
        
        
        mBackground.setColor(0x00ee88);
        
        mCamera.setPerspective( 60.0f,
        (float)getWidth()/ (float)getHeight(),
        1.0f,
        1000.0f );
        
        mLight.setColor(0xffffff);
        mLight.setIntensity(1.25f);
        
        
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
        
        while(mRunning) {
            
            if (isShown()) {
                
                mGraphics3D.bindTarget(g);
                
                mGraphics3D.clear(mBackground);
                
                mTransform.setIdentity();
                mTransform.postTranslate(0.0f, 0.0f, 10.0f);
                mGraphics3D.setCamera(mCamera, mTransform);
                
                mGraphics3D.resetLights();
                mGraphics3D.addLight(mLight, mTransform);
                
                mAngle += 1.0f;
                mTransform.setIdentity();
                mTransform.postRotate(mAngle,
                 0, 0, 1.0f );
               //  1.0f, 0, 0 );
                mGraphics3D.render(mVertexBuffer, mIndexBuffer, mAppearance, mTransform);
                
                mGraphics3D.releaseTarget();
                flushGraphics();
                try {Thread.sleep(40); }
                catch(InterruptedException ie){
                }
                
            }
        } // of while
    }
    
    
}
