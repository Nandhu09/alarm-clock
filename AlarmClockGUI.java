import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class AlarmClockGUI extends JFrame {
    private AlarmManager alarmManager;
    private JList<Alarm> alarmList;
    private DefaultListModel<Alarm> listModel;
    private JTextField timeField;
    private JTextField messageField;
    private JTextField soundField;
    private JCheckBox repeatCheckBox;
    private JSpinner snoozeSpinner;
    private JLabel statusLabel;
    private JLabel clockLabel;
    private Timer clockTimer;
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Font CLOCK_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);

    public AlarmClockGUI() {
        alarmManager = new AlarmManager();
        setupGUI();
        startClock();
    }

    private void setupGUI() {
        setTitle("Modern Alarm Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create components
        mainPanel.add(createClockPanel(), BorderLayout.NORTH);
        mainPanel.add(createInputPanel(), BorderLayout.CENTER);
        mainPanel.add(createAlarmList(), BorderLayout.EAST);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createClockPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        clockLabel = new JLabel();
        clockLabel.setFont(CLOCK_FONT);
        clockLabel.setForeground(ACCENT_COLOR);
        panel.add(clockLabel);
        return panel;
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            "Add New Alarm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LABEL_FONT,
            ACCENT_COLOR
        ));
        panel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Time input
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel timeLabel = new JLabel("Time (HH:mm):");
        timeLabel.setFont(LABEL_FONT);
        panel.add(timeLabel, gbc);
        gbc.gridx = 1;
        timeField = new JTextField(10);
        panel.add(timeField, gbc);

        // Message input
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(LABEL_FONT);
        panel.add(messageLabel, gbc);
        gbc.gridx = 1;
        messageField = new JTextField(20);
        panel.add(messageField, gbc);

        // Sound file input with browse button
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel soundLabel = new JLabel("Sound File:");
        soundLabel.setFont(LABEL_FONT);
        panel.add(soundLabel, gbc);
        gbc.gridx = 1;
        JPanel soundPanel = new JPanel(new BorderLayout(5, 0));
        soundPanel.setBackground(BACKGROUND_COLOR);
        soundField = new JTextField(20);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseSoundFile());
        soundPanel.add(soundField, BorderLayout.CENTER);
        soundPanel.add(browseButton, BorderLayout.EAST);
        panel.add(soundPanel, gbc);

        // Repeat checkbox
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel repeatLabel = new JLabel("Repeat:");
        repeatLabel.setFont(LABEL_FONT);
        panel.add(repeatLabel, gbc);
        gbc.gridx = 1;
        repeatCheckBox = new JCheckBox();
        panel.add(repeatCheckBox, gbc);

        // Snooze spinner
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel snoozeLabel = new JLabel("Snooze (min):");
        snoozeLabel.setFont(LABEL_FONT);
        panel.add(snoozeLabel, gbc);
        gbc.gridx = 1;
        SpinnerNumberModel model = new SpinnerNumberModel(5, 0, 60, 5);
        snoozeSpinner = new JSpinner(model);
        panel.add(snoozeSpinner, gbc);

        // Add button
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Alarm");
        addButton.setBackground(ACCENT_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addAlarm());
        panel.add(addButton, gbc);

        return panel;
    }

    private void browseSoundFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("WAV Files", "wav"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            soundField.setText(selectedFile.getAbsolutePath());
        }
    }

    private JScrollPane createAlarmList() {
        listModel = new DefaultListModel<>();
        alarmList = new JList<>(listModel);
        alarmList.setCellRenderer(new AlarmListRenderer());
        alarmList.setBackground(BACKGROUND_COLOR);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR),
            "Active Alarms",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LABEL_FONT,
            ACCENT_COLOR
        ));
        panel.setBackground(BACKGROUND_COLOR);
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.setBackground(ACCENT_COLOR);
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> removeSelectedAlarm());
        
        panel.add(new JScrollPane(alarmList), BorderLayout.CENTER);
        panel.add(removeButton, BorderLayout.SOUTH);
        
        return new JScrollPane(panel);
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(LABEL_FONT);
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private void addAlarm() {
        try {
            String timeStr = timeField.getText().trim();
            String message = messageField.getText().trim();
            String soundFile = soundField.getText().trim();
            
            if (timeStr.isEmpty() || message.isEmpty()) {
                statusLabel.setText("Please fill in all required fields");
                return;
            }

            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (soundFile.isEmpty()) {
                soundFile = "alarm.wav";
            }

            Alarm alarm = new Alarm(time, message, soundFile);
            alarm.setRepeating(repeatCheckBox.isSelected());
            alarm.setSnoozeMinutes((Integer) snoozeSpinner.getValue());
            
            alarmManager.addAlarm(alarm);
            listModel.addElement(alarm);
            
            // Clear input fields
            timeField.setText("");
            messageField.setText("");
            soundField.setText("");
            repeatCheckBox.setSelected(false);
            snoozeSpinner.setValue(5);
            
            statusLabel.setText("Alarm added successfully!");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void removeSelectedAlarm() {
        int selectedIndex = alarmList.getSelectedIndex();
        if (selectedIndex != -1) {
            Alarm alarm = listModel.getElementAt(selectedIndex);
            alarmManager.removeAlarm(alarm);
            listModel.remove(selectedIndex);
            statusLabel.setText("Alarm removed successfully!");
        } else {
            statusLabel.setText("Please select an alarm to remove");
        }
    }

    private class AlarmListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Alarm) {
                Alarm alarm = (Alarm) value;
                setText(alarm.toString());
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AlarmClockGUI().setVisible(true);
        });
    }
} 