package theknife.gestione;

import theknife.dao.RistoranteDAO;
import theknife.model.Ristorante;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe per la gestione dei ristoranti.
 * Gestisce il caricamento, la ricerca e l'aggiunta di ristoranti.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class GestoreRistoranti {
    
  private RistoranteDAO ristoranteDAO = new RistoranteDAO();
    /**
     * Aggiunge un nuovo ristorante.
     *
     * @param ristorante Ristorante da aggiungere
     */
    public void aggiungiRistorante(Ristorante ristorante) {
        ristoranteDAO.salvaRistorante(ristorante);
    }
//rimosso salvaRistoranti(): Il DAO farà l'INSERT INTO SQL
    

    /**
     * Cerca un ristorante per ID.
     *
     * @param idRistorante ID del ristorante
     * @return Ristorante trovato oppure null
     */

    public Ristorante cercaRistorantePerId(int idRistorante){
        return ristoranteDAO.trovaPerId(idRistorante);
    }

    /**
     * Cerca ristoranti per città.
     *
     * @param citta Città in cui cercare
     * @return Lista dei ristoranti trovati
     */

    public List<Ristorante> cercaPerCitta(String citta){
        return ristoranteDAO.cercaPerCitta(citta);
    }

    /**
     * Cerca ristoranti per tipo di cucina.
     *
     * @param tipoCucina Tipo di cucina
     * @param citta Città in cui cercare
     * @return Lista dei ristoranti trovati
     */
   
    public List<Ristorante> cercaPerTipoCucina(String tipoCucina, String citta){
        return cercaPerCitta(citta).stream()
                .filter(r -> r.getTipoCucina().toLowerCase().contains(tipoCucina.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Cerca ristoranti per fascia di prezzo.
     *
     * @param prezzoMin Prezzo minimo
     * @param prezzoMax Prezzo massimo
     * @param citta Città
     * @return Lista dei ristoranti trovati
     */
    public List<Ristorante> cercaPerFasciaPrezzo(double prezzoMin, double prezzoMax, String citta) {
        return cercaPerCitta(citta).stream()
            .filter(r -> r.getPrezzoMedio() >= prezzoMin && r.getPrezzoMedio() <= prezzoMax)
            .collect(Collectors.toList());
    }

    /**
     * Cerca ristoranti con servizio delivery.
     *
     * @param citta Città
     * @return Lista dei ristoranti trovati
     */
    public List<Ristorante> cercaConDelivery(String citta) {
        return cercaPerCitta(citta).stream()
            .filter(Ristorante::isDelivery)
            .collect(Collectors.toList());
    }

    /**
     * Cerca ristoranti con prenotazione online.
     *
     * @param citta Città
     * @return Lista dei ristoranti trovati
     */
    public List<Ristorante> cercaConPrenotazione(String citta) {
        return cercaPerCitta(citta).stream()
            .filter(Ristorante::isPrenotazione)
            .collect(Collectors.toList());
    }

    /**
     * Cerca ristoranti per media minima delle stelle.
     *
     * @param stelleMin Media minima
     * @param citta Città
     * @return Lista dei ristoranti trovati
     */
    public List<Ristorante> cercaPerStelle(double stelleMin, String citta) {
        return cercaPerCitta(citta).stream()
            .filter(r -> r.getMediaStelle() >= stelleMin)
            .collect(Collectors.toList());
    }

    /**
     * Ricerca avanzata con criteri multipli.
     * I filtri vengono applicati direttamente nel database.
     *
     * @param citta Città obbligatoria
     * @param tipoCucina Tipo di cucina opzionale
     * @param prezzoMin Prezzo minimo opzionale
     * @param prezzoMax Prezzo massimo opzionale
     * @param delivery Filtro delivery opzionale
     * @param prenotazione Filtro prenotazione opzionale
     * @param stelleMin Media stelle minima opzionale
     * @return Lista dei ristoranti corrispondenti
     */
    public List<Ristorante> cercaRistorante(String citta, String tipoCucina, Double prezzoMin, 
                                           Double prezzoMax, Boolean delivery, Boolean prenotazione, 
                                           Double stelleMin) {

        //Prendo tutti i ristoranti della citta dal db
        List<Ristorante> risultati = cercaPerCitta(citta);
        
        
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
     * Restituisce tutti i ristoranti.
     *
     * @return Lista di tutti i ristoranti
     */
    public List<Ristorante> getTuttiRistoranti() {
        return ristoranteDAO.cercaPerCitta("");
    }

    /**
     * Restituisce i ristoranti appartenenti a un gestore.
     *
     * @param idRistoratore ID del gestore
     * @return Lista dei ristoranti
     */
    public List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) {
        return ristoranteDAO.getRistorantiPerRistoratore(idRistoratore);
    }

    /**
     * Aggiorna temporaneamente i dati di valutazione
     * dell'oggetto ristorante.
     *
     * La media reale viene calcolata dinamicamente
     * tramite la vista vista_valutazioni_ristoranti.
     *
     * @param idRistorante ID del ristorante
     * @param nuovaMedia Nuova media
     * @param numeroRecensioni Numero di recensioni
     */
    public void aggiornaValutazione(int idRistorante, double nuovaMedia, int numeroRecensioni) {
        // Questo metodo verrà chiamato dal GestoreRecensioni.
        // Per ora non facciamo l'UPDATE su DB perché la media si calcola dinamicamente 
        // tramite la vista SQL 'vista_valutazioni_ristoranti' che abbiamo creato ieri!
        Ristorante r = cercaRistorantePerId(idRistorante);
        if (r != null) {
            r.setMediaStelle(nuovaMedia);
            r.setNumeroRecensioni(numeroRecensioni);
        }
    }
}