package theknife.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String password;// cifrata
    private String ruolo; // cliente o ristoratore
    private LocalDate dataNascita; // facoltativa
    private String luogoDomicilio;
    private List<String> ristorantiPreferiti; // lista ID ristoranti preferiti (solo per clienti)
    
    //costruttore
    public Utente(String nome, String cognome, String username, String password, 
                  String ruolo, LocalDate dataNascita, String luogoDomicilio) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
        this.dataNascita = dataNascita;
        this.luogoDomicilio = luogoDomicilio;
        this.ristorantiPreferiti = new ArrayList<>();
    }
    
    //costruttore semplificato per compatibilità
    public Utente(String nome, String cognome, String username, String password) {
        this(nome, cognome, username, password, "cliente", null, "");
    }
    /**
     * Costruttore vuoto per compatibilità framework
     */
    public Utente() {
        this.ristorantiPreferiti = new ArrayList<>();
    }
    //getter e setter
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
    
    public String getUsername (){
        return username;
    }
    public void setUsername (String username){
        this.username = username;
    }
    public String getPassword (){
        return password;
    }
    public void setPassword (String password){
        this.password = password;
    }
        public String getRuolo (){
            return ruolo;
        }
        public void setRuolo (String ruolo){
            this.ruolo = ruolo;
        }
        public LocalDate getDataNascita (){
            return dataNascita;
        }
        public void setDataNascita (LocalDate dataNascita){
            this.dataNascita = dataNascita;
        }
        public String getLuogoDomicilio (){
            return luogoDomicilio;
        }
        public void setLuogoDomicilio (String luogoDomicilio){
            this.luogoDomicilio = luogoDomicilio;
        }
        public List<String> getRistorantiPreferiti (){
            return ristorantiPreferiti;
        }
        public void setRistorantiPreferiti (List<String> ristorantiPreferiti){
            this.ristorantiPreferiti = ristorantiPreferiti;
        }
        /**
     * Aggiunge un ristorante alla lista dei preferiti
     */
    public boolean aggiungiPreferito(String idRistorante) {
        if (!ristorantiPreferiti.contains(idRistorante)) {
            ristorantiPreferiti.add(idRistorante);
            return true;
        }
        return false;
    }
    
    /**
     * Rimuove un ristorante dalla lista dei preferiti
     */
    public boolean rimuoviPreferito(String idRistorante) {
        return ristorantiPreferiti.remove(idRistorante);
    }
    
    /**
     * Verifica se un ristorante è tra i preferiti
     */
    public boolean isPreferito(String idRistorante) {
        return ristorantiPreferiti.contains(idRistorante);
    }

    
      @Override
      public String toString() {
        return String.join(",", nome, cognome, username, "****", ruolo, 
            dataNascita != null ? dataNascita.toString() : "", 
            luogoDomicilio,
            ristorantiPreferiti.toString());
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
        if (ruolo.equals("cliente") && !ristorantiPreferiti.isEmpty()) {
            sb.append(String.format("Ristoranti preferiti: %d\n", ristorantiPreferiti.size()));
        }
        return sb.toString();
    }
    @Override
     public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return Objects.equals(username, utente.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
        
    

    


    





