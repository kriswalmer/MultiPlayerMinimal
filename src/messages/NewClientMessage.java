package messages;

import client.Player;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.util.SafeArrayList;
import java.util.LinkedList;
import server.FieldData;

@Serializable
public class NewClientMessage extends AbstractMessage {

    public int ID;
    public LinkedList<FieldData> field;
    public String s;
//   public Player p;
    public float x, y, z;
    //public Vector3f target;
    public String ability;
    public boolean isPlayer;

    // -------------------------------------------------------------------------
    public NewClientMessage() {
    }

    // -------------------------------------------------------------------------
    public NewClientMessage(int ID, LinkedList<FieldData> playfield) {
        super();
        this.ID = ID;
        this.field = playfield;
        this.isPlayer= false;

    }

    public NewClientMessage(String st) {
        this.s = st;
    }

    public void setString(String s) {
        this.s = s;
    }

    public String getString() {
        return s;
    }

    public NewClientMessage(int ID, float x, float y, float z, String ability) {
        super();
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ability = ability;
        this.isPlayer = false;

    }

    public NewClientMessage(int ID, float x, float y, float z, boolean isPlayer) {
        super();
        this.ID = ID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.isPlayer = isPlayer;

    }
}
