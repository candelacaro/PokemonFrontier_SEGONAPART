package logic.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import logic.db.classes.Puntuaciones;
import logic.db.classes.Usuarios;

/*
 * Clase on guardem les dades dels nostres usuaris. Guardem el nom , el seu ID, la puntuacio, l'idioma i la data.
 */
public class PuntuacionsRepository {

    private final HibernateUtil hibernate;

    // El repositori necessita que li passis un HibernateUtil per funcionar
    public PuntuacionsRepository(HibernateUtil hibernate) {
        this.hibernate = hibernate;
    }

    public void guardarPuntuacion(String nicknameJugador, long punts) {
        Transaction tx = null;
        // Ara fem servir la instància 'hibernate' que hem rebut al constructor
        try (Session session = hibernate.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Usuarios usuari = buscarUsuari(session, nicknameJugador);
            if (usuari == null) {
                usuari = new Usuarios();
                usuari.setNombre(nicknameJugador);
                session.save(usuari);
            }

            Puntuaciones puntuacio = new Puntuaciones();
            puntuacio.setUsuarios(usuari);
            puntuacio.setPuntuacion(punts);
            puntuacio.setFecha(new Timestamp(System.currentTimeMillis()));
            puntuacio.setIdioma("Castellano");
            session.save(puntuacio);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    private Usuarios buscarUsuari(Session session, String nicknameJugador) {
        return session.createQuery("FROM Usuarios WHERE nombre = :nombre", Usuarios.class)
                .setParameter("nombre", nicknameJugador)
                .uniqueResult();
    }

    // Agafa les 10 millors puntuacions de la base de dades
    public List<Puntuacio> obtenirTop10() {
        List<Puntuacio> puntuacions = new ArrayList<>();
        // CORRECCIÓ: Ara fem servir la instància 'hibernate' en lloc de la crida estàtica
        try (Session session = hibernate.getSessionFactory().openSession()) {
            List<Puntuaciones> resultats = session
                    .createQuery("FROM Puntuaciones ORDER BY puntuacion DESC", Puntuaciones.class)
                    .setMaxResults(10)
                    .list();

            for (Puntuaciones puntuacio : resultats) {
                puntuacions.add(new Puntuacio(puntuacio.getUsuarios().getNombre(), puntuacio.getPuntuacion()));
            }
        } catch (final Exception e) {
            // Si la DB falla, simplement no ensenya la taula
        } 
        return puntuacions;
    }
}