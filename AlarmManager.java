PS C:\Users\user\Downloads\java1> java AlarmClock
java : The term 'java' is not recognized as the name of a cmdlet, 
function, script file, or operable program. Check the spelling of 
the name, or if a path was included, verify that the path is correct 
and try again.
At line:1 char:1
+ java AlarmClock
+ ~~~~
    + CategoryInfo          : ObjectNotFound: (java:String) [], Comm 
   andNotFoundException
    + FullyQualifiedErrorId : CommandNotFoundExceptionimport java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlarmManager {
    private List<Alarm> alarms;
    private SoundPlayer soundPlayer;
    private ScheduledExecutorService scheduler;

    public AlarmManager() {
        this.alarms = new ArrayList<>();
        this.soundPlayer = new SoundPlayer();
        this.scheduler = Executors.newScheduledThreadPool(1);
        startAlarmChecker();
    }

    private void startAlarmChecker() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime currentTime = LocalTime.now();
            for (Alarm alarm : alarms) {
                if (alarm.isActive() && 
                    alarm.getTime().getHour() == currentTime.getHour() && 
                    alarm.getTime().getMinute() == currentTime.getMinute()) {
                    triggerAlarm(alarm);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void triggerAlarm(Alarm alarm) {
        System.out.println("\nALARM: " + alarm.getMessage());
        soundPlayer.playSound(alarm.getSoundFile());
        
        if (!alarm.isRepeating()) {
            alarm.setActive(false);
        }
    }

    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
    }

    public void removeAlarm(Alarm alarm) {
        alarms.remove(alarm);
    }

    public List<Alarm> getAlarms() {
        return new ArrayList<>(alarms);
    }

    public void stopAllAlarms() {
        soundPlayer.stopSound();
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
} 