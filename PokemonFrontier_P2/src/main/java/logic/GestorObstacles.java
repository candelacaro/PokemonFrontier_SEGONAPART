package logic;

import java.awt.Rectangle;
import java.util.Random;

/**
 * Aquesta classe crea i controla els obstacles del mapa.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class GestorObstacles {

    private static final int TOTAL_OBSTACLES = 12;
    private static final int AMPLADA_PANTALLA = 400; // Amplada total de la pantalla
    private static final int ALTURA_PANTALLA = 600; // Altura total de la pantalla
    private static final int MIDA_OBSTACLE = 40; // Amplada i altura de cada obstacle
    private static final int INICI_ZONA_OBSTACLES = ALTURA_PANTALLA / 3; // Comenca el segon tros de la pantalla
    private static final int ALTURA_ZONA_OBSTACLES = ALTURA_PANTALLA / 3; // El segon tros es la zona dels obstacles

    private final Rectangle[] llistaObstacles; // Una llista per guardar 12 rectangles invisibles
    private final boolean[] visible; // Per saber si l'obstacle encara hi és o l'hem trencat
    private final Random aleatori = new Random(); // Un dau per posar els obstacles en llocs aleatoris

    public GestorObstacles() {
        this.llistaObstacles = new Rectangle[TOTAL_OBSTACLES]; // Preparem lloc per a 12 obstacles
        this.visible = new boolean[TOTAL_OBSTACLES]; // Preparem 12 interruptors
    }

    // Crea 12 obstacles en llocs aleatoris de la zona mitjana de la pantalla
    public void generarObstacles() {
        for (int i = 0; i < TOTAL_OBSTACLES; i++) { // Fem un bucle de 1 a 12
            final int x = aleatori.nextInt(AMPLADA_PANTALLA - MIDA_OBSTACLE); // Tria una X a l'atzar
            final int y = aleatori.nextInt(ALTURA_ZONA_OBSTACLES - MIDA_OBSTACLE) + INICI_ZONA_OBSTACLES; // Zona mitjana
            llistaObstacles[i] = new Rectangle(x, y, MIDA_OBSTACLE, MIDA_OBSTACLE); // Crea el quadrat de l'obstacle
            visible[i] = true; // El fem visible al principi
        }
    }

    public boolean comprovarXoc(final Rectangle pilota) {
        for (int i = 0; i < TOTAL_OBSTACLES; i++) { // Mirem tots els obstacles
            if (visible[i]) { // Si l'obstacle encara no l'hem trencat
                Rectangle hitbox = new Rectangle(llistaObstacles[i].x + 5, llistaObstacles[i].y + 5, 30, 30); // Lloc del xoc
                if (pilota.intersects(hitbox)) { // Si la pilota toca l'obstacle
                    visible[i] = false;
                    return true;
                }
            }
        }
        return false;
    }

    public Rectangle[] getLlistaObstacles() {
        return llistaObstacles;
    }

    public boolean[] getVisible() {
        return visible;
    }

    public int getTotalObstacles() {
        return TOTAL_OBSTACLES;
    }
}
