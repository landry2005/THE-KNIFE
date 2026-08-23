package theknife;

import theknife.gestione.GestoreRistoranti;
import theknife.gestione.GestoreUtenti;
import theknife.gestione.GestoreRecensioni;
import theknife.model.Ristorante;
import theknife.model.Utente;
import theknife.model.Recenzione;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale dell'applicazione TheKnife.
 * È una piattaforma per la ricerca e recensione di ristoranti.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class TheKnife {
    private static Scanner scanner = new Scanner(System.in);
    private static GestoreUtenti gestoreUtenti;
    private static GestoreRistoranti gestoreRistoranti;
    private static GestoreRecensioni gestoreRecensioni;
    private static String cittaCorrente = "";
    
    public static void main(String[] args) {
        // Inizializzazione gestori
        gestoreRistoranti = new GestoreRistoranti();
        gestoreUtenti = new GestoreUtenti();
        gestoreRecensioni = new GestoreRecensioni(gestoreRistoranti);
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("           BENVENUTO IN THE KNIFE");
        System.out.println("    La tua piattaforma per scoprire ristoranti");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        menuPrincipale();
    }
    
    /**
     * Menu principale dell'applicazione
     */
    private static void menuPrincipale() {
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("              MENU PRINCIPALE");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("1) Continua come ospite");
            System.out.println("2) Registrati");
            System.out.println("3) Accedi");            
            System.out.println("4) Password dimenticata?");
            System.out.println("0) Esci");
            System.out.println("═══════════════════════════════════════════════════════");
            
            int scelta = leggiIntero("Scelta: ");

            switch (scelta) {
                case 1:
                    modalitaOspite();    
                    break;
                case 2:
                    registrazione();                   
                    break;
                case 3:
                    login();
                    break;
                case 4:
                    recuperaPassword();
                    break;
                case 0:
                    System.out.println("\nGrazie per aver usato TheKnife. Arrivederci!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Scelta non valida!");
                    break;
            }
        }
    }
    
    /**
     * Gestisce la registrazione di un nuovo utente
     */
    private static void registrazione() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("              REGISTRAZIONE");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.println("\nScegli il tipo di account:");
        System.out.println("1) Cliente");
        System.out.println("2) Ristoratore");
        int tipoAccount = leggiIntero("Scelta: ");
        String ruolo = (tipoAccount == 2) ? "ristoratore" : "cliente";
        
        System.out.print("Data di nascita (dd/MM/yyyy, premi INVIO per saltare): ");
        String dataStr = scanner.nextLine();
        LocalDate dataNascita = null;
        if (!dataStr.isEmpty()) {
            try {
                dataNascita = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido, campo ignorato.");
            }
        }
        
        System.out.print("Città di domicilio: ");
        String cittaDomicilio = scanner.nextLine();
        
        // Domanda di sicurezza per recupero password
        System.out.println("\n--- Sicurezza Account ---");
        System.out.println("Imposta una domanda di sicurezza per recuperare la password:");
        System.out.println("Esempi:");
        System.out.println("  - Qual è il nome del tuo primo animale domestico?");
        System.out.println("  - In che città sei nato/a?");
        System.out.println("  - Qual è il tuo cibo preferito?");
        System.out.print("\nLa tua domanda di sicurezza: ");
        String domandaSicurezza = scanner.nextLine();
        
        System.out.print("Risposta: ");
        String rispostaSicurezza = scanner.nextLine();
        
        if (gestoreUtenti.registrazione(nome, cognome, username, password, ruolo, dataNascita, 
                                       cittaDomicilio, domandaSicurezza, rispostaSicurezza)) {
            System.out.println("\n✓ Registrazione completata con successo!");
            System.out.println("Ora puoi effettuare il login.");
        } else {
            System.out.println("\n✗ Errore: username già esistente.");
        }
    }
    
    /**
     * Gestisce il login dell'utente
     */
    private static void login() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("              LOGIN");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (gestoreUtenti.login(username, password)) {
            Utente utente = gestoreUtenti.getUtenteCorrente();
            System.out.println("\n✓ Login effettuato con successo!");
            System.out.println("Benvenuto, " + utente.getNome() + "!");
            
            cittaCorrente = utente.getCittaDomicilio();
            
            if (utente.getRuolo().equals("cliente")) {
                menuCliente();
            } else {
                menuRistoratore();
            }
        } else {
            System.out.println("\n✗ Username o password errati.");
            System.out.print("\nHai dimenticato la password? (s/n): ");
            String risposta = scanner.nextLine();
            if (risposta.equalsIgnoreCase("s")) {
                recuperaPassword();
            }
        }
    }
    
    /**
     * Gestisce il recupero password tramite domanda di sicurezza
     */
    private static void recuperaPassword() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("           RECUPERO PASSWORD");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        Utente utente = gestoreUtenti.cercaUtente(username);
        if (utente == null) {
            System.out.println("\n✗ Username non trovato.");
            return;
        }
        
        if (utente.getDomandaSicurezza() == null || utente.getDomandaSicurezza().isEmpty()) {
            System.out.println("\n✗ Questo account non ha una domanda di sicurezza configurata.");
            System.out.println("Contatta l'amministratore per reimpostare la password.");
            return;
        }
        
        System.out.println("\nDomanda di sicurezza:");
        System.out.println(utente.getDomandaSicurezza());
        System.out.print("\nRisposta: ");
        String risposta = scanner.nextLine();
        
        if (gestoreUtenti.verificaRispostaSicurezza(username, risposta)) {
            System.out.println("\n✓ Risposta corretta!");
            System.out.print("\nInserisci la nuova password: ");
            String nuovaPassword = scanner.nextLine();
            
            System.out.print("Conferma la nuova password: ");
            String conferma = scanner.nextLine();
            
            if (nuovaPassword.equals(conferma)) {
                if (gestoreUtenti.reimpostaPassword(username, nuovaPassword)) {
                    System.out.println("\n✓ Password reimpostata con successo!");
                    System.out.println("Ora puoi effettuare il login con la nuova password.");
                } else {
                    System.out.println("\n✗ Errore nella reimpostazione della password.");
                }
            } else {
                System.out.println("\n✗ Le password non coincidono.");
            }
        } else {
            System.out.println("\n✗ Risposta errata.");
        }
    }
    
    /**
     * Menu per utenti ospiti (non registrati)
     */
    private static void modalitaOspite() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("           MODALITÀ OSPITE");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Inserisci la città in cui vuoi cercare ristoranti: ");
        cittaCorrente = scanner.nextLine();
        
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("  MENU OSPITE - " + cittaCorrente);
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("1) Visualizza ristoranti vicini");
            System.out.println("2) Cerca ristoranti");
            System.out.println("3) Visualizza dettagli ristorante");
            System.out.println("0) Torna al menu principale");
            System.out.println("═══════════════════════════════════════════════════════");
            
            int scelta = leggiIntero("Scelta: ");
            
            switch (scelta) {
                case 1:
                    visualizzaRistorantiVicini(cittaCorrente);
                    break;
                case 2:
                    cercaRistoranti();
                    break;
                case 3:
                    visualizzaDettagliRistorante();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Scelta non valida!");
            }
        }
    }
    
    /**
     * Menu per clienti registrati
     */
    private static void menuCliente() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("  MENU CLIENTE - " + utente.getUsername());
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("1) Visualizza ristoranti vicini");
            System.out.println("2) Cerca ristoranti");
            System.out.println("3) Visualizza dettagli ristorante");
            System.out.println("4) Visualizza ristoranti preferiti");
            System.out.println("5) Gestisci recensioni");
            System.out.println("6) Profilo");
            System.out.println("7) Cambia password");
            System.out.println("0) Logout");
            System.out.println("═══════════════════════════════════════════════════════");
            
            int scelta = leggiIntero("Scelta: ");
            
            switch (scelta) {
                case 1:
                    visualizzaRistorantiVicini(cittaCorrente);
                    break;
                case 2:
                    cercaRistoranti();
                    break;
                case 3:
                    visualizzaDettagliRistoranteCliente();
                    break;
                case 4:
                    visualizzaPreferiti();
                    break;
                case 5:
                    menuRecensioniCliente();
                    break;
                case 6:
                    visualizzaProfilo();
                    break;
                case 7:
                    cambiaPassword();
                    break;
                case 0:
                    gestoreUtenti.logout();
                    System.out.println("Logout effettuato.");
                    return;
                default:
                    System.out.println("Scelta non valida!");
            }
        }
    }
    
    /**
     * Menu per ristoratori
     */
    private static void menuRistoratore() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("  MENU RISTORATORE - " + utente.getUsername());
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("1) Aggiungi ristorante");
            System.out.println("2) Visualizza i miei ristoranti");
            System.out.println("3) Visualizza riepilogo recensioni");
            System.out.println("4) Visualizza e rispondi alle recensioni");
            System.out.println("5) Profilo");
            System.out.println("6) Cambia password");
            System.out.println("0) Logout");
            System.out.println("═══════════════════════════════════════════════════════");
            
            int scelta = leggiIntero("Scelta: ");
            
            switch (scelta) {
                case 1:
                    aggiungiRistorante();
                    break;
                case 2:
                    visualizzaMieiRistoranti();
                    break;
                case 3:
                    visualizzaRiepilogoRecensioni();
                    break;
                case 4:
                    visualizzaERispondiRecensioni();
                    break;
                case 5:
                    visualizzaProfilo();
                    break;
                case 6:
                    cambiaPassword();
                    break;
                case 0:
                    gestoreUtenti.logout();
                    System.out.println("Logout effettuato.");
                    return;
                default:
                    System.out.println("Scelta non valida!");
            }
        }
    }
    
    // ===============================================================
    // FUNZIONI COMUNI
    // ===============================================================
    
    /**
     * Visualizza i ristoranti vicini a una città
     */
    private static void visualizzaRistorantiVicini(String citta) {
        List<Ristorante> ristoranti = gestoreRistoranti.cercaPerCitta(citta);
        
        if (ristoranti.isEmpty()) {
            System.out.println("\nNessun ristorante trovato in questa città.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  RISTORANTI A " + citta.toUpperCase());
        System.out.println("═══════════════════════════════════════════════════════");
        
        for (int i = 0; i < ristoranti.size(); i++) {
            System.out.println((i + 1) + ") " + ristoranti.get(i));
        }
    }
    
    /**
     * Funzione di ricerca ristoranti con criteri multipli
     */
    private static void cercaRistoranti() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("           CERCA RISTORANTI");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Città (obbligatoria): ");
        String citta = scanner.nextLine();
        
        System.out.print("Tipo di cucina (opzionale, INVIO per saltare): ");
        String tipoCucina = scanner.nextLine();
        tipoCucina = tipoCucina.isEmpty() ? null : tipoCucina;
        
        Double prezzoMin = leggiDoubleOpzionale("Prezzo minimo (opzionale, INVIO per saltare): ");
        
        Double prezzoMax = leggiDoubleOpzionale("Prezzo massimo (opzionale, INVIO per saltare): ");
        
        System.out.print("Solo con delivery? (s/n, INVIO per saltare): ");
        String deliveryStr = scanner.nextLine();
        Boolean delivery = deliveryStr.isEmpty() ? null : deliveryStr.equalsIgnoreCase("s");
        
        System.out.print("Solo con prenotazione? (s/n, INVIO per saltare): ");
        String prenotazioneStr = scanner.nextLine();
        Boolean prenotazione = prenotazioneStr.isEmpty() ? null : prenotazioneStr.equalsIgnoreCase("s");
        
        Double stelleMin = leggiDoubleOpzionale("Stelle minime (1-5, opzionale, INVIO per saltare): ");
        
        List<Ristorante> risultati = gestoreRistoranti.cercaRistorante(citta, tipoCucina, prezzoMin, 
                                                                       prezzoMax, delivery, prenotazione, stelleMin);
        
        if (risultati.isEmpty()) {
            System.out.println("\nNessun ristorante trovato con questi criteri.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  RISULTATI RICERCA (" + risultati.size() + ")");
        System.out.println("═══════════════════════════════════════════════════════");
        
        for (int i = 0; i < risultati.size(); i++) {
            System.out.println((i + 1) + ") " + risultati.get(i));
        }
    }
    
    /**
     * Visualizza dettagli di un ristorante (per ospiti)
     */
     private static void visualizzaDettagliRistorante() {
        int idRistorante = leggiIntero("\nInserisci l'ID del ristorante: ");
        Ristorante ristorante = gestoreRistoranti.cercaRistorantePerId(idRistorante);
        
        if (ristorante == null) {
            System.out.println("Ristorante non trovato.");
            return;
        }

        System.out.println("\n"+ristorante.toStringDettagliato());
        gestoreRecensioni.visualizzaRecensioni(idRistorante);
     }
    
    
    /**
     * Visualizza dettagli ristorante con opzioni per clienti
     */
    private static void visualizzaDettagliRistoranteCliente() {
        int idRistorante = leggiIntero("\nInserisci l'ID del ristorante: ");
        Ristorante ristorante = gestoreRistoranti.cercaRistorantePerId(idRistorante);
      
        if (ristorante == null) {
            System.out.println("Ristorante non trovato.");
            return;
        }
        
        System.out.println("\n" + ristorante.toStringDettagliato());
        
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        // Menu azioni
        System.out.println("\nAzioni disponibili:");
        System.out.println("1) Aggiungi/Rimuovi dai preferiti");
        System.out.println("2) Visualizza recensioni");
        System.out.println("3) Aggiungi recensione");
        System.out.println("0) Torna indietro");
        
        int scelta = leggiIntero("Scelta: ");
        
        switch (scelta) {
            case 1:
               // TODO: Gestione preferiti tramite DAO
                System.out.println("Funzionalità preferiti in fase di migrazione su Database.");
                break;
            case 2:
                gestoreRecensioni.visualizzaRecensioni(idRistorante);
                break;
            case 3:
                aggiungiRecensione(idRistorante);
                break;
        }
    }
    
    /**
     * Visualizza profilo utente
     */
    private static void visualizzaProfilo() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("              IL TUO PROFILO");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.print(utente.info());
    }
    
    /**
     * Permette all'utente di cambiare la propria password
     */
    private static void cambiaPassword() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("            CAMBIA PASSWORD");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Password attuale: ");
        String passwordVecchia = scanner.nextLine();
        
        System.out.print("Nuova password: ");
        String nuovaPassword = scanner.nextLine();
        
        System.out.print("Conferma nuova password: ");
        String conferma = scanner.nextLine();
        
        // Verifica che le nuove password coincidano
        if (!nuovaPassword.equals(conferma)) {
            System.out.println("\n✗ Le password non coincidono.");
            return;
        }
        
        // Verifica che la nuova password sia diversa dalla vecchia
        if (nuovaPassword.equals(passwordVecchia)) {
            System.out.println("\n✗ La nuova password deve essere diversa dalla vecchia.");
            return;
        }
        
        // Verifica lunghezza minima password
        if (nuovaPassword.length() < 6) {
            System.out.println("\n✗ La password deve essere di almeno 6 caratteri.");
            return;
        }
        
        // Tenta di cambiare la password
        if (gestoreUtenti.cambiaPassword(passwordVecchia, nuovaPassword)) {
            System.out.println("\n✓ Password cambiata con successo!");
            System.out.println("Usa la nuova password al prossimo login.");
        } else {
            System.out.println("\n✗ Password attuale errata. Riprova.");
        }
    }
    
    // ===============================================================
    // FUNZIONI CLIENTE
    // ===============================================================
    
    /**
     * Visualizza ristoranti preferiti
     */
    private static void visualizzaPreferiti() {
         // TODO: Gestione preferiti tramite DAO
        System.out.println("\nFunzionalità preferiti in fase di migrazione su Database.");
    }

    /**
     * Menu gestione recensioni cliente
     */
    private static void menuRecensioniCliente() {
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("           GESTIONE RECENSIONI");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("1) Visualizza le mie recensioni");
            System.out.println("2) Aggiungi recensione");
            System.out.println("3) Modifica recensione");
            System.out.println("4) Elimina recensione");
            System.out.println("0) Torna indietro");
            System.out.println("═══════════════════════════════════════════════════════");
            
            int scelta = leggiIntero("Scelta: ");
            
            switch (scelta) {
                case 1:
                    visualizzaMieRecensioni();
                    break;
                case 2:
                    System.out.print("Nome del ristorante: ");
                    String nome = scanner.nextLine();
                    System.out.print("Città: ");
                    String citta = scanner.nextLine();
                    String id = nome.replaceAll("\\s+", "_") + "_" + citta.replaceAll("\\s+", "_");
                    aggiungiRecensione(Integer.parseInt(id));
                    break;
                case 3:
                    modificaRecensione();
                    break;
                case 4:
                    eliminaRecensione();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Scelta non valida!");
            }
        }
    }
    
    /**
     * Visualizza le recensioni del cliente
     */
    private static void visualizzaMieRecensioni() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        List<Recenzione> recensioni = gestoreRecensioni.getRecensioniCliente(utente.getId());
        
        if (recensioni.isEmpty()) {
            System.out.println("\nNon hai ancora lasciato recensioni.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  LE TUE RECENSIONI");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        for (Recenzione recensione : recensioni) {
            Ristorante r = gestoreRistoranti.cercaRistorantePerId(recensione.getIdRistorante());
            if (r != null) {
                System.out.println("Ristorante: " + r.getNome() + " (" + r.getCitta() + ")");
            }
            System.out.println(recensione);
        }
    }
    
    /**
     * Aggiunge una recensione
     */
        private static void aggiungiRecensione(int idRistorante) {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        if (gestoreRecensioni.hasRecensione(utente.getId(), idRistorante)) {
            System.out.println("\nHai già recensito questo ristorante. Puoi modificare la tua recensione.");
            return;
        }
        int stelle = leggiIntero("Numero di stelle (1-5): ");
        if (stelle < 1 || stelle > 5) {
            System.out.println("Numero di stelle non valido.");
            return;
        }
        
        System.out.print("Testo della recensione: ");
        String testo = scanner.nextLine();
        
        if (gestoreRecensioni.aggiungiRecensione(utente.getId(), idRistorante, stelle, testo)) {
            System.out.println("\n✓ Recensione aggiunta con successo!");
        } else {
            System.out.println("\n✗ Errore nell'aggiunta della recensione.");
        }
    }
    
    /**
     * Modifica una recensione
     */
    private static void modificaRecensione() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        int idRistorante = leggiIntero("Inserisci ID del ristorante: ");
        
        if (!gestoreRecensioni.hasRecensione(utente.getId(), idRistorante)) {
            System.out.println("\nNon hai recensito questo ristorante.");
            return;
        }
        
        int nuoveStelle = leggiIntero("Nuovo numero di stelle (1-5): ");
        if (nuoveStelle < 1 || nuoveStelle > 5) {
            System.out.println("Numero di stelle non valido.");
            return;
        }
        
        System.out.print("Nuovo testo della recensione: ");
        String nuovoTesto = scanner.nextLine();
        
        if (gestoreRecensioni.modificaRecensione(utente.getId(), idRistorante, nuoveStelle, nuovoTesto)) {
            System.out.println("\n✓ Recensione modificata con successo!");
        } else {
            System.out.println("\n✗ Errore nella modifica della recensione.");
        }
    }
    
    /**
     * Elimina una recensione
     */
    private static void eliminaRecensione() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        int idRistorante = leggiIntero("Inserisci ID del ristorante: ");
        
        if (!gestoreRecensioni.hasRecensione(utente.getId(), idRistorante)) {
            System.out.println("\nNon hai recensito questo ristorante.");
            return;
        }
        
        System.out.print("Sei sicuro di voler eliminare questa recensione? (s/n): ");
        String conferma = scanner.nextLine();
        
        if (conferma.equalsIgnoreCase("s")) {
            if (gestoreRecensioni.eliminaRecensione(utente.getId(), idRistorante)) {
                System.out.println("\n✓ Recensione eliminata con successo!");
            } else {
                System.out.println("\n✗ Errore nell'eliminazione della recensione.");
            }
        }
    }
    
    // ===============================================================
    // FUNZIONI RISTORATORE
    // ===============================================================
    
    /**
     * Aggiunge un nuovo ristorante
     */
    private static void aggiungiRistorante() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("           AGGIUNGI RISTORANTE");
        System.out.println("═══════════════════════════════════════════════════════");
        
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        
        System.out.print("Nazione: ");
        String nazione = scanner.nextLine();
        
        System.out.print("Città: ");
        String citta = scanner.nextLine();
        
        System.out.print("Indirizzo: ");
        String indirizzo = scanner.nextLine();
        
        double latitudine = leggiDoubleObbligatorio("Latitudine: ");
        
        double longitudine = leggiDoubleObbligatorio("Longitudine: ");
        
        System.out.print("Tipo di cucina: ");
        String tipoCucina = scanner.nextLine();
        
        double prezzoMedio = leggiDoubleObbligatorio("Prezzo medio (€): ");
        
        System.out.print("Servizio delivery (s/n): ");
        boolean delivery = scanner.nextLine().equalsIgnoreCase("s");
        
        System.out.print("Prenotazione online (s/n): ");
        boolean prenotazione = scanner.nextLine().equalsIgnoreCase("s");
        
        Ristorante ristorante = new Ristorante(nome, nazione, citta, indirizzo, latitudine, 
                                              longitudine, tipoCucina, prezzoMedio, delivery, 
                                              prenotazione, utente.getId());
        
        gestoreRistoranti.aggiungiRistorante(ristorante);
        System.out.println("\n✓ Ristorante aggiunto con successo!");
    }
    
    /**
     * Visualizza i ristoranti del ristoratore
     */
    private static void visualizzaMieiRistoranti() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        List<Ristorante> ristoranti = gestoreRistoranti.getRistorantiPerRistoratore(utente.getId());
        
        if (ristoranti.isEmpty()) {
            System.out.println("\nNon hai ancora aggiunto ristoranti.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  I TUOI RISTORANTI");
        System.out.println("═══════════════════════════════════════════════════════");
        
        for (int i = 0; i < ristoranti.size(); i++) {
            System.out.println((i + 1) + ") " + ristoranti.get(i));
        }
    }
    
    /**
     * Visualizza riepilogo recensioni
     */
    private static void visualizzaRiepilogoRecensioni() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        gestoreRecensioni.visualizzaRiepilogo(utente.getId(), gestoreRistoranti);
    }
    
    /**
     * Visualizza e risponde alle recensioni
     */
    private static void visualizzaERispondiRecensioni() {
        Utente utente = gestoreUtenti.getUtenteCorrente();
        List<Ristorante> ristoranti = gestoreRistoranti.getRistorantiPerRistoratore(utente.getId());
        
        if (ristoranti.isEmpty()) {
            System.out.println("\nNon hai ancora aggiunto ristoranti.");
            return;
        }
        
        System.out.println("\nSeleziona un ristorante:");
        for (int i = 0; i < ristoranti.size(); i++) {
            System.out.println((i + 1) + ") " + ristoranti.get(i).getNome());
        }
        
        int scelta = leggiIntero("Scelta: ") - 1;
        if (scelta < 0 || scelta >= ristoranti.size()) {
            System.out.println("Scelta non valida.");
            return;
        }
        
        Ristorante ristorante = ristoranti.get(scelta);
        List<Recenzione> recensioni = gestoreRecensioni.getRecensioniRistorante(ristorante.getId());
        
        if (recensioni.isEmpty()) {
            System.out.println("\nNessuna recensione per questo ristorante.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  RECENSIONI - " + ristorante.getNome());
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        for (int i = 0; i < recensioni.size(); i++) {
            Recenzione rec = recensioni.get(i);
            System.out.println((i + 1) + ") " + rec.toStringCompatto());
            if (!rec.hasRisposta()) {
                System.out.println("   [Nessuna risposta]");
            }
        }
        
        System.out.print("\nVuoi rispondere a una recensione? (numero, 0 per uscire): ");
        int numRec = leggiIntero("Scelta: ") - 1;
        
        if (numRec >= 0 && numRec < recensioni.size()) {
            Recenzione recensione = recensioni.get(numRec);
            
            if (recensione.hasRisposta()) {
                System.out.println("\nHai già risposto a questa recensione:");
                System.out.println(recensione.getRispostaRistoratore());
                return;
            }
            
            System.out.println("\nRecensione completa:");
            System.out.println(recensione);
            
            System.out.print("La tua risposta: ");
            String risposta = scanner.nextLine();
            
            if (gestoreRecensioni.rispondiRecensione(recensione.getId(), risposta)) {
                System.out.println("\n✓ Risposta aggiunta con successo!");
            } else {
                System.out.println("\n✗ Errore nell'aggiunta della risposta.");
            }
        }
    }
   
    // ===============================================================
    // UTILITY
    // ===============================================================
    
    /**
     * Legge un intero da input con gestione errori
     */
    private static int leggiIntero(String messaggio) {
        while (true) {
            try {
                System.out.print(messaggio);
                int valore = Integer.parseInt(scanner.nextLine());
                return valore;
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }
        }
    }

    /**
     * Legge un double opzionale da input con gestione errori
     */
    private static Double leggiDoubleOpzionale(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }
        }
    }

    /**
     * Legge un double obbligatorio da input con gestione errori
     */
    private static double leggiDoubleObbligatorio(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }
        }
    }
}
