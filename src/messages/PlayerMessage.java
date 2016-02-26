/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import client.Player;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.LinkedList;
import server.FieldData;

/**
 *
 * @author Cary
 */
@Serializable
public class PlayerMessage extends AbstractMessage {

    public int ID;
    public float x, y, z;
    public String ability;

    // -------------------------------------------------------------------------
    public PlayerMessage() {
    }

    // -------------------------------------------------------------------------
    public PlayerMessage(int ID, float x, float y, float z) {
        super();
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.z = z;

    }
}
