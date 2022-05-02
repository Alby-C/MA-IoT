package com.service.vpnservice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dronedetection.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main class, la classe chiamata ad inizio esecuzione (da manifest)
 */
public class VpnClient extends Activity {
    //corrisponde ai campi presenti nell'user interface dell'activity ...
    public interface Prefs {
        String NAME = "connection";
        String SERVER_ADDRESS = "server.address";
        String SERVER_PORT = "server.port";
        String SHARED_SECRET = "shared.secret";
        String PROXY_HOSTNAME = "proxyhost";
        String PROXY_PORT = "proxyport";
        String ALLOW = "allow";
        String PACKAGES = "packages";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        //ASSOCIAZIONI CON LAYOUT--------------------------------------------------------------------
        final TextView serverAddressTV = findViewById(R.id.address);
        final TextView serverPortTV = findViewById(R.id.port);
        final TextView sharedSecretTV = findViewById(R.id.secret);
        final TextView proxyHostTV = findViewById(R.id.proxyhost);
        final TextView proxyPortTV = findViewById(R.id.proxyport);

        final RadioButton allowedRB = findViewById(R.id.allowed);
        final TextView packagesTV = findViewById(R.id.packages);
        //------------------------------------------------------------------------------------------

        //INIZIALIZZAZIONE TESTI TEXTBOXES-----------------------------------------------------------
        final SharedPreferences preferences = getSharedPreferences(Prefs.NAME, MODE_PRIVATE);
        serverAddressTV.setText(preferences.getString(Prefs.SERVER_ADDRESS, ""));   //guarda se esiste la preferenza, se non esiste restituisce il secondo parametro
        int serverPortPrefValue = preferences.getInt(Prefs.SERVER_PORT, 0);
        serverPortTV.setText(String.valueOf(serverPortPrefValue == 0 ? "" : serverPortPrefValue));
        sharedSecretTV.setText(preferences.getString(Prefs.SHARED_SECRET, ""));
        proxyHostTV.setText(preferences.getString(Prefs.PROXY_HOSTNAME, ""));
        int proxyPortPrefValue = preferences.getInt(Prefs.PROXY_PORT, 0);
        proxyPortTV.setText(proxyPortPrefValue == 0 ? "" : String.valueOf(proxyPortPrefValue));

        allowedRB.setChecked(preferences.getBoolean(Prefs.ALLOW, true));
        packagesTV.setText(String.join(", ", preferences.getStringSet(
                Prefs.PACKAGES, Collections.emptySet())));
        //-------------------------------------------------------------------------------------------

        //TASTO CONNECT ON CLICK LISTENER------------------------------------------------------------
        findViewById(R.id.connect).setOnClickListener(v -> {
            if (!checkProxyConfigs(proxyHostTV.getText().toString(),
                    proxyPortTV.getText().toString())) {
                return;
            }

            final Set<String> packageSet =
                    Arrays.stream(packagesTV.getText().toString().split(","))
                            .map(String::trim)  //il :: fa riferimento al metodo, restituendo un "delegato"
                            .filter(s -> !s.isEmpty())  //quelli vuoti li ignora
                            .collect(Collectors.toSet());
            if (!checkPackages(packageSet)) {
                return;
            }

            int serverPortNum;
            try {
                serverPortNum = Integer.parseInt(serverPortTV.getText().toString());
            } catch (NumberFormatException e) {
                serverPortNum = 0;
            }
            int proxyPortNum;
            try {
                proxyPortNum = Integer.parseInt(proxyPortTV.getText().toString());
            } catch (NumberFormatException e) {
                proxyPortNum = 0;
            }
            preferences.edit()  //modifica le sharedPreferences aggiungendo tutti i dati aggiunti nel form
                    .putString(Prefs.SERVER_ADDRESS, serverAddressTV.getText().toString())
                    .putInt(Prefs.SERVER_PORT, serverPortNum)
                    .putString(Prefs.SHARED_SECRET, sharedSecretTV.getText().toString())
                    .putString(Prefs.PROXY_HOSTNAME, proxyHostTV.getText().toString())
                    .putInt(Prefs.PROXY_PORT, proxyPortNum)
                    .putBoolean(Prefs.ALLOW, allowedRB.isChecked())
                    .putStringSet(Prefs.PACKAGES, packageSet)
                    .commit();  //conclude le modifiche
            Intent intent = android.net.VpnService.prepare(VpnClient.this); //stabilisce la connesione vpn
            if (intent != null) {
                startActivityForResult(intent, 0);
            } else {
                onActivityResult(0, RESULT_OK, null);   //0 corrisponde allo 0 della richiesta sopra
            }
        });
        //-------------------------------------------------------------------------------------------
        //BUTTON DISCONNECT ON CLICK LISTENER--------------------------------------------------------
        findViewById(R.id.disconnect).setOnClickListener(v -> {
            startService(getServiceIntent().setAction(myVpnService.ACTION_DISCONNECT));
        });
        //-------------------------------------------------------------------------------------------
    }
//===================================================================================================
    /**
     * Controlla che le configurazioni del proxy siano state compilate entrambe.
     * @param proxyHost
     * @param proxyPort
     * @return Restituisce falso se non sono state compilate entrambe, vero se sono state compilate o sono vuote.
     */
    private boolean checkProxyConfigs(String proxyHost, String proxyPort) {
        final boolean hasIncompleteProxyConfigs = proxyHost.isEmpty() != proxyPort.isEmpty();
        if (hasIncompleteProxyConfigs) {
            Toast.makeText(this, R.string.incomplete_proxy_settings, Toast.LENGTH_SHORT).show();
        }
        return !hasIncompleteProxyConfigs;
    }

    /**
     * Controlla che tutti i pacchetti (app folders(?)) specificati corrispondano ad effettivi pacchetti installati sul dispositivo.
     * @param packageNames
     * @return  true se esistono o se il set è vuoto, altrimenti false.
     */
    private boolean checkPackages(Set<String> packageNames) {
        final boolean hasCorrectPackageNames = packageNames.isEmpty() ||
                getPackageManager().getInstalledPackages(0).stream()
                        .map(pi -> pi.packageName)      //restituisce uno stream di risultati restituiti dalla funzione
                        .collect(Collectors.toSet())    //restituisce un set
                        .containsAll(packageNames);     //true se contiene tutti gli elementi specificati nel set
        if (!hasCorrectPackageNames) {
            Toast.makeText(this, R.string.unknown_package_names, Toast.LENGTH_SHORT).show();
        }
        return hasCorrectPackageNames;
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) { //chiamata quando una activity lanciata esce
        if (result == RESULT_OK) {
            startService(getServiceIntent().setAction(myVpnService.ACTION_CONNECT));
        }
    }

    /**
     * @return Restituisce l'intento realtivo alla classe VpnService.
     */
    private Intent getServiceIntent() {
        return new Intent(this, myVpnService.class);
    }
}
