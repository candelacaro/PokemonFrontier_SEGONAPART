package logic.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Gestor d'Hibernate basat en instància.
 */
public class HibernateUtil {

    private final SessionFactory sessionFactory;

    // El constructor s'encarrega de configurar la connexió
    public HibernateUtil() {
        try {
            this.sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error inicialitzant Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
