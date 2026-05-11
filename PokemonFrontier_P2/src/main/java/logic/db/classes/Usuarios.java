package logic.db.classes;
// Generated 5 may 2026, 12:11:25 by Hibernate Tools 6.5.1.Final

import java.util.HashSet;
import java.util.Set;

/**
 * Classe Usuarios que implementa serializable i també és implementada per hibernate
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Usuarios implements java.io.Serializable {

	// Identificador únic de l'usuari
	private int idUsuario;

	// Nom de l'usuari
	private String nombre;

	// Conjunt de puntuacions relacionades amb l'usuari
	private Set puntuacioneses = new HashSet(0);

	// Constructor buit necessari per Hibernate
	public Usuarios() {
	}

	// Constructor amb les dades principals
	public Usuarios(int idUsuario, String nombre) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
	}

	// Constructor complet amb les puntuacions
	public Usuarios(int idUsuario, String nombre, Set puntuacioneses) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.puntuacioneses = puntuacioneses;
	}

	// Retorna l'identificador de l'usuari
	public int getIdUsuario() {
		return this.idUsuario;
	}

	// Assigna l'identificador de l'usuari
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	// Retorna el nom de l'usuari
	public String getNombre() {
		return this.nombre;
	}

	// Assigna el nom de l'usuari
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// Retorna totes les puntuacions de l'usuari
	public Set getPuntuacioneses() {
		return this.puntuacioneses;
	}

	// Assigna les puntuacions de l'usuari
	public void setPuntuacioneses(Set puntuacioneses) {
		this.puntuacioneses = puntuacioneses;
	}

}