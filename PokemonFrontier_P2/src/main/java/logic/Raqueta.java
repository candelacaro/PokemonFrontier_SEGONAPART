package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda la posició i la mida de la raqueta.
 */
public class Raqueta {

    private static final int AMPLADA_FINESTRA = 400;

    private int x; // On està la raqueta horitzontalment
    private final int ample; // L'amplada de la raqueta que no cambia.

    public Raqueta(final int x, final int ample) {
        this.ample = ample;
        setX(x);
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, 530, ample, 20); // Crea el quadrat de la pala
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        // Limitem la X perquè la raqueta no surti per l'esquerra ni per la dreta
        if (x < 0) {
            this.x = 0;
        } else if (x > AMPLADA_FINESTRA - ample) {
            this.x = AMPLADA_FINESTRA - ample;
        } else {
            this.x = x;
        }
    }

    public int getAmple() {
        return ample;
    }
}
