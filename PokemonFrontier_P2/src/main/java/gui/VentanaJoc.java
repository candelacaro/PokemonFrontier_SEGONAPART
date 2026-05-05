package gui; // La carpeta on guardem el nostre codi

/**
 *
 * Aquí es munta tota la part visual: carreguem els dibuixos, la pala i la pilota.
 * Fem que el joc canvi de nivell cada 20 segons pujant la velocitat un 10%.
 * Posem música de fons i efectes de so per a cada rebot perquè sembli un joc de veritat.
 * Si prems la P sortira pausa, i si perds sortira game over amb les 10 millors puntuacions.
 *
 */
// Aquí demanem les eines a Java per dibuixar, fer sons i connectar-nos a la base de dades
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import logic.Bola;
import logic.GestorObstacles;
import logic.Raqueta;
import logic.db.Puntuacio;
import logic.db.PuntuacionsRepository;

// Aquesta és la classe que fabrica la finestra del joc
public class VentanaJoc extends JFrame { // Fem que aquesta classe sigui una finestra

    private static final long serialVersionUID = 1L;

    private final Bola pilota1 = new Bola(50, 50, 3, 3, true); // Declarem les variables per les dades de la pilota 1.
    private final Bola pilota2 = new Bola(100, 50, 3.5, 3.5, false); // Declarem les variables per les dades de la pilota 2.

    // Posem un límit perquè el joc sigui jugable al nivell 20
    private final double VELOCITAT_MAXIMA = 12.0;

    // Atributs de la raqueta i del comptador.
    private final Raqueta raqueta = new Raqueta(100, 100); // On està la raqueta horitzontalment
    private long punts = 0; // El marcador de punts
    private final long tempsInici; // Per saber a quina hora hem començat a jugar
    private int nivell = 1; // El nivell actual
    private int comptadorTempsNivell = 0; // Un rellotge intern per saber quan pujar de nivell
    private final String nomJugador; // El teu nom
    private boolean estaPausat = false; // Si el joc està aturat o no
    private final String idioma; // Per saber si parlem en català o castellà

    // Variable per controlar el rellotge del joc
    private Timer temporitzadorJoc; // El motor que fa que les coses es moguin soles

    private final RecursosJoc recursos = new RecursosJoc(); // Imatges i sons del joc
    private final GestorObstacles gestorObstacles = new GestorObstacles(); // Obstacles del mapa
    private final PuntuacionsRepository puntuacionsRepository = new PuntuacionsRepository(); // Classe que parla amb la base de dades

    // És el que s'executa quan obrim el joc

