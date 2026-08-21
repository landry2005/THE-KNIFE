package theknife.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe che rappresenta una recensione di un ristorante.
 * Contiene il numero di stelle (1-5), il testo della recensione,
 * e l'eventuale risposta del ristoratore.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class Recenzione {
    
    private String idRecensione;
    private String usernameCliente;
    private String idRistorante;
    private int stelle; // da 1 a 5
    private String testo;
    private LocalDateTime dataOra;
    private String rispostaRistoratore;
    private LocalDateTime dataOraRisposta;
    
    /**
     * Costruttore della classe Recensione
     * @param usernameCliente Username del cliente che lascia la recensione
     * @param idRistorante ID del ristorante recensito
     * @param stelle Numero di stelle (1-5)
     * @param testo Testo della recensione
     */
    public Recenzione(String usernameCliente, String idRistorante, int stelle, String testo) {
        this.usernameCliente = usernameCliente;
        this.idRistorante = idRistorante;
        setStelle(stelle); // validazione
        this.testo = testo;
        this.dataOra = LocalDateTime.now();
        this.rispostaRistoratore = null;
        this.dataOraRisposta = null;
        this.idRecensione = generaId();
    }

    /**
     * Costruttore per caricamento da file
     */
    public Recenzione(String idRecensione, String usernameCliente, String idRistorante, int stelle,
                      String testo, LocalDateTime dataOra, String rispostaRistoratore,
                      LocalDateTime dataOraRisposta) {
        this.idRecensione = idRecensione;
        this.usernameCliente = usernameCliente;
        this.idRistorante = idRistorante;
        setStelle(stelle); // validazione
        this.testo = testo;
        this.dataOra = (dataOra != null) ? dataOra : LocalDateTime.now();
        this.rispostaRistoratore = rispostaRistoratore;
        this.dataOraRisposta = dataOraRisposta;

        if (this.idRecensione == null || this.idRecensione.trim().isEmpty()) {
            this.idRecensione = generaId();
        }
    }
    
    /**
     * Genera un ID univoco per la recensione
     * @return ID univoco per la recensione
     */
    private String generaId() {
        return usernameCliente + "_" + idRistorante + "_" + 
               dataOra.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    // Getter e Setter
    public String getIdRecensione() {
        return idRecensione;
    }
    
    public String getUsernameCliente() {
        return usernameCliente;
    }
    
    public void setUsernameCliente(String usernameCliente) {
        this.usernameCliente = usernameCliente;
    }
    
    public String getIdRistorante() {
        return idRistorante;
    }
    
    public void setIdRistorante(String idRistorante) {
        this.idRistorante = idRistorante;
    }
    
    public int getStelle() {
        return stelle;
    }
    
    public void setStelle(int stelle) {
        if (stelle < 1 || stelle > 5) {
            throw new IllegalArgumentException("Le stelle devono essere comprese tra 1 e 5");
        }
        this.stelle = stelle;
    }
    
    public String getTesto() {
        return testo;
    }
    
    public void setTesto(String testo) {
        this.testo = testo;
    }
    
    public LocalDateTime getDataOra() {
        return dataOra;
    }
    
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }
    
    public String getRispostaRistoratore() {
        return rispostaRistoratore;
    }
    
    public void setRispostaRistoratore(String rispostaRistoratore) {
        this.rispostaRistoratore = rispostaRistoratore;
        this.dataOraRisposta = LocalDateTime.now();
    }
    
    public LocalDateTime getDataOraRisposta() {
        return dataOraRisposta;
    }
    
    public void setDataOraRisposta(LocalDateTime dataOraRisposta) {
        this.dataOraRisposta = dataOraRisposta;
    }
    
    /**
     * Verifica se il ristoratore ha risposto alla recensione
     * @return true se il ristoratore ha risposto alla recensione, false altrimenti
     */
    public boolean hasRisposta() {
        return rispostaRistoratore != null && !rispostaRistoratore.trim().isEmpty();
    }
    
    /**
     * Genera la rappresentazione visiva delle stelle
     * @return Stringa rappresentazione visiva delle stelle
     */
    public String getStelleSimbolo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stelle; i++) {
            sb.append("★");
        }
        for (int i = stelle; i < 5; i++) {
            sb.append("☆");
        }
        return sb.toString();
    }

    /**
     * Rappresentazione della recensione
     * @return Stringa rappresentazione della recensione
     */
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("───────────────────────────────────────────────────────\n");
        sb.append(String.format("%s - %s\n", getStelleSimbolo(), usernameCliente));
        sb.append(String.format("Data: %s\n", dataOra.format(formatter)));
        sb.append("───────────────────────────────────────────────────────\n");
        sb.append(testo).append("\n");
        
        if (hasRisposta()) {
            sb.append("───────────────────────────────────────────────────────\n");
            sb.append("Risposta del ristoratore:\n");
            sb.append(String.format("Data: %s\n", dataOraRisposta.format(formatter)));
            sb.append(rispostaRistoratore).append("\n");
        }
        sb.append("───────────────────────────────────────────────────────\n");
        
        return sb.toString();
    }
    
    /**
     * Rappresentazione compatta della recensione
     * @return Stringa rappresentazione compatta della recensione
     */
    public String toStringCompatto() {
        return String.format("%s - %s: %s", getStelleSimbolo(), usernameCliente, 
            testo.length() > 50 ? testo.substring(0, 47) + "..." : testo);
    }
}
