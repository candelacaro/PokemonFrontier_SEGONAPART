package logic.db;

import java.sql.Connection;
import java.sql.DriverManager;

/*
 *Aquesta classe és la clau de pas per entrar a la base de dades
 */
public class Conexio {
    
    // Aquí posem l'adreça la base de dades, el servidor local, port 3306 i el nom de la DB.
    private static String url = "jdbc:mysql://localhost:3306/retro_tennis_part2";
    // El nom d'usuari per defecte de MySQL
    private static String user = "root";
    // La teva contrasenya secreta per poder connectar-te
    private static String password = "7022"; 

    // Getter per saber quina URL estem fent servir actualment
    public static String getUrl() {
        return url;
    }

    // Per si algun dia canviem el servidor i posem un nou.
    public static void setUrl(final String novaUrl) {
        url = novaUrl;
    }

    // Getter per obtenir el nom d'usuari
    public static String getUser() {
        return user;
    }

    // Setter per si vols entrar amb un usuari que no sigui el root
    public static void setUser(final String nouUser) {
        user = nouUser;
    }

    // Getter per agafar la contrasenya
    public static String getPassword() {
        return password;
    }

    // Setter per si cambiem la clau de la base de dades
    public static void setPassword(final String novaPassword) {
        password = novaPassword;
    }

    /**
     * Aquest és el mètode estrella: intenta obrir la porta de la base de dades.
     * Si tot va bé, ens dóna una "connexió" activa per poder enviar-li dades.
     */
    public static Connection connectar() {
        Connection cn = null; // Comencem amb una connexió buida
        try {
            // Li demanem al gestor de Java que ens obri el camí amb les nostres credencials
            cn = DriverManager.getConnection(getUrl(), getUser(), getPassword());
        } catch (final Exception e) {
            // Si la base de dades està apagada o la clau és incorrecta, ens avisarà per aquí
            System.out.println("Error: " + e.getMessage());
        }
        return cn; // Tornem la connexió (o null si ha fallat)
    }
}