package logic.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
 * Clase on guardem les dades dels nostres usuaris. Guardem el nom , el seu ID, la puntuació, l'idioma i la data.
 */
public class PuntuacionsRepository {

    // Fem el mètode per guardar la puntuació d'una partida
    public void guardarPuntuacion(String nomJugador, long punts) {
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

    // Agafa les 10 millors puntuacions de la base de dades
    public List<Puntuacio> obtenirTop10() {
        List<Puntuacio> puntuacions = new ArrayList<>();
        try { // Connectem amb la base de dades SQL
            final Connection connexio = Conexio.connectar(); // Obrim la connexió
            if (connexio != null) { // Si la connexió funciona
                final String sql = "SELECT u.nombre, p.puntuacion FROM puntuaciones p JOIN usuarios u ON p.id_usuario = u.id_usuario ORDER BY p.puntuacion DESC LIMIT 10"; // Consulta SQL
                final PreparedStatement pst = connexio.prepareStatement(sql); // Preparem la frase per a la DB
                final ResultSet rs = pst.executeQuery(); // Executem i guardem el resultat

                while (rs.next()) { // Mentre hi hagi files de resultat
                    puntuacions.add(new Puntuacio(rs.getString("nombre"), rs.getLong("puntuacion"))); // Afegim línia a la taula
                }
                connexio.close(); // Tanquem la connexió amb la base de dades
            }
        } catch (final Exception e) { } // Si la DB falla, simplement no ensenya la taula
        return puntuacions;
    }
}
