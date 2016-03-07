package client;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Message;
import com.jme3.network.serializing.Serializable;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import java.awt.Dimension;
import java.awt.Toolkit;
import messages.NewClientMessage;
import messages.PlayerMessage;
import server.FieldData;
import server.PlayField;

@Serializable
public class GameClient extends SimpleApplication implements ClientNetworkListener, ActionListener {
    //

    private int ID = -1;
    protected ClientNetworkHandler networkHandler;
    private ClientPlayfield playfield;
    private Vector3f target;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Starting Client");
        //
        AppSettings aps = getAppSettings();
        //
        //     Serializer.registerClass(HelloMessage.class);
        GameClient app = new GameClient();
        app.setShowSettings(false);
        app.setSettings(aps);
        app.start();
    }

    // -------------------------------------------------------------------------
    public GameClient() {
        // this constructor has no fly cam!
        super(new StatsAppState(), new DebugKeysAppState());
    }

    // -------------------------------------------------------------------------
    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);
        //
        // CONNECT TO SERVER!
        networkHandler = new ClientNetworkHandler(this);
        //


        initGui();
        initCam();
        initLightandShadow();
        initPostProcessing();
        initKeys();
    }

    // -------------------------------------------------------------------------
    public void SimpleUpdate(float tpf) {
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
    private static AppSettings getAppSettings() {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        return (aps);
    }

    // -------------------------------------------------------------------------
    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

    // -------------------------------------------------------------------------
    private void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.7f, -1.3f, -0.9f)).normalizeLocal());
        sun.setColor(ColorRGBA.Gray);
        rootNode.addLight(sun);

        // Light 2: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        rootNode.addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }

    // -------------------------------------------------------------------------
    private void initPostProcessing() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setBlurScale(2.0f);
        bloom.setBloomIntensity(2.0f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/SKY.JPG", true);
        getRootNode().attachChild(sky);
    }

    // -------------------------------------------------------------------------
    private void initCam() {
        //flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(3f, 15f, 15f));
        cam.lookAt(new Vector3f(0, 0, 3), Vector3f.UNIT_Y);
    }

    // -------------------------------------------------------------------------
    // This client received its InitialClientMessage.
    private void initGame(NewClientMessage msg) {
        System.out.println("Received initial message from server. Initializing playfield.");
        //
        // store ID
        System.out.println(msg.getString());
        this.ID = msg.ID;
        System.out.println("My ID: " + this.ID);
        playfield = new ClientPlayfield(this);
        for (FieldData fd : msg.field) {
            playfield.addSphere(fd);

            float x = playfield.p.fd.x;
            float y = playfield.p.fd.y;
            float z = playfield.p.fd.z;

            NewClientMessage ncm = new NewClientMessage(this.ID, x, y, z, true);
            networkHandler.send(ncm);
        }



    }

    // -------------------------------------------------------------------------
    // Keyboard input
    private void initKeys() {
        inputManager.addMapping("PL_EXPLODE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Absorb", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("Attack", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Infusion", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("Donation", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Target", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(this, new String[]{"PL_EXPLODE", "Absorb", "Attack", "Infusion", "Donation", "Target"});


    }

    // key action
    @SuppressWarnings("empty-statement")
    public void onAction(String name, boolean isPressed, float tpf) {

        if (isPressed) {


            if (name.equals("Target")) {


                Vector2f mouseCoords = new Vector2f(inputManager.getCursorPosition().x, inputManager.getCursorPosition().y);
                CollisionResults results = new CollisionResults();
                Vector3f pos = cam.getWorldCoordinates(mouseCoords, 0).clone();
                Vector3f dir = cam.getWorldCoordinates(mouseCoords, 1f).clone();



                dir.subtractLocal(pos).normalizeLocal();

                Ray ray = new Ray(pos, dir);

                for (Player player : playfield.players) {
                    if (player.fd.id != this.ID) {
                        player.playerNode.collideWith(ray, results);

                    } else {
                        System.out.println("Cannot target yourself!");
                    }
                }


                System.out.println(ray.getDirection());
                //System.out.println(playfield.p.playerNode.getLocalTranslation());

                if (results.size() > 0) {

                    System.out.println(results.getClosestCollision().getContactPoint().toString());
                    target = results.getClosestCollision().getContactPoint();
                }

            } else {
                if (target == null) {
                    System.out.println("NO TARGET FOUND");
                } else {
                    float x = target.x;
                    float y = target.y;
                    float z = target.z;
                    NewClientMessage ncm = new NewClientMessage(this.ID, x, y, z, name);
                    networkHandler.send(ncm);

//
//            NewClientMessage ncm = new NewClientMessage(name + "name");
//            ncm.setString(name);
//            ncm.ID = ID;
//            networkHandler.send(ncm);
                }
            }

        }
    }

    // -------------------------------------------------------------------------
    // message received
    public void messageReceived(Message msg) {
        if (msg instanceof NewClientMessage) {


            NewClientMessage ncm = (NewClientMessage) msg;

            if (ncm.healths != null) {

                for (int i = 0; i < ncm.healths.size(); i++) {
                    playfield.updateHealthText(i, ncm.healths.get(i));
                    System.out.println("Player " + i + " has " + ncm.healths.get(i) + " health remaining.");
                }
            }



            if ((ncm.field != null) && (ncm.field.getLast() != null)) {
                if (this.ID == -1) {
                    initGame(ncm);
                } else if (ncm.field.getLast() != null) {
                    playfield.addSphere(ncm.field.getLast());

                }
            }




            if (!ncm.ability.equals("")) {

                int power = 0;
                Player targetedPlayer = new Player();

                for (Player play : playfield.players) {

                    if (play.fd.id == ncm.ID) {
                        power = playfield.p.energyLevel / 2;

                    }
                    if (play.fd.id == ncm.target) {
                        targetedPlayer = play;

                    }
                }
                float x = targetedPlayer.fd.x;
                float y = targetedPlayer.fd.y;
                float z = targetedPlayer.fd.z;

                float x2 = playfield.p.fd.x;
                float y2 = playfield.p.fd.y;
                float z2 = playfield.p.fd.z;

                Vector3f targetVector = new Vector3f(x, y, z);
                Vector3f myVector = new Vector3f(x2, y2, z2);


                for (Player p : playfield.players) {
                    System.out.println(" " + ncm.ID + " uses " + ncm.ability + " target " + ncm.target);
                    if (this.ID == ncm.ID && ncm.ability.equals("Absorb")) {
                        System.out.println("u are absorbing");

                        playfield.updateHealth(5, p);

                        

                        playfield.p.drawArrow(myVector, targetVector, ncm.ability);

                    } else if (this.ID == ncm.target && ncm.ability.equals("Absorb")) {

                        System.out.println("you are absorbed ");
                        playfield.updateHealth(-5, p);
                       
                        playfield.p.drawArrow(myVector, targetVector, ncm.ability);

                    } else if (this.ID == ncm.ID && ncm.ability.equals("Donate")) {

                        playfield.updateHealth(-5, p);
                        
                        playfield.p.drawArrow(myVector, targetVector, ncm.ability);

                    } else if (this.ID == ncm.target && ncm.ability.equals("Donate")) {
                        playfield.updateHealth(5, p);
                        
                        playfield.p.drawArrow(myVector, targetVector, ncm.ability);

                    } else if (this.ID != ncm.target && this.ID != ncm.ID) {
                        playfield.p.drawArrow(myVector, targetVector, ncm.ability);
                    }

                    if (ncm.ability.equals("Attack")) {


                        if (this.ID == ncm.ID) {



                            playfield.updateHealth(-power, p);
                           

                            playfield.p.drawArrow(myVector, targetVector, ncm.ability);


                            for (int i = 0; i < playfield.players.size(); i++) {
                                if (ncm.target == playfield.players.get(i).fd.id) {
                                    
                                }
                            }

                        } else if (this.ID == ncm.target) {
                            System.out.println("I was attacked. OW!");
                            playfield.updateHealth(-power, p);
                            
                            playfield.p.drawArrow(myVector, targetVector, ncm.ability);

                            for (int i = 0; i < playfield.players.size(); i++) {
                                if (ncm.ID == playfield.players.get(i).fd.id) {
                                   
                                }
                            }

                        } else if (this.ID != ncm.target && this.ID != ncm.ID) {
                            playfield.p.drawArrow(myVector, targetVector, ncm.ability);
                        }

                    }

                    if (ncm.ability.equals("Infusion")) {

                        if (this.ID == ncm.target) {
                            playfield.updateHealth(power, p);
                          
                            System.out.println("I AM BEING INFUSED");
                            playfield.p.drawArrow(new Vector3f(x2, y2, z2), new Vector3f(x, y, z), ncm.ability);


                        } else if (this.ID == ncm.ID) {

                            playfield.updateHealth(-power, p);
                            

                            playfield.p.drawArrow(new Vector3f(x2, y2, z2), new Vector3f(x, y, z), ncm.ability);
                        } else if (this.ID != ncm.target && this.ID != ncm.ID) {
                            playfield.p.drawArrow(myVector, targetVector, ncm.ability);
                        }

                    }

                }

                System.out.println("energy level " + playfield.p.energyLevel);
               

                NewClientMessage healthmessage = new NewClientMessage(this.ID, playfield.p.energyLevel);
                networkHandler.send(healthmessage);

            }

        }
    }

    public ClientPlayfield getPlayfield() {
        return playfield;
    }
}
