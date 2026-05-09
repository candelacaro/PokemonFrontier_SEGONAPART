package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda les dades i els moviments d'una pilota del joc.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class Bola {

    // Declarem les variables per les dades de la pilota.
    private double x; // Posició horitzontal
    private double y; // Posició vertical
    private double velX; // Quants píxels es mou de costat en cada pas
    private double velY; // Quants píxels puja o baixa
    private boolean activa; // Ens diu si la pilota encara està jugant
    
    private static final int DIMENSIONS_BOLA = 30; // Dimensions de la bola
    private static final double INCREMENT_VELOCITAT_BOLA = 1.10; // Increment de la velocitat de bola

    /**
     * Constructor per defecte
     * @param x, posició horitzonal
     * @param y, posició vertical
     * @param velX, píxels que es mou de costat
     * @param velY, píxels que puja o baixa
     * @param activa, si la pilota encara està jugant
     */
    public Bola(final double x, final double y, final double velX, final double velY, final boolean activa) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.activa = activa;
    }

    /**
     * Mètod que mou la pilota cap als costats i amunt i avall
     */
    public void moure() {
        x += velX;
        y += velY;
    }

    /**
     * Mètode que aplica el 10% de velocitat però respectant el límit màxim
     * @param velocitatMaxima
     */
    public void augmentarVelocitat(final double velocitatMaxima) {
        velX = Math.min(velX * INCREMENT_VELOCITAT_BOLA, velocitatMaxima);
        velY = Math.min(velY * INCREMENT_VELOCITAT_BOLA, velocitatMaxima);
    }

    /**
     * Mètode que crea el quadrat de la bola
     * @return el rectangle
     */
    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, DIMENSIONS_BOLA, DIMENSIONS_BOLA);
    }

    /**
     * Mètode getter que accedeix a x
     * @return el valor de x
     */
    public double getX() {
        return x;
    }

    /**
     * Mètode setter que modifica x
     * @param x, el valor de x
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * Mètode getter que accedeix a y
     * @return el valor de y
     */
    public double getY() {
        return y;
    }

    /**
     * Mètode setter que modifica y
     * @param y, el valor de y
     */
    public void setY(final double y) {
        this.y = y;
    }

    /**
     * Mètode getter que accedeix a velX
     * @return el valor de velX
     */
    public double getVelX() {
        return velX;
    }

    /**
     * Mètode setter que modifica velX
     * @param velX, el valor de velX
     */
    public void setVelX(final double velX) {
        this.velX = velX;
    }

    /**
     * Mètode getter que accedeix a velY
     * @return el valor de velY
     */
    public double getVelY() {
        return velY;
    }

    /**
     * Mètode setter que modifica velY
     * @param velY, el valor de velY
     */
    public void setVelY(final double velY) {
        this.velY = velY;
    }

    /**
     * Mètode que activa la pilota
     * @return que es activa (true)
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * Mètode setter que modifica el valor activa
     * @param activa, el valor d'activa (true/false)
     */
    public void setActiva(final boolean activa) {
        this.activa = activa;
    }
}
