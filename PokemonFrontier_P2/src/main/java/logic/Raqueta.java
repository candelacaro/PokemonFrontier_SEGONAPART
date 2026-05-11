package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda la posicio i la mida de la raqueta.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Raqueta {

    // Declaració i incialització de final per l'amplada màxima de la finestra del joc
    private static final int AMPLADA_FINESTRA = 500;

    // Declaració i inicialització d'atribut per la posició horitzontal de la raqueta
    private int x;

    // Declaració i incialització de final per la posició vertical de la raqueta
    private final int y;

    // Declaració i incialització de final per l'amplada de la raqueta
    private final int ample;

    // Declaració i incialització de final per l'alçada de la raqueta
    private final int alt;

    /**
     * Constructor de la raqueta
     * @param x, coordenada x
     * @param y, coordenada y
     * @param ample, amplada de la raqueta
     * @param alt, alçada de la raqueta
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
     * Mètode Rectangle que retorna el rectangle de col·lisió de la raqueta.
     */
    public Rectangle getRectangle() {

    	// Creem la hitbox de la raqueta
        return new Rectangle(x, y, ample, alt);
    }

    /**
     * Mètode getter que retorna la posició X
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Mètode setter que odifica la posició X de la raqueta.
     * @param x, la coordenada x
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

    /**
     * Mètode getter que retorna la posició Y
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Mètode getter que retorna l'amplada de la raqueta
     * @return ample
     */
    public int getAmple() {
        return ample;
    }

    /**
     * Mètode getter que retorna l'altura de la raqueta
     * @return alt
     */
    public int getAlt() {
        return alt;
    }
}
