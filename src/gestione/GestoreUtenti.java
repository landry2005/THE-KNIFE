package gestione;

import model.Utente;
import util.PasswordUtil;

import java.io.*;
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
    private static final String FILE_UTENTI = "data/utenti.dati";
    private List<Utente> utenti;
    private Utente utenteCorrente;
    
    /**
     * Costruttore del gestore utenti
     */
    public GestoreUtenti() {
        this.utenti = new ArrayList<>();
        this.utenteCorrente = null;
        caricaUtenti();
    }
    
    /**
     * Carica gli utenti dal file CSV
     */
    private void caricaUtenti() {
        File file = new File(FILE_UTENTI);
        if (!file.exists()) {
            // Crea la directory se non esiste
            file.getParentFile().mkdirs();
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean primaRiga = true;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (primaRiga && trimmed.toLowerCase().startsWith("nome,")) {
                    primaRiga = false;
                    continue;
                }
                primaRiga = false;
                String[] parti = line.split(",", -1); // -1 per mantenere campi vuoti
                if (parti.length >= 5) {
                    String nome = parti[0];
                    String cognome = parti[1];
                    String username = parti[2];
                    String password = parti[3]; // già cifrata
                    String ruolo = parti[4];
                    LocalDate dataNascita = null;
                    if (parti.length > 5 && !parti[5].isEmpty()) {
                        dataNascita = LocalDate.parse(parti[5]);
                    }
                    String luogoDomicilio = parti.length > 6 ? parti[6] : "";
                    String domandaSicurezza = parti.length > 7 ? parti[7] : "";
                    String rispostaSicurezza = parti.length > 8 ? parti[8] : "";
                    
                    Utente utente = new Utente(nome, cognome, username, password, ruolo, dataNascita, 
                                              luogoDomicilio, domandaSicurezza, rispostaSicurezza);
                    
                    // Carica preferiti se presenti (ora sono al campo 9)
                    if (parti.length > 9 && !parti[9].isEmpty()) {
                        String[] preferiti = parti[9].split(";");
                        for (String pref : preferiti) {
                            utente.aggiungiPreferito(pref);
                        }
                    }
                    
                    utenti.add(utente);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento degli utenti: " + e.getMessage());
        }
    }
    
    /**
     * Salva gli utenti nel file CSV
     */
    public void salvaUtenti() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_UTENTI))) {
            bw.write("nome,cognome,userId,password,tipoUtente,dataNascita,luogoDomicilio,domandaSicurezza,rispostaSicurezza,preferiti");
            bw.newLine();
            for (Utente utente : utenti) {
                bw.write(utente.getNome() + ",");
                bw.write(utente.getCognome() + ",");
                bw.write(utente.getUsername() + ",");
                bw.write(utente.getPassword() + ",");
                bw.write(utente.getRuolo() + ",");
                bw.write((utente.getDataNascita() != null ? utente.getDataNascita().toString() : "") + ",");
                bw.write(utente.getLuogoDomicilio() + ",");
                bw.write((utente.getDomandaSicurezza() != null ? utente.getDomandaSicurezza() : "") + ",");
                bw.write((utente.getRispostaSicurezza() != null ? utente.getRispostaSicurezza() : "") + ",");
                
                // Salva preferiti
                if (!utente.getRistoratiPreferiti().isEmpty()) {
                    bw.write(String.join(";", utente.getRistoratiPreferiti()));
                }
                
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio degli utenti: " + e.getMessage());
        }
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
        String passwordCifrata = PasswordUtil.cifraPassword(password);
        String rispostaCifrata = PasswordUtil.cifraPassword(rispostaSicurezza.toLowerCase().trim());
        
        // Crea nuovo utente
        Utente nuovoUtente = new Utente(nome, cognome, username, passwordCifrata, ruolo, dataNascita, 
                                       luogoDomicilio, domandaSicurezza, rispostaCifrata);
        utenti.add(nuovoUtente);
        
        // Salva su file
        salvaUtenti();
        
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
            String rispostaCifrata = PasswordUtil.cifraPassword(risposta.toLowerCase().trim());
            return rispostaCifrata.equals(utente.getRispostaSicurezza());
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
            String passwordCifrata = PasswordUtil.cifraPassword(nuovaPassword);
            utente.setPassword(passwordCifrata);
            salvaUtenti();
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
        if (!PasswordUtil.verificaPassword(passwordVecchia, utenteCorrente.getPassword())) {
            return false;
        }
        
        // Imposta nuova password
        String passwordCifrata = PasswordUtil.cifraPassword(nuovaPassword);
        utenteCorrente.setPassword(passwordCifrata);
        salvaUtenti();
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
        if (utente != null && PasswordUtil.verificaPassword(password, utente.getPassword())) {
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
            salvaUtenti();
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
            salvaUtenti();
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
            salvaUtenti();
            return true;
        }
        return false;
    }
}

