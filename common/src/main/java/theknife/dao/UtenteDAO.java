package theknife.dao;

import theknife.model.Utente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Data Access Object (DAO) per la gestione degli utenti nel database.
 * Fornisce metodi per operazioni CRUD (Create, Read, Update, Delete) sugli utenti.
 */
public class UtenteDAO {

    /**
     * Salva un nuovo utente nel database.
     * @param utente Oggetto Utente da salvare
     * @return true se l'operazione ha avuto successo, false altrimenti
     */

    public boolean salvaUtente(Utente utente) {
        String sql = "INSERT INTO utenti (nome,cognome,username, password_hash, ruolo,data_nascita,citta_domicilio,domanda_sicurezza,risposta_sicurezza) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getUsername());
            pstmt.setString(4, utente.getPasswordHash());
            pstmt.setString(5, utente.getRuolo());


            if (utente.getDataNascita() != null) {
                pstmt.setDate(6, Date.valueOf(utente.getDataNascita()));
            } else {
                pstmt.setNull(6, Types.DATE);
           
             }

            pstmt.setString(7, utente.getCittaDomicilio());
            pstmt.setString(8, utente.getDomandaSicurezza());
            pstmt.setString(9, utente.getRispostaSicurezza());
            
            int righeInserite = pstmt.executeUpdate();
            return righeInserite > 0;
        } catch (SQLException e) {
            //Se l'username è duplicato, PostgreSQL lancia un errore di violazione di vincolo UNIQUE
            if(e.getSQLState().equals("23505")) {
                System.err.println("Errore: Username già esistente.");
                return false;
            } 
        }
        System.err.println("Errore SQL durante l'inserimento dell'utente.");
        return false;
    }


    /**
     * Cerca un utente nel database tramite il suo username (per Login)
     * @param username Username dell'utente da cercare
     * @return Oggetto Utente se trovato, null altrimenti
     */

    public Utente trovaPerUsername(String username) {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String usernameDb = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String ruolo = rs.getString("ruolo");
                    
                    Date dataSql = rs.getDate("data_nascita");
                    LocalDate dataNascita = (dataSql != null) ? dataSql.toLocalDate() : null;
                    
                    String cittaDomicilio = rs.getString("citta_domicilio");
                    String domanda = rs.getString("domanda_sicurezza");
                    String risposta = rs.getString("risposta_sicurezza");
                 return new Utente(id, nome, cognome, usernameDb, passwordHash, ruolo, 
                                      dataNascita, cittaDomicilio, domanda, risposta);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore SQL durante la ricerca dell'utente: " + e.getMessage());
        }
        
        return null; // Utente non trovato
    }

       /**
     * Aggiorna la password di un utente nel database
     */
    public boolean aggiornaPassword(String username, String newPasswordHash) {
        String sql = "UPDATE utenti SET password_hash = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'aggiornamento della password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aggiorna il ruolo di un utente nel database
     */
    public boolean aggiornaRuolo(String username, String nuovoRuolo) {
        String sql = "UPDATE utenti SET ruolo = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuovoRuolo);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'aggiornamento del ruolo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un utente dal database
     */
    public boolean eliminaUtente(String username) {
        String sql = "DELETE FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'eliminazione dell'utente: " + e.getMessage());
            return false;
        }
    }
        /**
     * Aggiorna la domanda e risposta di sicurezza nel database
     */
    public boolean aggiornaDomandaSicurezza(String username, String domanda, String rispostaHash) {
        String sql = "UPDATE utenti SET domanda_sicurezza = ?, risposta_sicurezza = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, domanda);
            pstmt.setString(2, rispostaHash);
            pstmt.setString(3, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'aggiornamento della sicurezza: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ottiene tutti gli utenti dal database (per le statistiche admin)
     */
    public List<Utente> getTuttiUtenti() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM utenti";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String usernameDb = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String ruolo = rs.getString("ruolo");
                Date dataSql = rs.getDate("data_nascita");
                LocalDate dataNascita = (dataSql != null) ? dataSql.toLocalDate() : null;
                String cittaDomicilio = rs.getString("citta_domicilio");
                String domanda = rs.getString("domanda_sicurezza");
                String risposta = rs.getString("risposta_sicurezza");
                
                lista.add(new Utente(id, nome, cognome, usernameDb, passwordHash, ruolo, 
                                      dataNascita, cittaDomicilio, domanda, risposta));
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante il recupero degli utenti: " + e.getMessage());
        }
        return lista;
    }
}