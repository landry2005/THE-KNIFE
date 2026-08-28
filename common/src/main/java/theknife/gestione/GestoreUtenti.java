package theknife.gestione;

import theknife.model.Ristorante;
import theknife.model.Utente;
import theknife.util.PasswordUtil;
import theknife.dao.UtenteDAO;
import theknife.dao.PreferitoDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe per la gestione degli utenti (clienti e ristoratori).
 * Gestisce registrazione, autenticazione e persistenza su file.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class GestoreUtenti {
   
    private Utente utenteCorrente;
    private UtenteDAO utenteDAO = new UtenteDAO();
    
    /**
     * Costruttore del gestore utenti
     */
    public GestoreUtenti() {
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
        // Verifica se username già esiste nel DB
        if (utenteDAO.trovaPerUsername(username) != null) {
            return false;
        }
        
        // Cifra la password e la risposta di sicurezza
        String passwordHash = PasswordUtil.cifraPassword(password);
        String rispostaHash = PasswordUtil.cifraPassword(rispostaSicurezza.toLowerCase().trim());
        
        // Crea nuovo utente
        Utente nuovoUtente = new Utente(nome, cognome, username, passwordHash, ruolo, dataNascita, 
                                       luogoDomicilio, domandaSicurezza, rispostaHash);
        return utenteDAO.salvaUtente(nuovoUtente);
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
            //Aggiorna nel db
            return utenteDAO.aggiornaPassword(username, passwordHash);
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
        //Aggiorna nel db
        return utenteDAO.aggiornaPassword(utenteCorrente.getUsername(), passwordHash);
    }
    
    /**
     * Effettua il login di un utente
     * @param username Username
     * @param password Password in chiaro
     * @return true se login riuscito, false altrimenti
     */
    public boolean login(String username, String password) {
        Utente utente = utenteDAO.trovaPerUsername(username);
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
        return utenteDAO.trovaPerUsername(username);
    }
    
    /**
     * Restituisce la lista di tutti gli utenti
     * @return Lista degli utenti
     */
    public List<Utente> getUtenti() {
        return utenteDAO.getTuttiUtenti();
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
      return utenteDAO.eliminaUtente(username);
    }
    
    /**
     * Visualizza statistiche sugli utenti (solo admin)
     * @return Stringa con le statistiche
     */
    public String adminStatistiche() {
        //Prende gli utenti dal db non più dalla lista in memoria
        List<Utente> utentiDb = utenteDAO.getTuttiUtenti();
        int totaleUtenti = utentiDb.size();
        int clienti = 0;
        int ristoratori = 0;
        int conDomandaSicurezza = 0;
        
        for (Utente u : utentiDb) {
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
            String rispostaCifrata = PasswordUtil.cifraPassword(risposta.toLowerCase().trim());
            // Aggiorna nel database!
            return utenteDAO.aggiornaDomandaSicurezza(username, domanda, rispostaCifrata);
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
           return utenteDAO.aggiornaRuolo(username, nuovoRuolo);
        }
        return false;
    }

    // ===============================================================
    // GESTIONE PREFERITI
    // ===============================================================

    private PreferitoDAO preferitoDAO = new PreferitoDAO();

    public boolean aggiungiPreferito(int idRistorante) {
        if (utenteCorrente == null) {
            return false; // Nessun utente loggato
        }
        return preferitoDAO.aggiungiPreferito(utenteCorrente.getId(), idRistorante);
    }

    public boolean rimuoviPreferito(int idRistorante) {
        if (utenteCorrente == null) {
            return false; // Nessun utente loggato
        }
        return preferitoDAO.rimuoviPreferito(utenteCorrente.getId(), idRistorante);
    }

    public boolean isPreferito(int idRistorante) {
        if (utenteCorrente == null) {
            return false; // Nessun utente loggato
        }
        return preferitoDAO.isPreferito(utenteCorrente.getId(), idRistorante);
    }

    public List<Ristorante> getPreferiti() {
        if (utenteCorrente == null) {
            return new ArrayList<>(); // Nessun utente loggato
        }
        return preferitoDAO.getPreferiti(utenteCorrente.getId());
    }

}

