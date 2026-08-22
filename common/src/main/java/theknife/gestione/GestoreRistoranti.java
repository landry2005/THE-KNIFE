package theknife.gestione;

import theknife.model.Ristorante;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe per la gestione dei ristoranti.
 * Gestisce il caricamento, la ricerca e l'aggiunta di ristoranti.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class GestoreRistoranti {
    
    private List<Ristorante> ristoranti;
    
    /**
     * Costruttore del gestore ristoranti
     */
    public GestoreRistoranti() {
        this.ristoranti = new ArrayList<>();
    }
    
    /**
     * Aggiunge un nuovo ristorante alla lista
     * @param ristorante Ristorante da aggiungere
     */

    public void aggiungiRistorante(Ristorante ristorante) {
        ristoranti.add(ristorante);
//rimosso salvaRistoranti(): Il DAO farà l'INSERT INTO SQL
    }

    /**
     * Cerca un ristorante per ID
     * @param idRistorante ID del ristorante
     * @return Ristorante trovato o null se non trovato
     */

    public Ristorante cercaRistorantePerId(int idRistorante){
        for (Ristorante r : ristoranti) {
            if (r.getId() == idRistorante) {
                return r;
            }
        }
        return null;
    }

    /**
     * Cerca ristorante per locazione
     * @param citta Città in cui cercare
     * @return Lista di ristoranti nella città specificata
     */

    public List<Ristorante> cercaPerCitta(String citta){
        return ristoranti.stream()
                .filter(r -> r.getCitta().toLowerCase().equals(citta.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Cerca ristorante per tipo di cucina
     * @param tipoCucina Tipo di cucina da cercare
     * @param citta Città in cui cercare (opzionale, può essere null)
     * @return Lista di ristoranti che corrispondono ai criteri
     */

    public List<Ristorante> cercaPerTipoCucina(String tipoCucina, String citta){
        return ristoranti.stream()
                .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
                .filter(r -> r.getTipoCucina().toLowerCase().contains(tipoCucina.toLowerCase()))
                .collect(Collectors.toList());
}

/**
     * Cerca ristoranti per fascia di prezzo
     * @param prezzoMin Prezzo minimo
     * @param prezzoMax Prezzo massimo
     * @param citta Città (obbligatoria)
     * @return Lista di ristoranti trovati
     */
    public List<Ristorante> cercaPerFasciaPrezzo(double prezzoMin, double prezzoMax, String citta) {
        return ristoranti.stream()
            .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
            .filter(r -> r.getPrezzoMedio() >= prezzoMin && r.getPrezzoMedio() <= prezzoMax)
            .collect(Collectors.toList());
    }
    
    /**
     * Cerca ristoranti con delivery
     * @param citta Città (obbligatoria)
     * @return Lista di ristoranti con delivery
     */
    public List<Ristorante> cercaConDelivery(String citta) {
        return ristoranti.stream()
            .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
            .filter(Ristorante::isDelivery)
            .collect(Collectors.toList());
    }
    
    /**
     * Cerca ristoranti con prenotazione online
     * @param citta Città (obbligatoria)
     * @return Lista di ristoranti con prenotazione
     */
    public List<Ristorante> cercaConPrenotazione(String citta) {
        return ristoranti.stream()
            .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
            .filter(Ristorante::isPrenotazione)
            .collect(Collectors.toList());
    }
    
    /**
     * Cerca ristoranti per media stelle
     * @param stelleMin Numero minimo di stelle
     * @param citta Città (obbligatoria)
     * @return Lista di ristoranti trovati
     */
    public List<Ristorante> cercaPerStelle(double stelleMin, String citta) {
        return ristoranti.stream()
            .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
            .filter(r -> r.getMediaStelle() >= stelleMin)
            .collect(Collectors.toList());
    }
    
    /**
     * Ricerca avanzata con criteri multipli
     */
    public List<Ristorante> cercaRistorante(String citta, String tipoCucina, Double prezzoMin, 
                                           Double prezzoMax, Boolean delivery, Boolean prenotazione, 
                                           Double stelleMin) {
        List<Ristorante> risultati = new ArrayList<>(ristoranti);
        
        // Filtro per città (obbligatorio)
        if (citta != null && !citta.isEmpty()) {
            risultati = risultati.stream()
                .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Filtro per tipo di cucina
        if (tipoCucina != null && !tipoCucina.isEmpty()) {
            risultati = risultati.stream()
                .filter(r -> r.getTipoCucina().toLowerCase().contains(tipoCucina.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Filtro per prezzo
        if (prezzoMin != null) {
            risultati = risultati.stream()
                .filter(r -> r.getPrezzoMedio() >= prezzoMin)
                .collect(Collectors.toList());
        }
        if (prezzoMax != null) {
            risultati = risultati.stream()
                .filter(r -> r.getPrezzoMedio() <= prezzoMax)
                .collect(Collectors.toList());
        }
        
        // Filtro per delivery
        if (delivery != null && delivery) {
            risultati = risultati.stream()
                .filter(Ristorante::isDelivery)
                .collect(Collectors.toList());
        }
        
        // Filtro per prenotazione
        if (prenotazione != null && prenotazione) {
            risultati = risultati.stream()
                .filter(Ristorante::isPrenotazione)
                .collect(Collectors.toList());
        }
        
        // Filtro per stelle
        if (stelleMin != null) {
            risultati = risultati.stream()
                .filter(r -> r.getMediaStelle() >= stelleMin)
                .collect(Collectors.toList());
        }
        
        return risultati;
    }
    
    /**
     * Restituisce tutti i ristoranti
     * @return Lista di tutti i ristoranti
     */
    public List<Ristorante> getTuttiRistoranti() {
        return new ArrayList<>(ristoranti);
    }
    
    /**
     * Restituisce i ristoranti di un ristoratore
     * @param idRistoratore ID numerico del ristoratore
     * @return Lista dei ristoranti del ristoratore
     */
    public List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) {
        return ristoranti.stream()
            .filter(r -> r.getIdRistoratore() != null && r.getIdRistoratore() == idRistoratore)
            .collect(Collectors.toList());
    }
    
    /**
     * Aggiorna la media delle stelle di un ristorante
     * @param idRistorante ID del ristorante (numerico)
     * @param nuovaMedia Nuova media
     * @param numeroRecensioni Numero di recensioni
     */
    public void aggiornaValutazione(int idRistorante, double nuovaMedia, int numeroRecensioni) {
        Ristorante ristorante = cercaRistorantePerId(idRistorante);
        if (ristorante != null) {
            ristorante.setMediaStelle(nuovaMedia);
            ristorante.setNumeroRecensioni(numeroRecensioni);
            // RIMOSSO salvaRistoranti(): Il DAO farà l'UPDATE SQL se necessario
        }
    }
}