package theknife.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Classe di utilità per la gestione delle password.
 * Fornisce metodi per cifrare e verificare password usando SHA-256.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class PasswordUtil {
    
    /**
     * Cifra una password usando l'algoritmo SHA-256
     * @param password La password in chiaro
     * @return La password cifrata in formato esadecimale
     */
    public static String cifraPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Converte i byte in formato esadecimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore nella cifratura della password", e);
        }
    }
    
    /**
     * Verifica se una password corrisponde a quella cifrata
     * @param passwordChiaro La password in chiaro da verificare
     * @param passwordCifrata La password cifrata salvata
     * @return true se le password corrispondono, false altrimenti
     */
    public static boolean verificaPassword(String passwordChiaro, String passwordCifrata) {
        return cifraPassword(passwordChiaro).equals(passwordCifrata);
    }
}