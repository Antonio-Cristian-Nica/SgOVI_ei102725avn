package es.uji.ei1027.ovi.utils;

import org.jasypt.util.password.StrongPasswordEncryptor;

public class PasswordUtils {

    private static final StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    public static String encrypt(String password) {
        return encryptor.encryptPassword(password);
    }

    public static boolean check(String plainPassword, String encryptedPassword) {
        return encryptor.checkPassword(plainPassword, encryptedPassword);
    }
}
