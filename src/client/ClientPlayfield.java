/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.network.serializing.Serializable;
import com.jme3.util.SafeArrayList;
import java.util.Collection;
import java.util.LinkedList;
import server.FieldData;

/**
 *
 * @author Rolf
 */

public class ClientPlayfield {
    

    SimpleApplication sa;
    Player p;
    LinkedList<Player> players;
    Class<Player> elementType;

 
   public ClientPlayfield(SimpleApplication sa) {
        this.sa = sa;
        players = new LinkedList<Player>();
    }

    public void addSphere(FieldData fd) {
        p = new Player(fd, sa);        
        players.add(p);
        sa.getRootNode().attachChild(p.playerNode);
        initText(p);
    }

    public int getPlayerEnergy() {
        return p.energyLevel;
    }

    public void increaseEnergyLevel(int energy) {
        p.energyLevel += energy;
    }

    public void decreaseEnergyLevel(int energy) {
        p.energyLevel -= energy;
    }

    public void initText(Player p) {
        int space =  p.fd.id;
        BitmapText healthText = new BitmapText(sa.getAssetManager().loadFont("Interface/Fonts/Arial.fnt"));
        healthText.setSize(sa.getAssetManager().loadFont("Interface/Fonts/Arial.fnt").getCharSet().getRenderedSize());      // font size
        healthText.setColor(ColorRGBA.Blue);                             // font color
        healthText.setText(" Client " + space);             // the text
        healthText.setLocalTranslation(space * 55, healthText.getLineHeight() + 30, 0); // position
        sa.getGuiNode().attachChild(healthText);

    }
}
