package theknife.server;

import theknife.dao.PreferitoDAO;
import theknife.dao.RecensioneDAO;
import theknife.gestione.GestoreRistoranti;
import theknife.gestione.GestoreUtenti;
import theknife.model.Recenzione;
import theknife.model.Ristorante;
import theknife.network.Request;
import theknife.network.Response;
import theknife.network.SearchCriteria;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GestoreUtenti gestoreUtenti;
    private final GestoreRistoranti gestoreRistoranti;
    private final PreferitoDAO preferitoDAO;
    private final RecensioneDAO recensioneDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.gestoreUtenti = new GestoreUtenti();
        this.gestoreRistoranti = new GestoreRistoranti();
        this.preferitoDAO = new PreferitoDAO();
        this.recensioneDAO = new RecensioneDAO();
    }

    @Override
    public void run() {

        try (
            ObjectOutputStream out =
                new ObjectOutputStream(socket.getOutputStream());

            ObjectInputStream in =
                new ObjectInputStream(socket.getInputStream())
        ) {

            Request request =
                (Request) in.readObject();

            System.out.println(
                "Richiesta ricevuta: "
                + request.getType()
            );

            Response response;

            switch (request.getType()) {

                case LOGIN:

                    String username =
                        (String) request.getData("username");

                    String password =
                        (String) request.getData("password");

                    boolean loginOk =
                        gestoreUtenti.login(
                            username,
                            password
                        );

                    if (loginOk) {

                        response = new Response(
                            true,
                            "Login effettuato con successo",
                            gestoreUtenti.getUtenteCorrente()
                        );

                    } else {

                        response = new Response(
                            false,
                            "Username o password non validi"
                        );
                    }

                    break;

                case REGISTER:

                    String nome =
                        (String) request.getData("nome");

                    String cognome =
                        (String) request.getData("cognome");

                    String usernameRegistrazione =
                        (String) request.getData("username");

                    String passwordRegistrazione =
                        (String) request.getData("password");

                    String ruolo =
                        (String) request.getData("ruolo");

                    LocalDate dataNascita =
                        (LocalDate) request.getData("dataNascita");

                    String cittaDomicilio =
                        (String) request.getData("cittaDomicilio");

                    String domandaSicurezza =
                        (String) request.getData("domandaSicurezza");

                    String rispostaSicurezza =
                        (String) request.getData("rispostaSicurezza");

                    boolean registrazioneOk =
                        gestoreUtenti.registrazione(
                            nome,
                            cognome,
                            usernameRegistrazione,
                            passwordRegistrazione,
                            ruolo,
                            dataNascita,
                            cittaDomicilio,
                            domandaSicurezza,
                            rispostaSicurezza
                        );

                    response = new Response(
                        registrazioneOk,
                        registrazioneOk
                            ? "Registrazione effettuata con successo"
                            : "Username già esistente o errore durante la registrazione"
                    );

                    break;

                case SEARCH:

                    SearchCriteria criteri =
                        (SearchCriteria)
                            request.getData("criteria");

                    if (criteri == null
                            || criteri.getCitta() == null
                            || criteri.getCitta().isBlank()) {

                        response = new Response(
                            false,
                            "La città è obbligatoria"
                        );

                        break;
                    }

                    List<Ristorante> risultati =
                        gestoreRistoranti.cercaRistorante(
                            criteri.getCitta(),
                            criteri.getTipoCucina(),
                            criteri.getPrezzoMin(),
                            criteri.getPrezzoMax(),
                            criteri.getDelivery(),
                            criteri.getPrenotazione(),
                            criteri.getStelleMin()
                        );

                    response = new Response(
                        true,
                        "Ricerca completata",
                        risultati
                    );

                    break;

                case ADD_FAVORITE:

                    int idUtentePreferito =
                        (Integer)
                            request.getData("idUtente");

                    int idRistorantePreferito =
                        (Integer)
                            request.getData("idRistorante");

                    boolean preferitoAggiunto =
                        preferitoDAO.aggiungiPreferito(
                            idUtentePreferito,
                            idRistorantePreferito
                        );

                    response = new Response(
                        preferitoAggiunto,
                        preferitoAggiunto
                            ? "Ristorante aggiunto ai preferiti"
                            : "Ristorante già presente nei preferiti"
                    );

                    break;

                case REMOVE_FAVORITE:

                    int idUtenteRimozione =
                        (Integer)
                            request.getData("idUtente");

                    int idRistoranteRimozione =
                        (Integer)
                            request.getData("idRistorante");

                    boolean preferitoRimosso =
                        preferitoDAO.rimuoviPreferito(
                            idUtenteRimozione,
                            idRistoranteRimozione
                        );

                    response = new Response(
                        preferitoRimosso,
                        preferitoRimosso
                            ? "Ristorante rimosso dai preferiti"
                            : "Ristorante non presente nei preferiti"
                    );

                    break;

                case GET_FAVORITES:

                    int idUtentePreferiti =
                        (Integer)
                            request.getData("idUtente");

                    List<Ristorante> preferiti =
                        preferitoDAO.getPreferiti(
                            idUtentePreferiti
                        );

                    response = new Response(
                        true,
                        "Preferiti recuperati",
                        preferiti
                    );

                    break;

                case ADD_REVIEW:

                    int idUtenteRecensione =
                        (Integer)
                            request.getData("idUtente");

                    int idRistoranteRecensione =
                        (Integer)
                            request.getData("idRistorante");

                    int stelle =
                        (Integer)
                            request.getData("stelle");

                    String testo =
                        (String)
                            request.getData("testo");

                    if (stelle < 1 || stelle > 5) {

                        response = new Response(
                            false,
                            "Il numero di stelle deve essere compreso tra 1 e 5"
                        );

                        break;
                    }

                    if (recensioneDAO.hasRecensione(
                            idUtenteRecensione,
                            idRistoranteRecensione)) {

                        response = new Response(
                            false,
                            "Hai già recensito questo ristorante"
                        );

                        break;
                    }

                    Recenzione nuovaRecensione =
                        new Recenzione(
                            -1,
                            idUtenteRecensione,
                            idRistoranteRecensione,
                            stelle,
                            testo,
                            LocalDateTime.now(),
                            null,
                            null
                        );

                    boolean recensioneAggiunta =
                        recensioneDAO.salvaRecensione(
                            nuovaRecensione
                        );

                    response = new Response(
                        recensioneAggiunta,
                        recensioneAggiunta
                            ? "Recensione aggiunta con successo"
                            : "Errore durante l'aggiunta della recensione"
                    );

                    break;

                case EDIT_REVIEW:

                    int idUtenteModifica =
                        (Integer)
                            request.getData("idUtente");

                    int idRistoranteModifica =
                        (Integer)
                            request.getData("idRistorante");

                    int nuoveStelle =
                        (Integer)
                            request.getData("stelle");

                    String nuovoTesto =
                        (String)
                            request.getData("testo");

                    boolean recensioneModificata =
                        recensioneDAO.modificaRecensione(
                            idUtenteModifica,
                            idRistoranteModifica,
                            nuoveStelle,
                            nuovoTesto
                        );

                    response = new Response(
                        recensioneModificata,
                        recensioneModificata
                            ? "Recensione modificata con successo"
                            : "Recensione non trovata"
                    );

                    break;

                case DELETE_REVIEW:

                    int idUtenteElimina =
                        (Integer)
                            request.getData("idUtente");

                    int idRistoranteElimina =
                        (Integer)
                            request.getData("idRistorante");

                    boolean recensioneEliminata =
                        recensioneDAO.eliminaRecensione(
                            idUtenteElimina,
                            idRistoranteElimina
                        );

                    response = new Response(
                        recensioneEliminata,
                        recensioneEliminata
                            ? "Recensione eliminata con successo"
                            : "Recensione non trovata"
                    );

                    break;

                case GET_REVIEWS:

                    int idRistoranteRecensioni =
                        (Integer)
                            request.getData("idRistorante");

                    List<Recenzione> recensioni =
                        recensioneDAO
                            .getRecensioniPerRistorante(
                                idRistoranteRecensioni
                            );

                    response = new Response(
                        true,
                        "Recensioni recuperate",
                        recensioni
                    );

                    break;

                case ADD_RESTAURANT:

                    Ristorante nuovoRistorante =
                        (Ristorante)
                            request.getData("ristorante");

                    if (nuovoRistorante == null) {

                        response = new Response(
                            false,
                            "Dati del ristorante non validi"
                        );

                        break;
                    }

                    boolean ristoranteAggiunto =
                        gestoreRistoranti
                            .aggiungiRistorante(
                                nuovoRistorante
                            );

                    response = new Response(
                        ristoranteAggiunto,
                        ristoranteAggiunto
                            ? "Ristorante aggiunto con successo"
                            : "Errore durante l'aggiunta del ristorante"
                    );

                    break;

                case GET_MY_RESTAURANTS:

                    int idRistoratore =
                        (Integer)
                            request.getData("idRistoratore");

                    List<Ristorante> mieiRistoranti =
                        gestoreRistoranti
                            .getRistorantiPerRistoratore(
                                idRistoratore
                            );

                    response = new Response(
                        true,
                        "Ristoranti recuperati",
                        mieiRistoranti
                    );

                    break;

                case REPLY_REVIEW:

                    int idRecensione =
                        (Integer)
                            request.getData("idRecensione");

                    String risposta =
                        (String)
                            request.getData("risposta");

                    if (risposta == null
                            || risposta.isBlank()) {

                        response = new Response(
                            false,
                            "La risposta non può essere vuota"
                        );

                        break;
                    }

                    boolean rispostaAggiunta =
                        recensioneDAO
                            .rispondiRecensione(
                                idRecensione,
                                risposta
                            );

                    response = new Response(
                        rispostaAggiunta,
                        rispostaAggiunta
                            ? "Risposta aggiunta con successo"
                            : "Impossibile aggiungere la risposta"
                    );

                    break;

                default:

                    response = new Response(
                        false,
                        "Tipo di richiesta non ancora gestito"
                    );

                    break;
            }

            out.writeObject(response);
            out.flush();

        } catch (IOException
                 | ClassNotFoundException e) {

            System.err.println(
                "Errore nella gestione del client: "
                + e.getMessage()
            );

        } finally {

            try {
                socket.close();

            } catch (IOException e) {

                System.err.println(
                    "Errore nella chiusura del socket: "
                    + e.getMessage()
                );
            }
        }
    }
}