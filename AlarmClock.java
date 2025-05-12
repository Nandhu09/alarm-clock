import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmClock extends JFrame {
    private JLabel timeLabel;
    private JLabel dateLabel;
    private JTextField timeInput;
    private JTextField messageInput;
    private JComboBox<String> soundSelect;
    private JCheckBox repeatCheckbox;
    private JComboBox<Integer> snoozeSelect;
    private JButton addButton;
    private JPanel alarmsPanel;
    private ArrayList<Alarm> alarms;
    private Timer timer;
    private DateTimeFormatter timeFormatter;

    public AlarmClock() {
        alarms = new ArrayList<>();
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // Set up the frame
        setTitle("Java Alarm Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Clock display
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
        clockPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        clockPanel.add(timeLabel);
        clockPanel.add(Box.createVerticalStrut(10));
        clockPanel.add(dateLabel);
        
        // Alarm form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Alarm"));
        
        // Time input
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(new JLabel("Time:"));
        timeInput = new JTextField(10);
        timePanel.add(timeInput);
        
        // Message input
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.add(new JLabel("Message:"));
        messageInput = new JTextField(20);
        messagePanel.add(messageInput);
        
        // Sound selection
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        soundPanel.add(new JLabel("Sound:"));
        String[] sounds = {"Standard", "High Pitch", "Low Pitch", "Pulse"};
        soundSelect = new JComboBox<>(sounds);
        soundPanel.add(soundSelect);
        
        // Snooze selection
        JPanel snoozePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        snoozePanel.add(new JLabel("Snooze:"));
        Integer[] snoozeTimes = {5, 10, 15, 30};
        snoozeSelect = new JComboBox<>(snoozeTimes);
        snoozePanel.add(snoozeSelect);
        
        // Repeat checkbox
        JPanel repeatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        repeatCheckbox = new JCheckBox("Repeat Daily");
        repeatPanel.add(repeatCheckbox);
        
        // Add button
        addButton = new JButton("Add Alarm");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to form panel
        formPanel.add(timePanel);
        formPanel.add(messagePanel);
        formPanel.add(soundPanel);
        formPanel.add(snoozePanel);
        formPanel.add(repeatPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(addButton);
        
        // Alarms panel
        alarmsPanel = new JPanel();
        alarmsPanel.setLayout(new BoxLayout(alarmsPanel, BoxLayout.Y_AXIS));
        alarmsPanel.setBorder(BorderFactory.createTitledBorder("Active Alarms"));
        
        // Add panels to main panel
        mainPanel.add(clockPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(alarmsPanel);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Set up event listeners
        addButton.addActionListener(e -> addAlarm());
        
        // Start clock
        startClock();
    }
    
    private void startClock() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    LocalTime now = LocalTime.now();
                    timeLabel.setText(now.format(timeFormatter));
                    dateLabel.setText(java.time.LocalDate.now().toString());
                    checkAlarms();
                });
            }
        }, 0, 1000);
    }
    
    private void addAlarm() {
        String time = timeInput.getText();
        String message = messageInput.getText().isEmpty() ? "Wake up!" : messageInput.getText();
        String sound = (String) soundSelect.getSelectedItem();
        boolean repeat = repeatCheckbox.isSelected();
        int snooze = (Integer) snoozeSelect.getSelectedItem();
        
        if (time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a time");
            return;
        }
        
        Alarm alarm = new Alarm(time, message, sound, repeat, snooze);
        alarms.add(alarm);
        updateAlarmsPanel();
        clearForm();
    }
    
    private void updateAlarmsPanel() {
        alarmsPanel.removeAll();
        
        for (Alarm alarm : alarms) {
            JPanel alarmPanel = new JPanel(new BorderLayout());
            alarmPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.add(new JLabel(alarm.getTime()));
            infoPanel.add(new JLabel(alarm.getMessage()));
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton snoozeButton = new JButton("Snooze");
            JButton deleteButton = new JButton("Delete");
            
            snoozeButton.addActionListener(e -> snoozeAlarm(alarm));
            deleteButton.addActionListener(e -> deleteAlarm(alarm));
            
            buttonPanel.add(snoozeButton);
            buttonPanel.add(deleteButton);
            
            alarmPanel.add(infoPanel, BorderLayout.CENTER);
            alarmPanel.add(buttonPanel, BorderLayout.EAST);
            
            alarmsPanel.add(alarmPanel);
            alarmsPanel.add(Box.createVerticalStrut(5));
        }
        
        alarmsPanel.revalidate();
        alarmsPanel.repaint();
    }
    
    private void clearForm() {
        timeInput.setText("");
        messageInput.setText("");
        soundSelect.setSelectedIndex(0);
        repeatCheckbox.setSelected(false);
        snoozeSelect.setSelectedIndex(0);
    }
    
    private void checkAlarms() {
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        for (Alarm alarm : alarms) {
            if (alarm.getTime().equals(currentTime) && alarm.isActive()) {
                triggerAlarm(alarm);
            }
        }
    }
    
    private void triggerAlarm(Alarm alarm) {
        // Play sound
        Toolkit.getDefaultToolkit().beep();
        
        // Show dialog
        int choice = JOptionPane.showConfirmDialog(
            this,
            alarm.getMessage() + "\n\nSnooze?",
            "Alarm",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            snoozeAlarm(alarm);
        } else {
            if (!alarm.isRepeat()) {
                deleteAlarm(alarm);
            }
        }
    }
    
    private void snoozeAlarm(Alarm alarm) {
        String[] timeParts = alarm.getTime().split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        
        minutes += alarm.getSnooze();
        if (minutes >= 60) {
            hours = (hours + minutes / 60) % 24;
            minutes = minutes % 60;
        }
        
        alarm.setTime(String.format("%02d:%02d", hours, minutes));
        updateAlarmsPanel();
    }
    
    private void deleteAlarm(Alarm alarm) {
        alarms.remove(alarm);
        updateAlarmsPanel();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AlarmClock().setVisible(true);
        });
    }
} 