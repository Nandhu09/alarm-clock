import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlarmDataManager {
    private static final String DATA_FILE = "alarms.dat";
    
    public static void saveAlarms(List<AlarmItem> alarms) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(alarms);
            System.out.println("Alarms saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving alarms: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<AlarmItem> loadAlarms() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<AlarmItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading alarms: " + e.getMessage());
            return new ArrayList<>();
        }
    }
} 