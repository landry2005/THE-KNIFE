package gestione;

import model.Ristorante;

import java.io.*;
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
    private static final String FILE_RISTORANTI = "data/ristoranti.dati";
    private List<Ristorante> ristoranti;
    
    /**
     * Costruttore del gestore ristoranti
     */
    public GestoreRistoranti() {
        this.ristoranti = new ArrayList<>();
        caricaRistoranti();
    }
    
    /**
     * Carica i ristoranti dal file DATI
     */
    private void caricaRistoranti() {
        File file = new File(FILE_RISTORANTI);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean primaRiga = true;
            while ((line = br.readLine()) != null) {
                // Salta l'intestazione se presente
                if (primaRiga && line.startsWith("Nome")) {
                    primaRiga = false;
                    continue;
                }
                primaRiga = false;
                
                String[] parti = line.split(",", -1);
                if (parti.length >= 8) {
                    String nome = parti[0];
                    String nazione = parti[1];
                    String citta = parti[2];
                    String indirizzo = parti[3];
                    double latitudine = parseDoubleSafe(parti[4], 0.0);
                    double longitudine = parseDoubleSafe(parti[5], 0.0);
                    String tipoCucina = parti[6];
                    double prezzoMedio = parseDoubleSafe(parti[7], 0.0);
                    boolean delivery = parti.length > 8 && parti[8].equalsIgnoreCase("true");
                    boolean prenotazione = parti.length > 9 && parti[9].equalsIgnoreCase("true");
                    String idRistoratore = parti.length > 10 ? parti[10] : null;
                    
                    Ristorante ristorante = new Ristorante(nome, nazione, citta, indirizzo, latitudine, 
                                                           longitudine, tipoCucina, prezzoMedio, 
                                                           delivery, prenotazione, idRistoratore);
                    
                    // Carica valutazione se presente
                    if (parti.length > 11 && !parti[11].isEmpty()) {
                        ristorante.setMediaStelle(parseDoubleSafe(parti[11], 0.0));
                    }
                    if (parti.length > 12 && !parti[12].isEmpty()) {
                        ristorante.setNumeroRecensioni(parseIntSafe(parti[12], 0));
                    }
                    
                    ristoranti.add(ristorante);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento dei ristoranti: " + e.getMessage());
        }
    }
    
    /**
     * Salva i ristoranti nel file DATI
     */
    public void salvaRistoranti() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_RISTORANTI))) {
            // Intestazione
            bw.write("Nome,Nazione,Citta,Indirizzo,Latitudine,Longitudine,TipoCucina,PrezzoMedio,Delivery,Prenotazione,IdRistoratore,MediaStelle,NumeroRecensioni");
            bw.newLine();
            
            for (Ristorante r : ristoranti) {
                bw.write(r.getNome() + ",");
                bw.write(r.getNazione() + ",");
                bw.write(r.getCitta() + ",");
                bw.write(r.getIndirizzo() + ",");
                bw.write(r.getLatitudine() + ",");
                bw.write(r.getLongitudine() + ",");
                bw.write(r.getTipoCucina() + ",");
                bw.write(r.getPrezzoMedio() + ",");
                bw.write(r.isDelivery() + ",");
                bw.write(r.isPrenotazione() + ",");
                bw.write((r.getIdRistoratore() != null ? r.getIdRistoratore() : "") + ",");
                bw.write(r.getMediaStelle() + ",");
                bw.write(String.valueOf(r.getNumeroRecensioni()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio dei ristoranti: " + e.getMessage());
        }
    }
    
    /**
     * Aggiunge un nuovo ristorante
     * @param ristorante Il ristorante da aggiungere
     */
    public void aggiungiRistorante(Ristorante ristorante) {
        ristoranti.add(ristorante);
        salvaRistoranti();
    }
    
    /**
     * Cerca un ristorante per ID
     * @param idRistorante ID del ristorante
     * @return Il ristorante trovato o null
     */
    public Ristorante cercaRistorantePerId(String idRistorante) {
        for (Ristorante r : ristoranti) {
            if (r.getId().equals(idRistorante)) {
                return r;
            }
        }
        return null;
    }
    
    /**
     * Cerca ristoranti per locazione (città)
     * @param citta Città da cercare
     * @return Lista di ristoranti trovati
     */
    public List<Ristorante> cercaPerCitta(String citta) {
        return ristoranti.stream()
            .filter(r -> r.getCitta().toLowerCase().contains(citta.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * Cerca ristoranti per tipo di cucina
     * @param tipoCucina Tipo di cucina
     * @param citta Città (obbligatoria)
     * @return Lista di ristoranti trovati
     */
    public List<Ristorante> cercaPerTipoCucina(String tipoCucina, String citta) {
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
     * @param usernameRistoratore Username del ristoratore
     * @return Lista dei ristoranti del ristoratore
     */
    public List<Ristorante> getRistorantiPerRistoratore(String usernameRistoratore) {
        return ristoranti.stream()
            .filter(r -> usernameRistoratore.equals(r.getIdRistoratore()))
            .collect(Collectors.toList());
    }
    
    /**
     * Aggiorna la media delle stelle di un ristorante
     * @param idRistorante ID del ristorante
     * @param nuovaMedia Nuova media
     * @param numeroRecensioni Numero di recensioni
     */
    public void aggiornaValutazione(String idRistorante, double nuovaMedia, int numeroRecensioni) {
        Ristorante ristorante = cercaRistorantePerId(idRistorante);
        if (ristorante != null) {
            ristorante.setMediaStelle(nuovaMedia);
            ristorante.setNumeroRecensioni(numeroRecensioni);
            salvaRistoranti();
        }
    }

    private double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

