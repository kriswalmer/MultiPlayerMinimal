/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import server.FieldData;

/**
 *
 * @author Cary
 */
@Serializable
public class Player extends Node {

    public int energyLevel = 100;
    public FieldData fd;
    public Node playerNode = new Node();
    public SimpleApplication sa;

    public Player() {
    }

    public Player(FieldData fd, SimpleApplication sa) {
        this.sa = sa;
        this.fd = fd;
        Sphere s = new Sphere(32, 32, 1);
        Geometry sg = new Geometry("", s);
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

    public void drawArrow(Vector3f position, Vector3f target) {


        Vector3f unitX = new Vector3f(1, 0, 0);
        Vector3f rotAxis = unitX.cross(target);
        float sinAlpha = rotAxis.length();
        float cosineAlpha = unitX.dot(target);
        float alpha = FastMath.atan2(sinAlpha, cosineAlpha);

        Quaternion q = new Quaternion();
        q.fromAngleAxis(alpha, rotAxis);
        Arrow laser1 = new Arrow(position);
        Geometry laser1Geom = new Geometry("Laser1", laser1);

        laser1.setLineWidth(100f);
        

        Material laser1Mat = new Material(sa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        laser1Mat.setBoolean("UseMaterialColors", true);
        laser1Mat.setColor("Ambient", ColorRGBA.Red);
        laser1Mat.setColor("Diffuse", ColorRGBA.Red);
        laser1Geom.setMaterial(laser1Mat);

        playerNode.attachChild(laser1Geom);


    }
}
