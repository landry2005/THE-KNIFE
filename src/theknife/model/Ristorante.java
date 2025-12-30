package theknife.model;

/**
 * Classe che rappresenta un ristorante nella piattaforma TheKnife.
 * Contiene tutte le informazioni relative al ristorante.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class Ristorante {
    
    private String nome;
    private String nazione;
    private String citta;
    private String indirizzo;
    private double latitudine;
    private double longitudine;
    private String tipoCucina;
    private double prezzoMedio;
    private boolean delivery;
    private boolean prenotazione;
    private double mediaStelle;
    private int numeroRecensioni;
    private String idRistoratore; // username del ristoratore proprietario

    /**
     * Costruttore completo della classe Ristorante
     */
    public Ristorante(String nome, String nazione, String citta, String indirizzo, 
                      double latitudine, double longitudine, String tipoCucina, 
                      double prezzoMedio, boolean delivery, boolean prenotazione, 
                      String idRistoratore) {
        this.nome = nome;
        this.nazione = nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.tipoCucina = tipoCucina;
        this.prezzoMedio = prezzoMedio;
        this.delivery = delivery;
        this.prenotazione = prenotazione;
        this.mediaStelle = 0.0;
        this.numeroRecensioni = 0;
        this.idRistoratore = idRistoratore;
    }
    
    /**
     * Costruttore semplificato per compatibilità
     */
    public Ristorante(String nome, String nazione, String citta, String indirizzo, 
                      String tipoCucina, int prezzoMedio, boolean delivery, boolean prenotazione) {
        this(nome, nazione, citta, indirizzo, 0.0, 0.0, tipoCucina, 
             prezzoMedio, delivery, prenotazione, null);
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getNazione() {
        return nazione;
    }
    
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }
    
    public String getCitta() {
        return citta;
    }
    
    public void setCitta(String citta) {
        this.citta = citta;
    }
    
    public String getIndirizzo() {
        return indirizzo;
    }
    
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
    
    public double getLatitudine() {
        return latitudine;
    }
    
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }
    
    public double getLongitudine() {
        return longitudine;
    }
    
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }
    
    public String getTipoCucina() {
        return tipoCucina;
    }
    
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }
    
    public double getPrezzoMedio() {
        return prezzoMedio;
    }
    
    public void setPrezzoMedio(double prezzoMedio) {
        this.prezzoMedio = prezzoMedio;
    }
    
    public boolean isDelivery() {
        return delivery;
    }
    
    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }
    
    public boolean isPrenotazione() {
        return prenotazione;
    }
    
    public void setPrenotazione(boolean prenotazione) {
        this.prenotazione = prenotazione;
    }
    
    public double getMediaStelle() {
        return mediaStelle;
    }
    
    public void setMediaStelle(double mediaStelle) {
        this.mediaStelle = mediaStelle;
    }
    
    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }
    
    public void setNumeroRecensioni(int numeroRecensioni) {
        this.numeroRecensioni = numeroRecensioni;
    }
    
    public String getIdRistoratore() {
        return idRistoratore;
    }
    
    public void setIdRistoratore(String idRistoratore) {
        this.idRistoratore = idRistoratore;
    }
    
    /**
     * Genera un ID univoco per il ristorante basato su nome e città
     */
    public String getId() {
        return nome.replaceAll("\\s+", "_") + "_" + citta.replaceAll("\\s+", "_");
    }
    
    /**
     * Restituisce la locazione completa del ristorante
     */
    public String getLocazione() {
        return indirizzo + ", " + citta + ", " + nazione;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - Prezzo medio: %.2f€ - ★ %.1f (%d recensioni)%s%s",
            nome, citta, tipoCucina, prezzoMedio, mediaStelle, numeroRecensioni,
            delivery ? " [Delivery]" : "",
            prenotazione ? " [Prenotazione]" : "");
    }
    
    /**
     * Restituisce una rappresentazione dettagliata del ristorante
     */
    public String toStringDettagliato() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("  ").append(nome.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("Locazione:     ").append(getLocazione()).append("\n");
        sb.append("Coordinate:    ").append(String.format("%.6f, %.6f", latitudine, longitudine)).append("\n");
        sb.append("Tipo cucina:   ").append(tipoCucina).append("\n");
        sb.append("Prezzo medio:  ").append(String.format("%.2f€", prezzoMedio)).append("\n");
        sb.append("Delivery:      ").append(delivery ? "Sì" : "No").append("\n");
        sb.append("Prenotazione:  ").append(prenotazione ? "Sì" : "No").append("\n");
        sb.append("Valutazione:   ").append(String.format("★ %.1f (%d recensioni)", mediaStelle, numeroRecensioni)).append("\n");
        sb.append("═══════════════════════════════════════════════════════\n");
        return sb.toString();
    }
}
