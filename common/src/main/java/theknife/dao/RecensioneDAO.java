package theknife.dao;

import theknife.model.Recenzione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


/**
 * Data Access Object per la tabella 'recensioni'.
 * Gestisce le operazioni CRUD relative alle recensioni.
 * 
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
*/

public class RecensioneDAO {

    /**
     * Salva nuova recensione nel db
    */

    public boolean salvaRecensione(Recenzione recensione){
        String sql = "INSERT INTO  recensioni (id_utente,id_ristorante,stelle,testo,data_creazione) VALUES (?,?,?,?,?)";
    
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, recensione.getIdUtente());
            pstmt.setInt(2, recensione.getIdRistorante());
            pstmt.setInt(3, recensione.getStelle());
            pstmt.setString(4, recensione.getTesto());
            pstmt.setTimestamp(5, Timestamp.valueOf(recensione.getDataOra()!=null ? recensione.getDataOra() : LocalDateTime.now()));

            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            //Vincolo UNIQUE violato (l'utente ha già recensito questo ristorante
            if ("23505".equals(e.getSQLState())) {
                System.err.println("Errore: Recensione già esistente nel database.");
            } else {
                System.err.println("Errore SQL durante il salvataggio della recensione: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Verifica se un utente ha già recensito un ristorante
    */
    
    public boolean hasRecensione (int idUtente, int idRistorante){
        String sql ="SELECT 1 FROM recensioni WHERE id_utente = ? AND id_ristorante = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idRistorante);
            
            try(ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Se c'è almeno una riga, l'utente ha già recensito il ristorante
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante la verifica della recensione: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ottiene tutte le recensioni di un ristorante
    */
    public List<Recenzione> getRecensioniPerRistorante(int idRistorante) {
        List<Recenzione> lista=new ArrayList<>();
        String sql = "SELECT * FROM recensioni WHERE id_ristorante = ? ORDER BY data_creazione DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRistorante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante il recupero delle recensioni: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Ottiene tutte le recensioni di un utente
    */
   public List<Recenzione> getRecensioniPerUtente(int idUtente) {
        List<Recenzione> lista=new ArrayList<>();
        String sql = "SELECT * FROM recensioni WHERE id_utente = ? ORDER BY data_creazione DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUtente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL durante il recupero delle recensioni: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Modifica le stelle e il testo di una recensione esistente
     */
    public boolean modificaRecensione(int idUtente, int idRistorante, int nuoveStelle, String nuovoTesto) {
        String sql = "UPDATE recensioni SET stelle = ?, testo = ? WHERE id_utente = ? AND id_ristorante = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuoveStelle);
            pstmt.setString(2, nuovoTesto);
            pstmt.setInt(3, idUtente);
            pstmt.setInt(4, idRistorante);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore SQL durante la modifica della recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una recensione esistente
     */
    public boolean eliminaRecensione(int idUtente, int idRistorante) {
        String sql = "DELETE FROM recensioni WHERE id_utente = ? AND id_ristorante = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, idRistorante);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'eliminazione della recensione: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Aggiunge la risposta del ristoratore a una recensione esistente
     */
    public boolean rispondiRecensione(int idRecensione,String risposta){
        String sql = "UPDATE recensioni SET risposta = ?,data_risposta = ? WHERE id = ? AND risposta IS NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, risposta);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(3, idRecensione);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore SQL durante l'aggiunta della risposta alla recensione: " + e.getMessage());
            return false;
        }
    }

    /**
     * Metodo utility privato per mappare una riga del ResultSet in un oggetto Recenzione
     */
    private Recenzione mappaRiga(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int idUtente = rs.getInt("id_utente");
        int idRistorante = rs.getInt("id_ristorante");
        int stelle = rs.getInt("stelle");
        String testo = rs.getString("testo");
       
        Timestamp tsCreazione = rs.getTimestamp("data_creazione");
        LocalDateTime dataOra = (tsCreazione != null) ? tsCreazione.toLocalDateTime() : LocalDateTime.now();
       
        String risposta = rs.getString("risposta");
        
        Timestamp tsRisposta = rs.getTimestamp("data_risposta");
        LocalDateTime dataRisposta = (tsRisposta != null) ? tsRisposta.toLocalDateTime() : null;
        
        return new Recenzione(id, idUtente, idRistorante, stelle, testo, dataOra, risposta, dataRisposta);
    }
}
