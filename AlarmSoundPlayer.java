import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.Toolkit;

public class AlarmSoundPlayer {
    private static final int SAMPLE_RATE = 44100; // 44.1 kHz
    private static final int SAMPLE_SIZE = 8;     // 8 bits
    private static final int CHANNELS = 1;        // mono
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    
    private Clip clip;
    
    public void playSound(String soundType) {
        try {
            // Generate sound data based on the type
            byte[] soundData = generateSoundData(soundType);
            
            // Set up audio format
            AudioFormat format = new AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
            );
            
            // Create audio input stream
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(soundData),
                format,
                soundData.length
            );
            
            // Get a clip to play the sound
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Add listener to release resources when done
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    event.getLine().close();
                }
            });
            
            // Play the sound
            clip.start();
            
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
            // Fallback to system beep
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
    
    private byte[] generateSoundData(String soundType) {
        // Sound parameters
        double durationSeconds = 3.0;
        int numSamples = (int) (durationSeconds * SAMPLE_RATE);
        byte[] data = new byte[numSamples];
        
        switch (soundType) {
            case "High Pitch":
                generateTone(data, 1000, 0.5); // 1000 Hz, 50% volume
                break;
                
            case "Low Pitch":
                generateTone(data, 200, 0.5); // 200 Hz, 50% volume
                break;
                
            case "Pulse":
                generatePulseTone(data, 500, 0.5); // 500 Hz, 50% volume
                break;
                
            default: // Standard
                generateAlarmTone(data, 440, 0.5); // 440 Hz, 50% volume
        }
        
        return data;
    }
    
    private void generateTone(byte[] data, double frequency, double amplitude) {
        double period = SAMPLE_RATE / frequency;
        
        for (int i = 0; i < data.length; i++) {
            double angle = 2.0 * Math.PI * i / period;
            data[i] = (byte) (Math.sin(angle) * amplitude * 127);
        }
    }
    
    private void generatePulseTone(byte[] data, double frequency, double amplitude) {
        double period = SAMPLE_RATE / frequency;
        double pulseFrequency = 2.0; // 2 Hz pulse (on/off cycle every 0.5s)
        
        for (int i = 0; i < data.length; i++) {
            double angle = 2.0 * Math.PI * i / period;
            
            // Apply pulse envelope (on/off pattern)
            double pulseTime = i / (double) SAMPLE_RATE;
            double pulseEnvelope = Math.sin(2 * Math.PI * pulseFrequency * pulseTime) > 0 ? 1.0 : 0.2;
            
            data[i] = (byte) (Math.sin(angle) * amplitude * 127 * pulseEnvelope);
        }
    }
    
    private void generateAlarmTone(byte[] data, double frequency, double amplitude) {
        double period = SAMPLE_RATE / frequency;
        double beepFrequency = 4.0; // 4 Hz beep pattern
        
        for (int i = 0; i < data.length; i++) {
            double angle = 2.0 * Math.PI * i / period;
            
            // Apply beep pattern
            double beepTime = i / (double) SAMPLE_RATE;
            double beepPattern = Math.sin(2 * Math.PI * beepFrequency * beepTime) > 0 ? 1.0 : 0.0;
            
            data[i] = (byte) (Math.sin(angle) * amplitude * 127 * beepPattern);
        }
    }
} 