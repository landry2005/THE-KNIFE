package theknife.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import theknife.model.Ristorante;

/**
 * Classe che gestisce i ristoranti della piattaforma TheKnife.
 * Si occupa del caricamento dei ristoranti dal file CSV Michelin
 * e fornisce metodi di accesso ai dati.
 *
 * @author
 * Nome Cognome - Matricola - Sede
 */
public class GestoreRistoranti {

    // Percorso del file CSV fornito dal docente
    private static final String FILE_PATH = "data/michelin_my_maps.csv";

    // Lista dei ristoranti caricati
    private List<Ristorante> ristoranti;

    /**
     * Costruttore: inizializza la lista e carica i ristoranti da file
     */
    public GestoreRistoranti() {
        ristoranti = new ArrayList<>();
        caricaRistoranti();
    }

    /**
     * Carica i ristoranti dal file CSV Michelin.
     * Il file contiene campi separati da virgole, con valori tra virgolette.
     */
    private void caricaRistoranti() {
        ristoranti.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {

            String linea;
            boolean primaRiga = true;

            while ((linea = br.readLine()) != null) {

                // Salta la riga di intestazione
                if (primaRiga) {
                    primaRiga = false;
                    continue;
                }

                // Split corretto per CSV con virgole e virgolette
                String[] campi = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Controllo minimo dei campi necessari
                if (campi.length < 7) {
                    continue;
                }

                String nome = rimuoviVirgolette(campi[0]);
                String indirizzo = rimuoviVirgolette(campi[1]);
                String location = rimuoviVirgolette(campi[2]);
                String prezzo = rimuoviVirgolette(campi[3]);
                String tipoCucina = rimuoviVirgolette(campi[4]);

                double longitudine = Double.parseDouble(campi[5]);
                double latitudine = Double.parseDouble(campi[6]);

                // Estrazione città e nazione dal campo location
                String[] loc = location.split(",");
                String citta = loc[0].trim();
                String nazione = (loc.length > 1) ? loc[1].trim() : "";

                // Conversione simbolica del prezzo (€€ -> valore medio)
                int prezzoMedio = prezzo.length() * 20;

                // Servizi (semplificazione)
                boolean delivery = linea.toLowerCase().contains("delivery");
                boolean prenotazione = true; // Michelin implica prenotazione

                // Creazione oggetto Ristorante
                Ristorante ristorante = new Ristorante(
                        nome,
                        nazione,
                        citta,
                        indirizzo,
                        latitudine,
                        longitudine,
                        tipoCucina,
                        prezzoMedio,
                        delivery,
                        prenotazione,
                        null   // ristoratore non definito nel dataset
                );

                ristoranti.add(ristorante);
            }

            System.out.println("✓ Ristoranti caricati: " + ristoranti.size());

        } catch (IOException | NumberFormatException e) {
            System.out.println("✗ Errore nel caricamento dei ristoranti: " + e.getMessage());
        }
    }

    /**
     * Restituisce la lista completa dei ristoranti
     */
    public List<Ristorante> getRistoranti() {
        return ristoranti;
    }
/**
 * Ricerca ristoranti per città
 */
public List<Ristorante> cercaPerCitta(String citta) {

    List<Ristorante> risultati = new ArrayList<>();

    for (Ristorante r : ristoranti) {
        if (r.getCitta().equalsIgnoreCase(citta)) {
            risultati.add(r);
        }
    }

    return risultati;
}


    /**
     * Rimuove le virgolette dai campi CSV
     */
    private String rimuoviVirgolette(String s) {
        return s.replace("\"", "").trim();
    }

     /**
 * Ricerca ristoranti per tipo di cucina
 */
public List<Ristorante> cercaPerTipo(String tipo) {

    List<Ristorante> risultati = new ArrayList<>();

    for (Ristorante r : ristoranti) {
        if (r.getTipoCucina().toLowerCase().contains(tipo.toLowerCase())) {
            risultati.add(r);
        }
    }

    return risultati;

}
}

        int scelta = leggiIntero();

        switch (scelta) {

            case 1:
                System.out.print("Inserisci la città: ");
                String citta = in.nextLine();
                List<Ristorante> perCitta = gestoreRistoranti.cercaPerCitta(citta);
                visualizzaRistoranti(perCitta);
                break;
            case 2:
                System.out.print("Inserisci il tipo di cucina: ");
                String tipo = in.nextLine();
                List<Ristorante> perTipo = gestoreRistoranti.cercaPerTipo(tipo);
                visualizzaRistoranti(perTipo);
                break;
            case 0:
                indietro = true;
                break;
            default:
                System.out.println("Scelta non valida.");
        }

                
