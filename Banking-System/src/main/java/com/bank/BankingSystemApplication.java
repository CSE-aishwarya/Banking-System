package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import com.bank.config.JwtConfig;
import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class BankingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            BankingSystemApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        System.out.println("\n");
        System.out.println(
            "╔══════════════════════════════════════╗");
        System.out.println(
            "║    🏦 SECUREBANK IS RUNNING! 🏦      ║");
        System.out.println(
            "╠══════════════════════════════════════╣");
        System.out.println(
            "║                                      ║");
        System.out.println(
            "║  🌐 URL: http://localhost:8080       ║");
        System.out.println(
            "║                                      ║");
        System.out.println(
            "║  ✅ Browser opening automatically... ║");
        System.out.println(
            "║                                      ║");
        System.out.println(
            "╚══════════════════════════════════════╝");
        System.out.println("\n");

        // Auto open browser
        openBrowser("http://localhost:8080");
    }

    private void openBrowser(String url) {
        try {
            // Method 1: Java Desktop API
            if (Desktop.isDesktopSupported() &&
                Desktop.getDesktop().isSupported(
                    Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(
                    new URI(url));
                System.out.println(
                    "✅ Browser opened successfully!");
                return;
            }
        } catch (Exception e) {
            System.out.println(
                "Trying alternative method...");
        }

        try {
            // Method 2: Windows Command
            String os = System.getProperty("os.name")
                .toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("win")) {
                rt.exec("rundll32 url.dll," +
                    "FileProtocolHandler " + url);
                System.out.println(
                    "✅ Browser opened!");
            } else if (os.contains("mac")) {
                rt.exec("open " + url);
            } else if (os.contains("nix") ||
                       os.contains("nux")) {
                rt.exec("xdg-open " + url);
            }
        } catch (Exception e) {
            System.out.println(
                "Please open manually: " + url);
        }
    }
}