package theknife;

import java.util.Scanner;
import theknife.model.Ristorante;
import theknife.model.Utente;
import theknife.service.GestoreRistoranti;
import theknife.service.GestoreUtenti;
import java.util.List;

/**
 * Classe principale dell'applicazione TheKnife.
 * Gestisce il menu iniziale e la navigazione dell'utente.
 *
 * @author
 * Nome Cognome - Matricola - Sede
 */
public class TheKnife {

    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {

        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        GestoreRistoranti gestoreRistoranti = new GestoreRistoranti();

        boolean uscita = false;

        while (!uscita) {

            // ===== MENU PRINCIPALE =====
            System.out.println("=================================");
            System.out.println("        BENVENUTO IN THEKNIFE     ");
            System.out.println("=================================");
            System.out.println("1) Login");
            System.out.println("2) Registrazione");
            System.out.println("3) Continua come guest");
            System.out.println("0) Esci");
            System.out.print("Scelta: ");

            int scelta = leggiIntero();

            switch (scelta) {

                case 1:
                    login(gestoreUtenti);
                    break;

                case 2:
                    System.out.println("Registrazione non ancora implementata.");
                    pausa();
                    break;

                case 3:
                    menuGuest();
                    break;

                case 0:
                    uscita = true;
                    System.out.println("Arrivederci!");
                    break;

                default:
                    System.out.println("Scelta non valida.");
            }
        }

        in.close();
    }

    // =====================================================
    // LOGIN
    // =====================================================
    private static void login(GestoreUtenti gestoreUtenti) {

        System.out.print("Username: ");
        String username = in.nextLine();

        System.out.print("Password: ");
        String password = in.nextLine();

        Utente utente = gestoreUtenti.login(username, password);

        if (utente == null) {
            pausa();
            return;
        }

        System.out.println("Benvenuto " + utente.getNome());
        System.out.println("Ruolo: " + utente.getRuolo());

        if (utente.getRuolo().equalsIgnoreCase("cliente")) {
            menuCliente(utente);
        } else if (utente.getRuolo().equalsIgnoreCase("ristoratore")) {
            menuRistoratore(utente);
        }
    }

    // =====================================================
// MENU GUEST
// =====================================================
private static void menuGuest(GestoreRistoranti gestoreRistoranti) {

    boolean indietro = false;

    while (!indietro) {

        System.out.println("\n===== MENU GUEST =====");
        System.out.println("1) Cerca ristoranti per città");
        System.out.println("2) Cerca ristoranti per tipo di cucina");
        System.out.println("0) Torna al menu principale");
        System.out.print("Scelta: ");

        int scelta = leggiIntero();

        switch (scelta) {

            case 1:
                System.out.print("Inserisci la città: ");
                String citta = in.nextLine();
                List<Ristorante> perCitta = gestoreRistoranti.cercaPerCitta(citta);
                visualizzaRistoranti(perCitta);
                break;

            case 2:
                System.out.print("Inserisci il tipo di cucina: ");
                String tipo = in.nextLine();
                List<Ristorante> perTipo = gestoreRistoranti.cercaPerTipo(tipo);
                visualizzaRistoranti(perTipo);
                break;

            case 0:
                indietro = true;
                break;

            default:
                System.out.println("Scelta non valida.");
        }
    }
}


    // =====================================================
    // MENU CLIENTE
    // =====================================================
    private static void menuCliente(Utente utente) {
        System.out.println("\n===== MENU CLIENTE =====");
        System.out.println("Funzionalità cliente in sviluppo.");
        pausa();
    }

    // =====================================================
    // MENU RISTORATORE
    // =====================================================
    private static void menuRistoratore(Utente utente) {
        System.out.println("\n===== MENU RISTORATORE =====");
        System.out.println("Funzionalità ristoratore in sviluppo.");
        pausa();
    }

    // =====================================================
    // UTILITÀ
    // =====================================================
    private static void visualizzaRistoranti(List<Ristorante> ristoranti) {

        System.out.println("\n===== ELENCO RISTORANTI =====");

        for (Ristorante r : ristoranti) {
            System.out.println("----------------------------------");
            System.out.println("Nome: " + r.getNome());
            System.out.println("Città: " + r.getCitta());
            System.out.println("Cucina: " + r.getTipoCucina());
            System.out.println("Prezzo medio: " + r.getPrezzoMedio() + "€");
            System.out.println("Delivery: " + (r.isDelivery() ? "Sì" : "No"));
            System.out.println("Prenotazione: " + (r.isPrenotazione() ? "Sì" : "No"));
        }

        pausa();
    }

    private static int leggiIntero() {
        try {
            return Integer.parseInt(in.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
       
    }

    private static void pausa() {
        System.out.println("\nPremi INVIO per continuare...");
        in.nextLine();
    }
}
