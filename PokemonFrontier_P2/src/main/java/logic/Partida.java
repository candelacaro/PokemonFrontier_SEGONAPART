package logic;

/**
 * Aquesta classe serveix per guardar les dades bàsiques d'una partida 
 * Cada cop que algú comença a jugar, creem un "objecte" d'aquests.
 * 
 * @author dam1
 */
public class Partida {

    // Declarem les dades que volem recordar de la partida
    private String nom; // El nom que ha posat el jugador al menú
    private String idioma; // Si ha triat Català o Castellà
    private int nivell;     // El nivell on ha començat o on es troba

    /**
     * El "constructor": és el que fa servir el programa per fabricar 
     * una partida nova amb tota la informació de cop.
     * 
     * @param nombre El nom del jugador
     * @param idioma L'idioma seleccionat
     * @param nivel El nivell inicial
     */
    public Partida(String nom, String idioma, int nivell) {
        this.nom = nom; // Guardem el nom que ens passen a la nostra variable
        this.idioma = idioma; // Guardem l'idioma
        this.nivell = nivell;   // Guardem el nivell
    }

    /**
     * Aquest "getter" serveix per poder llegir el nom del jugador 
     * des d'altres classes sense poder modificar-lo per error.
     * 
     * @return El nom del jugador
     */
    public String getNom() {
        return nom; // Simplement ens torna el nom guardat
    }
}