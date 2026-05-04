package logic.db;


/*
 * Clase on guardem les dades dels nostres usuaris. Guardem el nom , el seu ID, la puntuació, l'idioma i la data.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class DesarPuntuacions {

    // Fem el mètode perquè el puguem cridar sense crear un objecte nou
    public static void guardarPuntuacion(String nomJugador, long punts) {
        try {
            Connection cn = Conexio.connectar();
            if (cn != null) {
                // 1. Inserim l'usuari si no existeix
                String queryUsuario = "INSERT IGNORE INTO usuarios (nombre) VALUES (?)";
                PreparedStatement pst1 = cn.prepareStatement(queryUsuario);
                pst1.setString(1, nomJugador);
                pst1.executeUpdate();

                // 2. Busquem l'ID de l'usuari
                String queryId = "SELECT id_usuario FROM usuarios WHERE nombre = ?";
                PreparedStatement pst2 = cn.prepareStatement(queryId);
                pst2.setString(1, nomJugador);
                ResultSet rs = pst2.executeQuery();

                if (rs.next()) {
                    int idUsuari = rs.getInt("id_usuario");
                    // 3. Guardem la puntuació
                    String sql = "INSERT INTO puntuaciones (id_usuario, puntuacion, fecha, idioma) VALUES (?, ?, ?, ?)";
                    PreparedStatement pst3 = cn.prepareStatement(sql);
                    pst3.setInt(1, idUsuari);
                    pst3.setLong(2, punts);
                    pst3.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pst3.setString(4, "Castellano");
                    pst3.executeUpdate();
                }
                cn.close();
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
