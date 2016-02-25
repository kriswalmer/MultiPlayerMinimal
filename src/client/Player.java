/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import server.FieldData;

/**
 *
 * @author Cary
 */
public class Player  {
    
    int energyLevel=100;
    FieldData fd;
    Node playerNode = new Node();
    
    
    
    public Player(FieldData fd, SimpleApplication sa){
        
        this.fd = fd;
        Sphere s = new Sphere(32,32,1);
        Geometry sg = new Geometry("",s);
        Material mat = new Material(sa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", fd.color);
        mat.setColor("Diffuse", ColorRGBA.Orange);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f); // shininess from 1-128
        sg.setMaterial(mat);
        sg.setLocalTranslation(fd.x, fd.y, fd.z);
        playerNode.attachChild(sg);
        
     
    }
    
    
}
