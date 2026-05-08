package logic.db;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import logic.db.classes.Puntuaciones;
import logic.db.classes.Usuarios; // Entitat que guarda el nom

public class PuntuacionsRepository {

    private final HibernateUtil hibernateUtil;

    public PuntuacionsRepository(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    /**
     * Guarda la puntuació assegurant que l'usuari existeixi a la BD
     */
    public void guardarPuntuacio(Puntuaciones p, String nickName) {
        Session session = hibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Busquem l'usuari pel nom a la taula d'usuaris
            Query<Usuarios> query = session.createQuery("FROM Usuarios WHERE nombre = :n", Usuarios.class);
            query.setParameter("n", nickName);
            Usuarios user = query.uniqueResult();

            // Si no existeix l'usuari, el creem primer
            if (user == null) {
                user = new Usuarios();
                user.setNombre(nickName);
                session.save(user);
            }

            // Assignem l'usuari trobat/creat a l'objecte puntuació
            p.setUsuarios(user);
            
            // Guardem la puntuació
            session.save(p);

            tx.commit(); // CONFIRMA ELS CANVIS (Sense això surten NULLs o no es guarda)
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Obté el rànquing de les 10 millors puntuacions
     */
    public List<Puntuaciones> obtenirTop10() {
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            // "JOIN FETCH" porta el nom de l'usuari directament per evitar NULLs a la llista
            return session.createQuery("SELECT p FROM Puntuaciones p JOIN FETCH p.usuarios ORDER BY p.puntuacion DESC", Puntuaciones.class)
                          .setMaxResults(10)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}