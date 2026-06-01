import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class AuthService {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_HASH =
        "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";

    public boolean checkCredentials(String username, String password) {
        String hashedInput = hash(password);
        return ADMIN_USER.equals(username) && ADMIN_HASH.equals(hashedInput);
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            System.err.println("[AuthService] Hashing failed: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
