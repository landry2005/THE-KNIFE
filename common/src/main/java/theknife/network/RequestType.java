package theknife.network;

/**
 * Tipi di richieste supportate dal protocollo client-server.
 *
 * @author Scafidi Michaela - 760101 - VA
 * @author Wafo Tene Wilfried Landry - 763687 - VA
 * @author Fotso Alex Castany - 762919 - VA
 */
public enum RequestType {

    LOGIN,

    REGISTER,

    SEARCH,

    GET_DETTAGLIO,

    // Recensioni
    ADD_REVIEW,
    EDIT_REVIEW,
    DELETE_REVIEW,
    GET_REVIEWS,
    REPLY_REVIEW,

    // Preferiti
    ADD_FAVORITE,
    REMOVE_FAVORITE,
    GET_FAVORITES,

    // Ristoratore
    ADD_RESTAURANT,
    GET_MY_RESTAURANTS,

    // Account
    GET_SUMMARY,
    CHANGE_PASSWORD,
    RESET_PASSWORD
}