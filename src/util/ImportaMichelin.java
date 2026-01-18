package util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Classe di utilità per importare dati dal file Michelin Guide CSV
 * e convertirli nel formato TheKnife.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class ImportaMichelin {
    
    private static final String FILE_INPUT = "data/michelin_my_maps.csv";
    private static final String FILE_OUTPUT = "data/ristoranti.dati";
    
    /**
     * Converte il simbolo del prezzo Michelin in un valore numerico
     * @param priceSymbol Simboli €, €€, €€€, €€€€
     * @return Prezzo medio stimato in euro
     */
    private static double convertiPrezzo(String priceSymbol) {
        if (priceSymbol == null || priceSymbol.isEmpty()) {
            return 10.0; // Default
        }
        
        int numEuro = 0;
        for (char c : priceSymbol.toCharArray()) {
            if (c == '€') numEuro++;
        }
        
        switch (numEuro) {
            case 1: return 20.0;  // €
            case 2: return 40.0;  // €€
            case 3: return 75.0;  // €€€
            case 4: return 120.0; // €€€€
            default: return 10.0;
        }
    }
    
    /**
     * Estrae la città dalla stringa Location
     * Es: "Munich, Germany" -> "Munich"
     * @param location Stringa location dal CSV Michelin
     * @return Nome della città
     */
    private static String estraiCitta(String location) {
        if (location == null || location.isEmpty()) {
            return "Unknown";
        }
        String[] parts = location.split(",");
        if (parts.length > 0) {
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    return trimmed;
                }
            }
        }
        return "Unknown";
    }
    
    /**
     * Estrae la nazione dalla stringa Location
     * Es: "Munich, Germany" -> "Germany"
     * @param location Stringa location dal CSV Michelin
     * @return Nome della nazione
     */
    private static String estraiNazione(String location) {
        if (location == null || location.isEmpty()) {
            return "Unknown";
        }
        String[] parts = location.split(",");
        if (parts.length > 0) {
            for (int i = parts.length - 1; i >= 0; i--) {
                String trimmed = parts[i].trim();
                if (!trimmed.isEmpty()) {
                    return trimmed;
                }
            }
        }
        return "Unknown";
    }
    
    /**
     * Pulisce una stringa CSV (rimuove virgolette e caratteri speciali)
     * @param field Campo CSV da pulire
     * @return Campo pulito
     */
    private static String pulisciCampo(String field) {
        if (field == null) return "";
        // Rimuove virgolette all'inizio e alla fine
        field = field.trim();
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
        }
        // Sostituisce virgolette doppie interne
        field = field.replace("\"\"", "\"");
        return field;
    }
    
    /**
     * Parsa una riga CSV gestendo campi tra virgolette
     * @param line Riga da parsare
     * @return Array di campi
     */
    private static String[] parsaRigaCSV(String line) {
        java.util.List<String> campi = new java.util.ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean inVirgolette = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inVirgolette = !inVirgolette;
            } else if (c == ',' && !inVirgolette) {
                campi.add(campo.toString());
                campo = new StringBuilder();
            } else {
                campo.append(c);
            }
        }
        campi.add(campo.toString());
        
        return campi.toArray(new String[0]);
    }
    
    /**
     * Importa i dati dal file Michelin e li converte nel formato TheKnife
     */
    public static void importaDati() {
        File inputFile = new File(FILE_INPUT);
        if (!inputFile.exists()) {
            System.err.println("ERRORE: File " + FILE_INPUT + " non trovato!");
            System.err.println("Assicurati che il file michelin_my_maps.csv sia nella directory del progetto.");
            return;
        }
        
        // Crea directory data se non esiste
        new File("data").mkdirs();
        
        int ristoratiImportati = 0;
        int righeIgnorate = 0;
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_OUTPUT), StandardCharsets.UTF_8))) {
            
            // Scrivi header
            bw.write("Nome,Nazione,Citta,Indirizzo,Latitudine,Longitudine,TipoCucina,PrezzoMedio,Delivery,Prenotazione,IdRistoratore,MediaStelle,NumeroRecensioni");
            bw.newLine();
            
            // Salta l'header del file Michelin
            String headerLine = br.readLine();
            System.out.println("Header Michelin: " + (headerLine != null ? "OK" : "Errore"));
            
            String line;
            int numeroRiga = 1;
            
            while ((line = br.readLine()) != null) {
                numeroRiga++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String[] campi = parsaRigaCSV(line);
                    
                    // Verifica che ci siano abbastanza campi
                    if (campi.length < 7) {
                        System.out.println("Riga " + numeroRiga + " ignorata: campi insufficienti");
                        righeIgnorate++;
                        continue;
                    }
                    
                    // Estrai i dati (ordine dal CSV Michelin)
                    // Name,Address,Location,Price,Cuisine,Longitude,Latitude,...
                    String nome = pulisciCampo(campi[0]);
                    String indirizzo = pulisciCampo(campi[1]);
                    String location = pulisciCampo(campi[2]);
                    String prezzo = pulisciCampo(campi[3]);
                    String cucina = pulisciCampo(campi[4]);
                    String longitudeStr = pulisciCampo(campi[5]);
                    String latitudeStr = pulisciCampo(campi[6]);
                    
                    // Salta ristoranti senza nome
                    if (nome.isEmpty()) {
                        righeIgnorate++;
                        continue;
                    }
                    
                    // Converti dati
                    String citta = estraiCitta(location);
                    String nazione = estraiNazione(location);
                    double prezzoMedio = convertiPrezzo(prezzo);
                    
                    // Parsa coordinate (gestisci errori)
                    double latitudine = 0.0;
                    double longitudine = 0.0;
                    try {
                        if (!longitudeStr.isEmpty()) {
                            longitudine = Double.parseDouble(longitudeStr);
                        }
                        if (!latitudeStr.isEmpty()) {
                            latitudine = Double.parseDouble(latitudeStr);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Riga " + numeroRiga + ": coordinate non valide, uso 0.0");
                    }
                    
                    // Scrivi nel file output (usa Locale.US per punto decimale)
                    bw.write(String.format(java.util.Locale.US, "%s,%s,%s,%s,%.7f,%.7f,%s,%.1f,%s,%s,,%s,%s",
                        nome.replace(",", ";"),  // Sostituisci virgole per evitare problemi CSV
                        nazione.replace(",", ";"),
                        citta.replace(",", ";"),
                        indirizzo.replace(",", ";"),
                        latitudine,
                        longitudine,
                        cucina.replace(",", "/"),  // Sostituisci virgole con /
                        prezzoMedio,
                        "false",  // delivery (non specificato da Michelin)
                        "true",   // prenotazione (presunto per ristoranti stellati)
                        "0.0",    // media stelle
                        "0"       // numero recensioni
                    ));
                    bw.newLine();
                    
                    ristoratiImportati++;
                    
                    // Progresso ogni 100 ristoranti
                    if (ristoratiImportati % 100 == 0) {
                        System.out.println("Importati: " + ristoratiImportati + " ristoranti...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Errore riga " + numeroRiga + ": " + e.getMessage());
                    righeIgnorate++;
                }
            }
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("         IMPORTAZIONE COMPLETATA!");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("Ristoranti importati: " + ristoratiImportati);
            System.out.println("Righe ignorate:       " + righeIgnorate);
            System.out.println("File di output:       " + FILE_OUTPUT);
            System.out.println("═══════════════════════════════════════════════════════");
            
        } catch (IOException e) {
            System.err.println("Errore durante l'importazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main per eseguire l'importazione standalone
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("    IMPORTATORE MICHELIN GUIDE -> THEKNIFE");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        System.out.println("File input:  " + FILE_INPUT);
        System.out.println("File output: " + FILE_OUTPUT);
        System.out.println("\nInizio importazione...\n");
        
        importaDati();
        
        System.out.println("\nImportazione terminata!");
        System.out.println("Ora puoi avviare TheKnife con: java -cp bin TheKnife");
    }
}

