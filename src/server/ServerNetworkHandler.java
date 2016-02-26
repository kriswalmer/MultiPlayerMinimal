package server;

import client.Player;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.NewClientMessage;
import messages.PlayerMessage;
import messages.Registration;

public class ServerNetworkHandler implements MessageListener, ConnectionListener {

    public static int SERVERPORT = 6143;
    Server server;
    ServerNetworkListener gameServer;
    public PlayField playfield;
    public Hashtable<Integer, Vector3f> playerLocations = new Hashtable<Integer, Vector3f>();
    public int targetID = -1;

    // -------------------------------------------------------------------------
    public ServerNetworkHandler(GameServer l) {
        gameServer = l;
        this.playfield = l.getPlayfield();
        try {
            server = Network.createServer(SERVERPORT);
            Registration.registerMessages();
            server.addMessageListener(this);
            server.addConnectionListener(this);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // -------------------------------------------------------------------------
    public void messageReceived(Object source, Message msg) {
        gameServer.messageReceived(msg);
        final int STATE_ABSORB = 1;
        final int STATE_ATTACK = 2;
        final int STATE_DONATION = 3;
        final int STATE_INFUSION = 4;
        final int STATE_STOP_ABSORB = 5;
        final int STATE_STOP_DONATION = 6;
        int state = 0;



        if (msg instanceof NewClientMessage) {
            // do something with the message
            NewClientMessage ncm = (NewClientMessage) msg;

            if (ncm.isPlayer) {
                Vector3f playerLocation = new Vector3f(ncm.x, ncm.y, ncm.z);
                System.out.println(playerLocation.toString());
                playerLocations.put(ncm.ID, playerLocation);
            } else {
                Vector3f target = new Vector3f(ncm.x, ncm.y, ncm.z);
                targetID = -1;
                //System.out.println(playerLocations.size());

                for (int i = 0; i < playerLocations.size(); i++) {

                    if (playerLocations.get(i).distance(target) <= 1f) {
                        targetID = i;

                    }

                }
                System.out.println(" received: '" + ncm.ability + " from player " + ncm.ID + " shooting @ " + target + " who's ID is " + targetID);



                if (ncm.ability.equals("Absorb") && state != STATE_ABSORB) {
                    state = STATE_ABSORB;

                } else if (ncm.ability.equals("Absorb") && state == STATE_ABSORB) {
                    state = STATE_STOP_ABSORB;
                } else if (ncm.ability.equals("Attack")) {
                    state = STATE_ATTACK;
                } else if (ncm.ability.equals("Donation") && state != STATE_DONATION) {
                    state = STATE_DONATION;
                } else if (ncm.ability.equals("Donation") && state == STATE_DONATION) {
                    state = STATE_STOP_DONATION;
                } else if (ncm.ability.equals("Infusion")) {
                    state = STATE_INFUSION;
                } else {
                    state = 0;
                }


                switch (state) {
                    case (STATE_ABSORB): {
                        //ncm.target start decreasing using System.time.currentMILLIS()
                        //ncm.ID start inscreasing
                        boolean actor = true;
                        NewClientMessage ability = new NewClientMessage(ncm.ID, "Absorb", targetID, actor);;
                       // sendToClient(ncm.ID, ability);
                         broadcast(ability); 
                      
                        


                    }
                    break;
                    case (STATE_ATTACK): {
                        boolean actor = true;
                        NewClientMessage ability = new NewClientMessage(ncm.ID, "Attack", targetID, actor);
                        sendToClient(ncm.ID, ability);

                        NewClientMessage myAbility = new NewClientMessage(targetID, "Attack", ncm.ID, !actor);
                        sendToClient(targetID, myAbility);
                 
                    }
                    break;
                    case (STATE_DONATION): {
//                    ncm.setString(ncm.target + " being donated from " + ncm.ID);
//                    sendToClient(ncm.target, ncm);

                        boolean actor = true;
                        NewClientMessage ability = new NewClientMessage(ncm.ID, "Donate", targetID, actor);;
                        sendToClient(ncm.ID, ability);

                        NewClientMessage myAbility = new NewClientMessage(targetID, "Donate", ncm.ID, !actor);
                        sendToClient(targetID, myAbility);



                        /* exact same concept as absorb*/
                    }
                    break;
                    case (STATE_INFUSION): {
//                    ncm.setString(ncm.target + " being infused from " + ncm.ID);
//                    sendToClient(ncm.target, ncm);
                        boolean actor = true;
                        NewClientMessage ability = new NewClientMessage(ncm.ID, "Infusion", targetID, actor);
                        sendToClient(ncm.ID, ability);

                        NewClientMessage myAbility = new NewClientMessage(targetID, "Infusion", ncm.ID, !actor);
                        sendToClient(targetID, myAbility);
                    }
                    break;
                    case (STATE_STOP_ABSORB): {
//                    ncm.setString(ncm.target + " stopping absorb from " + ncm.ID);
//                    sendToClient(ncm.target, ncm);
                    
                    
                    boolean actor = true;
                        NewClientMessage ability = new NewClientMessage(ncm.ID, "StopAbsorb", targetID, actor);;
                        sendToClient(ncm.ID, ability);

                        NewClientMessage myAbility = new NewClientMessage(targetID, "StopAbsorb", ncm.ID, !actor);
                        sendToClient(targetID, myAbility);
                    
                    
                    }
                    break;
                    case (STATE_STOP_DONATION): {
//                    }
                    break;


                }
               
            }
        }
    }
    }

    // -------------------------------------------------------------------------
    public void connectionAdded(Server server, HostedConnection conn) {
        int connID = conn.getId();
        System.out.println("Client " + connID + " connected");
        Message m;
        try {
            // gameServer.newConnectionReceived throws an Exception
            // if the connection should not be accepted.
            m = gameServer.newConnectionReceived(connID);
            
            // if all is ok, broadcast gameServer-created message
            // this is usually an InitialClientMessage
            // (which is just not hard coded here to keep it customizable).
            broadcast(m);
        } catch (Exception e) {
            // Connection not accepted.
            System.out.println("Connection not accepted. Kicking out client. TODO!!!" + connID);
        }

    }

    // -------------------------------------------------------------------------
    public void sendToClient(int ID, Message msg) {
        try {
            HostedConnection hc = server.getConnection(ID);
            server.broadcast(Filters.in(hc), msg);
        } catch (Exception e) {
            System.out.println("ERROR in sendToClient()");
        }
    }

    // -------------------------------------------------------------------------
    public void broadcast(Message m) {
        server.broadcast(m);
    }

    // -------------------------------------------------------------------------
    public void connectionRemoved(Server server, HostedConnection conn) {
        // TODO
    }
}
