package theknife.network;

import java.io.Serializable;

public class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String citta;
    private String tipoCucina;
    private Double prezzoMin;
    private Double prezzoMax;
    private Boolean delivery;
    private Boolean prenotazione;
    private Double stelleMin;

    public SearchCriteria() {
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getTipoCucina() {
        return tipoCucina;
    }

    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

    public Double getPrezzoMin() {
        return prezzoMin;
    }

    public void setPrezzoMin(Double prezzoMin) {
        this.prezzoMin = prezzoMin;
    }

    public Double getPrezzoMax() {
        return prezzoMax;
    }

    public void setPrezzoMax(Double prezzoMax) {
        this.prezzoMax = prezzoMax;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public Boolean getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Boolean prenotazione) {
        this.prenotazione = prenotazione;
    }

    public Double getStelleMin() {
        return stelleMin;
    }

    public void setStelleMin(Double stelleMin) {
        this.stelleMin = stelleMin;
    }
}