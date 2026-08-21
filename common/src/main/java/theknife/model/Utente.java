package theknife.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un utente della piattaforma TheKnife.
 * Un utente può essere un cliente o un ristoratore.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class Utente {
    
    private String nome;
    private String cognome;
    private String username;
    private String password; // cifrata
    private String ruolo; // "cliente" o "ristoratore"
    private LocalDate dataNascita; // facoltativa
    private String luogoDomicilio;
    private List<String> ristoratiPreferiti; // lista ID ristoranti preferiti (solo per clienti)
    private String domandaSicurezza; // domanda per recupero password
    private String rispostaSicurezza; // risposta cifrata per recupero password

    /**
     * Costruttore completo della classe Utente
     */
    public Utente(String nome, String cognome, String username, String password, 
                  String ruolo, LocalDate dataNascita, String luogoDomicilio,
                  String domandaSicurezza, String rispostaSicurezza) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.luogoDomicilio = luogoDomicilio;
        this.ristoratiPreferiti = new ArrayList<>();
        this.domandaSicurezza = domandaSicurezza;
        this.rispostaSicurezza = rispostaSicurezza;
    }
    
    /**
     * Costruttore semplificato per compatibilità
     */
    public Utente(String nome, String cognome, String username, String password) {
        this(nome, cognome, username, password, "cliente", null, "", "", "");
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCognome() {
        return cognome;
    }
    
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
    
    public LocalDate getDataNascita() {
        return dataNascita;
    }
    
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }
    
    public String getLuogoDomicilio() {
        return luogoDomicilio;
    }
    
    public void setLuogoDomicilio(String luogoDomicilio) {
        this.luogoDomicilio = luogoDomicilio;
    }
    
    public List<String> getRistoratiPreferiti() {
        return ristoratiPreferiti;
    }
    
    public void setRistoratiPreferiti(List<String> ristoratiPreferiti) {
        this.ristoratiPreferiti = ristoratiPreferiti;
    }
    
    /**
     * Aggiunge un ristorante alla lista dei preferiti
     */
    public boolean aggiungiPreferito(String idRistorante) {
        if (!ristoratiPreferiti.contains(idRistorante)) {
            ristoratiPreferiti.add(idRistorante);
            return true;
        }
        return false;
    }
    
    /**
     * Rimuove un ristorante dalla lista dei preferiti
     */
    public boolean rimuoviPreferito(String idRistorante) {
        return ristoratiPreferiti.remove(idRistorante);
    }
    
    /**
     * Verifica se un ristorante è tra i preferiti
     */
    public boolean isPreferito(String idRistorante) {
        return ristoratiPreferiti.contains(idRistorante);
    }
    
    public String getDomandaSicurezza() {
        return domandaSicurezza;
    }
    
    public void setDomandaSicurezza(String domandaSicurezza) {
        this.domandaSicurezza = domandaSicurezza;
    }
    
    public String getRispostaSicurezza() {
        return rispostaSicurezza;
    }
    
    public void setRispostaSicurezza(String rispostaSicurezza) {
        this.rispostaSicurezza = rispostaSicurezza;
    }

    @Override
    public String toString() {
        return String.join(",", nome, cognome, username, password, ruolo, 
            dataNascita != null ? dataNascita.toString() : "", 
            luogoDomicilio,
            domandaSicurezza != null ? domandaSicurezza : "",
            rispostaSicurezza != null ? rispostaSicurezza : "");
    }
    
    /**
     * Restituisce informazioni leggibili sull'utente
     */
    public String info() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Utente: %s %s\n", nome, cognome));
        sb.append(String.format("Username: %s\n", username));
        sb.append(String.format("Ruolo: %s\n", ruolo));
        if (dataNascita != null) {
            sb.append(String.format("Data di nascita: %s\n", dataNascita));
        }
        sb.append(String.format("Domicilio: %s\n", luogoDomicilio));
        if (ruolo.equals("cliente") && !ristoratiPreferiti.isEmpty()) {
            sb.append(String.format("Ristoranti preferiti: %d\n", ristoratiPreferiti.size()));
        }
        return sb.toString();
    }
}
