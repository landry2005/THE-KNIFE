package theknife.model;

import java.time.LocalDate;
import java.io.Serializable;

/**
 * Classe che rappresenta un utente della piattaforma TheKnife.
 * Un utente può essere un cliente o un ristoratore.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class Utente implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private int id; // ID univoco dell'utente (SERIAL)
    private String nome;
    private String cognome;
    private String username; //usato per login
    private String passwordHash; // cifrata
    private String ruolo; // "cliente" o "ristoratore"
    private LocalDate dataNascita; // facoltativa
    private String cittaDomicilio;
    private String domandaSicurezza; // domanda per recupero password
    private String rispostaSicurezza; // risposta cifrata per recupero password

    /**
     * Costruttore completo della classe Utente (usato per caricare DB)
     */
    public Utente(int id,String nome, String cognome, String username, String passwordHash, 
                  String ruolo, LocalDate dataNascita, String cittaDomicilio,
                  String domandaSicurezza, String rispostaSicurezza) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordHash = passwordHash;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.cittaDomicilio = cittaDomicilio;
        this.domandaSicurezza = domandaSicurezza;
        this.rispostaSicurezza = rispostaSicurezza;
    }
    
    /**
     * Costruttore semplificato per compatibilità
     * Se il ruolo non viene specificato correttamente si assume "cliente" come default.
     */
    public Utente(String nome, String cognome, String username, String passwordHash,String ruolo, LocalDate dataNascita, String cittaDomicilio,
                  String domandaSicurezza, String rispostaSicurezza) {
        this(-1, nome, cognome, username, passwordHash,(ruolo==null || ruolo.trim().isEmpty()) ? "cliente" : ruolo,dataNascita, cittaDomicilio, domandaSicurezza, rispostaSicurezza);
    }

    // Getter e Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
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

    public String getPasswordHash() {
        return passwordHash ;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
    
    public String getCittaDomicilio() {
        return cittaDomicilio;
    }
    
    public void setCittaDomicilio(String cittaDomicilio) {
        this.cittaDomicilio = cittaDomicilio;           
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
           return "Utente [id=" + id + ", nome=" + nome + ", cognome=" + cognome + ", username=" + username + 
               ", ruolo=" + ruolo + ", domicilio=" + cittaDomicilio + "]";
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
        sb.append(String.format("Domicilio: %s\n", cittaDomicilio));
        return sb.toString();
    }
}
