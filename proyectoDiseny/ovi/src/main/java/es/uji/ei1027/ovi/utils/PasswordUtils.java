package es.uji.ei1027.ovi.utils;

import org.jasypt.util.password.StrongPasswordEncryptor;

/*
PasswordUtils centraliza el cifrado. Usamos Jasypt (StrongPasswordEncryptor) que aplica SHA-256 con sal y muchas iteraciones.
Hay dos métodos: encrypt() para cifrar antes de guardar en BBDD, y
check() para verificar en login. Nunca se descifra una contraseña:
solo se cifra la que el usuario escribe y se compara con la de la BBDD.
 */

public class PasswordUtils {

    private static final StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    private PasswordUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String encrypt(String password) {
        return encryptor.encryptPassword(password);
    }

    public static boolean check(String plainPassword, String encryptedPassword) {
        return encryptor.checkPassword(plainPassword, encryptedPassword);
    }
}