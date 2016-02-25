package messages;

import client.Player;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.LinkedList;
import server.FieldData;

@Serializable
public class NewClientMessage extends AbstractMessage {

    public int ID;
    
    public LinkedList<FieldData> field;
    public String s  ; 
    public Vector3f target;
    public Player player;
    

    // -------------------------------------------------------------------------
    public NewClientMessage() {
    }

    // -------------------------------------------------------------------------
    public NewClientMessage(int ID, LinkedList<FieldData> playfield) {
        super();
        this.ID = ID;
        this.field = playfield;
    }
    public NewClientMessage(String st )
    {
    this.s = st ; 
    }
    
   public void  setString(String s )
    {
    this.s = s ; 
    }
   
   public String getString( )
    {
    return s ; 
    }
   
      //get click location, send to server, compare to fielddata
       public NewClientMessage(Player player, int ID, Vector3f target, String s) {
        super();
        this.ID = ID;
        this.s = s;
        this.target=target;
        this.player = player;
    }
   
}
