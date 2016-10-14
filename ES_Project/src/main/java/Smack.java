/**
 * Created by gabriel on 19-09-2016.
 */

import org.jivesoftware.smack.*;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
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

    static List<String> chatsJoined = new ArrayList<String>();

    static MultiUserChatManager manager;

    public static MessageListener messageListener;
    public static Scanner sc = new Scanner(System.in);
    public static void main(String [] args) throws Exception{

        System.out.print("Username: ");
        usernameLogin = sc.next();
        System.out.print("Password: ");
        passwordLogin = sc.next();
        XmppOpenfire xmppOp = new XmppOpenfire(usernameLogin, passwordLogin);

        connection = xmppOp.createConnection(usernameLogin, passwordLogin);

        //connection = createConnection();
        System.out.println("Hello " + xmppOp.getLoginUsername() + "!");
        do {
            System.out.println("What do you want to do?");
            System.out.println("1 - Create a new user");
            System.out.println("2 - Create a Group Chat");
            System.out.println("3 - Join a room");
            System.out.println("4 - List chats joined");
            System.out.println("0 - Exit");
            System.out.print("Option --> ");
            op = sc.nextInt();

            switch (op){
                case 1:
                    xmppOp.createUser(connection, newUsername, newUserPass); // create user
                    break;
                case 2:
                    do {
                        System.out.println("Persistent chat or Instant chat?");
                        System.out.println("1 - Persistent");
                        System.out.println("2 - Instant");
                        System.out.println("0 - Back");
                        System.out.print("Option --> ");
                        chatOp = sc.nextInt();
                        if(chatOp == 0)
                            break;

                        System.out.print("Chat Name: ");
                        String chatName = sc.next();
                        /*System.out.print("Description: ");
                        System.out.print("Raio: ");
                        System.out.print("Public or private:");*/

                        xmppOp.createChat(connection, chatOp, chatName);
                    }while(chatOp != 0);
                    break;
                case 3:
                    System.out.println("Available chats");
                    System.out.println(xmppOp.getChatRooms().toString().replace("[", "").replace("]", ""));
                    System.out.println("Input the name of the chat to join: ");
                    String chatNameToJoin = sc.next();
                    xmppOp.joinToChatRoom(connection, chatNameToJoin);

                    // create private chat and send a private message
                    //System.out.print("Input the name of the user that you want to talk: ");
                    //usernameLogin = sc.next();
                    //xmppOp.sendPrivateMessage(connection, usernameLogin, chatNameToJoin);

                    // send public message to room
                    xmppOp.sendPublicMessage(connection, chatNameToJoin);

                    break;

                case 4:
                    chatsJoined = xmppOp.getJoinedRooms(connection, usernameLogin);
                    System.out.println(chatsJoined.toString());
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
}
