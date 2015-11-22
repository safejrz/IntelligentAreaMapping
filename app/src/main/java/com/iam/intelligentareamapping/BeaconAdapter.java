package com.iam.intelligentareamapping;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconDevice;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.Proximity;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by jaime on 11/21/15.
 */
public class BeaconAdapter implements ProximityManager.ProximityListener
{
    private TreeSet<Expositor> expositorsVisited = new TreeSet<Expositor>();
    private ProximityManager deviceManager;
    private ScanContext scanContext;
    //private TreeSet<IBeaconDevice> activeDevices;
    private SortedByDistanceList activeDevices;


    private List<EventType> eventTypes = Arrays.asList(EventType.DEVICE_LOST, EventType.DEVICE_DISCOVERED, EventType.DEVICES_UPDATE);

    public BeaconAdapter(Context ctx)
    {
        deviceManager = new ProximityManager(ctx);
        scanContext = createScanContext();
        //activeDevices = new TreeSet<IBeaconDevice>();
        activeDevices = new SortedByDistanceList();
        deviceManager.initializeScan(scanContext, new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                deviceManager.attachListener(BeaconAdapter.this);
            }

            @Override
            public void onConnectionFailure() {
            }
        });

    }

    protected IBeaconScanContext iBeaconScanContext = new com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext.Builder()
         .setIBeaconFilters(Collections.singleton(IBeaconFilters.newProximityUUIDFilter(KontaktSDK.DEFAULT_KONTAKT_BEACON_PROXIMITY_UUID)))
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
    public void onScanStart() {

    }

    @Override
    public void onScanStop() {

    }

    @Override
    public void  onEvent(final BluetoothDeviceEvent event) {

        DeviceProfile deviceProfile = event.getDeviceProfile();
        synchronized (this) {

            IBeaconDeviceEvent iBeaconDeviceEvent = (IBeaconDeviceEvent) event;
            final IBeaconRegion region = iBeaconDeviceEvent.getRegion();
            final List<IBeaconDevice> devicesList = iBeaconDeviceEvent.getDeviceList();
            final List<IBeaconDevice> filteredDevicesList = filterDevices(devicesList);
            switch (event.getEventType()) {
                case DEVICE_DISCOVERED:
                    activeDevices.addAll(filteredDevicesList);
                    break;
                case DEVICES_UPDATE:
                    for (IBeaconDevice ibd : filteredDevicesList)
                        //Log.i("EVENTOS", "Device updated "+ibd.getUniqueId() );
                        printDevices(devicesList);
                    break;
                case DEVICE_LOST:
                    activeDevices.removeAll(filteredDevicesList);
                    break;
            }
            StringBuffer activeBeaconsList = new StringBuffer("Beacons Activos { ");
            for (IBeaconDevice bd : activeDevices) {
                activeBeaconsList.append("" + bd.getUniqueId() + ":" + bd.getProximity() + " ");
            }
            activeBeaconsList.append("}");
            Log.i("ActiveBeacons", activeBeaconsList.toString());
            List<IBeaconDevice> nearDevices = getNearActiveDevices();
            activeBeaconsList = new StringBuffer("Near Beacons Activos { ");
            for (IBeaconDevice bd : nearDevices) {
                activeBeaconsList.append("" + bd.getUniqueId() + ":" + bd.getProximity() + ":" + bd.getDistance() + " ");
            }
            activeBeaconsList.append("}");
            Log.i("ActiveBeacons", activeBeaconsList.toString());
        }
    }

    private List<IBeaconDevice> filterDevices(List<IBeaconDevice> deviceList)
    {
        ArrayList<IBeaconDevice> filteredList = new ArrayList<>();
        for(IBeaconDevice device : deviceList) {
            String name = device.getName();
            if("iam".equals(name)) filteredList.add(device);
        }
        return filteredList;
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
    }


    public List<IBeaconDevice> getActiveDevices()
    {
        return activeDevices;
    }

    public List<IBeaconDevice> getNearActiveDevices()
    {
        List<IBeaconDevice> nearDevices = new ArrayList<IBeaconDevice>();
        for(IBeaconDevice ibd: getActiveDevices())
        {
            if(ibd.getProximity() == Proximity.NEAR)
              nearDevices.add(ibd);
        }
        return nearDevices;
    }

    public void finishScan() {
        deviceManager.finishScan();
    }
}
