package logic.db;

/**
 * Aquesta classe representa una puntuació del rànquing.
 */
public class Puntuacio {

    private final String nomJugador; // El nom del jugador
    private final long punts; // Els punts aconseguits

    public Puntuacio(final String nomJugador, final long punts) {
        this.nomJugador = nomJugador;
        this.punts = punts;
    }

    public String getNomJugador() {
        return nomJugador;
    }

    public long getPunts() {
        return punts;
    }
}
