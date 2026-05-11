package logic;

import java.io.Serializable;

/**
 * Aquesta classe serveix per guardar les dades basiques d'una partida.
 * Cada cop que algu comenca a jugar, creem un "objecte" d'aquests.
 * Implementem Serializable per poder desar la partida en un fitxer.
 *
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    // Declarem els atributs de les dades que volem recordar de la partida
    private String nomJugador1; // El nom que ha posat el jugador 1 al menu
    private String nomJugador2; // El nom que ha posat el jugador 2 al menu
    private String nickName1; // El nom que ha posat el jugador 1 al menu
    private String nickName2; // El nom que ha posat el jugador 2 al menu
    // Atribut per si ha triat Catala o Castella
    private String idioma;

    /**
     * Mètode getter que accedeix a idioma 
     * @return l'idioma triat
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Mètode setter que modifica idioma 
     * @param idioma, idioma escollit
     */
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    //Declaració i inicialització d'atributs privats
    private int nivell;     // El nivell on ha comencat o on es troba
    private long punts; // Els punts que portava la partida quan es va guardar
    
    //Declaració i inicialització de final
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


		/**
		 * Mètode getter que retorna el nivell actual
		 * @return nivell
		 */
		public int getNivell() {
			return nivell;
		}

		/**
		 * Mètode setter que modifica el nivell actual
		 * @param nivell, el nivell que es vol jugar
		 */
		public void setNivell(int nivell) {
			this.nivell = nivell;
		}

		/**
		 * Mètode getter que retorna els punts de la partida
		 * @return punts
		 */
		public long getPunts() {
			return punts;
		}

		/**
		 * Mètode setter que modifica els punts de la partida
		 * @param punts, els punts que te
		 */
		public void setPunts(long punts) {
			this.punts = punts;
		}}