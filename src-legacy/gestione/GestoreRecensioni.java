package gestione;

import model.Recenzione;
import model.Ristorante;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe per la gestione delle recensioni.
 * Gestisce l'aggiunta, modifica, cancellazione e persistenza delle recensioni.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class GestoreRecensioni {
    private static final String FILE_RECENSIONI = "data/recensioni.dati";
    private List<Recenzione> recensioni;
    private GestoreRistoranti gestoreRistoranti;
    
    /**
     * Costruttore del gestore recensioni
     * @param gestoreRistoranti Riferimento al gestore ristoranti per aggiornare le valutazioni
     */
    public GestoreRecensioni(GestoreRistoranti gestoreRistoranti) {
        this.recensioni = new ArrayList<>();
        this.gestoreRistoranti = gestoreRistoranti;
        caricaRecensioni();
    }
    
    /**
     * Carica le recensioni dal file DATI
     * @throws IOException Se si verifica un errore di I/O
     */
    private void caricaRecensioni() {
        File file = new File(FILE_RECENSIONI);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return;
        }

        Integer signature = leggiSignature(file);
        if (signature != null && signature == 0xACED) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                recensioni = (List<Recenzione>) ois.readObject();
                return;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Errore nel caricamento delle recensioni: " + e.getMessage());
                recensioni = new ArrayList<>();
                return;
            }
        }

        recensioni = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean primaRiga = true;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (primaRiga && trimmed.toLowerCase().startsWith("idrecensione,")) {
                    primaRiga = false;
                    continue;
                }
                primaRiga = false;
                String[] parti = line.split(",", -1);
                if (parti.length < 5) {
                    continue;
                }

                String idRecensione = parti[0];
                String usernameCliente = parti[1];
                String idRistorante = parti[2];
                int stelle = parseIntSafe(parti[3], 1);
                String testo = unescapeCampo(parti[4]);
                LocalDateTime dataOra = parseDateTimeSafe(parti.length > 5 ? parti[5] : "");
                String rispostaRistoratore = parti.length > 6 ? unescapeCampo(parti[6]) : "";
                LocalDateTime dataOraRisposta = parseDateTimeSafe(parti.length > 7 ? parti[7] : "");

                if (rispostaRistoratore != null && rispostaRistoratore.isEmpty()) {
                    rispostaRistoratore = null;
                }
                if (dataOraRisposta != null && rispostaRistoratore == null) {
                    dataOraRisposta = null;
                }

                Recenzione recensione = new Recenzione(
                    idRecensione, usernameCliente, idRistorante, stelle, testo,
                    dataOra, rispostaRistoratore, dataOraRisposta
                );
                recensioni.add(recensione);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle recensioni: " + e.getMessage());
            recensioni = new ArrayList<>();
        }
    }
    
    /**
     * Salva le recensioni nel file (serializzazione)
     */
    public void salvaRecensioni() {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_RECENSIONI), StandardCharsets.UTF_8))) {
            bw.write("idRecensione,usernameCliente,idRistorante,stelle,testo,dataOra,rispostaRistoratore,dataOraRisposta");
            bw.newLine();
            for (Recenzione recensione : recensioni) {
                String risposta = recensione.getRispostaRistoratore();
                String dataRisposta = recensione.getDataOraRisposta() != null
                    ? recensione.getDataOraRisposta().toString()
                    : "";
                bw.write(escapeCampo(recensione.getIdRecensione()));
                bw.write(",");
                bw.write(escapeCampo(recensione.getUsernameCliente()));
                bw.write(",");
                bw.write(escapeCampo(recensione.getIdRistorante()));
                bw.write(",");
                bw.write(String.valueOf(recensione.getStelle()));
                bw.write(",");
                bw.write(escapeCampo(recensione.getTesto()));
                bw.write(",");
                bw.write(recensione.getDataOra() != null ? recensione.getDataOra().toString() : "");
                bw.write(",");
                bw.write(escapeCampo(risposta != null ? risposta : ""));
                bw.write(",");
                bw.write(dataRisposta);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio delle recensioni: " + e.getMessage());
        }
    }

    private Integer leggiSignature(File file) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            int b1 = is.read();
            int b2 = is.read();
            if (b1 == -1 || b2 == -1) {
                return null;
            }
            return ((b1 & 0xFF) << 8) | (b2 & 0xFF);
        } catch (IOException e) {
            return null;
        }
    }

    private String escapeCampo(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r\n", "\\n")
            .replace("\n", "\\n")
            .replace(",", ";");
    }

    private String unescapeCampo(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\n", "\n");
    }

    private LocalDateTime parseDateTimeSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception e) {
            return null;
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
    
    /**
     * Aggiunge una nuova recensione
     * @param usernameCliente Username del cliente
     * @param idRistorante ID del ristorante
     * @param stelle Numero di stelle (1-5)
     * @param testo Testo della recensione
     * @return true se aggiunta con successo, false se il cliente ha già recensito il ristorante
     */
    public boolean aggiungiRecensione(String usernameCliente, String idRistorante, int stelle, String testo) {
        // Verifica se l'utente ha già recensito questo ristorante
        if (hasRecensione(usernameCliente, idRistorante)) {
            return false;
        }
        
        Recenzione recensione = new Recenzione(usernameCliente, idRistorante, stelle, testo);
        recensioni.add(recensione);
        
        // Aggiorna la valutazione del ristorante
        aggiornaValutazioneRistorante(idRistorante);
        
        salvaRecensioni();
        return true;
    }
    
    /**
     * Modifica una recensione esistente
     * @param usernameCliente Username del cliente
     * @param idRistorante ID del ristorante
     * @param nuoveStelle Nuovo numero di stelle
     * @param nuovoTesto Nuovo testo
     * @return true se modificata con successo, false altrimenti
     */
    public boolean modificaRecensione(String usernameCliente, String idRistorante, int nuoveStelle, String nuovoTesto) {
        Recenzione recensione = getRecensione(usernameCliente, idRistorante);
        if (recensione != null) {
            recensione.setStelle(nuoveStelle);
            recensione.setTesto(nuovoTesto);
            
            // Aggiorna la valutazione del ristorante
            aggiornaValutazioneRistorante(idRistorante);
            
            salvaRecensioni();
            return true;
        }
        return false;
    }
    
    /**
     * Elimina una recensione
     * @param usernameCliente Username del cliente
     * @param idRistorante ID del ristorante
     * @return true se eliminata con successo, false altrimenti
     */
    public boolean eliminaRecensione(String usernameCliente, String idRistorante) {
        Recenzione recensione = getRecensione(usernameCliente, idRistorante);
        if (recensione != null) {
            recensioni.remove(recensione);
            
            // Aggiorna la valutazione del ristorante
            aggiornaValutazioneRistorante(idRistorante);
            
            salvaRecensioni();
            return true;
        }
        return false;
    }
    
    /**
     * Aggiunge una risposta del ristoratore a una recensione
     * @param idRecensione ID della recensione
     * @param risposta Testo della risposta
     * @return true se aggiunta con successo, false altrimenti
     */
    public boolean rispondiRecensione(String idRecensione, String risposta) {
        for (Recenzione recensione : recensioni) {
            if (recensione.getIdRecensione().equals(idRecensione)) {
                if (!recensione.hasRisposta()) {
                    recensione.setRispostaRistoratore(risposta);
                    salvaRecensioni();
                    return true;
                }
                return false; // Già risposto
            }
        }
        return false;
    }
    
    /**
     * Verifica se un cliente ha già recensito un ristorante
     * @param usernameCliente Username del cliente
     * @param idRistorante ID del ristorante
     * @return true se ha già recensito, false altrimenti
     */
    public boolean hasRecensione(String usernameCliente, String idRistorante) {
        return getRecensione(usernameCliente, idRistorante) != null;
    }
    
    /**
     * Ottiene la recensione di un cliente per un ristorante
     * @param usernameCliente Username del cliente
     * @param idRistorante ID del ristorante
     * @return La recensione o null se non esiste
     */
    public Recenzione getRecensione(String usernameCliente, String idRistorante) {
        for (Recenzione recensione : recensioni) {
            if (recensione.getUsernameCliente().equals(usernameCliente) && 
                recensione.getIdRistorante().equals(idRistorante)) {
                return recensione;
            }
        }
        return null;
    }
    
    /**
     * Ottiene tutte le recensioni di un ristorante
     * @param idRistorante ID del ristorante
     * @return Lista delle recensioni
     */
    public List<Recenzione> getRecensioniRistorante(String idRistorante) {
        return recensioni.stream()
            .filter(r -> r.getIdRistorante().equals(idRistorante))
            .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutte le recensioni di un cliente
     * @param usernameCliente Username del cliente
     * @return Lista delle recensioni
     */
    public List<Recenzione> getRecensioniCliente(String usernameCliente) {
        return recensioni.stream()
            .filter(r -> r.getUsernameCliente().equals(usernameCliente))
            .collect(Collectors.toList());
    }
    
    /**
     * Calcola la media delle stelle per un ristorante
     * @param idRistorante ID del ristorante
     * @return Media delle stelle
     */
    public double calcolaMediaStelle(String idRistorante) {
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
    private void aggiornaValutazioneRistorante(String idRistorante) {
        List<Recenzione> recensioniRistorante = getRecensioniRistorante(idRistorante);
        double media = calcolaMediaStelle(idRistorante);
        int numeroRecensioni = recensioniRistorante.size();
        
        gestoreRistoranti.aggiornaValutazione(idRistorante, media, numeroRecensioni);
    }
    
    /**
     * Visualizza le recensioni di un ristorante
     * @param idRistorante ID del ristorante
     */
    public void visualizzaRecensioni(String idRistorante) {
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
     * @param usernameRistoratore Username del ristoratore
     * @param gestoreRistoranti Gestore ristoranti
     */
    public void visualizzaRiepilogo(String usernameRistoratore, GestoreRistoranti gestoreRistoranti) {
        List<Ristorante> ristoranti = gestoreRistoranti.getRistorantiPerRistoratore(usernameRistoratore);
        
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

