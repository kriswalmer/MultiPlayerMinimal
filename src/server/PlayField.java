/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Rolf
 */
public class PlayField {

    public final static float MINMAX = 10f;
    public final static float RADIUS = 1f;
    public static LinkedList<FieldData> data;

    // -------------------------------------------------------------------------
    public PlayField() {
        data = new LinkedList<FieldData>();
    }

    // -------------------------------------------------------------------------
    public boolean addElement(int id) {
        Random rand = new Random();
        float x = rand.nextFloat() * 2 * MINMAX - MINMAX;
        float y = rand.nextFloat() * 2 * MINMAX - MINMAX;
        float z = rand.nextFloat() * 2 * MINMAX - MINMAX;
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        ColorRGBA c = new ColorRGBA(r, g, b, 1.0f);
        //
        FieldData newData = new FieldData(id, x, y, z, c);
        data.addLast(newData);
        //
        // here we could add a test for max. number of players reached.
        // (TODO)
        return (true);
    }
    
      public static int getClosestPlayer(Vector3f target){
          int id = -1;
          for(FieldData fd : data){
              if(fd.getMyLocation().distance(target)<= 1){
                  id = fd.id;
              }
          }
        
        return id;
    }
}
