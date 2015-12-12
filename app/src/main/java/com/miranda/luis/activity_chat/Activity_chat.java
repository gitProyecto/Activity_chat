package com.miranda.luis.activity_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
// Smack libraries
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLExternalMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import javax.net.ssl.SSLContext;

public class Activity_chat extends AppCompatActivity implements View.OnClickListener {

    ImageView imgStatus;
    TextView nameUser;
    ListView lista;
    LinearLayout mess;
    String msg = "PM1139 : ";

    XMPPTCPConnectionConfiguration configChatIOT ;
    AbstractXMPPConnection conxChatIOT;
    Roster roster;

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_chat);


        //Objetos del layout
        nameUser = (TextView) findViewById(R.id.textView);
        imgStatus = (ImageView) findViewById(R.id.imageView);
        imgStatus.setOnClickListener(this);

        mess = (LinearLayout) findViewById(R.id.mess);
        mess.setVisibility(View.GONE);

        lista = (ListView) findViewById(R.id.listView);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //View.setVisibility(View.GONE / View.VISIBLE / View.INVISIBLE).
                lista.setVisibility(View.GONE);
                mess.setVisibility(View.VISIBLE);

            }
        });

        //cargamos el chat

        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage(chatText.getText().toString(),true);
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage(chatText.getText().toString(),true);
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });



        //Propiedades de la conexion xmpp
        SmackConfiguration.setDefaultPacketReplyTimeout(10000);
        try {
            SSLContext context = SSLContext.getInstance("TLSv1");
            context.init(null, null, null);

            configChatIOT = XMPPTCPConnectionConfiguration.builder()
                    .setHost("techno-world.net")
                    .setPort(5222)
                    .setServiceName("techno-world.net")
                    .setCustomSSLContext(context)
                    .setUsernameAndPassword("android@techno-world.net", "android")
                    .setDebuggerEnabled(true)
                    .setEnabledSSLProtocols(new String[]{"TLS","SSL"})
                    .setEnabledSSLCiphers(new String[]{"ECDHE-RSA-RC4-SHA", "RC4-SHA"})
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setResource("Soporte T-W")
                    .setCompressionEnabled(true).build();
            conxChatIOT = new XMPPTCPConnection(configChatIOT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }



        //Ininiciamos la conexion y hacemos login
        new pmConnect().execute();





        //obtenemmos el status si es que se guardo
        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        int stat = sharedPreferences.getInt("status", -1);

        switch (stat) {
            case 0:
                imgStatus.setImageResource(R.drawable.sta1);
                break;
            case 1:
                imgStatus.setImageResource(R.drawable.sta2);
                break;
            case 2:
                imgStatus.setImageResource(R.drawable.sta3);
                break;
            default:
                imgStatus.setImageResource(R.drawable.sta1);
                sharedPreferences.edit().putInt("status", 0).apply();
                break;
        }


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imageView){
            selectStatus();
        }
    }

    //Selec de status de la cuenta
    public void selectStatus(){

       final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final AlertDialog.Builder menuAleart = new  AlertDialog.Builder(Activity_chat.this);
        final String[] menuList = { "Available", "Not available", "Ofline" };
        menuAleart.setTitle("New status");

        menuAleart.setItems(menuList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        imgStatus.setImageResource(R.drawable.sta1);
                        sharedPreferences.edit().putInt("status", 0).apply();


                        break;
                    case 1:
                        imgStatus.setImageResource(R.drawable.sta2);
                        break;
                    case 2:
                        imgStatus.setImageResource(R.drawable.sta3);
                        conxChatIOT.disconnect();
                        sharedPreferences.edit().putInt("status", 2).apply();
                        break;
                }

            }
        });
        AlertDialog menuDrop = menuAleart.create();
        menuDrop.show();
    }





    //Conexion xmpp
    private class pmConnect extends AsyncTask<String, Void, String> {

       String user="_Name_user";
       protected String doInBackground(String... dummy) {

            try {
                    conxChatIOT.connect();
                    SASLMechanism sm = new SASLExternalMechanism();
                    SASLAuthentication.registerSASLMechanism(sm.instanceForAuthentication(conxChatIOT));
                    SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                    SmackConfiguration.addDisabledSmackClass("org.jivesoftware.smack.debugger.JulDeb ugger");
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    XMPPTCPConnection.setUseStreamManagementDefault(true);

                    conxChatIOT.login();
                    Log.d(msg, "Conectado : " + conxChatIOT.getUser());
                    user = conxChatIOT.getUser();

                ChatManager chatmanager = ChatManager.getInstanceFor(conxChatIOT);
                chatmanager.addChatListener(new ChatManagerListener() {
                    public void chatCreated(final Chat chat, final boolean createdLocally) {
                        chat.addMessageListener(new ChatMessageListener() {
                            public void processMessage(Chat chat, Message message) {
                                Log.d(msg, "Received message: " + message);
                                if (!message.getBody().isEmpty()) {
                                    Notificaciones x = new Notificaciones(message.getBody(), Activity_chat.this);
                                    sendChatMessage(message.getBody(),false);
                                } else {
                                    //Esta escribirndo...
                                }
                            }
                        });
                    }
                });

                roster = Roster.getInstanceFor(conxChatIOT);
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesDeleted(Collection<String> addresses) {
                        Log.d(msg, "Usuario elimando");
                    }

                    @Override
                    public void entriesAdded(Collection<String> collection) {
                        Log.d(msg, "New user");
                    }

                    @Override
                    public void entriesUpdated(Collection<String> addresses) {
                        Log.d(msg, "Actualizacion de estado");
                    }

                    @Override
                    public void presenceChanged(Presence presence) {
                        Log.d(msg, presence.getFrom() + " " + presence);
                        new xmpp_playerlist().execute();
                    }
                });

                } catch (SmackException | IOException | XMPPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return user;
        }

        protected void onPostExecute(String user){
                if(!user.isEmpty()){
                    nameUser.setText("  " + conxChatIOT.getUser());
                    new xmpp_playerlist().execute();
                }
        }


    }




    //Cargamos la lista de usuarios
    private class xmpp_playerlist extends AsyncTask<String, Void, String> {

        ItemCompraAdapter adapter;
        @Override
        protected String doInBackground(String... params) {
            ArrayList<ItemCompra> itemsCompra = obtenerItems();
            adapter = new ItemCompraAdapter(Activity_chat.this, itemsCompra);
            return null;
        }

        protected void onPostExecute(String user){
            lista.setAdapter(adapter);
        }

    }

    private ArrayList<ItemCompra> obtenerItems() {

        ArrayList<ItemCompra> items = new ArrayList<ItemCompra>();
        roster = Roster.getInstanceFor(conxChatIOT);

        try {
            roster.reloadAndWait();
            Collection<RosterEntry> entries = roster.getEntries();
            int i=1;

            for (RosterEntry entry : entries) {

                String user = entry.getUser();
                Presence presence = roster.getPresence(user);
                String dis = ""+presence.getType();

                //Log.d(msg, "Presence : "+presence);
                Log.d(msg, "Presence type: " + dis);
                //Log.d(msg, "Presence mode: " + presence.getMode());

                String stat="drawable/sta3", text="";

                if(dis.equals("available")){
                    stat="drawable/sta1";
                }
                if(presence.getStatus() != null){
                    text=presence.getStatus();
                }
                items.add(new ItemCompra(i, entry.getUser(), text, "@mipmap/ic_launcher", stat));



            }
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return items;
    }




    private class pmSend extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void...dummy) {

            Random randomGenerator = new Random();
            int pseudoSensorData;
            ChatManager chatmanager = ChatManager.getInstanceFor(conxChatIOT);


            Chat ChatIOT = chatmanager.createChat("luis.flores@techno-world.net", new ChatMessageListener() {
                public void processMessage(Chat chat, Message message) {
                    Log.d(msg, "Received message: " + message);
                }
            });

            try {
                ChatIOT.sendMessage("Prueba desde android");
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }


            return null;
        }

    }







    private boolean sendChatMessage(String text,  boolean user) {

        chatArrayAdapter.add(new ChatMessage(user, text.trim()));

        if(user==true){
            chatText.setText("");
        }
        //side = !side;
        return true;
    }














}


