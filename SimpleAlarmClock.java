import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SimpleAlarmClock extends JFrame {
    private final List<AlarmItem> alarms = new ArrayList<>();
    private final javax.swing.Timer clockTimer;
    private final JLabel timeLabel;
    private final DefaultListModel<String> alarmListModel;
    private final JList<String> alarmList;
    private AlarmSoundPlayer soundPlayer;
    private final JComboBox<String> soundTypeCombo;

    public SimpleAlarmClock() {
        // Setup the window
        super("Java Alarm Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Initialize sound player
        soundPlayer = new AlarmSoundPlayer();

        // Create main panels
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create clock panel
        JPanel clockPanel = new JPanel();
        timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        clockPanel.add(timeLabel);

        // Create alarm control panel
        JPanel controlPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        // Time selection
        controlPanel.add(new JLabel("Set Alarm Time (HH:MM):"));
        JPanel timeInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SpinnerModel hoursModel = new SpinnerNumberModel(12, 0, 23, 1);
        SpinnerModel minutesModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner hoursSpinner = new JSpinner(hoursModel);
        JSpinner minutesSpinner = new JSpinner(minutesModel);
        timeInputPanel.add(hoursSpinner);
        timeInputPanel.add(new JLabel(":"));
        timeInputPanel.add(minutesSpinner);
        controlPanel.add(timeInputPanel);

        // Message input
        controlPanel.add(new JLabel("Alarm Message:"));
        JTextField messageField = new JTextField("Wake up!");
        controlPanel.add(messageField);

        // Sound type selection
        controlPanel.add(new JLabel("Sound Type:"));
        soundTypeCombo = new JComboBox<>(new String[]{"Standard", "High Pitch", "Low Pitch", "Pulse"});
        controlPanel.add(soundTypeCombo);

        // Repeat option
        controlPanel.add(new JLabel("Repeat Daily:"));
        JCheckBox repeatCheckbox = new JCheckBox();
        controlPanel.add(repeatCheckbox);

        // Buttons
        JButton addButton = new JButton("Add Alarm");
        JButton deleteButton = new JButton("Delete Selected");

        controlPanel.add(addButton);
        controlPanel.add(deleteButton);

        // Create alarm list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Alarms"));
        alarmListModel = new DefaultListModel<>();
        alarmList = new JList<>(alarmListModel);
        JScrollPane scrollPane = new JScrollPane(alarmList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(clockPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Set the content pane
        setContentPane(mainPanel);

        // Setup timer for clock
        clockTimer = new javax.swing.Timer(1000, e -> {
            updateClockDisplay();
            checkAlarms();
        });
        clockTimer.start();

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                int hours = (Integer) hoursSpinner.getValue();
                int minutes = (Integer) minutesSpinner.getValue();
                String timeString = String.format("%02d:%02d", hours, minutes);
                String message = messageField.getText();
                String soundType = (String) soundTypeCombo.getSelectedItem();
                boolean repeat = repeatCheckbox.isSelected();

                AlarmItem alarm = new AlarmItem(timeString, message, soundType, repeat);
                alarms.add(alarm);
                updateAlarmList();
                
                JOptionPane.showMessageDialog(this, 
                    "Alarm added successfully for " + timeString, 
                    "Alarm Added", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding alarm: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            int selectedIndex = alarmList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < alarms.size()) {
                alarms.remove(selectedIndex);
                updateAlarmList();
            }
        });

        // Load saved alarms
        loadAlarms();
    }

    private void updateClockDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        timeLabel.setText(sdf.format(new Date()));
    }

    private void updateAlarmList() {
        alarmListModel.clear();
        for (AlarmItem alarm : alarms) {
            alarmListModel.addElement(alarm.toString());
        }
        saveAlarms();
    }

    private void checkAlarms() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        Iterator<AlarmItem> iterator = alarms.iterator();
        while (iterator.hasNext()) {
            AlarmItem alarm = iterator.next();
            if (alarm.isEnabled() && alarm.getTime().equals(currentTime)) {
                triggerAlarm(alarm);
                
                if (!alarm.isRepeatDaily()) {
                    iterator.remove();
                }
            }
        }
        updateAlarmList();
    }

    private void triggerAlarm(AlarmItem alarm) {
        soundPlayer.playSound(alarm.getSoundType());
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            alarm.getMessage(),
            "Alarm!",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        soundPlayer.stop();
    }

    private void saveAlarms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("alarms.dat"))) {
            oos.writeObject(alarms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAlarms() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("alarms.dat"))) {
            List<AlarmItem> loadedAlarms = (List<AlarmItem>) ois.readObject();
            alarms.clear();
            alarms.addAll(loadedAlarms);
            updateAlarmList();
        } catch (IOException | ClassNotFoundException e) {
            // Ignore - probably first run
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            SimpleAlarmClock alarmClock = new SimpleAlarmClock();
            alarmClock.setVisible(true);
        });
    }
} 