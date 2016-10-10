/**
 * Created by gabriel on 03-10-2016.
 */

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.*;
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
import java.io.InputStream;
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

    private int chatID;

    private List<String> chatrooms = new ArrayList<>();

    private MultiUserChatManager manager;
    public static MessageListener messageListener;
    public ChatMessageListener chatMessage;

    private FileTransferManager filemanager;

    public static Scanner sc = new Scanner(System.in);
    private String body;

    public XmppOpenfire(String loginUsername, String loginPassword){
        this.loginUsername = loginUsername;
        this.loginPassword = loginPassword;
        chatOp = 0;
        newUsername = "";
        newUserPass = "";


    }




            // criacao da ligacao
    public AbstractXMPPConnection createConnection(String loginUsername, String loginPassword) throws Exception {
        XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration
                .builder()
                .setServiceName("jabber.org")
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

    public void createChat(AbstractXMPPConnection connection, int chatOp, String chatName) throws Exception {

        // Get the MultiUserChatManager
        manager = getInstanceForConnection(connection);
        //MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        chatrooms.add(chatID,chatName);
        // Get a MultiUserChat using MultiUserChatManager
        MultiUserChat muc = manager.getMultiUserChat(chatName+"@conference.admin");

        // persistent chat
        muc.create("chat"); // nickname
        Form form = muc.getConfigurationForm();
        Form answerForm = form.createAnswerForm();
        answerForm.setAnswer("muc#roomconfig_publicroom", true);
        if(chatOp == 1)
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
        else if(chatOp == 2)
            answerForm.setAnswer("muc#roomconfig_persistentroom", false);
        answerForm.setAnswer("muc#roomconfig_roomname", chatName);

        muc.sendConfigurationForm(answerForm);
        System.out.println("Persistent chat was created :)!");
        chatID++;

    }

    public void joinToChatRoom(AbstractXMPPConnection connection, String chatToJoin) throws Exception{
        // Create a MultiUserChat using an XMPPConnection for a room
        manager = getInstanceForConnection(connection);

        MultiUserChat muc2 = manager.getMultiUserChat(chatToJoin+"@conference.admin");

        // User2 joins the new room using a password and the nickname is your username
        // the amount of history to receive. In this example we are requesting the last 10 messages.
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(10); // request the last 10 messages
        muc2.join(connection.getUser(), "password", history, connection.getPacketReplyTimeout());
        System.out.println("Joined to chat :)");

        // room information
        RoomInfo info = manager.getRoomInfo(chatToJoin+"@conference.admin");
        System.out.println("Number of occupants: " + info.getOccupantsCount());
        System.out.println("Room Name: "+info.getName());


        /*Message message = new Message(chatToJoin + "@conference.admin", Message.Type.groupchat);
        String body;

        do {
            System.out.print("--> ");
            body = sc.nextLine();
            //if(body.equals("@files"))
            //    TransferFile(connection, chatToJoin);
            message.setBody(body);
            message.setType(Message.Type.groupchat);
            message.setTo(chatToJoin);
            muc2.sendMessage(message);
            muc2.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    System.out.println("Received message: "
                            + (message != null ? message.getBody() : "NULL") + "  , Message sender :" + message.getFrom());
                }
            });
        }while(!body.equals("@exit"));*/







        /*Chat chat = muc2.createPrivateChat("sala@conference.admin/admin@admin/Smack", (ChatMessageListener) messageListener);
        System.out.println("Send a message: (input @back to turn back to menu)");
        String message;*/


        /*do {
            System.out.print("--> ");
            message = sc.nextLine();
            chat.sendMessage(message);
        }while(!(message.equals("@back")));*/
    }

        // send a public message to chatroom
    public void sendPublicMessage(AbstractXMPPConnection connection, String chatName) throws Exception{
        manager = getInstanceForConnection(connection);
        MultiUserChat muc2 = manager.getMultiUserChat(chatName+"@conference.admin");
        Message message = new Message(chatName + "@conference.admin", Message.Type.groupchat);
        String body;

        do {
            System.out.print("--> ");
            body = sc.nextLine();
            //if(body.equals("@files"))
            //    TransferFile(connection, chatToJoin);
            message.setBody(body);
            message.setType(Message.Type.groupchat);
            message.setTo(chatName);
            muc2.sendMessage(message);
            muc2.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    System.out.println("Received message: "
                            + (message != null ? message.getBody() : "NULL") + "  , Message sender :" + message.getFrom());
                }
            });
        }while(!body.equals("@exit"));
    }

    // send private message to one user inside of the same room
    public void sendPrivateMessage(AbstractXMPPConnection connection, String userName, String chatName) throws Exception{
        manager = getInstanceForConnection(connection);
        MultiUserChat muser = manager.getMultiUserChat(chatName+"@conference.admin");
        Chat chat = muser.createPrivateChat(chatName+"@conference.admin/"+userName+"@admin/Smack", chatMessage);
        System.out.println("Send a message: (input @back to turn back to menu)");
        String message;

        do {
            System.out.print("--> ");
            message = sc.nextLine();
            chat.sendMessage(message);
        }while(!(message.equals("@back")));
    }

    public void TransferFile(AbstractXMPPConnection connection, String chatToJoin){
        OutgoingFileTransfer transfer = filemanager.createOutgoingFileTransfer(chatToJoin+"@conference.admin");
    }
    public String getLoginUsername(){
        return loginUsername;
    }

    public String getLoginPassword(){
        return loginPassword;
    }

    public List<String> getChatRooms(){
        return chatrooms;
    }


    // get instance connection
    public  MultiUserChatManager getInstanceForConnection(AbstractXMPPConnection connection){
        manager = MultiUserChatManager.getInstanceFor(connection);
        return manager;
    }
}
