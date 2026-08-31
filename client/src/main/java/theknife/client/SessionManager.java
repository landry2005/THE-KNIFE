package theknife.client;

import theknife.model.Utente;
/**
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */

public final class SessionManager {

    private static Utente utenteCorrente;

    private SessionManager() {
    }

    public static void setUtente(Utente utente) {
        utenteCorrente = utente;
    }

    public static Utente getUtente() {
        return utenteCorrente;
    }

    public static boolean isLoggato() {
        return utenteCorrente != null;
    }

    public static boolean isCliente() {
        return isLoggato()
                && "cliente".equalsIgnoreCase(utenteCorrente.getRuolo());
    }

    public static boolean isRistoratore() {
        return isLoggato()
                && "ristoratore".equalsIgnoreCase(utenteCorrente.getRuolo());
    }

    public static void logout() {
        utenteCorrente = null;
    }
}