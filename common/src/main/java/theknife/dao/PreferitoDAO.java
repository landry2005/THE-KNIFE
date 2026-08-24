package theknife.dao;

import theknife.model.Ristorante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la tabella 'preferiti'.
 * Gestisce la relazione N:N tra utenti e ristoranti
 */

public class PreferitoDAO {
    /**
     * Aggiunge un ristorante ai preferiti di un utente
     */
    public boolean aggiungiPreferito(int idUtente, int idRistorante) {
        String sql = "INSERT INTO preferiti (id_utente, id_ristorante) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idRistorante);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
                System.err.println("Errore SQL aggiungi preferito: " + e.getMessage());
                return false;
            }
        }

        /** 
         * Rimuove un ristorante dai preferiti di un utente
        */
    public boolean rimuoviPreferito(int idUtente, int idRistorante) {
        String sql = "DELETE FROM preferiti WHERE id_utente = ? AND id_ristorante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idRistorante);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore SQL rimuovi preferito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Controlla se un ristorante è tra i preferiti di un utente
     */
    public boolean isPreferito(int idUtente, int idRistorante) {
        String sql = "SELECT 1 FROM preferiti WHERE id_utente = ? AND id_ristorante = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idRistorante);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL verifica preferito: " + e.getMessage());
        }
        return false;
    }

    /**
     * Restituisce il numero di ristoranti preferiti da un utente
     */
    public List<Ristorante> getPreferiti(int idUtente) {
        List<Ristorante> lista = new ArrayList<>();
        // Prendiamo solo gli ID dei ristoranti salvati nei preferiti
        String sql = "SELECT id_ristorante FROM preferiti WHERE id_utente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idRist = rs.getInt("id_ristorante");
                    // Chiediamo i dettagli del ristorante al RistoranteDAO
                    Ristorante r = new RistoranteDAO().trovaPerId(idRist);
                    if (r != null) {
                        lista.add(r);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL get preferiti: " + e.getMessage());
        }
        return lista;
    }
}

