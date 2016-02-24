package server;

import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.NewClientMessage;
import messages.Registration;

public class ServerNetworkHandler implements MessageListener, ConnectionListener {

    public static int SERVERPORT = 6143;
    Server server;
    ServerNetworkListener gameServer;

    // -------------------------------------------------------------------------
    public ServerNetworkHandler(GameServer l) {
        gameServer = l;
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
         final int STATE_ABSORB = 1 ; 
         final int STATE_ATTACK = 2 ; 
         final int STATE_DONATION = 3 ; 
         final int STATE_INFUSION = 4 ; 
         final int STATE_STOP_ABSORB = 5 ;
         final int STATE_STOP_DONATION = 6 ;
         int state = 0  ; 
         
         
     if (msg instanceof NewClientMessage) {
      // do something with the message
      NewClientMessage ncm = (NewClientMessage) msg ; 
      System.out.println(" received: '"+ncm.getString()+"'");
      
      
      if(ncm.getString().equals("Absorb") && state != STATE_ABSORB)
      {  state  = STATE_ABSORB ; 
         } 
      else if(ncm.getString().equals("Absorb") && state == STATE_ABSORB)
      {  state  = STATE_STOP_ABSORB ; 
         } 
      
      else if(ncm.getString().equals("Attack"))
      {  state  = STATE_ATTACK ; } 
      
      else if(ncm.getString().equals("Donation") && state != STATE_DONATION)
      {  state  = STATE_DONATION ; } 
      
      else if(ncm.getString().equals("Donation")&& state == STATE_DONATION)
      {  state  = STATE_STOP_DONATION ; } 
      
      else if(ncm.getString().equals("Infusion"))
      {  state  = STATE_INFUSION ; } 
      
      
          switch(state)
          {
              case ( STATE_ABSORB ):         
              {
                
                 }
                  
               case ( STATE_ATTACK ):         
              {
     
                 }
               case ( STATE_DONATION ):         
              {
     
                 }
               case ( STATE_INFUSION ):         
              {
     
                 }
               case(STATE_STOP_ABSORB):
               {
               
               
               }
               case(STATE_STOP_DONATION):
               {
               
               
               
               }
                  
                  
                  
             }
      //    System.out.println("absorb target ");
      //    NewClientMessage ncmtarget = (NewClientMessage) msg ; 
      // TODO add ncm to target after target ID is in NCM class  
      //sendToClient(ncm.getTarget() ncmtarget);
      // AttackMethods am = new AttackMethod(); 
      //  am.absorb()
      
      
      
       ncm.setString("message recieved + " + ncm.getString());
        
       sendToClient(ncm.ID, ncm );
      
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
