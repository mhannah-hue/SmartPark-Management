package SmartParkManagement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Shared parking data used by ALL forms.
 * Cars: 42 slots | Motorcycles: 60 slots (counted separately).
 * Click inside ParkingData.java, press Ctrl+A, then paste this whole file.
 */
public class ParkingData {

    public static final int TOTAL_CAR_SLOTS = 42;
    public static final int TOTAL_MOTOR_SLOTS = 60;

    // CHANGE THESE to your own parking rates (pesos per hour)
    public static final int RATE_CAR_PER_HOUR = 20;
    public static final int RATE_MOTOR_PER_HOUR = 10;

    // One parked vehicle
    public static class VehicleRecord {
        public final String owner;
        public final String plate;
        public final String type;
        public final int slot;
        public final LocalDateTime timeIn;
        public LocalDateTime timeOut = null;          // set when the vehicle times out

        public VehicleRecord(String owner, String plate, String type, int slot) {
            this.owner = owner;
            this.plate = plate;
            this.type = type;
            this.slot = slot;
            this.timeIn = LocalDateTime.now();
        }

        public String getDateText() {
            return timeIn.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }

        public String getTimeInText() {
            return timeIn.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        }

        public String getTimeOutText() {
            if (timeOut == null) {
                return "-";
            }
            return timeOut.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        }

        // C-5 = car slot 5, M-12 = motorcycle slot 12
        public String getSlotText() {
            return (type.equals("Motorcycle") ? "M-" : "C-") + slot;
        }

        // Hours charged: rounded UP to the next hour, minimum 1 hour
        public int getHoursCharged() {
            LocalDateTime end = (timeOut != null) ? timeOut : LocalDateTime.now();
            long minutes = Duration.between(timeIn, end).toMinutes();
            return (int) Math.max(1, (minutes + 59) / 60);
        }

        // Total parking fee in pesos
        public int getFee() {
            return getHoursCharged() * getRatePerHour(type);
        }
    }

    private static final boolean[] carSlotTaken = new boolean[TOTAL_CAR_SLOTS + 1];
    private static final boolean[] motorSlotTaken = new boolean[TOTAL_MOTOR_SLOTS + 1];
    private static final Map<String, VehicleRecord> parked = new HashMap<>();

    // Data passed between forms
    public static String pendingOwner = "";
    public static String pendingPlate = "";
    public static String pendingType = "";
    public static VehicleRecord lastRegistered = null;
    public static VehicleRecord lastTimedOut = null;

    // Name of the personnel on duty (set in Login after a successful login)
    public static String currentPersonnel = "";

    private static String key(String plate) {
        return plate.trim().toUpperCase();
    }

    private static boolean[] slotsOf(String type) {
        return type.equals("Motorcycle") ? motorSlotTaken : carSlotTaken;
    }

    public static int getTotal(String type) {
        return type.equals("Motorcycle") ? TOTAL_MOTOR_SLOTS : TOTAL_CAR_SLOTS;
    }

    public static int getOccupied(String type) {
        int count = 0;
        for (VehicleRecord r : parked.values()) {
            if (r.type.equals(type)) {
                count++;
            }
        }
        return count;
    }

    public static int getAvailable(String type) {
        return getTotal(type) - getOccupied(type);
    }

    public static boolean isParked(String plate) {
        return parked.containsKey(key(plate));
    }

    public static int getRatePerHour(String type) {
        return type.equals("Motorcycle") ? RATE_MOTOR_PER_HOUR : RATE_CAR_PER_HOUR;
    }

    // Text for the Parking Rate label, e.g. "P50 / hour"
    public static String getRate(String type) {
        return "P" + getRatePerHour(type) + " / hour";
    }
    
    public static java.util.List<VehicleRecord> getParkedList() {
    return new java.util.ArrayList<>(parked.values());
}

    // REGISTER: occupied +1, available -1. Returns null if full or plate already parked.
    public static VehicleRecord registerVehicle(String owner, String plate, String type) {
        if (getAvailable(type) <= 0 || isParked(plate)) {
            return null;
        }
        boolean[] taken = slotsOf(type);
        for (int i = 1; i <= getTotal(type); i++) {
            if (!taken[i]) {
                taken[i] = true;
                VehicleRecord rec = new VehicleRecord(owner, key(plate), type, i);
                parked.put(key(plate), rec);
                lastRegistered = rec;
                return rec;
            }
        }
        return null;
    }

    // TIME-OUT: occupied -1, available +1. Returns null if the plate is not parked.
    public static VehicleRecord timeOut(String plate) {
        VehicleRecord rec = parked.remove(key(plate));
        if (rec != null) {
            rec.timeOut = LocalDateTime.now();
            slotsOf(rec.type)[rec.slot] = false;
            lastTimedOut = rec;
        }
        return rec;
    }
}