   /**
    * El constructor que rep nivell i nom
    * @param nivellTriat
    * @param nom
    * @param idiomaSeleccionat
    */
    public VentanaJoc(final int nivellTriat, final String nom, String idiomaSeleccionat) {

        this.idioma = idiomaSeleccionat; // Guardem l'idioma per a que tot el joc sàpiga què mostrar

        // Carreguem els recursos primer de tot per poder utilitzar el so del menú
        recursos.carregarRecursos(); // Anem a buscar els fitxers de la carpeta resources.

        // Preparem els textos del diàleg segons l'idioma que hagi triat el que juga
        String titol = idioma.equals("Català") ? "Selecció de Nivell" : "Selección de Nivel";
        String pregunta = idioma.equals("Català") ? "Introdueix el nivell (1-20):" : "Introduce el nivel (1-20):";

        // Aquí demanem el nivell per teclat amb una finestra blanca
        String entradaNivell = JOptionPane.showInputDialog(null, pregunta, titol, JOptionPane.QUESTION_MESSAGE);

        // Fem sonar el clic després de tancar el diàleg
        reproduirClic(); // Cridem al soroll de menú

        int nivellFinal = 1; // Creem una variable temporal per al nivell
        try { // Intentem fer la conversió de text a número sense que exploti
            nivellFinal = Integer.parseInt(entradaNivell); // Intentem convertir el text en número
            if (nivellFinal < 1) nivellFinal = 1; // Si posen menys d'1, posem 1
            if (nivellFinal > 20) nivellFinal = 20; // Si posen més de 20, posem 20
        } catch (Exception e) { // Si passa qualsevol error al escriure
            nivellFinal = 1; // Si escriuen lletres, comencem al nivell 1 per defecte
        }

        this.nivell = nivellFinal; // Guardem el nivell triat
        this.nomJugador = nom; // Guardem el nom

        // Apliquem el 10% de velocitat però respectant el límit màxim
        for (int i = 1; i < nivellFinal; i++) {
            pilota1.augmentarVelocitat(VELOCITAT_MAXIMA);
            pilota2.augmentarVelocitat(VELOCITAT_MAXIMA);
        }

        // Ajustem la finestra
        this.setSize(400, 600); // Mida de la finestra
        this.setResizable(false); // No deixem que s'estiri la pantalla
        this.setLocationRelativeTo(null); // Que surti al mig de la pantalla
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Si tanquem la X, s'atura tot

        generarObstacles(); // Posem els obstacles pel mapa
        reproduirMusica(); // Posem aixo per a que comenci la musica
        reproduirIniciPartida(); // Fem sonar el so de inici de partida

        this.tempsInici = System.currentTimeMillis(); // Mirem el rellotge

        // Això serveix perquè la raqueta segueixi el teu ratolí
        this.addMouseMotionListener(new MouseMotionAdapter() { // Escoltador per al moviment del ratolí
            @Override
            public void mouseMoved(final MouseEvent e) { // Quan el ratolí es mou
                if (!getEstaPausat()) { // Només si el joc no està en pausa
                    setRaquetaX(e.getX() - (getRaquetaAmple() / 2)); // Centrem la raqueta al punter
                }
            }
        });

        // Això serveix perquè si prems la p o les fletxes, el joc reaccioni
        this.addKeyListener(new KeyAdapter() { // Escoltador per al teclat
            @Override
            public void keyPressed(final KeyEvent e) { // Quan es prem una tecla.
                int codi = e.getKeyCode();
                if (codi == KeyEvent.VK_P) { // Si la tecla és la P
                    setEstaPausat(!getEstaPausat()); // Canviem de pausa a joc o viceversa
                }

                // També podem moure la raqueta amb les fletxes del teclat (esquerra i dreta)
                if (!getEstaPausat()) {
                    if (codi == KeyEvent.VK_LEFT) {
                        setRaquetaX(getRaquetaX() - 25); // Movem 25 píxels a l'esquerra
                    }
                    if (codi == KeyEvent.VK_RIGHT) {
                        setRaquetaX(getRaquetaX() + 25); // Movem 25 píxels a la dreta
                    }
                }
            }
        });
    }

    //Posem la música de Pokémon en bucle
    public void reproduirMusica() { // Mètode per la cançó de fons
        recursos.reproduirMusica();
    }

    // Mètode per reproduir el so d'inici de partida
    public void reproduirIniciPartida() { // So de benvinguda
        recursos.reproduirIniciPartida();
    }

    // Fa el soroll quan la pilota rebota a la paret
    public void sonarRebot() { // Rebot paret
        recursos.sonarRebot();
    }

    // Fa el soroll quan la pilota toca la teva raqueta
    public void sonarRaqueta() { // Rebot pala (canya)
        recursos.sonarRaqueta();
    }

    // Fa el soroll quan perds la partida
    public void sonarMort() { // So de Game Over
        recursos.sonarMort();
    }

    // Fa el soroll quan cliques al menú
    public void reproduirClic() { // Clic botons
        recursos.reproduirClic();
    }

    // Crea 12 obstacles en llocs aleatoris de la pantalla
    public void generarObstacles() { // Posa els enemics al mapa
        gestorObstacles.generarObstacles();
    }

    // Aquí es calcula cada moviment i cada xoc
    public void moureTot() {
        if (getEstaPausat()) return; // Si està en pausa, no fem res

        // Actualitzem els punts segons el temps que portes viu
        setPunts(System.currentTimeMillis() - getTempsInici()); // Calcula quants milisegons portes jugant

        actualitzarNivell(); // Cada 20 segons pugem de nivell

        // Si arribes al nivell 8, surt la segona pilota
        if (getNivell() >= 8 && !pilota2.isActiva() && pilota2.getY() < 600) { // Condició per afegir dificultat
            pilota2.setActiva(true); // Activem la segona pilota
        }

        mourePilota(pilota1); // Lògica de la pilota 1
        mourePilota(pilota2); // Lògica de la pilota 2

        comprovarFinalPartida(); // Si ja no queden pilotes vives, s'ha acabat el joc
    }

