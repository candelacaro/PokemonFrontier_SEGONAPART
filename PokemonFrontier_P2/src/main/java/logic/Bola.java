package logic;

import java.awt.Rectangle;

/**
 * Aquesta classe guarda les dades i els moviments d'una pilota del joc.
 */
public class Bola {

    // Declarem les variables per les dades de la pilota.
    private double x; // Posició horitzontal
    private double y; // Posició vertical
    private double velX; // Quants píxels es mou de costat en cada pas
    private double velY; // Quants píxels puja o baixa
    private boolean activa; // Ens diu si la pilota encara està jugant

    public Bola(final double x, final double y, final double velX, final double velY, final boolean activa) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.activa = activa;
    }

    // Movem la pilota cap als costats i amunt i avall
    public void moure() {
        x += velX;
        y += velY;
    }

    // Apliquem el 10% de velocitat però respectant el límit màxim
    public void augmentarVelocitat(final double velocitatMaxima) {
        velX = Math.min(velX * 1.10, velocitatMaxima);
        velY = Math.min(velY * 1.10, velocitatMaxima);
    }

    // Crea el quadrat de la bola
    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, 30, 30);
    }

    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(final double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(final double velY) {
        this.velY = velY;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(final boolean activa) {
        this.activa = activa;
    }
}
