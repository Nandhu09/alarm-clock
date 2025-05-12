import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmClockApp extends JFrame {
    private ArrayList<AlarmItem> alarms;
    private JLabel timeLabel, dateLabel;
    private JTextField timeField, messageField;
    private JComboBox<String> soundComboBox;
    private JComboBox<Integer> snoozeComboBox;
    private JCheckBox repeatCheckBox;
    private JPanel alarmsPanel;

    public AlarmClockApp() {
        // Load saved alarms
        alarms = new ArrayList<>(AlarmDataManager.loadAlarms());
        
        // Set up the JFrame
        setTitle("Alarm Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Clock Panel
        JPanel clockPanel = createClockPanel();
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Alarms Panel
        JPanel alarmListPanel = createAlarmsPanel();
        
        // Add panels to main panel
        mainPanel.add(clockPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(alarmListPanel);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add window listener to save alarms on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AlarmDataManager.saveAlarms(alarms);
            }
        });
        
        // Update alarms panel
        updateAlarmsPanel();
        
        // Start clock
        startClock();
    }
    
    private JPanel createClockPanel() {
        JPanel clockPanel = new JPanel();
        clockPanel.setLayout(new BoxLayout(clockPanel, BoxLayout.Y_AXIS));
        clockPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clockPanel.setBackground(new Color(58, 123, 213));
        clockPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        clockPanel.add(timeLabel);
        clockPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        clockPanel.add(dateLabel);
        
        return clockPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Alarm"));
        
        // Time input
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeField = new JTextField(10);
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        
        // Message input
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel messageLabel = new JLabel("Message:");
        messageField = new JTextField(20);
        messagePanel.add(messageLabel);
        messagePanel.add(messageField);
        
        // Sound selection
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel soundLabel = new JLabel("Sound:");
        String[] sounds = {"Standard", "High Pitch", "Low Pitch", "Pulse"};
        soundComboBox = new JComboBox<>(sounds);
        soundPanel.add(soundLabel);
        soundPanel.add(soundComboBox);
        
        // Snooze selection
        JPanel snoozePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel snoozeLabel = new JLabel("Snooze (minutes):");
        Integer[] snoozeOptions = {5, 10, 15, 30};
        snoozeComboBox = new JComboBox<>(snoozeOptions);
        snoozePanel.add(snoozeLabel);
        snoozePanel.add(snoozeComboBox);
        
        // Repeat checkbox
        JPanel repeatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        repeatCheckBox = new JCheckBox("Repeat Daily");
        repeatPanel.add(repeatCheckBox);
        
        // Add button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Alarm");
        addButton.addActionListener(e -> addAlarm());
        buttonPanel.add(addButton);
        
        // Add components to form panel
        formPanel.add(timePanel);
        formPanel.add(messagePanel);
        formPanel.add(soundPanel);
        formPanel.add(snoozePanel);
        formPanel.add(repeatPanel);
        formPanel.add(buttonPanel);
        
        return formPanel;
    }
    
    private JPanel createAlarmsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Active Alarms"));
        
        alarmsPanel = new JPanel();
        alarmsPanel.setLayout(new BoxLayout(alarmsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(alarmsPanel);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void startClock() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    LocalTime now = LocalTime.now();
                    LocalDate today = LocalDate.now();
                    
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
                    
                    timeLabel.setText(now.format(timeFormatter));
                    dateLabel.setText(today.format(dateFormatter));
                    
                    checkAlarms(now);
                });
            }
        }, 0, 1000);
    }
    
    private void addAlarm() {
        String timeText = timeField.getText().trim();
        if (timeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a time (HH:MM)");
            return;
        }
        
        try {
            // Parse time
            String[] parts = timeText.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid time format");
            }
            
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                throw new IllegalArgumentException("Invalid time values");
            }
            
            // Create new alarm
            String message = messageField.getText().isEmpty() ? "Wake up!" : messageField.getText();
            String sound = (String) soundComboBox.getSelectedItem();
            int snooze = (int) snoozeComboBox.getSelectedItem();
            boolean repeat = repeatCheckBox.isSelected();
            
            AlarmItem alarm = new AlarmItem(
                String.format("%02d:%02d", hours, minutes),
                message,
                sound,
                snooze,
                repeat
            );
            
            alarms.add(alarm);
            updateAlarmsPanel();
            clearForm();
            
            // Save alarms
            AlarmDataManager.saveAlarms(alarms);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:MM");
        }
    }
    
    private void updateAlarmsPanel() {
        alarmsPanel.removeAll();
        
        if (alarms.isEmpty()) {
            JLabel emptyLabel = new JLabel("No alarms set");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            alarmsPanel.add(emptyLabel);
        } else {
            for (AlarmItem alarm : alarms) {
                JPanel alarmPanel = new JPanel(new BorderLayout());
                alarmPanel.setBorder(BorderFactory.createEtchedBorder());
                alarmPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                JLabel timeLabel = new JLabel(alarm.getTime());
                timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
                JLabel messageLabel = new JLabel(alarm.getMessage());
                
                infoPanel.add(timeLabel);
                infoPanel.add(messageLabel);
                
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
                alarmsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        alarmsPanel.revalidate();
        alarmsPanel.repaint();
    }
    
    private void snoozeAlarm(AlarmItem alarm) {
        String[] parts = alarm.getTime().split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        
        minutes += alarm.getSnooze();
        if (minutes >= 60) {
            hours = (hours + minutes / 60) % 24;
            minutes = minutes % 60;
        }
        
        alarm.setTime(String.format("%02d:%02d", hours, minutes));
        updateAlarmsPanel();
        
        // Save alarms
        AlarmDataManager.saveAlarms(alarms);
    }
    
    private void deleteAlarm(AlarmItem alarm) {
        alarms.remove(alarm);
        updateAlarmsPanel();
        
        // Save alarms
        AlarmDataManager.saveAlarms(alarms);
    }
    
    private void clearForm() {
        timeField.setText("");
        messageField.setText("");
        soundComboBox.setSelectedIndex(0);
        snoozeComboBox.setSelectedIndex(0);
        repeatCheckBox.setSelected(false);
    }
    
    private void checkAlarms(LocalTime now) {
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        
        for (AlarmItem alarm : new ArrayList<>(alarms)) {
            if (alarm.getTime().equals(currentTime) && alarm.isActive()) {
                triggerAlarm(alarm);
            }
        }
    }
    
    private void triggerAlarm(AlarmItem alarm) {
        // Play sound
        AlarmSoundPlayer soundPlayer = new AlarmSoundPlayer();
        soundPlayer.playSound(alarm.getSound());
        
        // Show dialog
        int option = JOptionPane.showConfirmDialog(
            this,
            alarm.getMessage() + "\n\nSnooze alarm?",
            "Alarm",
            JOptionPane.YES_NO_OPTION
        );
        
        // Stop sound
        soundPlayer.stop();
        
        if (option == JOptionPane.YES_OPTION) {
            snoozeAlarm(alarm);
        } else {
            if (!alarm.isRepeat()) {
                deleteAlarm(alarm);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            AlarmClockApp app = new AlarmClockApp();
            app.setVisible(true);
        });
    }
} 