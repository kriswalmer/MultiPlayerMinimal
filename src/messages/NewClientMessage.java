package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.LinkedList;
import server.FieldData;

@Serializable
public class NewClientMessage extends AbstractMessage {

    public int ID;
    public int target;
    public LinkedList<FieldData> field;
    public String s  ; 

    // -------------------------------------------------------------------------
    public NewClientMessage() {
    }

    // -------------------------------------------------------------------------
    public NewClientMessage(int ID, LinkedList<FieldData> playfield) {
        super();
        this.ID = ID;
        this.field = playfield;
        this.target = ID +1;
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
   
   
}
