package edu.rsztandera.ubbgps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconRangingService extends Service implements BeaconConsumer {
    protected static final String TAG = "RangingService";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private boolean isRanging = false;
    private RangeNotifier rangeNotifier = null;

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "beacon service connected");
        final BeaconReferenceApplication app = (BeaconReferenceApplication) this.getApplicationContext();
        rangeNotifier = new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() == 0) {
                    return;
                }
                String beaconsFoundStr = "";
                for (Beacon firstBeacon : beacons) {
                    MtBeacon positionBeacon = app.getBeaconByLib(firstBeacon);
                    if (beaconsFoundStr != "") {
                        beaconsFoundStr += "\n";
                    }
                    beaconsFoundStr += "Beacon " +firstBeacon.getBluetoothName()+
                            " (rssi:" + firstBeacon.getRssi() +
                            ", avgrssi:" + firstBeacon.getRunningAverageRssi() +
                            ", measurement cnt:" + firstBeacon.getMeasurementCount() +
                            ", tx:" + firstBeacon.getTxPower() +
                            ", packets: " + firstBeacon.getPacketCount()+
                            ", " + firstBeacon.getDistance() + " m)";
                }
//                app.addBeaconLog(beaconsFoundStr);
//                updateBeaconMonitoringDisplay();
                Log.i(TAG, beaconsFoundStr);
            }
        };
        beaconManager.addRangeNotifier(rangeNotifier);

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        BeaconRangingService getService() {
            return BeaconRangingService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        Log.i(TAG, "beacon service created");
        showNotification();
        toggleBeaconRanging();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "Start command; Received start id " + startId + ": " + intent, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        toggleBeaconRanging();
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BeaconMonitoringActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    private void toggleBeaconRanging() {
        if(isRanging) {
            beaconManager.removeRangeNotifier(rangeNotifier);
            beaconManager.unbind(this);
        }
        else {
            beaconManager.bind(this);
        }
        isRanging = !isRanging;
        Log.i(TAG, isRanging? "Ranging started." : "Ranging stopped");
    }
}