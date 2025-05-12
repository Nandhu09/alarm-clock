import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AlarmClockServer {
    private static final int PORT = 8080;
    private static final String HTML_FILE = "AlarmClockWeb.html";
    private static final String JS_FILE = "app.js";
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Create context for serving the HTML file
        server.createContext("/", new FileHandler(HTML_FILE, "text/html"));
        
        // Create context for serving the JavaScript file
        server.createContext("/app.js", new FileHandler(JS_FILE, "application/javascript"));
        
        // Start the server
        server.setExecutor(null);
        server.start();
        
        System.out.println("Alarm Clock server running at http://localhost:" + PORT);
    }
    
    // Handler for serving files
    static class FileHandler implements HttpHandler {
        private final String filePath;
        private final String contentType;
        
        public FileHandler(String filePath, String contentType) {
            this.filePath = filePath;
            this.contentType = contentType;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Read the file content
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                
                // Set response headers
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
                // Send the response
                exchange.sendResponseHeaders(200, fileContent.length);
                
                // Write the file content to the response
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } catch (IOException e) {
                String response = "Error: " + e.getMessage();
                exchange.sendResponseHeaders(500, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
} 