    private void actualitzarNivell() {
        setComptadorTempsNivell(getComptadorTempsNivell() + 10); // Sumem temps al comptador de nivell
        //Fem una condicional per sumar cada 20000 punts un 10% de velocitat.
        if (getComptadorTempsNivell() >= 20000 && getNivell() < 20) { // Si passen 20 segons i no som al top
            //Sumem un nivell per cada 20000 punts
            setNivell(getNivell() + 1); // Pugem el número del nivell

            // Pugem la velocitat un 10% però mai passem del límit
            pilota1.augmentarVelocitat(VELOCITAT_MAXIMA);
            pilota2.augmentarVelocitat(VELOCITAT_MAXIMA);

            setComptadorTempsNivell(0); // Resetejem el comptador per als pròxims 20 segons
            generarObstacles(); // Posem obstacles nous
        }
    }

    private void mourePilota(final Bola pilota) {
        if (!pilota.isActiva()) return; // Si la pilota encara està viva

        pilota.moure(); // Movem la pilota

        if (pilota.getX() < 0 || pilota.getX() > 360) { pilota.setVelX(-pilota.getVelX()); sonarRebot(); } // Rebota si toca els costats
        if (pilota.getY() < 30) { pilota.setY(30); pilota.setVelY(-pilota.getVelY()); sonarRebot(); } // Rebota si toca el sostre

        Rectangle rPilota = pilota.getRectangle(); // Crea el quadrat de la bola
        Rectangle rRaqueta = raqueta.getRectangle(); // Crea el quadrat de la pala

        if (rPilota.intersects(rRaqueta)) { // Si la pilota xoca amb la pala
            pilota.setVelY(-pilota.getVelY()); pilota.setY(530 - 30); sonarRaqueta(); // Invertim direcció i fem soroll
        }

        if (gestorObstacles.comprovarXoc(rPilota)) { // Si la pilota toca l'obstacle
            pilota.setVelY(-pilota.getVelY()); sonarRebot(); // El trenquem i rebotem
        }

        if (pilota.getY() > 600) pilota.setActiva(false); // Si cau per sota, la pilota mor
    }

    private void comprovarFinalPartida() {
        // Si ja no queden pilotes vives, s'ha acabat el joc i mostra l'usuari un game over.
        if (!pilota1.isActiva() && !pilota2.isActiva()) { // Condició final de derrota
            if (temporitzadorJoc != null) temporitzadorJoc.cancel(); // Aturem el timer per evitar el bucle
            recursos.pararMusica(); // Parem la música de fons
            sonarMort(); // Fem que soni l'audio de derrota

            // Adaptem el missatge de derrota a l'idioma
            String titolGameOver = idioma.equals("Català") ? "HAS PERDUT" : "GAME OVER";
            String etiquetaJug = idioma.equals("Català") ? "Jugador: " : "Jugador: ";
            String etiquetaPts = idioma.equals("Català") ? "\nPunts: " : "\nPuntos: ";

            //Li mostrem un missatge a l'usuari un game over i li mostrem el nom de l'usuari i els punts que he conseguit.
            JOptionPane.showMessageDialog(this, titolGameOver + "\n" + etiquetaJug + getNomJugador() + etiquetaPts + getPunts()); // Cartell de final
            logic.db.DesarPuntuacions.guardarPuntuacion(getNomJugador(), getPunts()); // Trucada al nou mètode extern
            mostrarTop10(); // Ensenyem el rànquing
            new MenuInici().setVisible(true); // Tornem al menú
            this.dispose(); // Tanquem la finestra
        }
    }

    // Inicia el cronòmetre que fa que el joc es mogui 100 vegades per segon
    public void iniciarJoc() { // El mètode que arranca el motor
        temporitzadorJoc = new Timer(); // Creem un rellotge nou
        final TimerTask tasca = new TimerTask() { // Definim què farà el rellotge
            @Override
            public void run() { moureTot(); repaint(); } // Calcula i torna a dibuixar
        };
        temporitzadorJoc.scheduleAtFixedRate(tasca, 0, 10); // Cada 10 milisegons s'executa
    }

