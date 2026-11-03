package com.campus.trade.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * 密码工具：PBKDF2-HMAC-SHA256，格式 pbkdf2$迭代次数$盐$摘要。
 * 不依赖第三方加密库，JDK 自带即可运行。
 */
public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        byte[] digest = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        return PREFIX + "$" + ITERATIONS + "$" + HEX.formatHex(salt) + "$" + HEX.formatHex(digest);
    }

    public static boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = HEX.parseHex(parts[2]);
            byte[] expected = HEX.parseHex(parts[3]);
            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            var spec = new javax.crypto.spec.PBEKeySpec(password, salt, iterations, keyLengthBits);
            return javax.crypto.SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("密码加密失败", ex);
        }
    }

    /** 仅用于本地验证，不应在业务代码中调用。 */
    public static void main(String[] args) {
        String raw = args.length > 0 ? args[0] : "123456";
        String encoded = hash(raw);
        System.out.println(encoded);
        System.out.println(new String(encoded.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        System.out.println("matches=" + matches(raw, encoded));
    }
}
