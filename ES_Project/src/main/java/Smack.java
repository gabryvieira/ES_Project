/**
 * Created by gabriel on 19-09-2016.
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
import java.util.*;


public class Smack {

    static String usernameLogin;
    static String passwordLogin;
    static final String localhost = "127.0.0.1";
    public static AbstractXMPPConnection connection;
    static int op;
    static int chatOp;
    static String newUsername;

    static String newUserPass;

    static MultiUserChatManager manager;

    public static MessageListener messageListener;
    public static Scanner sc = new Scanner(System.in);
    public static void main(String [] args) throws Exception{

        System.out.print("Username: ");
        usernameLogin = sc.next();
        System.out.print("Password:");
        passwordLogin = sc.next();
        XmppOpenfire xmppOp = new XmppOpenfire();

        connection = xmppOp.createConnection(usernameLogin, passwordLogin);

        //connection = createConnection();
        System.out.println("Hello " + usernameLogin + "!");
        do {
            System.out.println("What do you want to do?");
            System.out.println("1 - Create a new user");
            System.out.println("2 - Create a Group Chat");
            System.out.println("3 - Join a room");
            System.out.println("0 - Exit");
            System.out.print("Option --> ");
            op = sc.nextInt();

            switch (op){
                case 1:
                    createUser(connection);
                    break;
                case 2:
                    createChat(connection);
                    break;
                case 3:
                    joinToChatRoom(connection);
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please, choose one of these options!");
                    break;
            }
        }while(op != 0);


        connection.disconnect();
    }

    /*public static AbstractXMPPConnection createConnection() throws Exception {
        // Create a connection to the jabber.org server on specific port.

        XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName("example.com")
                .setHost(localhost)
                .setPort(5222)
                .setCompressionEnabled(false)
                .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                .setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                })
                .setUsernameAndPassword(usernameAdmin, passwordAdmin).build(); // user com que se esta a "mexer"

        connection = new XMPPTCPConnection(connConfig);
        connection.connect();
        connection.login(); // credentials by default to access openfire (ADMIN ONLY)
        System.out.print("Connected to openfire server!");

        return connection;
    }*/

    public static void createUser(AbstractXMPPConnection connection) throws Exception {

        System.out.print("Username: ");
        newUsername = sc.next();
        System.out.print("Password: ");
        newUserPass = sc.next();

        AccountManager ac = AccountManager.getInstance(connection);
        ac.createAccount(newUsername, newUserPass);

        System.out.println("User created :)");
    }

    // instant chat
    public static void createChat(AbstractXMPPConnection connection) throws Exception{

        // Get the MultiUserChatManager
        manager = getInstanceForConnection(connection);
        //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        // Get a MultiUserChat using MultiUserChatManager
        MultiUserChat muc = manager.getMultiUserChat("sala@conference.admin");

        do {
            System.out.println("Persistent chat or Instant chat?");
            System.out.println("1 - Persistent");
            System.out.println("2 - Instant");
            System.out.println("0 - Back");
            System.out.print("Option --> ");
            chatOp = sc.nextInt();

            switch (chatOp){
                case 1:
                    // Create the room
                    muc.create("testbot");
                    Form form = muc.getConfigurationForm();
                    Form answerForm = form.createAnswerForm();
                    answerForm.setAnswer("muc#roomconfig_persistentroom", true);
                    muc.sendConfigurationForm(answerForm);
                    System.out.println("Persistent chat was created :)!");
                    // sending the configuration form unlocks the room
                    break;

                case 2:
                    // Create the room
                    muc.create("testbot");
                    // Send an empty room configuration form which indicates that we want an instant room
                    muc.sendConfigurationForm(new Form(DataForm.Type.submit));
                    System.out.println("Instant chat was created :)!");
                    break;

                default:
                    System.out.println("Please, choose one of these options!");
                    break;

            }
        }while(chatOp != 0);
    }

            // juntar se a uma sala de chat
    public static void joinToChatRoom(AbstractXMPPConnection connection) throws Exception{
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
    }



                        // get Instance for Connection used for chatrooms
    public static MultiUserChatManager getInstanceForConnection(AbstractXMPPConnection connection){
        manager = MultiUserChatManager.getInstanceFor(connection);
        return manager;
    }


    /*private class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            String from = message.getFrom();
            String body = message.getBody();
            System.out.println(String.format("Received message " + body + " from " + from));
        }

    }*/
}
