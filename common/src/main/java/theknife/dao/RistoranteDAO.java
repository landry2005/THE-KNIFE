package theknife.dao;

import theknife.model.Ristorante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la tabella 'ristoranti'.
 * Gestisce le operazioni CRUD relative ai ristoranti.
 */
public class RistoranteDAO {

    /**
     * Salva un nuovo ristorante nel database
     */
    public boolean salvaRistorante(Ristorante ristorante) {
        String sql = "INSERT INTO ristoranti (nome, nazione, citta, indirizzo, latitudine, longitudine, " +
                     "fascia_prezzo, delivery, prenotazione_online, tipo_cucina, id_gestore) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ristorante.getNome());
            pstmt.setString(2, ristorante.getNazione());
            pstmt.setString(3, ristorante.getCitta());
            pstmt.setString(4, ristorante.getIndirizzo());
            pstmt.setDouble(5, ristorante.getLatitudine());
            pstmt.setDouble(6, ristorante.getLongitudine());
            pstmt.setDouble(7, ristorante.getPrezzoMedio());
            pstmt.setBoolean(8, ristorante.isDelivery());
            pstmt.setBoolean(9, ristorante.isPrenotazione());
            pstmt.setString(10, ristorante.getTipoCucina());
            
            if (ristorante.getIdRistoratore() != null) {
                pstmt.setInt(11, ristorante.getIdRistoratore());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.err.println("Errore: Ristorante già esistente nel database.");
            } else {
                System.err.println("Errore SQL durante il salvataggio del ristorante: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Cerca un ristorante per ID numerico
     */
    public Ristorante trovaPerId(int id) {
        String sql = "SELECT * FROM ristoranti WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mappaRiga(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL ricerca ristorante per ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cerca ristoranti per città (Case Insensitive)
     */
    public List<Ristorante> cercaPerCitta(String citta) {
        List<Ristorante> lista = new ArrayList<>();
        // Usiamo ILIKE per rendere la ricerca insensibile alle maiuscole/minuscole
        String sql = "SELECT * FROM ristoranti WHERE citta ILIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + citta + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL ricerca per città: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Restituisce i ristoranti creati da un determinato gestore
     */
    public List<Ristorante> getRistorantiPerRistoratore(int idGestore) {
        List<Ristorante> lista = new ArrayList<>();
        String sql = "SELECT * FROM ristoranti WHERE id_gestore = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idGestore);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mappaRiga(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL ricerca ristoranti per gestore: " + e.getMessage());
        }
        return lista;
    }
    public List<Ristorante> cercaConCriteri(
        String citta,
        String tipoCucina,
        Double prezzoMin,
        Double prezzoMax,
        Boolean delivery,
        Boolean prenotazione,
        Double stelleMin) {

    List<Ristorante> risultati = new ArrayList<>();

    StringBuilder sql = new StringBuilder(
        "SELECT r.*, " +
        "COALESCE(v.media_stelle, 0) AS media_stelle, " +
        "COALESCE(v.numero_recensioni, 0) AS numero_recensioni " +
        "FROM ristoranti r " +
        "LEFT JOIN vista_valutazioni_ristoranti v ON v.id = r.id " +
        "WHERE r.citta ILIKE ?"
    );

    List<Object> parametri = new ArrayList<>();
    parametri.add("%" + citta + "%");

    if (tipoCucina != null && !tipoCucina.isBlank()) {
        sql.append(" AND r.tipo_cucina ILIKE ?");
        parametri.add("%" + tipoCucina + "%");
    }

    if (prezzoMin != null) {
        sql.append(" AND r.fascia_prezzo >= ?");
        parametri.add(prezzoMin);
    }

    if (prezzoMax != null) {
        sql.append(" AND r.fascia_prezzo <= ?");
        parametri.add(prezzoMax);
    }

    if (Boolean.TRUE.equals(delivery)) {
        sql.append(" AND r.delivery = TRUE");
    }

    if (Boolean.TRUE.equals(prenotazione)) {
        sql.append(" AND r.prenotazione_online = TRUE");
    }

    if (stelleMin != null) {
        sql.append(" AND COALESCE(v.media_stelle, 0) >= ?");
        parametri.add(stelleMin);
    }

    sql.append(" ORDER BY r.nome");

    try (
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql.toString())
    ) {

        for (int i = 0; i < parametri.size(); i++) {
            pstmt.setObject(i + 1, parametri.get(i));
        }

        try (ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Ristorante ristorante = mappaRiga(rs);

                ristorante.setMediaStelle(
                    rs.getDouble("media_stelle")
                );

                ristorante.setNumeroRecensioni(
                    rs.getInt("numero_recensioni")
                );

                risultati.add(ristorante);
            }
        }

    } catch (SQLException e) {
        System.err.println(
            "Errore SQL durante la ricerca avanzata: "
            + e.getMessage()
        );
    }

    return risultati;
}

    /**
     * Metodo utility privato per mappare una riga del ResultSet in un oggetto Ristorante
     */
    private Ristorante mappaRiga(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String nazione = rs.getString("nazione");
        String citta = rs.getString("citta");
        String indirizzo = rs.getString("indirizzo");
        double lat = rs.getDouble("latitudine");
        double lon = rs.getDouble("longitudine");
        double prezzo = rs.getDouble("fascia_prezzo");
        boolean delivery = rs.getBoolean("delivery");
        boolean prenotazione = rs.getBoolean("prenotazione_online");
        String cucina = rs.getString("tipo_cucina");
        
        // Gestione chiave esterna nullable
        int idGest = rs.getInt("id_gestore");
        Integer idGestore = rs.wasNull() ? null : idGest;
        
        return new Ristorante(id, nome, nazione, citta, indirizzo, lat, lon, cucina, 
                              prezzo, delivery, prenotazione, idGestore);
    }
}