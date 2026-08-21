package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Classe di utilità per importare dati dal file Michelin Guide CSV
 * direttamente nel database PostgreSQL di TheKnife.
 * 
 * @author [Nome Cognome - Matricola - Sede]
 */
public class ImportaMichelin {
    
    private static final String FILE_INPUT = "data/michelin_my_maps.csv";
    private static final String DB_PROPS_PATH = "db.properties.env"; //Percorso del file con credenziali db
    
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
     * Importa i dati dal file Michelin e li inserisce nel database PostgreSQL di TheKnife
     */
    public static void importaDati() {
        File inputFile = new File(FILE_INPUT); //Crea oggetto file per il csv
        if (!inputFile.exists()) {
            System.err.println("ERRORE: File " + FILE_INPUT + " non trovato!");
            System.err.println("Assicurati che il file michelin_my_maps.csv sia nella directory del progetto.");
            return;
        }
        
        // Caricamento credenziali DB
        Properties props = new Properties();
        try (FileReader fr = new FileReader(DB_PROPS_PATH)) { //Apre file db.properties.env
            props.load(fr); //Carica credenziali
        } catch (IOException e) {
            System.err.println("ERRORE: Impossibile caricare le credenziali del database."); 
            e.printStackTrace();
            return;
        }

         // Costruzione URL JDBC (es. jdbc:postgresql://localhost:5432/theknifedb)
        String jdbcUrl = "jdbc:postgresql://" + props.getProperty("db.host") + ":" + props.getProperty("db.port") + "/" + props.getProperty("db.name"); // Crea l'URL
        String user = props.getProperty("db.user"); // Prende l'utente
        String password = props.getProperty("db.password"); // Prende la password

        // Query SQL di inserimento con PreparedStatement
       String insertSQL = "INSERT INTO ristoranti (nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo, tipo_cucina, delivery, prenotazione_online) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (nome, indirizzo) DO NOTHING;";
        int righeIgnorate = 0; //Righe saltate
        int ristorantiImportati = 0; //Ristoranti importati

        //Chiudere automaticamente Connection ,PreparedStatement e BufferReader
        try (Connection conn= DriverManager.getConnection(jdbcUrl,user,password);
            PreparedStatement pstmt = conn.prepareStatement(insertSQL); //Prepara query
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))){ //Apre il CSV
                
            
            // Salta l'header del file Michelin
            String headerLine = br.readLine();
            System.out.println("Header Michelin saltato: " + (headerLine != null ? "OK" : "Errore"));
            
            String line;
            int numeroRiga = 1; //Contatore righe per debug
            
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
                    
                //Imposta i parametri della query SQL
                pstmt.setString(1,nome);
                pstmt.setString(2,nazione);
                pstmt.setString(3,citta);
                pstmt.setString(4,indirizzo);
                pstmt.setDouble(5,latitudine);
                pstmt.setDouble(6,longitudine);
                pstmt.setDouble(7,prezzoMedio);
                pstmt.setString(8,cucina);
                pstmt.setBoolean(9,false);// Sostituisce il 9° ? con delivery (false di default)
                pstmt.setBoolean(10,true);// Sostituisce il 10° ? con prenotazione (true di default)

                pstmt.executeUpdate(); //Esegue l'INSERT nel db

                    ristorantiImportati++;
                    
                    // Progresso ogni 100 ristoranti
                    if (ristorantiImportati % 100 == 0) {
                        System.out.println("Importati nel DB: " + ristorantiImportati + " ristoranti...");
                    }
                    
                } catch (Exception e) {
                    System.err.println("Errore riga " + numeroRiga + ": " + e.getMessage());
                    righeIgnorate++;
                }
            }
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("         IMPORTAZIONE DATABASE COMPLETATA!");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("Ristoranti inseriti nel DB: " + ristorantiImportati);
            System.out.println("Righe ignorate:       " + righeIgnorate);
            System.out.println("═══════════════════════════════════════════════════════");
            
        } catch (SQLException e) { //Errori connessione db
            System.err.println("Errore fatale di connessione al database: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e){
            System.err.println("Errore durante la lettura del file CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main per eseguire l'importazione standalone
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("    IMPORTATORE MICHELIN GUIDE -> POSTGRESQL");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        System.out.println("File input:  " + FILE_INPUT);
        System.out.println("\nInizio importazione nel database...\n");
        
        importaDati();
        
        System.out.println("\nImportazione terminata!");
        System.out.println("Ora puoi avviare TheKnife con: java -cp bin TheKnife");
    }
}

