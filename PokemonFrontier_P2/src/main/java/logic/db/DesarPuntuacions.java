package logic.db;

public class DesarPuntuacions {

    private static final PuntuacionsRepository REPOSITORY = new PuntuacionsRepository();

    // Fem el mètode perquè el puguem cridar sense crear un objecte nou
    public static void guardarPuntuacion(String nomJugador, long punts) {
        REPOSITORY.guardarPuntuacion(nomJugador, punts);
    }
}
