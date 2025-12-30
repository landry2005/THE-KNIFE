package theknife.service;

import theknife.model.Utente;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestoreUtenti {

    // Percorso del file CSV degli utenti
    private final String filePath = "data/utenti.csv";

    // Lista degli utenti caricati in memoria
    private final List<Utente> utenti;

    // Costruttore
    public GestoreUtenti() {
        this.utenti = new ArrayList<>();

        // Assicura che la cartella "data" esista
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Carica gli utenti dal file CSV all'avvio
        caricaUtenti();
    }

    /**
     * Metodo che carica gli utenti dal file CSV
     */
    public final void caricaUtenti() {
        utenti.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            boolean primaLinea = true; // per saltare l'intestazione

            while ((linea = br.readLine()) != null) {

                // Salta la prima riga (intestazione CSV)
                if (primaLinea) {
                    primaLinea = false;
                    continue;
                }

                // Divide la riga usando il separatore ';'
                String[] dati = linea.split(";");

                // Controllo di sicurezza sul numero di campi
                if (dati.length < 7) {
                    continue;
                }

                String nome = dati[0];
                String cognome = dati[1];
                String username = dati[2];
                String password = dati[3];
                String ruolo = dati[4];

                // Data di nascita facoltativa
                LocalDate dataNascita = null;
                if (!dati[5].isEmpty()) {
                    dataNascita = LocalDate.parse(dati[5]);
                }

                String domicilio = dati[6];

                // Creazione dell'oggetto Utente
                Utente u = new Utente(
                        nome,
                        cognome,
                        username,
                        password,
                        ruolo,
                        dataNascita,
                        domicilio
                );

                utenti.add(u);
            }

            System.out.println("✓ Utenti caricati: " + utenti.size());

        } catch (FileNotFoundException e) {
            System.out.println("⚠ File utenti non trovato.");
        } catch (IOException e) {
            System.out.println("✗ Errore di lettura: " + e.getMessage());
        }
    }

    /**
     * Salva gli utenti nel file CSV
     */
    public void salvaUtenti() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            // Scrive l'intestazione
            pw.println("nome;cognome;username;password;ruolo;dataNascita;domicilio");

            // Scrive ogni utente come riga CSV
            for (Utente u : utenti) {
                pw.println(
                        u.getNome() + ";" +
                        u.getCognome() + ";" +
                        u.getUsername() + ";" +
                        u.getPassword() + ";" +
                        u.getRuolo() + ";" +
                        (u.getDataNascita() != null ? u.getDataNascita() : "") + ";" +
                        u.getLuogoDomicilio()
                );
            }

            System.out.println("✓ Utenti salvati");

        } catch (IOException e) {
            System.out.println("✗ Errore nel salvataggio: " + e.getMessage());
        }
    }

    /**
     * Registra un nuovo utente se lo username non esiste già
     */
    public boolean registraUtente(Utente utente) {

        if (usernameEsiste(utente.getUsername())) {
            System.out.println("✗ Username già esistente!");
            return false;
        }

        utenti.add(utente);
        salvaUtenti();
        System.out.println("✓ Utente registrato con successo!");
        return true;
    }

    /**
     * Effettua il login controllando username e password
     */
    public Utente login(String username, String password) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username) &&
                u.getPassword().equals(password)) {
                System.out.println("✓ Login effettuato!");
                return u;
            }
        }
        System.out.println("✗ Username o password errati!");
        return null;
    }

    /**
     * Controlla se uno username esiste già
     */
    public boolean usernameEsiste(String username) {
        for (Utente u : utenti) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
