package es.uji.ei1027.ovi.utils;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class PasswordUtils {

    private static final StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    private PasswordUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Xifra una contrasenya abans de guardar-la
    public static String encrypt(String password) {
        return encryptor.encryptPassword(password);
    }

    // Comprova si una contrasenya coincidix amb la seua versió xifrada
    public static boolean check(String plainPassword, String encryptedPassword) {
        return encryptor.checkPassword(plainPassword, encryptedPassword);
    }
}