    // Aquí és on es dibuixa tot el que veus a la pantalla
    @Override
    public void paint(final Graphics g) { // El mètode que pinta cada frame

        // Dibuixem primer en una imatge invisible per evitar que la pantalla parpellegi
        final Image imatgeOffscreen = createImage(getWidth(), getHeight()); // Creem el llenç invisible
        if (imatgeOffscreen == null) return; // Si no podem crear-lo, ens aturem
        final Graphics gOff = imatgeOffscreen.getGraphics(); // Agafem els "pinzells" del llenç invisible
        Graphics2D g2d = (Graphics2D) gOff; // Fem servir el motor 2D més modern

        // Millorem la qualitat del dibuix
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Perquè les fotos no es vegin pixelades
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Perquè les vores siguin suaus

        dibuixarFons(g2d);
        dibuixarObstacles(g2d);
        dibuixarPilotes(g2d);
        dibuixarRaqueta(g2d);
        dibuixarTextos(g2d);
        dibuixarPausa(g2d);

        // Passem tot el dibuix a la pantalla real
        g.drawImage(imatgeOffscreen, 0, 0, null); // Enviem el llenç invisible a la vista de l'usuari
    }

    private void dibuixarFons(final Graphics2D g2d) {
        // Dibuixem el fons de Pokémon segons el nivell
        ImageIcon fonsActual = recursos.getFonsActual(getNivell()); // Variable per saber quina foto de fons toca
        if (fonsActual != null) g2d.drawImage(fonsActual.getImage(), 0, 0, 400, 600, null); // Pintem el fons triat
    }

    private void dibuixarObstacles(final Graphics2D g2d) {
        Rectangle[] llistaObstacles = gestorObstacles.getLlistaObstacles();
        boolean[] visible = gestorObstacles.getVisible();

        // Dibuixem els obstacles segons el nivell
        for (int i = 0; i < gestorObstacles.getTotalObstacles(); i++) { // Recorrem la llista d'obstacles
            if (visible[i]) { // Només si els obstacles estan visibles
                ImageIcon iconaTriada = recursos.getObstacleActual(getNivell()); // Variable per a la foto del monstre

                // Si la imatge s'ha carregat, la dibuixem
                if (iconaTriada != null && iconaTriada.getImage() != null) { // Seguretat per no pintar buit
                    g2d.drawImage(iconaTriada.getImage(), llistaObstacles[i].x, llistaObstacles[i].y, 40, 40, null); // Pintem l'obstacle
                }
            }
        }
    }

    private void dibuixarPilotes(final Graphics2D g2d) {
        ImageIcon imgPilota = recursos.getImgPilota();

        // Dibuixem les pilotes
        if (pilota1.isActiva() && imgPilota != null) g2d.drawImage(imgPilota.getImage(), (int) getPilotaX(), (int) getPilotaY(), 30, 30, null); // Pinta bola 1
        if (pilota2.isActiva() && imgPilota != null) g2d.drawImage(imgPilota.getImage(), (int) pilota2.getX(), (int) pilota2.getY(), 30, 30, null); // Pinta bola 2 si n'hi ha
    }

    private void dibuixarRaqueta(final Graphics2D g2d) {
        ImageIcon imgRaqueta = recursos.getImgRaqueta();

        // Dibuixem la raqueta
        if (imgRaqueta != null) g2d.drawImage(imgRaqueta.getImage(), getRaquetaX(), 520, getRaquetaAmple(), 40, null); // Pinta la nostra pala
    }

    private void dibuixarTextos(final Graphics2D g2d) {
        // Dibuixem els textos d'informació (Nom, Punts, Nivell) adaptats a l'idioma
        g2d.setColor(Color.WHITE); // Color del text en blanc
        g2d.setFont(new Font("Arial", Font.BOLD, 14)); // Tipus de lletra clara

        String etiquetaJugador = idioma.equals("Català") ? "Jugador: " : "Jugador: ";
        String etiquetaPunts = idioma.equals("Català") ? "Punts: " : "Puntos: ";
        String etiquetaNivell = idioma.equals("Català") ? "Nivell: " : "Nivel: ";

        g2d.drawString(etiquetaJugador + getNomJugador(), 20, 50); // Escribim el nom del jugador
        g2d.drawString(etiquetaPunts + getPunts(), 20, 70); // Escribim els milisegons de puntuació
        g2d.drawString(etiquetaNivell + getNivell(), 300, 50); // Escribim el nivell a la dreta
    }

