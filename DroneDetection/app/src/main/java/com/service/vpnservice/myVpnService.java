package com.service.vpnservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.dronedetection.R;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe iscritta all'intent filter VpnService, si occupa di creare la connessione ì.
 */
public class myVpnService extends android.net.VpnService implements Handler.Callback {
    private static final String TAG = myVpnService.class.getSimpleName(); //restituisce il nome della classe

    public static final String ACTION_CONNECT = "com.example.android.vpn.START";
    public static final String ACTION_DISCONNECT = "com.example.android.vpn.STOP";

    private Handler mHandler;

    //CREAZIONE DI UNA CLASSE CONNECTION CONTENENTE UNA TUPLA------------------------------------
    /**
     * Classe costituita da una tupla (Thread, ParcelFileDescriptor).
     */
    private static class Connection extends Pair<Thread, ParcelFileDescriptor> {
        public Connection(Thread thread, ParcelFileDescriptor pfd) {
            super(thread, pfd);
        }
    }
//===================================================================================================
    //atomicReference è utile per operazioni importanti che devono essere eseguite atomicaente (e.g. operazione thread safe)
    private final AtomicReference<Thread> mConnectingThread = new AtomicReference<>();
    private final AtomicReference<Connection> mConnection = new AtomicReference<>();
    //come AtomicReference, un intero la cui incrementazione dev'essere atomica
    private AtomicInteger mNextConnectionId = new AtomicInteger(1);

    private PendingIntent mConfigureIntent; //PendingIntent è semplicemente un riferimento ad un intento, con i dati originali, usato ad esempio per poter passare la gestione ad un'altro metodo
//===================================================================================================

    @Override
    public void onCreate() {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);   // viene specificato l'handler che gestirà la callback
        }

        // Create the intent to "configure" the connection (just start VpnClient).
        mConfigureIntent = PendingIntent.getActivity(this, 0, new Intent(this, VpnClient.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
//===================================================================================================
    //Chiamata dal sistema ogni volta che il client fa partire esplicitamente il service chiamando Context.startService(Intent),
    //passando i parametri richiesti, e un token unnico identificando la richiesta di inizio.
     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_DISCONNECT.equals(intent.getAction())) {
            disconnect();
            return START_NOT_STICKY;    //Da restituire qualora il servizio venga ucciso nel onStartCommand().
        } else {
            connect();
            return START_STICKY;
        }
    }
//===================================================================================================

    @Override
    public void onDestroy() {
        disconnect();
    }
//===================================================================================================
    //IMPLEMENTAZIONE INTERFACCIA HANDLER.CALLBACK----------------------------------------------
    @Override
    public boolean handleMessage(Message message) {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();   //what indica il contenuto del messaggio definito dall'utente
        if (message.what != R.string.disconnectedMessage) {
            updateForegroundNotification(message.what);
        }
        return true;
    }
//===================================================================================================

    /**
     * Gestisce la preparazione di tutte le informazioni per la connessione del Vpn e poi
     * fa partire l'effettiva connessione.
     */
    private void connect() {
        // Become a foreground service. Background services can be VPN services too, but they can
        // be killed by background check before getting a chance to receive onRevoke().
        updateForegroundNotification(R.string.connectingMessage);
        mHandler.sendEmptyMessage(R.string.connectingMessage);  //-->   toast:"ToyVPN is connecting..."

        // Extract information from the shared preferences.
        // In questo momento le informazioni sono già state estratte dal form di input
        final SharedPreferences prefs = getSharedPreferences(VpnClient.Prefs.NAME, MODE_PRIVATE);
        final String server = prefs.getString(VpnClient.Prefs.SERVER_ADDRESS, "");
        final byte[] secret = prefs.getString(VpnClient.Prefs.SHARED_SECRET, "").getBytes();
        final boolean allow = prefs.getBoolean(VpnClient.Prefs.ALLOW, true);
        final Set<String> packages =
                prefs.getStringSet(VpnClient.Prefs.PACKAGES, Collections.emptySet());
        final int port = prefs.getInt(VpnClient.Prefs.SERVER_PORT, 0);
        final String proxyHost = prefs.getString(VpnClient.Prefs.PROXY_HOSTNAME, "");
        final int proxyPort = prefs.getInt(VpnClient.Prefs.PROXY_PORT, 0);
        startConnection(new VpnConnection(
                this, mNextConnectionId.getAndIncrement(), server, port, secret,
                proxyHost, proxyPort, allow, packages));
    }
//===================================================================================================

    /**
     * Stabilisce la vero a propria connessione VPN.
     * @param connection
     */
    private void startConnection(final VpnConnection connection) {
        // Replace any existing connecting thread with the  new one.
        final Thread thread = new Thread(connection, "ToyVpnThread");
        setConnectingThread(thread);

        // Handler to mark as connected once onEstablish is called.
        connection.setConfigureIntent(mConfigureIntent);    //in VpnConnection, riga: 112
        connection.setOnEstablishListener(tunInterface -> { //in VpnConnection, riga: 117
            mHandler.sendEmptyMessage(R.string.connectedMessage);   //invia un messaggio che contiene solo il campo what

            mConnectingThread.compareAndSet(thread, null);  //imposta null se il valore di mConnectingThread==thread
            setConnection(new Connection(thread, tunInterface));
        });
        thread.start();
    }
//===================================================================================================

    /**
     * Imposta il Thread in connessione, chiudendo, nel caso ce ne sia uno, il precedente thread attivo.
     * @param thread thread da connettere.
     */
    private void setConnectingThread(final Thread thread) {
        final Thread oldThread = mConnectingThread.getAndSet(thread);   //imposta atomicamente un nuovo valore ad mConnectingThread e restituisce il vecchio valore.
        if (oldThread != null) {
            oldThread.interrupt();
        }
    }
//===================================================================================================

    /**
     * Imposta la connessione, chiudendo, nel caso ce ne sia uno, la precedente, interrompendo
     * il thread e chiudendo il parcelFileDescriptor.
     * @param connection connessione da impostare.
     */
    private void setConnection(final Connection connection) {
        final Connection oldConnection = mConnection.getAndSet(connection);
        if (oldConnection != null) {
            try {
                oldConnection.first.interrupt();
                oldConnection.second.close();
            } catch (IOException e) {
                Log.e(TAG, "Closing VPN interface", e);
            }
        }
    }
//===================================================================================================

    /**
     * Gestisce la disconnessione dal servizio VPN.
     */
    private void disconnect() {
        mHandler.sendEmptyMessage(R.string.disconnectedMessage);
        setConnectingThread(null);
        setConnection(null);
        stopForeground(true);
    }
//===================================================================================================

    /**
     * Gestisce la notifica relativa ai foreground service.
     * @param message il messaggio da mostrare nella barra di notifica.
     */
    private void updateForegroundNotification(final int message) {
        final String NOTIFICATION_CHANNEL_ID = "Vpn";
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(  //Restituisce la gestione di un servizio a livello di sistema.
                NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT));
        startForeground(1, new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(mConfigureIntent)
                .build());
    }
}
