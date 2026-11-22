
package controller;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SALT = 16;

    // Generar el hash de una contraseña
    public static String generarHash(String contrasena) {

        try {
            // Crear un salt aleatorio
            byte[] salt = new byte[LONGITUD_SALT];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // Generar el hash
            PBEKeySpec spec = new PBEKeySpec(
                contrasena.toCharArray(),
                salt,
                ITERACIONES,
                LONGITUD_HASH
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            // Convertir salt y hash a texto
            String saltTexto = Base64.getEncoder().encodeToString(salt);
            String hashTexto = Base64.getEncoder().encodeToString(hash);

            // Guardar iteraciones, salt y hash juntos
            return ITERACIONES + ":" + saltTexto + ":" + hashTexto;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el hash", e);
        }
    }

    // Verificar una contraseña
    public static boolean verificarPassword(
            String contrasena,
            String hashGuardado) {

        try {
            String[] partes = hashGuardado.split(":");

            int iteraciones = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] hashOriginal = Base64.getDecoder().decode(partes[2]);

            PBEKeySpec spec = new PBEKeySpec(
                contrasena.toCharArray(),
                salt,
                iteraciones,
                hashOriginal.length * 8
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hashNuevo = factory.generateSecret(spec).getEncoded();

            spec.clearPassword();

            // Comparar los hashes de forma segura
            return MessageDigest.isEqual(hashOriginal, hashNuevo);

        } catch (Exception e) {
            return false;
        }
    }
}

