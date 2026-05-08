package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda la posicio i la mida de la raqueta.
 */
public class Raqueta {

    // Amplada màxima de la finestra del joc
    private static final int AMPLADA_FINESTRA = 500;

    // Posició horitzontal de la raqueta
    private int x;

    // Posició vertical de la raqueta
    private final int y;

    // Amplada de la raqueta
    private final int ample;

    // Altura de la raqueta
    private final int alt;

    /**
     * Constructor de la raqueta.
     */
    public Raqueta(final int x, final int y, final int ample, final int alt) {

    	// Guardem la posició vertical
        this.y = y;

        // Guardem l'amplada
        this.ample = ample;

        // Guardem l'altura
        this.alt = alt;

        // Assignem la posició X controlant els límits
        setX(x);
    }

    /**
     * Retorna el rectangle de col·lisió de la raqueta.
     */
    public Rectangle getRectangle() {

    	// Creem la hitbox de la raqueta
        return new Rectangle(x, y, ample, alt);
    }

    // Retorna la posició X
    public int getX() {
        return x;
    }

    /**
     * Modifica la posició X de la raqueta.
     */
    public void setX(final int x) {

        // Evita que la raqueta surti per l'esquerra
        if (x < 0) {

            this.x = 0;

        // Evita que la raqueta surti per la dreta
        } else if (x > AMPLADA_FINESTRA - ample) {

            this.x = AMPLADA_FINESTRA - ample;

        } else {

        	// Assigna la nova posició
            this.x = x;
        }
    }

    // Retorna la posició Y
    public int getY() {
        return y;
    }

    // Retorna l'amplada de la raqueta
    public int getAmple() {
        return ample;
    }

    // Retorna l'altura de la raqueta
    public int getAlt() {
        return alt;
    }
}
