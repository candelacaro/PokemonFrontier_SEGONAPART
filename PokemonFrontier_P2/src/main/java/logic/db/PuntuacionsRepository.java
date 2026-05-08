package logic.db;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import logic.db.classes.Puntuaciones;
import logic.db.classes.Usuarios; // Entitat que guarda el nom

public class PuntuacionsRepository {

    // Objecte utilitzat per gestionar la connexió amb Hibernate
    private final HibernateUtil hibernateUtil;

    // Constructor que rep la configuració Hibernate
    public PuntuacionsRepository(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    /**
     * Guarda una puntuació a la base de dades.
     * Si l'usuari no existeix, es crea automàticament.
     */
    public void guardarPuntuacio(Puntuaciones p, String nickName) {

        // Obrim una sessió amb la base de dades
        Session session = hibernateUtil.getSessionFactory().openSession();

        // Variable per controlar la transacció
        Transaction tx = null;

        try {

            // Iniciem la transacció
            tx = session.beginTransaction();

            // Busquem l'usuari pel nom
            Query<Usuarios> query = session.createQuery(
                    "FROM Usuarios WHERE nombre = :n",
                    Usuarios.class
            );

            // Assignem el valor del paràmetre
            query.setParameter("n", nickName);

            // Guardem el resultat de la consulta
            Usuarios user = query.uniqueResult();

            // Si l'usuari no existeix, el creem
            if (user == null) {

                user = new Usuarios();

                // Assignem el nom introduït
                user.setNombre(nickName);

                // Guardem l'usuari a la BD
                session.save(user);
            }

            // Relacionem la puntuació amb l'usuari
            p.setUsuarios(user);

            // Guardem la puntuació
            session.save(p);

            // Confirmem els canvis
            tx.commit();

        } catch (Exception e) {

            // Si hi ha error, desfem la transacció
            if (tx != null)
                tx.rollback();

            // Mostrem l'error per consola
            e.printStackTrace();

        } finally {

            // Tanquem la sessió
            session.close();
        }
    }

    /**
     * Retorna el Top 10 de puntuacions.
     */
    public List<Puntuaciones> obtenirTop10() {

        // Obrim la sessió automàticament
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {

            // Consulta per obtenir les puntuacions ordenades de major a menor
            return session.createQuery(
                    "SELECT p FROM Puntuaciones p " +
                    "JOIN FETCH p.usuarios " +
                    "ORDER BY p.puntuacion DESC",
                    Puntuaciones.class
            )

            // Limitem el resultat a 10 puntuacions
            .setMaxResults(10)

            // Executem la consulta
            .list();

        } catch (Exception e) {

            // Mostrem l'error
            e.printStackTrace();

            return null;
        }
    }
}