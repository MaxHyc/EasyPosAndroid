package com.devhyc.easypos.ui.impresora;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.devhyc.easypos.R;
import com.devhyc.easypos.utilidades.AlertView;
import com.devhyc.easypos.utilidades.Globales;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zebra.sdk.comm.BluetoothConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Impresora extends AppCompatActivity {

    private static final String TAG = "BluetoothConnectMenu";
    private static final int REQUEST_ENABLE_BT = 2;

    ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Vector<BluetoothDevice> remoteDevices;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver discoveryResult;
    private Thread hThread;
    private Context context;
    private SharedPreferences.Editor editor;
    private BluetoothDevice dispositivo;

    public String getBtAddrBox() {
        if (lastConnAddr == null)
            return btAddrBox.getText().toString();
        else return lastConnAddr;
    }

    // UI
    private TextView btAddrBox;
    //private Button connectButton;
    private FloatingActionButton bGuardar;
    private FloatingActionButton bBuscar;
    private ListView list;
    private CheckBox checkBoxTipoImpresora;

    // BT
    private BluetoothConnection bluetoothPort;


    @SuppressLint("ResourceType")
    private Boolean bluetoothSetup() {
        Boolean bOk = true;
        try {
            clearBtDevData();
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
//                Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
                AlertView.showAlert("¡Atención!", "¡El dipositivo no soporta bluetooth!", Impresora.this);
                bOk = false;
                return bOk;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } catch (Exception e) {
            bOk = false;
            AlertView.showAlert("Error",
                    e.getMessage(), Impresora.this);
        }
        return bOk;
    }

    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
    private static final String fileName = dir + "//BTPrinter";
    private String lastConnAddr;

    private void loadSettingFile() {
        int rin = 0;
        char[] buf = new char[128];
        try {
            FileReader fReader = new FileReader(fileName);
            rin = fReader.read(buf);
            if (rin > 0) {
                lastConnAddr = new String(buf, 0, rin);
            }
            fReader.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No existe conexion historica.");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void saveSettingFile() {
        try {
            File tempDir = new File(dir);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            FileWriter fWriter = new FileWriter(fileName);
            if (lastConnAddr != null)
                fWriter.write(lastConnAddr);
            fWriter.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // clear device data used list.
    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    // add paired device to list
    private void addPairedDevices() {
        try {
            BluetoothDevice pairedDevice;
            Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
            Globales.sharedPreferences = getSharedPreferences("prefCompartidas", Context.MODE_PRIVATE);
            String mac = Globales.sharedPreferences.getString("MAC", null);
            while (iter.hasNext()) {
                pairedDevice = iter.next();
                adapter.add(pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Vinculada]");
                remoteDevices.add(pairedDevice);
            }
        } catch (Exception e) {
            AlertView.showAlert("Error",
                    e.getMessage(), Impresora.this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_impresora);

            // Setting
            bBuscar = (FloatingActionButton) findViewById(R.id.btnBuscarImpresora);
            bBuscar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            list = (ListView) findViewById(R.id.ListImpresoras);

            context = this;
            // Setting
            loadSettingFile();
            if (bluetoothSetup())
            {
                // Bluetooth Device List
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
                list.setAdapter(adapter);
                addPairedDevices();
            }
            else
            {
                bBuscar.setVisibility(View.GONE);
            }
            // Search Button
            bBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBluetoothAdapter.isDiscovering()) {
                        clearBtDevData();
                        adapter.clear();
                        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            mBluetoothAdapter.startDiscovery();
                        }else{
                            AlertView.showAlert("¡Atención!",
                                    "No se buscarán los dispositivos ya que falta conceder permisos a la aplicación", Impresora.this);
                        }
                    } else {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                }
            });
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    BluetoothDevice btDev = remoteDevices.elementAt(arg2);
                    try {
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
                        btConn(btDev);
                    } catch (Exception e) {
                        AlertView.showAlert("Error al conectar impresora",e.getMessage(), context);
                        return;
                    }
                }
            });

            discoveryResult = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String key;
                    BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (remoteDevice != null) {
                        if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                            key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                        } else {
                            key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [SELECCIONADA]";
                        }
                        remoteDevices.add(remoteDevice);
                        adapter.add(key);
                    }
                }
            };
            registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            searchStart = new BroadcastReceiver() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onReceive(Context context, Intent intent) {
                    bBuscar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    bBuscar.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    Toast.makeText(context, "Iniciando búsqueda", Toast.LENGTH_SHORT).show();
                }
            };
            registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            searchFinish = new BroadcastReceiver() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onReceive(Context context, Intent intent) {
                    bBuscar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    bBuscar.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_search));
                    Toast.makeText(context, "Búsqueda detenida", Toast.LENGTH_SHORT).show();
                }
            };
            registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            //

        } catch (Exception e) {
            AlertView.showAlert("Error",
                    e.getMessage(), Impresora.this);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            saveSettingFile();
        } catch (Exception e) {
            AlertView.showAlert("Error",
                    e.getMessage(), Impresora.this);
        }
        if ((hThread != null) && (hThread.isAlive())) {
            hThread.interrupt();
            hThread = null;
        }
        unregisterReceiver(searchFinish);
        unregisterReceiver(searchStart);
        unregisterReceiver(discoveryResult);
        super.onDestroy();
    }

    // Bluetooth Connection method.
    public void btConn(BluetoothDevice btDev) throws IOException {
        try {
            btDev.createBond();
            new connTask().execute(btDev);
        } catch (Exception ex) {
            AlertView.showAlert("Error",
                    ex.getMessage(), Impresora.this);
        }
    }

    // Bluetooth Connection Task.
    class connTask extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(Impresora.this);

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Asociando impresora");
            dialog.setIcon(R.drawable.printer);
            dialog.setMessage("Aguarde unos instantes...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                Globales.sharedPreferences = getSharedPreferences("prefCompartidas", Context.MODE_PRIVATE);
                editor = Globales.sharedPreferences.edit();
                editor.putString("NOMBRE_IMPRESORA",params[0].getName());
                editor.putString("MAC", params[0].getAddress());
                editor.putBoolean("seconfiguro",false);
                editor.commit();
                retVal = new Integer(0);
            } catch (Exception e) {
                retVal = new Integer(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result.intValue() == 0)    // Connection success.
            {
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast toast = Toast.makeText(context, "Impresora guardada", Toast.LENGTH_SHORT);
                toast.show();
            } else    // Connection failed.
            {
                if (dialog.isShowing())
                    dialog.dismiss();
                AlertView.showAlert("Conexión fallída",
                        "Error al almacenar la MAC de la impresora", context);
            }
            super.onPostExecute(result);
        }
    }

    private void LimpiarLista()
    {
        adapter.clear();
        remoteDevices.clear();
        addPairedDevices();
    }
}