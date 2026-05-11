package logic.db;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import logic.db.classes.Puntuaciones;
import logic.db.classes.Usuarios; // Entitat que guarda el nom

/**
 * Classe PuntuacionsRepository que connecta amb Hibernate, desa les puntuacions i realitza el ranking
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class PuntuacionsRepository {

    // Objecte utilitzat per gestionar la connexió amb Hibernate
    private final HibernateUtil hibernateUtil;

    // Declaració i incialització dels deu millors puntuacions
    private static final int LIMITACIO_DELS_DEU_MILLORS = 10;
    
    // Constructor que rep la configuració Hibernate
    public PuntuacionsRepository(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    /**
     * Mètode que guarda una puntuació a la base de dades.
     * Si l'usuari no existeix, es crea automàticament.
     * @param p, instància que es desa de la classe Puntuaciones
     * @param nickName, nickName del jugador
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
     * Mètode que retorna el Top 10 de puntuacions 
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
            .setMaxResults(LIMITACIO_DELS_DEU_MILLORS)

            // Executem la consulta
            .list();

        } catch (Exception e) {

            // Mostrem l'error
            e.printStackTrace();

            return null;
        }
    }
}