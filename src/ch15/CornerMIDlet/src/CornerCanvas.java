import javax.microedition.lcdui.*;
import javax.microedition.m3g.*;
import javax.microedition.lcdui.game.*;

public class CornerCanvas extends GameCanvas implements Runnable {
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
    private Image           mImage;
    public CornerCanvas() {
        super(true);
    }
    
    public void init()  {
        
        short[] vertices = {
            0, 0, 0,  3, 0, 0,  0, 3,  0,  3, 3,  0,
            3, 0, 0,  3, 3, 0,  3, 0, -3,  3, 3, -3,
            0, 0, 0,  3, 0, 0,  0, 0, -3,  3, 0, -3
        }; 
        
        VertexArray vertexArray = new VertexArray(vertices.length / 3, 3, 2);
        vertexArray.set(0, vertices.length/3, vertices);
        
        byte[] normals = {
            0,      0, 127,    0,    0, 127,     0,  0,  127,    0,    0, 127,
            127,    0,   0,  127,    0,   0,   127,  0,    0,  127,    0,   0,
            0,   -127,   0,    0, -127,   0,     0, -127,  0,    0, -127,   0
        };
        
        VertexArray normalsArray = new VertexArray(normals.length / 3, 3, 1);
        normalsArray.set(0, normals.length/3, normals);
        
        short[] texturecords = {
            0,1,     1,1,    0,  0,  1,  0,
            0,1,     0,0,    1,  1,  1,  0,
            0,0,     1,0,    0,  1,  1,  1  };
            
            VertexArray textureArray =
            new VertexArray(texturecords.length / 2, 2, 2);
            textureArray.set(0, texturecords.length/2, texturecords);
            
            int[] stripLength = { 4, 4, 4};
            
            VertexBuffer verbuf = mVertexBuffer = new VertexBuffer();
            verbuf.setPositions(vertexArray, 1.0f, null);
            verbuf.setNormals(normalsArray);
            verbuf.setTexCoords(0, textureArray, 1.0f, null);
            
            mIndexBuffer = new TriangleStripArray( 0, stripLength );
            try {
                mImage = Image.createImage( "/texture.png" );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            Image2D image2D = new Image2D( Image2D.RGB, mImage );
            Texture2D texture = new Texture2D( image2D );           
            mAppearance.setTexture(0, texture);
            mAppearance.setMaterial(mMaterial);
            mMaterial.setColor(Material.DIFFUSE, 0xffffffff);
            mMaterial.setColor(Material.SPECULAR, 0xffffffff);
            mMaterial.setShininess(100.0f);
            
            PolygonMode tPoly = new PolygonMode();
            tPoly.setCulling(PolygonMode.CULL_NONE);
            mAppearance.setPolygonMode(tPoly);
            mBackground.setColor(0x00ee88);
            
            mCamera.setPerspective( 60.0f,
            (float)getWidth()/ (float)getHeight(),
            1.0f,
            100.0f );
            
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
                
                mGraphics3D.bindTarget(g, true,
                Graphics3D.DITHER |
                Graphics3D.TRUE_COLOR);
                
                mGraphics3D.clear(mBackground);
                
                mTransform.setIdentity();
                mTransform.postTranslate(0.0f, 0.0f, 10.0f);
                mGraphics3D.setCamera(mCamera, mTransform);
                
                mGraphics3D.resetLights();
                mGraphics3D.addLight(mLight, mTransform);
                
                mAngle += 1.0f;
                mTransform.setIdentity();
                mTransform.postRotate(mAngle,
                1.0f, 1.0f, 1.0f );
                mGraphics3D.render(mVertexBuffer, mIndexBuffer,
                mAppearance, mTransform);
                
                mGraphics3D.releaseTarget();
                
                flushGraphics();
                try {Thread.sleep(40); }
                catch(InterruptedException ie){
                }
                
            }
        }
    }
    
}
