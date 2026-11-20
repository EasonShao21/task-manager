package com.interview.task_manager.card;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

// [AI assisted chat-010] 手機掃碼時提供可連到開發電腦的區網網址。
@RestController
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "http://192.168.*.*:*", "http://10.*.*.*:*", "http://172.*.*.*:*"})
public class ShareConfigController {
    private final String configuredFrontendBaseUrl;

    public ShareConfigController(@Value("${cardfolio.frontend-base-url:}") String configuredFrontendBaseUrl) {
        this.configuredFrontendBaseUrl = configuredFrontendBaseUrl;
    }

    @GetMapping("/api/share-config")
    public Map<String, String> getShareConfig() {
        String baseUrl = configuredFrontendBaseUrl.isBlank()
            ? "http://" + findLanAddress() + ":5173"
            : configuredFrontendBaseUrl.replaceAll("/+$", "");
        return Map.of("frontendBaseUrl", baseUrl);
    }

    private String findLanAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isUp() || network.isLoopback() || network.isVirtual()) continue;
                String name = network.getName().toLowerCase();
                if (name.contains("docker") || name.contains("veth") || name.contains("virtual") || name.contains("wsl")) continue;
                Enumeration<java.net.InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    var address = addresses.nextElement();
                    if (address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress())
                        return address.getHostAddress();
                }
            }
        } catch (Exception ignored) { }
        return "localhost";
    }
}
