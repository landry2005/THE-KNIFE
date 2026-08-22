package theknife.gestione;

import theknife.model.Utente;
import theknife.util.PasswordUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe per la gestione degli utenti (clienti e ristoratori).
 * Gestisce registrazione, autenticazione e persistenza su file.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class GestoreUtenti {
   
    private List<Utente> utenti;
    private Utente utenteCorrente;
    
    /**
     * Costruttore del gestore utenti
     */
    public GestoreUtenti() {
        this.utenti = new ArrayList<>();
        this.utenteCorrente = null;
    }
    
    /**
     * Registra un nuovo utente
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param username Username scelto
     * @param password Password in chiaro
     * @param ruolo Ruolo (cliente o ristoratore)
     * @param dataNascita Data di nascita (può essere null)
     * @param luogoDomicilio Luogo di domicilio
     * @param domandaSicurezza Domanda di sicurezza per recupero password
     * @param rispostaSicurezza Risposta alla domanda di sicurezza
     * @return true se la registrazione è andata a buon fine, false se username già esistente
     */
    public boolean registrazione(String nome, String cognome, String username, String password, 
                                 String ruolo, LocalDate dataNascita, String luogoDomicilio,
                                 String domandaSicurezza, String rispostaSicurezza) {
        // Verifica se username già esiste
        if (cercaUtente(username) != null) {
            return false;
        }
        
        // Cifra la password e la risposta di sicurezza
        String passwordHash = PasswordUtil.cifraPassword(password);
        String rispostaHash = PasswordUtil.cifraPassword(rispostaSicurezza.toLowerCase().trim());
        
        // Crea nuovo utente
        Utente nuovoUtente = new Utente(nome, cognome, username, passwordHash, ruolo, dataNascita, 
                                       luogoDomicilio, domandaSicurezza, rispostaHash);
        utenti.add(nuovoUtente);
        
        
        return true;
    }
    
    /**
     * Recupera password tramite domanda di sicurezza
     * @param username Username dell'utente
     * @param risposta Risposta alla domanda di sicurezza
     * @return true se la risposta è corretta, false altrimenti
     */
    public boolean verificaRispostaSicurezza(String username, String risposta) {
        Utente utente = cercaUtente(username);
        if (utente != null && utente.getRispostaSicurezza() != null) {
            String rispostaHash = PasswordUtil.cifraPassword(risposta.toLowerCase().trim());
            return rispostaHash.equals(utente.getRispostaSicurezza());
        }
        return false;
    }
    
    /**
     * Reimposta la password dell'utente
     * @param username Username dell'utente
     * @param nuovaPassword Nuova password in chiaro
     * @return true se reimpostata con successo, false altrimenti
     */
    public boolean reimpostaPassword(String username, String nuovaPassword) {
        Utente utente = cercaUtente(username);
        if (utente != null) {
            String passwordHash = PasswordUtil.cifraPassword(nuovaPassword);
            utente.setPasswordHash(passwordHash);
            return true;
        }
        return false;
    }
    
    /**
     * Cambia la password dell'utente corrente
     * @param passwordVecchia Password vecchia in chiaro (per verifica)
     * @param nuovaPassword Nuova password in chiaro
     * @return true se cambiata con successo, false se password vecchia errata
     */
    public boolean cambiaPassword(String passwordVecchia, String nuovaPassword) {
        if (utenteCorrente == null) {
            return false;
        }
        
        // Verifica che la password vecchia sia corretta
        if (!PasswordUtil.verificaPassword(passwordVecchia, utenteCorrente.getPasswordHash())) {
            return false;
        }
        
        // Imposta nuova password
        String passwordHash = PasswordUtil.cifraPassword(nuovaPassword);
        utenteCorrente.setPasswordHash(passwordHash);
        return true;
    }
    
    /**
     * Effettua il login di un utente
     * @param username Username
     * @param password Password in chiaro
     * @return true se login riuscito, false altrimenti
     */
    public boolean login(String username, String password) {
        Utente utente = cercaUtente(username);
        if (utente != null && PasswordUtil.verificaPassword(password, utente.getPasswordHash())) {
            this.utenteCorrente = utente;
            return true;
        }
        return false;
    }
    
    /**
     * Effettua il logout dell'utente corrente
     */
    public void logout() {
        this.utenteCorrente = null;
    }
    
    /**
     * Restituisce l'utente attualmente loggato
     * @return L'utente corrente o null se nessuno è loggato
     */
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }
    
    /**
     * Verifica se c'è un utente loggato
     * @return true se c'è un utente loggato, false altrimenti
     */
    public boolean isLoggato() {
        return utenteCorrente != null;
    }
    
    /**
     * Cerca un utente per username
     * @param username Username da cercare
     * @return L'utente trovato o null
     */
    public Utente cercaUtente(String username) {
        for (Utente utente : utenti) {
            if (utente.getUsername().equalsIgnoreCase(username)) {
                return utente;
            }
        }
        return null;
    }
    
    /**
     * Restituisce la lista di tutti gli utenti
     * @return Lista degli utenti
     */
    public List<Utente> getUtenti() {
        return new ArrayList<>(utenti);
    }
    
    // ===============================================================
    // FUNZIONALITÀ AMMINISTRATORE
    // ===============================================================
    
    /**
     * Reimposta la password di un utente (solo admin)
     * @param username Username dell'utente
     * @param nuovaPassword Nuova password in chiaro
     * @return true se reimpostata con successo
     */
    public boolean adminReimpostaPassword(String username, String nuovaPassword) {
        return reimpostaPassword(username, nuovaPassword);
    }
    
    /**
     * Elimina un utente (solo admin)
     * @param username Username dell'utente da eliminare
     * @return true se eliminato con successo
     */
    public boolean adminEliminaUtente(String username) {
        Utente utente = cercaUtente(username);
        if (utente != null) {
            utenti.remove(utente);
            return true;
        }
        return false;
    }
    
    /**
     * Visualizza statistiche sugli utenti (solo admin)
     * @return Stringa con le statistiche
     */
    public String adminStatistiche() {
        int totaleUtenti = utenti.size();
        int clienti = 0;
        int ristoratori = 0;
        int conDomandaSicurezza = 0;
        
        for (Utente u : utenti) {
            if (u.getRuolo().equals("cliente")) {
                clienti++;
            } else {
                ristoratori++;
            }
            if (u.getDomandaSicurezza() != null && !u.getDomandaSicurezza().isEmpty()) {
                conDomandaSicurezza++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("         STATISTICHE UTENTI\n");
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append(String.format("Totale utenti:            %d\n", totaleUtenti));
        sb.append(String.format("Clienti:                  %d\n", clienti));
        sb.append(String.format("Ristoratori:              %d\n", ristoratori));
        sb.append(String.format("Con domanda sicurezza:    %d\n", conDomandaSicurezza));
        sb.append(String.format("Senza domanda sicurezza:  %d\n", totaleUtenti - conDomandaSicurezza));
        sb.append("═══════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Imposta domanda e risposta di sicurezza per un utente (solo admin)
     * @param username Username dell'utente
     * @param domanda Domanda di sicurezza
     * @param risposta Risposta alla domanda
     * @return true se impostata con successo
     */
    public boolean adminImpostaDomandaSicurezza(String username, String domanda, String risposta) {
        Utente utente = cercaUtente(username);
        if (utente != null) {
            utente.setDomandaSicurezza(domanda);
            String rispostaCifrata = PasswordUtil.cifraPassword(risposta.toLowerCase().trim());
            utente.setRispostaSicurezza(rispostaCifrata);
            return true;
        }
        return false;
    }
    
    /**
     * Modifica il ruolo di un utente (solo admin)
     * @param username Username dell'utente
     * @param nuovoRuolo Nuovo ruolo ("cliente" o "ristoratore")
     * @return true se modificato con successo
     */
    public boolean adminModificaRuolo(String username, String nuovoRuolo) {
        Utente utente = cercaUtente(username);
        if (utente != null && (nuovoRuolo.equals("cliente") || nuovoRuolo.equals("ristoratore"))) {
            utente.setRuolo(nuovoRuolo);
            return true;
        }
        return false;
    }
}

