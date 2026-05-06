package logic;

/**
 * Aquesta classe serveix per guardar les dades basiques d'una partida.
 * Cada cop que algu comenca a jugar, creem un "objecte" d'aquests.
 *
 * @author dam1
 */
public class Partida {

    // Declarem les dades que volem recordar de la partida
    private String nomJugador1; // El nom que ha posat el jugador 1 al menu
    private String nomJugador2; // El nom que ha posat el jugador 2 al menu
    private String nickName1;
    private String nickName2;
    private String idioma; // Si ha triat Catala o Castella
    private int nivell;     // El nivell on ha comencat o on es troba

    /**
     * El "constructor": es el que fa servir el programa per fabricar
     * una partida nova amb tota la informacio de cop.
     * @param nom1 El nom del jugador 1
     * @param nick1 El nickname del jugador 1
     * @param nom2 El nom del jugador 2
     * @param nick2 El nickname del jugador 2
     * @param idioma L'idioma seleccionat
     * @param nivell El nivell inicial
     */
    public Partida(String nom1, String nick1, String nom2, String nick2, String idioma, int nivell) {
        this.nomJugador1 = nom1; 
        this.nickName1 = nick1;
        this.nomJugador2 = nom2; 
        this.nickName2 = nick2;
        this.idioma = idioma; 
        this.nivell = nivell;   
    }

    /**
     * Mètode "getter" serveix per poder llegir el nom del jugador 1
     * des d'altres classes sense poder modificar-lo per error.
     *
     * @return El nom del jugador 1
     */
    public String getNomJugador1() {
        return nomJugador1; // Simplement ens torna el nom guardat
    }

    /**
     * Mètode "getter" serveix per poder llegir el nom del jugador 2
     * des d'altres classes sense poder modificar-lo per error.
     * @return El nom del jugador 2
     */
    public String getNomJugador2() {
        return nomJugador2; // Simplement ens torna el nom del segon jugador
    }

    /**
     * Mètode "getter" serveix per poder llegir el nom del nickName del jugador1
     * des d'altres classes sense poder modificar-lo per error.
     * @return el nickName del jugador 1
     */
	public String getNickName1() {
		return nickName1;
	}


	/**
	 * Mètode "getter" serveix per poder llegir el nickName del jugador 2
     * des d'altres classes sense poder modificar-lo per error.
	 * @return el nickName del jugador 2
	 */
	public String getNickName2() {
		return nickName2;
	}


    
    
}
