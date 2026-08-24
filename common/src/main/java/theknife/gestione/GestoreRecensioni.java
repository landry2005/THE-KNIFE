package theknife.gestione;

import theknife.model.Recenzione;
import theknife.model.Ristorante;
import theknife.dao.RecensioneDAO;


import java.util.List;


/**
 * Classe per la gestione delle recensioni.
 * Gestisce l'aggiunta, modifica, cancellazione e persistenza delle recensioni.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class GestoreRecensioni {

    private RecensioneDAO recensioneDAO = new RecensioneDAO();
    private GestoreRistoranti gestoreRistoranti;
    
    /**
     * Costruttore del gestore recensioni
     * @param gestoreRistoranti Riferimento al gestore ristoranti per aggiornare le valutazioni
     */
    public GestoreRecensioni(GestoreRistoranti gestoreRistoranti) {
        this.gestoreRistoranti = gestoreRistoranti;
    }

    /**
     * Aggiunge una nuova recensione e aggiorna la media stelle del ristorante
     * @param idUtente ID dell'utente che lascia la recensione
     * @param idRistorante ID del ristorante recensito
     * @param stelle Numero di stelle (1-5)
     * @param testo Testo della recensione
     * @return true se aggiunta con successo, false se il clienta ha gia recensito il ristorante
     */
    
  public boolean aggiungiRecensione(int idUtente, int idRistorante, int stelle, String testo) {
        // Verifica se l'utente ha già recensito questo ristorante
        if (hasRecensione(idUtente, idRistorante)) {
            return false;
        }
        
        Recenzione recensione = new Recenzione(idUtente, idRistorante, stelle, testo);
        boolean successo = recensioneDAO.salvaRecensione(recensione);
        
        // Aggiorna la valutazione del ristorante
        if(successo) {
            aggiornaValutazioneRistorante(idRistorante);
        }
        return successo;
    }
    
    /**
     * Modifica una recensione esistente
     * @param idUtente ID del cliente
     * @param idRistorante ID del ristorante
     * @param nuoveStelle Nuovo numero di stelle
     * @param nuovoTesto Nuovo testo
     * @return true se modificata con successo, false altrimenti
     */
    public boolean modificaRecensione(int idUtente, int idRistorante, int nuoveStelle, String nuovoTesto) {
        boolean successo = recensioneDAO.modificaRecensione(idUtente, idRistorante, nuoveStelle, nuovoTesto);
        if (successo) {
            aggiornaValutazioneRistorante(idRistorante);
        }
        return successo;
    }
    
    /**
     * Elimina una recensione
     * @param idUtente ID del cliente
     * @param idRistorante ID del ristorante
     * @return true se eliminata con successo, false altrimenti
     */
    public boolean eliminaRecensione(int idUtente, int idRistorante) {
        boolean successo = recensioneDAO.eliminaRecensione(idUtente, idRistorante);
        if (successo) {
            aggiornaValutazioneRistorante(idRistorante);
        }
        return successo;
    }
    
    /**
     * Aggiunge una risposta del ristoratore a una recensione
     * @param idRecensione ID della recensione
     * @param risposta Testo della risposta
     * @return true se aggiunta con successo, false altrimenti
     */
    public boolean rispondiRecensione(int idRecensione, String risposta) {
        return recensioneDAO.rispondiRecensione(idRecensione, risposta);
    }
    
    /**
     * Verifica se un cliente ha già recensito un ristorante
     * @param idUtente ID del cliente
     * @param idRistorante ID del ristorante
     * @return true se ha già recensito, false altrimenti
     */
    public boolean hasRecensione(int idUtente, int idRistorante) {
        return recensioneDAO.hasRecensione(idUtente, idRistorante);
    }
    
    
    /**
     * Ottiene tutte le recensioni di un ristorante
     * @param idRistorante ID del ristorante
     * @return Lista delle recensioni
     */
    public List<Recenzione> getRecensioniRistorante(int idRistorante) {
        return recensioneDAO.getRecensioniPerRistorante(idRistorante);
    }
    
    /**
     * Ottiene tutte le recensioni di un cliente
     * @param idUtente ID del cliente
     * @return Lista delle recensioni
     */
    public List<Recenzione> getRecensioniCliente(int idUtente) {
        return recensioneDAO.getRecensioniPerUtente(idUtente);
    }
    
    /**
     * Calcola la media delle stelle per un ristorante
     * @param idRistorante ID del ristorante
     * @return Media delle stelle
     */
    public double calcolaMediaStelle(int idRistorante) {
        List<Recenzione> recensioniRistorante = getRecensioniRistorante(idRistorante);
        if (recensioniRistorante.isEmpty()) {
            return 0.0;
        }
        
        double somma = 0;
        for (Recenzione recensione : recensioniRistorante) {
            somma += recensione.getStelle();
        }
        
        return somma / recensioniRistorante.size();
    }
    
    /**
     * Aggiorna la valutazione di un ristorante
     * @param idRistorante ID del ristorante
     */
    private void aggiornaValutazioneRistorante(int idRistorante) {
        double media = calcolaMediaStelle(idRistorante);
        int numeroRecensioni = getRecensioniRistorante(idRistorante).size();
        
        // Assumendo che GestoreRistoranti abbia questo metodo (da sistemare nel prossimo file)
        gestoreRistoranti.aggiornaValutazione(idRistorante, media, numeroRecensioni);
    }
    
    /**
     * Visualizza le recensioni di un ristorante
     * @param idRistorante ID del ristorante
     */
    public void visualizzaRecensioni(int idRistorante) {
        List<Recenzione> recensioniRistorante = getRecensioniRistorante(idRistorante);
        
        if (recensioniRistorante.isEmpty()) {
            System.out.println("Nessuna recensione disponibile per questo ristorante.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  RECENSIONI (" + recensioniRistorante.size() + ")");
        System.out.println("  Media: ★ " + String.format("%.1f", calcolaMediaStelle(idRistorante)));
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        for (Recenzione recensione : recensioniRistorante) {
            System.out.println(recensione);
        }
    }
    
    /**
     * Visualizza il riepilogo delle recensioni per i ristoranti di un ristoratore
     * @param idRistoratore ID del ristoratore
     * @param gestoreRistoranti Gestore ristoranti
     */
    public void visualizzaRiepilogo(int idRistoratore, GestoreRistoranti gestoreRistoranti) {
        List<Ristorante> ristoranti = gestoreRistoranti.getRistorantiPerRistoratore(idRistoratore);
        
        if (ristoranti.isEmpty()) {
            System.out.println("Non hai ancora aggiunto ristoranti.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  RIEPILOGO RISTORANTI");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        for (Ristorante ristorante : ristoranti) {
            System.out.println(ristorante.getNome() + " (" + ristorante.getCitta() + ")");
            System.out.println("  Valutazione: ★ " + String.format("%.1f", ristorante.getMediaStelle()));
            System.out.println("  Recensioni: " + ristorante.getNumeroRecensioni());
            System.out.println("───────────────────────────────────────────────────────");
        }
    }
}