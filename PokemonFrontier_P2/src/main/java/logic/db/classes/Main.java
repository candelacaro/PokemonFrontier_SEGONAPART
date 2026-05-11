package logic.db.classes;

import gui.MenuInici;
import logic.db.HibernateUtil;
import logic.db.PuntuacionsRepository;
import logic.db.classes.Puntuaciones;
import java.util.List;

/**
 * Classe principal que gestiona l'arrencada del joc i la comprovació inicial de la base de dades.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Main {

    public static void main(String[] args) {
        //Inicialitzem Hibernate per comprovar que la configuració XML és correcta
        HibernateUtil hUtil = new HibernateUtil();
        
        //Missatge de validació
        System.out.println("S'ha establert connexió amb la base de dades correctament.");

        //Llançar el Menú d'Inici (Swing)
        java.awt.EventQueue.invokeLater(() -> {
            MenuInici menu = new MenuInici();
            menu.setVisible(true);
        });

        //Mostrar rànquing actual per consola al carregar (per si de cas)
        PuntuacionsRepository repo = new PuntuacionsRepository(hUtil);
        List<Puntuaciones> tops = repo.obtenirTop10();
        System.out.println("Top 1 actual: " + (tops.isEmpty() ? "Buit" : tops.get(0).getUsuarios().getNombre()));
        
    }
}