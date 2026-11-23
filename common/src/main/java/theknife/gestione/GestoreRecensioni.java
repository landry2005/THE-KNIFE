package theknife.gestione;

import theknife.model.Recenzione;
import theknife.model.Ristorante;
import theknife.dao.RecensioneDAO;

import java.util.List;

/**
 * Classe per la gestione delle recensioni.
 * Delega la persistenza al RecensioneDAO.
 * La media delle stelle viene calcolata automaticamente
 * dalla vista SQL vista_valutazioni_ristoranti.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public class GestoreRecensioni {

    private final RecensioneDAO recensioneDAO;
    private final GestoreRistoranti gestoreRistoranti;

    /**
     * Costruttore del gestore recensioni.
     *
     * @param gestoreRistoranti riferimento al gestore ristoranti
     *                          per aggiornare le valutazioni
     */
    public GestoreRecensioni(GestoreRistoranti gestoreRistoranti) {
        this.recensioneDAO = new RecensioneDAO();
        this.gestoreRistoranti = gestoreRistoranti;
    }

    /**
     * Aggiunge una nuova recensione.
     * Il constraint UNIQUE del database protegge già
     * dai duplicati: se l'utente ha già recensito,
     * salvaRecensione() restituisce false.
     *
     * @param idUtente     ID del cliente che scrive la recensione
     * @param idRistorante ID del ristorante recensito
     * @param stelle       numero di stelle (1-5)
     * @param testo        testo della recensione
     * @return true se aggiunta con successo,
     *         false se l'utente ha già recensito o errore
     */
    public boolean aggiungiRecensione(int idUtente, int idRistorante, int stelle, String testo) {
        Recenzione recensione = new Recenzione(idUtente, idRistorante, stelle, testo);
        return recensioneDAO.salvaRecensione(recensione);
    }

    /**
     * Modifica una recensione esistente.
     *
     * @param idUtente     ID del cliente
     * @param idRistorante ID del ristorante
     * @param nuoveStelle  nuovo numero di stelle (1-5)
     * @param nuovoTesto   nuovo testo
     * @return true se modificata con successo, false altrimenti
     */
    public boolean modificaRecensione(int idUtente, int idRistorante, int nuoveStelle, String nuovoTesto) {
        return recensioneDAO.modificaRecensione(idUtente, idRistorante, nuoveStelle, nuovoTesto);
    }

    /**
     * Elimina una recensione.
     *
     * @param idUtente     ID del cliente
     * @param idRistorante ID del ristorante
     * @return true se eliminata con successo, false altrimenti
     */
    public boolean eliminaRecensione(int idUtente, int idRistorante) {
        return recensioneDAO.eliminaRecensione(idUtente, idRistorante);
    }

    /**
     * Aggiunge la risposta del ristoratore a una recensione.
     *
     * @param idRecensione ID della recensione
     * @param risposta     testo della risposta
     * @return true se aggiunta con successo, false altrimenti
     */
    public boolean rispondiRecensione(int idRecensione, String risposta) {
        return recensioneDAO.rispondiRecensione(idRecensione, risposta);
    }

    /**
     * Verifica se un utente ha già recensito un ristorante.
     * Metodo utility: il controllo reale sui duplicati
     * è garantito dal constraint UNIQUE del database.
     *
     * @param idUtente     ID del cliente
     * @param idRistorante ID del ristorante
     * @return true se ha già recensito, false altrimenti
     */
    public boolean hasRecensione(int idUtente, int idRistorante) {
        return recensioneDAO.hasRecensione(idUtente, idRistorante);
    }

    /**
     * Ottiene tutte le recensioni di un ristorante.
     *
     * @param idRistorante ID del ristorante
     * @return lista delle recensioni
     */
    public List<Recenzione> getRecensioniRistorante(int idRistorante) {
        return recensioneDAO.getRecensioniPerRistorante(idRistorante);
    }

    /**
     * Ottiene tutte le recensioni scritte da un utente.
     *
     * @param idUtente ID del cliente
     * @return lista delle recensioni
     */
    public List<Recenzione> getRecensioniCliente(int idUtente) {
        return recensioneDAO.getRecensioniPerUtente(idUtente);
    }

    /**
     * Calcola la media delle stelle per un ristorante.
     *
     * @param idRistorante ID del ristorante
     * @return media delle stelle (0.0 se nessuna recensione)
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
     * Visualizza le recensioni di un ristorante.
     *
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
     * Visualizza il riepilogo delle recensioni per i ristoranti
     * di un ristoratore.
     *
     * @param idRistoratore      ID del ristoratore
     * @param gestoreRistoranti  gestore ristoranti
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