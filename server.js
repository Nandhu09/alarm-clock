const express = require('express');
const path = require('path');
const app = express();
const PORT = 5000;

// Serve static files
app.use(express.static(__dirname));

// Route for the main page
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'AlarmClockWeb.html'));
});

// Start the server
app.listen(PORT, () => {
  console.log(`Alarm Clock server running at http://localhost:${PORT}`);
}); 