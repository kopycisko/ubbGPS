package edu.rsztandera.ubbgps;

import org.altbeacon.beacon.Beacon;

public class MtBeacon{
    public String name;
    public String corridorId;
    public int workingMeter = 0;
    public float currRssi = Definitions.RSSI_NOT_KNOWN;

    public MtBeacon(Beacon origBeacon)
    {
//        this.id1 = origBeacon.getId1().toString();
//        this.id2 = origBeacon.getId2().toString();
//        this.id3 = origBeacon.getId3().toString();
//        this.uuid = origBeacon.getServiceUuid();
//        this.addr = origBeacon.getBluetoothAddress();
        this.name = origBeacon.getBluetoothName();
    }

    public MtBeacon(String name, String corridorId, int workingMeter) {
        this.name = name;
        this.corridorId = corridorId;
        this.workingMeter = workingMeter;
    }

    public boolean ifContainPosition() {
        return !corridorId.isEmpty();
    }

}
