package logic;

import java.io.Serializable;

/**
 * Aquesta classe serveix per guardar les dades basiques d'una partida.
 * Cada cop que algu comenca a jugar, creem un "objecte" d'aquests.
 * Implementem Serializable per poder desar la partida en un fitxer.
 *
 * @author dam1
 */
public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    // Declarem les dades que volem recordar de la partida
    private String nomJugador1; // El nom que ha posat el jugador 1 al menu
    private String nomJugador2; // El nom que ha posat el jugador 2 al menu
    private String nickName1; // El nom que ha posat el jugador 1 al menu
    private String nickName2; // El nom que ha posat el jugador 2 al menu
 // Si ha triat Catala o Castella
    private String idioma;

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    private int nivell;     // El nivell on ha comencat o on es troba
    private long punts; // Els punts que portava la partida quan es va guardar
    private static final int PUNTUACIO_DE_INICI = 0; // Puntuacio de inici dels jugadors

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
    public Partida(String nomJugador1, String nickName1, String nomJugador2, String nickName2, String idioma, int nivell) {
        this.nomJugador1 = nomJugador1; 
        this.nickName1 = nickName1;
        this.nomJugador2 = nomJugador2; 
        this.nickName2 = nickName2;
        this.idioma = idioma; 
        this.nivell = nivell;
        this.punts = PUNTUACIO_DE_INICI;
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


	// Retorna el nivell actual
		public int getNivell() {
			return nivell;
		}

		// Modifica el nivell actual
		public void setNivell(int nivell) {
			this.nivell = nivell;
		}

		// Retorna els punts de la partida
		public long getPunts() {
			return punts;
		}

		// Modifica els punts de la partida
		public void setPunts(long punts) {
			this.punts = punts;
		}}