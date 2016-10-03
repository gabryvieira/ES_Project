/**
 * Created by gabriel on 03-10-2016.
 */

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.*;
public class XmppOpenfire {

    private String loginUsername;
    private String loginPassword;

    private final String localhost = "127.0.0.1";
    private final int port = 5222; // client port number
    private AbstractXMPPConnection connection;
    private int chatOp;

    private String newUsername;
    private String newUserPass;

    private MultiUserChatManager manager;

    public XmppOpenfire(){
        chatOp = 0;
        newUsername = "";
        newUserPass = "";
    }



            // get instance connection
    public  MultiUserChatManager getInstanceForConnection(AbstractXMPPConnection connection){
        manager = MultiUserChatManager.getInstanceFor(connection);
        return manager;
    }


            // criacao da ligacao
    public AbstractXMPPConnection createConnection(String loginUsername, String loginPassword) throws Exception {
        XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName("example.com")
                .setHost(localhost)
                .setPort(port)
                .setCompressionEnabled(false)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                })
                .setUsernameAndPassword(loginUsername, loginPassword).build(); // user com que se esta a "mexer"

        connection = new XMPPTCPConnection(connConfig);
        connection.connect();
        connection.login(); // credentials by default to access openfire (ADMIN ONLY)
        System.out.print("Connected to openfire server!");

        return connection;
    }


            // criacao de um novo user
    public void createUser(AbstractXMPPConnection connection, String newUsername, String newUserPass) throws Exception {
        AccountManager ac = AccountManager.getInstance(connection);
        ac.createAccount(newUsername, newUserPass);
        System.out.println("User created :)");
    }

    public void createChat(AbstractXMPPConnection connection, int chatOp) throws Exception {

        // Get the MultiUserChatManager
        manager = getInstanceForConnection(connection);
        //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        // Get a MultiUserChat using MultiUserChatManager
        MultiUserChat muc = manager.getMultiUserChat("chat@conference.admin");

        switch(chatOp) {
            case 1:
                // persistent chat
                muc.create("testbot"); // nickname
                Form form = muc.getConfigurationForm();
                Form answerForm = form.createAnswerForm();
                answerForm.setAnswer("muc#roomconfig_persistentroom", true);
                muc.sendConfigurationForm(answerForm);
                System.out.println("Persistent chat was created :)!");
                break;

            case 2:
                // instant chat
                muc.create("testbot"); // nickname
                // Send an empty room configuration form which indicates that we want an instant room
                muc.sendConfigurationForm(new Form(DataForm.Type.submit));
                System.out.println("Instant chat was created :)!");
                break;

            case 0:
                break;

            default:
                System.out.println("Please, choose one of these options!");
                break;
        }
    }

    /*public void joinToChatRoom(AbstractXMPPConnection connection) throws Exception{
        // Create a MultiUserChat using an XMPPConnection for a room
        manager = getInstanceForConnection(connection);
        MultiUserChat muc2 = manager.getMultiUserChat("sala@conference.admin");

        // User2 joins the new room using a password and the nickname is your username
        // the amount of history to receive. In this example we are requesting the last 10 messages.
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(10); // request the last 10 messages
        muc2.join(connection.getUser(), "password", history, connection.getPacketReplyTimeout());
        System.out.println("Joined to chat :)");

        // room information
        RoomInfo info = manager.getRoomInfo("sala@conference.admin");
        System.out.println("Number of occupants: " + info.getOccupantsCount());
        System.out.println("Room Subject:" + info.getSubject());

        Chat chat = muc2.createPrivateChat("sala@conference.admin/admin@admin/Smack", (ChatMessageListener) messageListener);
        System.out.println("Send a message: (input @back to turn back to menu)");
        String message;
        do {
            System.out.print("--> ");
            message = sc.nextLine();
            chat.sendMessage(message);
        }while(!(message.equals("@back")));
    }*/
}
