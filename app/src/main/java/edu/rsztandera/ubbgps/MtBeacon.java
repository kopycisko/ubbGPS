package edu.rsztandera.ubbgps;

import org.altbeacon.beacon.Beacon;

public class MtBeacon{
    public String id1;
    public String id2;
    public String id3;
    public String corridorId;
    public int workingMeter = 0;

    public MtBeacon(Beacon origBeacon)
    {
        this.id1 = origBeacon.getId1().toString();
        this.id2 = origBeacon.getId2().toString();
        this.id3 = origBeacon.getId3().toString();
//        this.uuid = origBeacon.getServiceUuid();
//        this.mac = origBeacon.getBluetoothAddress();
//        this.name = origBeacon.getBluetoothName();
    }

    public MtBeacon(String id1, String id2, String id3,
                    String corridorId, int workingMeter) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.corridorId = corridorId;
        this.workingMeter = workingMeter;
    }

    public boolean equals(Beacon beacon) {
        return id1.contentEquals(beacon.getId1().toString()) &&
                id2.contentEquals(beacon.getId2().toString()) &&
                id3.contentEquals(beacon.getId3().toString());
    }
}
