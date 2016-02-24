package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.LinkedList;
import server.FieldData;

@Serializable
public class NewClientMessage extends AbstractMessage {

    public int ID;
    public LinkedList<FieldData> field;
    public String s  ; 
    int target;
    

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
   
     
       public NewClientMessage(int ID, LinkedList<FieldData> playfield, int target, String attackType) {
        super();
        this.ID = ID;
        this.field = playfield;
        this.target=target;
        s = attackType;
                
    }
   
}
