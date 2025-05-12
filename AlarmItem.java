import java.io.Serializable;

public class AlarmItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String time;
    private String message;
    private String soundType;
    private boolean repeatDaily;
    private boolean enabled;

    public AlarmItem(String time, String message, String soundType, boolean repeatDaily) {
        this.time = time;
        this.message = message;
        this.soundType = soundType;
        this.repeatDaily = repeatDaily;
        this.enabled = true;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getSoundType() {
        return soundType;
    }

    public boolean isRepeatDaily() {
        return repeatDaily;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return time + " - " + message + " (" + (repeatDaily ? "Daily" : "Once") + ")";
    }
} 