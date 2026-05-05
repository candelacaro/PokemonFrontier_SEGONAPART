package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda la posicio i la mida de la raqueta.
 */
public class Raqueta {

    private static final int AMPLADA_FINESTRA = 400;

    private int x; // On esta la raqueta horitzontalment
    private final int y; // On esta la raqueta verticalment
    private final int ample; // L'amplada de la raqueta que no cambia.
    private final int alt; // L'alcada de la raqueta

    public Raqueta(final int x, final int y, final int ample, final int alt) {
        this.y = y;
        this.ample = ample;
        this.alt = alt;
        setX(x);
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, ample, alt); // Crea el quadrat de la pala
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        // Limitem la X perque la raqueta no surti per l'esquerra ni per la dreta
        if (x < 0) {
            this.x = 0;
        } else if (x > AMPLADA_FINESTRA - ample) {
            this.x = AMPLADA_FINESTRA - ample;
        } else {
            this.x = x;
        }
    }

    public int getY() {
        return y;
    }

    public int getAmple() {
        return ample;
    }

    public int getAlt() {
        return alt;
    }
}
