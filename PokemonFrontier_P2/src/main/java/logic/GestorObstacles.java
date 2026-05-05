package logic;

import java.awt.Rectangle;
import java.util.Random;

/**
 * Aquesta classe crea i controla els obstacles del mapa.
 */
public class GestorObstacles {

    private static final int TOTAL_OBSTACLES = 12;

    private final Rectangle[] llistaObstacles; // Una llista per guardar 12 rectangles invisibles
    private final boolean[] visible; // Per saber si l'obstacle encara hi és o l'hem trencat
    private final Random aleatori = new Random(); // Un dau per posar els obstacles en llocs aleatoris

    public GestorObstacles() {
        this.llistaObstacles = new Rectangle[TOTAL_OBSTACLES]; // Preparem lloc per a 12 obstacles
        this.visible = new boolean[TOTAL_OBSTACLES]; // Preparem 12 interruptors
    }

    // Crea 12 obstacles en llocs aleatoris de la pantalla
    public void generarObstacles() {
        for (int i = 0; i < TOTAL_OBSTACLES; i++) { // Fem un bucle de 1 a 12
            final int x = aleatori.nextInt(300) + 20; // Tria una X a l'atzar
            final int y = aleatori.nextInt(200) + 50; // Tria una Y a l'atzar a la part superior.
            llistaObstacles[i] = new Rectangle(x, y, 40, 40); // Crea el quadrat de l'obstacle
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
