package logic.db.classes;
// Generated 5 may 2026, 12:11:25 by Hibernate Tools 6.5.1.Final

import java.sql.Timestamp;

/**
 * Classe Puntuaciones implementada des de hibernate
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Puntuaciones implements java.io.Serializable {

	// Identificador únic de la puntuació
	private int idPuntuacion;

	// Usuari relacionat amb la puntuació
	private Usuarios usuarios;

	// Valor de la puntuació aconseguida
	private long puntuacion;

	// Data i hora en què s'ha guardat la puntuació
	private Timestamp fecha;

	// Idioma utilitzat durant la partida
	private String idioma;

	// Constructor buit necessari per Hibernate
	public Puntuaciones() {
	}

	// Constructor amb les dades principals
	public Puntuaciones(int idPuntuacion, Usuarios usuarios, long puntuacion, Timestamp fecha) {
		this.idPuntuacion = idPuntuacion;
		this.usuarios = usuarios;
		this.puntuacion = puntuacion;
		this.fecha = fecha;
	}

	// Constructor complet amb idioma
	public Puntuaciones(int idPuntuacion, Usuarios usuarios, long puntuacion, Timestamp fecha, String idioma) {
		this.idPuntuacion = idPuntuacion;
		this.usuarios = usuarios;
		this.puntuacion = puntuacion;
		this.fecha = fecha;
		this.idioma = idioma;
	}

	// Retorna l'identificador de la puntuació
	public int getIdPuntuacion() {
		return this.idPuntuacion;
	}

	// Assigna un identificador a la puntuació
	public void setIdPuntuacion(int idPuntuacion) {
		this.idPuntuacion = idPuntuacion;
	}

	// Retorna l'usuari relacionat
	public Usuarios getUsuarios() {
		return this.usuarios;
	}

	// Assigna un usuari a la puntuació
	public void setUsuarios(Usuarios usuarios) {
		this.usuarios = usuarios;
	}

	// Retorna la puntuació guardada
	public long getPuntuacion() {
		return this.puntuacion;
	}

	// Assigna el valor de la puntuació
	public void setPuntuacion(long puntuacion) {
		this.puntuacion = puntuacion;
	}

	// Retorna la data de la partida
	public Timestamp getFecha() {
		return this.fecha;
	}

	// Assigna la data de la partida
	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	// Retorna l'idioma de la partida
	public String getIdioma() {
		return this.idioma;
	}

	// Assigna l'idioma utilitzat
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	// Mètode pendent d'implementar
	public Object getNomJugador() {

		// TODO: afegir funcionalitat
		return null;
	}

}
