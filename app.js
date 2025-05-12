class AlarmClock {
    constructor() {
        this.alarms = [];
        this.apiBaseUrl = '/api/alarms';
        
        this.initializeElements();
        this.startClock();
        this.setupEventListeners();
        this.loadAlarms();
        this.requestNotificationPermission();
    }

    initializeElements() {
        // Clock elements
        this.hoursElement = document.getElementById('hours');
        this.minutesElement = document.getElementById('minutes');
        this.secondsElement = document.getElementById('seconds');
        this.dateElement = document.getElementById('date');

        // Form elements
        this.timeInput = document.getElementById('alarm-time');
        this.messageInput = document.getElementById('alarm-message');
        this.soundSelect = document.getElementById('alarm-sound');
        this.repeatCheckbox = document.getElementById('alarm-repeat');
        this.snoozeSelect = document.getElementById('snooze-time');
        this.addButton = document.getElementById('add-alarm');
        this.alarmsContainer = document.getElementById('alarms-container');
    }

    requestNotificationPermission() {
        if ('Notification' in window) {
            Notification.requestPermission();
        }
    }

    async loadAlarms() {
        try {
            const response = await fetch(this.apiBaseUrl);
            if (!response.ok) {
                throw new Error('Failed to load alarms');
            }
            this.alarms = await response.json();
            this.renderAlarms();
        } catch (error) {
            console.error('Error loading alarms:', error);
            // Fallback to empty array if server is not available
            this.alarms = [];
        }
    }

    async addAlarm() {
        const time = this.timeInput.value;
        const message = this.messageInput.value || 'Wake up!';
        const sound = this.soundSelect.value;
        const repeat = this.repeatCheckbox.checked;
        const snooze = parseInt(this.snoozeSelect.value);

        if (!time) {
            alert('Please select a time for the alarm');
            return;
        }

        try {
            const response = await fetch(this.apiBaseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    time,
                    message,
                    sound,
                    repeat,
                    snooze,
                    active: true
                })
            });

            if (!response.ok) {
                throw new Error('Failed to add alarm');
            }
            
            const alarm = await response.json();
            this.alarms.push(alarm);
            this.renderAlarms();
            this.clearForm();
        } catch (error) {
            console.error('Error adding alarm:', error);
            alert('Failed to add alarm. Using local storage instead.');
            
            // Fallback to local storage if server is not available
            const alarm = {
                id: Date.now(),
                time,
                message,
                sound,
                repeat,
                snooze,
                active: true
            };
            
            this.alarms.push(alarm);
            this.renderAlarms();
            this.clearForm();
        }
    }

    async removeAlarm(id) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Failed to delete alarm');
            }

            this.alarms = this.alarms.filter(alarm => alarm.id !== id);
            this.renderAlarms();
        } catch (error) {
            console.error('Error removing alarm:', error);
            // Remove from local array even if server fails
            this.alarms = this.alarms.filter(alarm => alarm.id !== id);
            this.renderAlarms();
        }
    }

    async snoozeAlarm(id) {
        const alarm = this.alarms.find(a => a.id === id);
        if (alarm && alarm.snooze > 0) {
            const [hours, minutes] = alarm.time.split(':');
            const alarmTime = new Date();
            alarmTime.setHours(parseInt(hours));
            alarmTime.setMinutes(parseInt(minutes) + alarm.snooze);
            
            const updatedTime = `${String(alarmTime.getHours()).padStart(2, '0')}:${String(alarmTime.getMinutes()).padStart(2, '0')}`;
            
            try {
                const response = await fetch(`${this.apiBaseUrl}/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        ...alarm,
                        time: updatedTime
                    })
                });

                if (!response.ok) {
                    throw new Error('Failed to snooze alarm');
                }
                
                const updatedAlarm = await response.json();
                const index = this.alarms.findIndex(a => a.id === id);
                if (index !== -1) {
                    this.alarms[index] = updatedAlarm;
                    this.renderAlarms();
                }
            } catch (error) {
                console.error('Error snoozing alarm:', error);
                // Update locally even if server fails
                alarm.time = updatedTime;
                this.renderAlarms();
            }
        }
    }

    playAlarmSound(soundType) {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        switch(soundType) {
            case 'High Pitch':
                oscillator.frequency.value = 1000;
                break;
            case 'Low Pitch':
                oscillator.frequency.value = 200;
                break;
            case 'Pulse':
                oscillator.frequency.value = 500;
                gainNode.gain.setValueAtTime(0.5, audioContext.currentTime);
                gainNode.gain.linearRampToValueAtTime(0, audioContext.currentTime + 0.5);
                break;
            default: // Standard
                oscillator.frequency.value = 440;
                break;
        }
        
        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);
        oscillator.start();
        oscillator.stop(audioContext.currentTime + 1);
    }

    triggerAlarm(alarm) {
        console.log('Triggering alarm:', alarm);
        
        // Add visual feedback
        const alarmElement = document.querySelector(`[data-alarm-id="${alarm.id}"]`);
        if (alarmElement) {
            alarmElement.classList.add('alarm-triggered');
        }

        // Play alarm sound
        this.playAlarmSound(alarm.sound);

        // Show notification if permission is granted
        if (Notification.permission === 'granted') {
            new Notification('Alarm', {
                body: alarm.message,
                icon: '/images/alarm-icon.png'
            });
        }

        // Show alert
        const snooze = confirm(`${alarm.message}\n\nSnooze?`);
        
        if (snooze && alarm.snooze > 0) {
            this.snoozeAlarm(alarm.id);
        } else {
            if (!alarm.repeat) {
                this.removeAlarm(alarm.id);
            }
        }

        // Remove visual feedback
        if (alarmElement) {
            alarmElement.classList.remove('alarm-triggered');
        }
    }

    checkAlarms(now) {
        const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
        
        this.alarms.forEach(alarm => {
            if (alarm.active && alarm.time === currentTime) {
                this.triggerAlarm(alarm);
            }
        });
    }

    startClock() {
        this.updateClock();
        setInterval(() => this.updateClock(), 1000);
    }

    updateClock() {
        const now = new Date();
        this.hoursElement.textContent = String(now.getHours()).padStart(2, '0');
        this.minutesElement.textContent = String(now.getMinutes()).padStart(2, '0');
        this.secondsElement.textContent = String(now.getSeconds()).padStart(2, '0');
        this.dateElement.textContent = now.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        this.checkAlarms(now);
    }

    setupEventListeners() {
        this.addButton.addEventListener('click', () => this.addAlarm());
    }

    renderAlarms() {
        this.alarmsContainer.innerHTML = '';
        
        if (this.alarms.length === 0) {
            this.alarmsContainer.innerHTML = '<p>No alarms set.</p>';
            return;
        }
        
        this.alarms.forEach(alarm => {
            const alarmElement = document.createElement('div');
            alarmElement.className = 'alarm-item';
            alarmElement.setAttribute('data-alarm-id', alarm.id);
            alarmElement.innerHTML = `
                <div>
                    <div class="alarm-time">${alarm.time}</div>
                    <div class="alarm-message">${alarm.message}</div>
                </div>
                <div class="actions">
                    <button class="btn-snooze">Snooze</button>
                    <button class="btn-danger">Delete</button>
                </div>
            `;
            
            // Add event listeners
            const snoozeButton = alarmElement.querySelector('.btn-snooze');
            const deleteButton = alarmElement.querySelector('.btn-danger');
            
            snoozeButton.addEventListener('click', () => this.snoozeAlarm(alarm.id));
            deleteButton.addEventListener('click', () => this.removeAlarm(alarm.id));
            
            this.alarmsContainer.appendChild(alarmElement);
        });
    }

    clearForm() {
        this.timeInput.value = '';
        this.messageInput.value = '';
        this.soundSelect.value = 'standard';
        this.repeatCheckbox.checked = false;
        this.snoozeSelect.value = '5';
    }
}

// Initialize the alarm clock when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    const alarmClock = new AlarmClock();
}); 