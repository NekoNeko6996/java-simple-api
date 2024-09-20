package src.libraries;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import src.config.ConfigLoader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JWT {
    private static final String SECRET_KEY = ConfigLoader.getConfig().getAuth_secret_key();
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private static String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes()));
    }

    public static String createToken(Map<String, String> payload, long expireTimeInSeconds) throws Exception {
        long issuedAt = System.currentTimeMillis() / 1000;
        long expiration = issuedAt + expireTimeInSeconds;

        // set expiration time
        payload.put("iat", Long.toString(issuedAt));
        payload.put("exp", Long.toString(expiration));

        // Encode header và payload
        String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(HEADER.getBytes());
        StringBuilder payloadBuilder = new StringBuilder("{");
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            payloadBuilder.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }

        // Delete last comma
        payloadBuilder.deleteCharAt(payloadBuilder.length() - 1).append("}");
        String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadBuilder.toString().getBytes());

        // Create signature
        String signature = hmacSha256(headerEncoded + "." + payloadEncoded, SECRET_KEY);
        return headerEncoded + "." + payloadEncoded + "." + signature;
    }

    public static boolean verifyToken(String token) throws Exception {

        // split token into header, payload, and signature
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        Map<String, String> payload = parsePayload(payloadJson);

        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        long exp = Long.parseLong(payload.get("exp"));

        if (exp < currentTimeInSeconds) {
            System.out.println("[JWT][verifyToken] Token expired");
            return false; // Token đã hết hạn
        }

        // Verify signature
        String headerAndPayload = parts[0] + "." + parts[1];
        String signature = hmacSha256(headerAndPayload, SECRET_KEY);

        return signature.equals(parts[2]);
    }

    private static Map<String, String> parsePayload(String json) {
        Map<String, String> payload = new HashMap<>();
        json = json.replace("{", "").replace("}", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            payload.put(keyValue[0].replace("\"", ""), keyValue[1].replace("\"", ""));
        }
        return payload;
    }

    private static String decodeBase64Url(String input) {
        input = input.replace("-", "+").replace("_", "/");
        switch (input.length() % 4) {
            case 2:
                input += "==";
                break;
            case 3:
                input += "=";
                break;
            default:
                break;
        }
        return new String(Base64.getDecoder().decode(input));
    }

    public static Map<String, String> decodePayload(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        String payloadJson = decodeBase64Url(parts[1]);

        return parsePayload(payloadJson);
    }

    public static Map<String, String> getPayload(String json) {
        Map<String, String> payload = new HashMap<>();
        json = json.trim().replace("{", "").replace("}", "");
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                payload.put(key, value);
            }
        }
        return payload;
    }
}