    private void dibuixarPausa(final Graphics2D g2d) {
        // Si el joc està pausat, enfosquim la pantalla.
        if (getEstaPausat()) {
            // Dibuixem un rectangle negre semitransparent que ocupa tota la finestra
            g2d.setColor(new Color(0, 0, 0, 150)); // El 150 és el nivell de transparència
            g2d.fillRect(0, 0, 400, 600);

            // Posem el text de pausa ben gran al centre
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Impact", Font.BOLD, 50));
            g2d.drawString("PAUSA", 130, 300);

            String msgPausa = idioma.equals("Català") ? "Prem 'P' per continuar" : "Pulsa 'P' para continuar";
            g2d.setFont(new Font("Arial", Font.PLAIN, 15));
            g2d.drawString(msgPausa, 125, 340);
        }
    }

    // Agafa les 10 millors puntuacions de la base de dades i les ensenya
    private void mostrarTop10() { // Mètode per la llista de guanyadors
        List<Puntuacio> puntuacions = puntuacionsRepository.obtenirTop10(); // Connectem amb la base de dades SQL
        if (puntuacions.isEmpty()) return; // Si la DB falla, simplement no ensenya la taula

        String colPos = "POS";
        String colJug = idioma.equals("Català") ? "JUGADOR" : "JUGADOR";
        String colPts = idioma.equals("Català") ? "PUNTS" : "PUNTOS";

        String taula = String.format("%-5s %-15s %-10s\n", colPos, colJug, colPts); // Capçalera de la taula
        int pos = 1; // Comptador de posició
        for (Puntuacio puntuacio : puntuacions) { // Mentre hi hagi files de resultat
            taula += String.format("%-5d %-15s %-10d\n", pos++, puntuacio.getNomJugador(), puntuacio.getPunts()); // Afegim línia a la taula
        }
        JTextArea area = new JTextArea(taula); // Fem una àrea de text per ensenyar-ho
        area.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Font per a que les columnes quedin rectes
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "TOP 10", JOptionPane.INFORMATION_MESSAGE); // Mostrem la finestra emergent
    }

    // Aqui posem tots els getters i setters

    public double getPilotaX() { // Ens dona la X de la bola
        return pilota1.getX();
    }

    /**
     * Canvia la X de la bola
     * @param pilotaX
     */
    public void setPilotaX(final double pilotaX) {
        pilota1.setX(pilotaX);
    }

    public double getPilotaY() { // Ens dona la Y de la bola
        return pilota1.getY();
    }

    /**
     * Canvia la Y de la bola
     * @param pilotaY
     */
    public void setPilotaY(final double pilotaY) {
        pilota1.setY(pilotaY);
    }

    public double getVelX() { // Ens dona la velocitat lateral
        return pilota1.getVelX();
    }

    /**
     * Canvia la velocitat lateral
     * @param velX
     */
    public void setVelX(final double velX) {
        pilota1.setVelX(velX);
    }

    public double getVelY() { // Ens dona la velocitat vertical
        return pilota1.getVelY();
    }

    /**
     * Canvia la velocitat vertical
     * @param velY
     */
    public void setVelY(final double velY) {
        pilota1.setVelY(velY);
    }

    public int getRaquetaX() { // Ens dona on està la pala
        return raqueta.getX();
    }

    /**
     * Movem la pala a una X nova
     * @param raquetaX
     */
    public void setRaquetaX(int raquetaX) {
        raqueta.setX(raquetaX);
    }

    public int getRaquetaAmple() { // Ens diu quant medeix la pala
        return raqueta.getAmple();
    }

    public long getPunts() { // Ens dona los punts actuals
        return punts;
    }

    public void setPunts(final long punts) { // Canvia els punts del marcador
        this.punts = punts;
    }

    public long getTempsInici() { // Ens diu quan vam començar
        return tempsInici;
    }

    public int getNivell() { // Ens diu en quin nivell estem
        return nivell;
    }

    /**
     * Ens permet canviar el nivell
     * @param nivell
     */
    public void setNivell(final int nivell) {
        this.nivell = nivell;
    }

    public int getComptadorTempsNivell() { // Ens diu quants ms portem al nivell actual
        return comptadorTempsNivell;
    }

    /**
     * Ajusta el rellotge de nivell
     * @param comptador
     */
    public void setComptadorTempsNivell(final int comptador) {
        this.comptadorTempsNivell = comptador;
    }

    public String getNomJugador() { // Ens diu el nom del jugador.
        return nomJugador;
    }

    public boolean getEstaPausat() { // Ens diu si el joc està aturat
        return estaPausat;
    }

    /**
     * Activa o desactiva la pausa
     * @param pausat
     */
    public void setEstaPausat(final boolean pausat) {
        this.estaPausat = pausat;
    }
}
