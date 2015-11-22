package com.iam.intelligentareamapping;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.Proximity;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IBeaconActivity extends AppCompatActivity implements ProximityManager.ProximityListener {

    private ProximityManager deviceManager;
    private ScanContext scanContext;
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    private List<EventType> eventTypes = Arrays.asList(
            EventType.DEVICE_LOST,
            EventType.DEVICE_DISCOVERED,
            EventType.DEVICES_UPDATE);

    protected IBeaconScanContext iBeaconScanContext = new IBeaconScanContext.Builder()
            .setIBeaconFilters(Collections.singleton(
                    IBeaconFilters.newProximityUUIDFilter(KontaktSDK.DEFAULT_KONTAKT_BEACON_PROXIMITY_UUID)
            ))
            .setEventTypes(eventTypes)
            .setRssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(5))
            .build();


    IBeaconScanContext getIBeaconScanContext() {
        return iBeaconScanContext;
    }

    private ScanContext createScanContext() {
        return new ScanContext.Builder()
                .setScanPeriod(new ScanPeriod(3000,2000))
                .setScanMode(ProximityManager.SCAN_MODE_LOW_LATENCY)
                .setActivityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                .setIBeaconScanContext(getIBeaconScanContext())
                .build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        deviceManager = new ProximityManager(this);
        scanContext = createScanContext();
    }

    @Override
    protected void onStart() {
        writetoTv("inside");
        super.onStart();
        if (!BluetoothUtils.isBluetoothEnabled()) {
            writetoTv("bt not enabled");
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            writetoTv("bt enabled");

            //startMonitoring();

            deviceManager.initializeScan(scanContext, new OnServiceReadyListener() {
                @Override
                public void onServiceReady() {
                    deviceManager.attachListener(IBeaconActivity.this);
                }

                @Override
                public void onConnectionFailure() {
                    writetoTv("Connection Failure!");
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        deviceManager.finishScan();
    }

    @Override
    public void onScanStart() {

    }

    @Override
    public void onScanStop() {

    }

    @Override
    public void onEvent(final BluetoothDeviceEvent event) {

        DeviceProfile deviceProfile = event.getDeviceProfile();

        IBeaconDeviceEvent iBeaconDeviceEvent = (IBeaconDeviceEvent) event;
        final IBeaconRegion region = iBeaconDeviceEvent.getRegion();
        final List<IBeaconDevice> devicesList = iBeaconDeviceEvent.getDeviceList();
        Log.i("iam.com","Event received at"+new GregorianCalendar());



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.getEventType()) {
                    case SPACE_ENTERED:
                        for(IBeaconDevice ibd: devicesList)
                            Log.i("EVENTOS", "Space entered "+ibd.getUniqueId() );
                        break;
                    case DEVICE_DISCOVERED:
                        for(IBeaconDevice ibd: devicesList)
                            Log.i("EVENTOS", "device discovered "+ibd.getUniqueId() );
                        break;
                    case DEVICES_UPDATE:
                        for(IBeaconDevice ibd: devicesList)
                            Log.i("EVENTOS", "Device updated "+ibd.getUniqueId() );
                        printDevices(devicesList);
                        break;
                    case DEVICE_LOST:
                        for(IBeaconDevice ibd: devicesList)
                            Log.i("EVENTOS", "device lost "+ibd.getUniqueId() );
                        break;
                }
                /*switch (event.getEventType()) {
                    case SPACE_ENTERED:

                        break;
                    case DEVICE_DISCOVERED:

                        break;
                    case DEVICES_UPDATE:
                        printDevices(devicesList);
                        break;
                    case SPACE_ABANDONED:
                        break;
                }*/
            }
        });
    }

    public void writetoTv(String str){

        TextView textView = (TextView)findViewById(R.id.tv1);
        textView.setText(str);

    }

    public void printDevices(List<IBeaconDevice> deviceList){
        String devicesStr="";

        for(IBeaconDevice device : deviceList) {

            String name = device.getName();
            if(!"iam".equals(name)) continue;

            double dist = device.getDistance();
            Proximity proximity = device.getProximity();

            String id = device.getUniqueId();
            String namespace = device.getName();
            int txPower = device.getTxPower();
            double rssi = device.getRssi();

            devicesStr+= "Unique Id: " + id + "\n";
            //devicesStr+= "Name: " + namespace + "\n";
            //devicesStr+= "TX power: " + txPower + "\n";
            //devicesStr+= "RSSi: " + rssi + "\n";
            devicesStr+= "Proximity: " + proximity.name()  + "\n";
            devicesStr+= "Distance: " + dist + "\n";
            //devicesStr+= "Distance AVG: " + distancesAVG.get(id) + "\n";
            //devicesStr+= "\n";
            devicesStr+= "-------------------------------" + "\n";

        }
        writetoTv(devicesStr);


        }
    }
