package logic.db; 

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Classe utilitària per gestionar Hibernate.
 * S'encarrega de crear i tancar la connexió amb la base de dades.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class HibernateUtil {

    // Declaració i incialització de variable que guardarà la SessionFactory d'Hibernate
    private final SessionFactory sessionFactory;

    /**
     * Constructor per defecte
     */
    public HibernateUtil() {

        try {
            // Carrega la configuració del fitxer hibernate.cfg.xml
            // i crea la SessionFactory
            this.sessionFactory = new Configuration()
                    .configure()
                    .buildSessionFactory();

        } catch (Throwable ex) {

            // Mostra el missatge d'error si Hibernate falla
            System.err.println("Error inicialitzant Hibernate: " + ex);

            // Llança una excepció si la inicialització no funciona
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Mètode getter per obtenir la SessionFactory
     * @return sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Mètode per tancar la SessionFactory i alliberar recursos
     */
    public void shutdown() {

        // Comprovem que la SessionFactory existeixi
        if (sessionFactory != null) {

            // Tanquem la connexió d'Hibernate
            sessionFactory.close();
        }
    }
}