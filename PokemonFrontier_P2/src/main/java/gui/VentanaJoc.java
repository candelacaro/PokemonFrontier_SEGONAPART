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
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

import logic.db.Conexio;

import javax.sound.sampled.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Aquesta és la classe que fabrica la finestra del joc
public class VentanaJoc extends JFrame { // Fem que aquesta classe sigui una finestra 

    // Declarem les variables per les dades de la pilota 1. 
    private double pilotaX = 50; // Posició horitzontal 
    private double pilotaY = 50; // Posició vertical 
    private double velX = 3;     // Quants píxels es mou de costat en cada pas
    private double velY = 3;     // Quants píxels puja o baixa
    private boolean bola1Activa = true; // Ens diu si la pilota encara està jugant

    // Declarem les variables per les dades de la pilota 2.
    private double pilota2X = 100; // Posició inicial horitzontal de la segona pilota
    private double pilota2Y = 50; // Posició inicial vertical de la segona pilota
    private double vel2X = 3.5; // Velocitat lateral de la segona bola
    private double vel2Y = 3.5; // Velocitat vertical de la segona bola
    private boolean bola2Activa = false; // Al principi de la partida no esta, sortira al nivell 8.

    // Posem un límit perquè el joc sigui jugable al nivell 20 
    private final double VELOCITAT_MAXIMA = 12.0; 

    // Atributs de la raqueta i del comptador.
    private int raquetaX = 100; // On està la raqueta horitzontalment
    private final int raquetaAmple = 100; // L'amplada de la raqueta que no cambia.
    private long punts = 0; // El marcador de punts
    private final long tempsInici; // Per saber a quina hora hem començat a jugar
    private int nivell = 1; // El nivell actual
    private int comptadorTempsNivell = 0; // Un rellotge intern per saber quan pujar de nivell
    private final String nomJugador; // El teu nom
    private boolean estaPausat = false; // Si el joc està aturat o no
    private final String idioma; // Per saber si parlem en català o castellà
    
    // Variable per controlar el rellotge del joc
    private Timer temporitzadorJoc; // El motor que fa que les coses es moguin soles
    
    // Declarem les variables privades per les musiques del nostre codi.
    private Clip musica; // El reproductor per a la música de fons
    private Clip soRebot; // El reproductor per els rebots amb la paret
    private Clip soRaqueta; // El reproductor per quan toques la pala
    private Clip soMenu; // El reproductor per als sons de la interfície
    private Clip soPerdre; // El reproductor per quan la pilota s'escapa
    private Clip soIniciPartida; // Variable per al so d'inici
    
    // Declarem les variables privades per les imatges del nostre codi.
    private ImageIcon imgFons; // Imatge del primer gimnàs
    private ImageIcon imgFons2; // Imatge del segon gimnàs
    private ImageIcon imgFons3; // Imatge del tercer gimnàs
    private ImageIcon imgFons4; // Imatge de l'últim gimnàs
    private ImageIcon imgPilota; // El dibuix de la Pokèball que rebota
    private ImageIcon imgObstacle; // Serà Gastly
    private ImageIcon imgRaqueta; // El dibuix de la pala que controlem
    private ImageIcon imgObstacle2; // Serà Hunter
    private ImageIcon imgObstacle3; // Serà Gengar
    private ImageIcon imgObstacle4; // Serà Megagengar
    
    private final Rectangle[] llistaObstacles; // Una llista per guardar 12 rectangles invisibles
    private final boolean[] visible; // Per saber si l'obstacle encara hi és o l'hem trencat
    private final Random aleatori = new Random(); // Un dau per posar els obstacles en llocs aleatoris

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
        carregarRecursos(); // Anem a buscar els fitxers de la carpeta resources.

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
            this.velX = Math.min(this.velX * 1.10, VELOCITAT_MAXIMA);
            this.velY = Math.min(this.velY * 1.10, VELOCITAT_MAXIMA);
            this.vel2X = Math.min(this.vel2X * 1.10, VELOCITAT_MAXIMA);
            this.vel2Y = Math.min(this.vel2Y * 1.10, VELOCITAT_MAXIMA);
        }

        this.llistaObstacles = new Rectangle[12]; // Preparem lloc per a 12 obstacles
        this.visible = new boolean[12]; // Preparem 12 interruptors 

        // Ajustem la finestra
        this.setSize(400, 600); // Mida de la finestra
        this.setResizable(false); // No deixem que s'estiri la pantalla
        this.setLocationRelativeTo(null); // Que surti al mig de la pantalla
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Si tanquem la X, s'atura tot

        generarObstacles(); // Posem els obstacles pel mapa
        reproduirMusica(); // Posem aixo per a que començi la musica
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

    // Carreguem tots els fitxers de so i imatges 
    private void carregarRecursos() { // Mètode per omplir les variables amb fitxers reals
        try { // Intentem carregar tout per si algun fitxer falta
            // Carreguem les imatges
            imgFons = new ImageIcon(getClass().getResource("/fondopoke.png")); // Carrega el gimnàs inicial
            imgFons2 = new ImageIcon(getClass().getResource("/gimnasio2.png")); // Carrega el gimnàs 2
            imgFons3 = new ImageIcon(getClass().getResource("/gimnasio3.png")); // Carrega el gimnàs 3
            imgFons4 = new ImageIcon(getClass().getResource("/gimnasio4.png")); // Carrega el gimnàs 4
            imgPilota = new ImageIcon(getClass().getResource("/pokemonverda.png")); // Carrega la pokeball
            imgRaqueta = new ImageIcon(getClass().getResource("/cañaraqueta.png")); // Carrega la canya 
            imgObstacle = new ImageIcon(getClass().getResource("/gastly.png")); // Imatge dels primers obstacles
            imgObstacle2 = new ImageIcon(getClass().getResource("/hunter.png")); // Imatge per els segons obstacles
            imgObstacle3 = new ImageIcon(getClass().getResource("/pokemonobstaculo.png")); // Imatge per els tercers obstacles
            imgObstacle4 = new ImageIcon(getClass().getResource("/megagengarshiny.png")); // Imatge per els ultims obstacles.

            // Carreguem els sons
            URL urlRebot = getClass().getResource("/rebotBola.wav"); // Busca el fitxer del rebot de la pilota.
            if (urlRebot != null) { // Si el fitxer existeix
                soRebot = AudioSystem.getClip(); // Preparem el clip de so
                soRebot.open(AudioSystem.getAudioInputStream(urlRebot)); // Obrim el fitxer d'àudio
            }
            URL urlRaqueta = getClass().getResource("/sonidoraqueta.wav"); // Busca el fitxer del toc de la raqueta (canya)
            if (urlRaqueta != null) { // Si troba el so
                soRaqueta = AudioSystem.getClip(); // El guardem a la variable
                soRaqueta.open(AudioSystem.getAudioInputStream(urlRaqueta)); // El deixem llest per sonar
            }
            URL urlMort = getClass().getResource("/partidaPerduda.wav"); // Busca el so de quan perdis.
            if (urlMort != null) { // Si el troba
                soPerdre = AudioSystem.getClip(); // Preparem el clip de la derrota
                soPerdre.open(AudioSystem.getAudioInputStream(urlMort)); // El carreguem a memòria
            }
            
            // Carreguem el so d'inici de partida
            URL urlInici = getClass().getResource("/iniciPartida.wav"); // Busca l'audio de començament de la partida.
            if (urlInici != null) { // Si existeix el fitxer
                soIniciPartida = AudioSystem.getClip(); // Preparem el clip inicial
                soIniciPartida.open(AudioSystem.getAudioInputStream(urlInici)); // Obrim l'àudio
            }
            
        } catch (Exception e) { // Si hi ha un error carregant 
            System.out.println("Error carregant recursos: " + e.getMessage()); // Ens avisa per consola
        }
    }

    //Posem la música de Pokémon en bucle
    public void reproduirMusica() { // Mètode per la cançó de fons
        try { // Intentem reproduir-la
            final URL url = getClass().getResource("/jocActiu.wav"); // Agafem la cançó de la partida
            if (url != null) { // Si el fitxer hi és
                final AudioInputStream ais = AudioSystem.getAudioInputStream(url); // Creem el flux d'entrada
                musica = AudioSystem.getClip(); // Agafem el clip
                musica.open(ais); // L'obrim
                musica.loop(Clip.LOOP_CONTINUOUSLY); // Posem això perque aixi mai es pari la musica.
                musica.start(); // I començara la musica.
            }
        } catch (final Exception e) { } // Si falla la música, el joc segueix en silenci
    }

    // Mètode per reproduir el so d'inici de partida
    public void reproduirIniciPartida() { // So de benvinguda
        if (soIniciPartida != null) { // Si el clip s'ha carregat bé
            soIniciPartida.setFramePosition(0); // Tornem al principi del so
            soIniciPartida.start(); // El reproduïm un cop
        }
    }

    // Fa el soroll quan la pilota rebota a la paret
    public void sonarRebot() { // Rebot paret
        if (soRebot != null) { // Si el so està carregat
            soRebot.setFramePosition(0); // Reiniciem el so al segon 0
            soRebot.start(); // Fem que soni
        }
    }

    // Fa el soroll quan la pilota toca la teva raqueta
    public void sonarRaqueta() { // Rebot pala (canya)
        if (soRaqueta != null) { // Si el so de pala existeix
            soRaqueta.setFramePosition(0); // Reset del clip
            soRaqueta.start(); // I sona quan li dones a la pilota amb la raqueta.
        }
    }

    // Fa el soroll quan perds la partida
    public void sonarMort() { // So de Game Over
        if (soPerdre != null) { // Si el clip de derrota s'ha trobat
            soPerdre.setFramePosition(0); // Reset a l'inici
            soPerdre.start(); // Reprodueix la derrota
        }
    }
    
    // Fa el soroll quan cliques al menú
    public void reproduirClic() { // Clic botons
        if (soMenu != null) { // Si el clip de menú està llest
            soMenu.setFramePosition(0); // Reiniciem
            soMenu.start(); // Comença el soroll quan cliques.
        }
    }

    // Crea 12 obstacles en llocs aleatoris de la pantalla
    public void generarObstacles() { // Posa els enemics al mapa
        for (int i = 0; i < 12; i++) { // Fem un bucle de 1 a 12
            final int x = aleatori.nextInt(300) + 20; // Tria una X a l'atzar
            final int y = aleatori.nextInt(200) + 50; // Tria una Y a l'atzar a la part superior.
            llistaObstacles[i] = new Rectangle(x, y, 40, 40); // Crea el quadrat de l'obstacle
            visible[i] = true; // El fem visible al principi
        }
    }

    // Aquí es calcula cada moviment i cada xoc
    public void moureTot() { 
        if (getEstaPausat()) return; // Si està en pausa, no fem res

        // Actualitzem els punts segons el temps que portes viu
        setPunts(System.currentTimeMillis() - getTempsInici()); // Calcula quants milisegons portes jugant
        
        // Cada 20 segons pugem de nivell
        setComptadorTempsNivell(getComptadorTempsNivell() + 10); // Sumem temps al comptador de nivell
        //Fem una condicional per sumar cada 20000 punts un 10% de velocitat.
        if (getComptadorTempsNivell() >= 20000 && getNivell() < 20) { // Si passen 20 segons i no som al top
            //Sumem un nivell per cada 20000 punts
            setNivell(getNivell() + 1); // Pugem el número del nivell

            // Pugem la velocitat un 10% però mai passem del límit 
            setVelX(Math.min(getVelX() * 1.10, VELOCITAT_MAXIMA)); // Fem que la pilota vagi un 10% més ràpid
            setVelY(Math.min(getVelY() * 1.10, VELOCITAT_MAXIMA)); // També en vertical
            vel2X = Math.min(vel2X * 1.10, VELOCITAT_MAXIMA); // Accelera la segona bola també
            vel2Y = Math.min(vel2Y * 1.10, VELOCITAT_MAXIMA); // Accelera la segona bola també

            setComptadorTempsNivell(0); // Resetejem el comptador per als pròxims 20 segons
            generarObstacles(); // Posem obstacles nous
        }

        // Si arribes al nivell 8, surt la segona pilota
        if (getNivell() >= 8 && !bola2Activa && pilota2Y < 600) { // Condició per afegir dificultat
            bola2Activa = true; // Activem la segona pilota
        }

        // Lògica de la pilota 1 
        if (bola1Activa) { // Si la primera pilota encara està viva
            setPilotaX(getPilotaX() + getVelX()); // Movem la pilota cap als costats
            setPilotaY(getPilotaY() + getVelY()); // Movem la pilota amunt i avall

            if (getPilotaX() < 0 || getPilotaX() > 360) { setVelX(-getVelX()); sonarRebot(); } // Rebota si toca els costats
            if (getPilotaY() < 30) { setPilotaY(30); setVelY(-getVelY()); sonarRebot(); } // Rebota si toca el sostre

            Rectangle rPilota = new Rectangle((int) getPilotaX(), (int) getPilotaY(), 30, 30); // Crea el quadrat de la bola
            Rectangle rRaqueta = new Rectangle(getRaquetaX(), 530, getRaquetaAmple(), 20); // Crea el quadrat de la pala

            if (rPilota.intersects(rRaqueta)) { // Si la pilota xoca amb la pala
                setVelY(-getVelY()); setPilotaY(530 - 30); sonarRaqueta(); // Invertim direcció i fem soroll
            }

            for (int i = 0; i < 12; i++) { // Mirem tots els obstacles
                if (visible[i]) { // Si l'obstacle encara no l'hem trencat
                    Rectangle hitbox = new Rectangle(llistaObstacles[i].x + 5, llistaObstacles[i].y + 5, 30, 30); // Lloc del xoc
                    if (rPilota.intersects(hitbox)) { // Si la pilota toca l'obstacle
                        visible[i] = false; setVelY(-getVelY()); sonarRebot(); break; // El trenquem i rebotem
                    }
                }
            }
            if (getPilotaY() > 600) bola1Activa = false; // Si cau per sota, la pilota mor
        }

        // Lògica de la pilota 2 
        if (bola2Activa) { // Si la segona pilota ha entrat al joc
            pilota2X += vel2X; pilota2Y += vel2Y; // Movem la segona pilota
            if (pilota2X < 0 || pilota2X > 360) { vel2X = -vel2X; sonarRebot(); } // Rebota a esquerra o dreta
            if (pilota2Y < 30) { pilota2Y = 30; vel2Y = -vel2Y; sonarRebot(); } // Rebota al sostre
            Rectangle rPilota2 = new Rectangle((int) pilota2X, (int) pilota2Y, 30, 30); // Hitbox de la segona bola
            Rectangle rRaqueta = new Rectangle(getRaquetaX(), 530, getRaquetaAmple(), 20); // Hitbox de la pala
            if (rPilota2.intersects(rRaqueta)) { // Si la toquem amb la pala
                vel2Y = -vel2Y; pilota2Y = 530 - 30; sonarRaqueta(); // Rebotem cap amunt
            }
            
            //Fem un bucle per afegir els 12 obstacles per cada nivell amb el seu tamany.
            for (int i = 0; i < 12; i++) { // Mirem xocs de la segona bola
                if (visible[i]) { // Si l'obstacle està present
                    Rectangle hitbox = new Rectangle(llistaObstacles[i].x + 5, llistaObstacles[i].y + 5, 30, 30); // Àrea de col·lisió
                    if (rPilota2.intersects(hitbox)) { // Si hi ha col·lisió
                        visible[i] = false; vel2Y = -vel2Y; sonarRebot(); break; // Trenquem i rebotem
                    }
                }
            }
            if (pilota2Y > 600) bola2Activa = false; // Si la segona cau, es desactiva
        }

        // Si ja no queden pilotes vives, s'ha acabat el joc i mostra l'usuari un game over.
        if (!bola1Activa && !bola2Activa) { // Condició final de derrota
            if (temporitzadorJoc != null) temporitzadorJoc.cancel(); // Aturem el timer per evitar el bucle
            if (musica != null) musica.stop(); // Parem la música de fons
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

        // Dibuixem el fons de Pokémon segons el nivell
        ImageIcon fonsActual; // Variable per saber quina foto de fons toca
        if (getNivell() <= 4) { // Del nivell 1 al 4
            fonsActual = imgFons; // Posem el fons del gimnàs 1
        } else if (getNivell() <= 9) { // Del nivell 5 al 9
            fonsActual = imgFons2; // Posem el fons del gimnàs 2
        } else if (getNivell() <= 14) { // Del nivell 10 al 14
            fonsActual = imgFons3; // Posem el fons del gimnàs 3
        } else { // Nivells màxims
            fonsActual = imgFons4; // Posem el fons final
        }
        if (fonsActual != null) g2d.drawImage(fonsActual.getImage(), 0, 0, 400, 600, null); // Pintem el fons triat

        // Dibuixem els obstacles segons el nivell
        for (int i = 0; i < 12; i++) { // Recorrem la llista d'obstacles
            if (visible[i]) { // Només si els obstacles estan visibles
                ImageIcon iconaTriada; // Variable per a la foto del monstre
                if (getNivell() <= 4) { // Nivell baix
                    iconaTriada = imgObstacle; // gastly.png
                } else if (getNivell() <= 9) { // Nivell mig-baix
                    iconaTriada = imgObstacle2; // hunter.png
                } else if (getNivell() <= 19) { // Nivell mig-alt
                    iconaTriada = imgObstacle3; // pokemonobstaculo.png
                } else { // Nivell màxim
                    iconaTriada = imgObstacle4; // megagengarshiny.png
                }
                
                // Si la imatge s'ha carregat, la dibuixem
                if (iconaTriada != null && iconaTriada.getImage() != null) { // Seguretat per no pintar buit
                    g2d.drawImage(iconaTriada.getImage(), llistaObstacles[i].x, llistaObstacles[i].y, 40, 40, null); // Pintem l'obstacle
                }
            }
        }

        // Dibuixem les pilotes
        if (bola1Activa && imgPilota != null) g2d.drawImage(imgPilota.getImage(), (int) getPilotaX(), (int) getPilotaY(), 30, 30, null); // Pinta bola 1
        if (bola2Activa && imgPilota != null) g2d.drawImage(imgPilota.getImage(), (int) pilota2X, (int) pilota2Y, 30, 30, null); // Pinta bola 2 si n'hi ha

        // Dibuixem la raqueta
        if (imgRaqueta != null) g2d.drawImage(imgRaqueta.getImage(), getRaquetaX(), 520, getRaquetaAmple(), 40, null); // Pinta la nostra pala

        // Dibuixem els textos d'informació (Nom, Punts, Nivell) adaptats a l'idioma
        g2d.setColor(Color.WHITE); // Color del text en blanc
        g2d.setFont(new Font("Arial", Font.BOLD, 14)); // Tipus de lletra clara
        
        String etiquetaJugador = idioma.equals("Català") ? "Jugador: " : "Jugador: ";
        String etiquetaPunts = idioma.equals("Català") ? "Punts: " : "Puntos: ";
        String etiquetaNivell = idioma.equals("Català") ? "Nivell: " : "Nivel: ";

        g2d.drawString(etiquetaJugador + getNomJugador(), 20, 50); // Escribim el nom del jugador
        g2d.drawString(etiquetaPunts + getPunts(), 20, 70); // Escribim els milisegons de puntuació
        g2d.drawString(etiquetaNivell + getNivell(), 300, 50); // Escribim el nivell a la dreta

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

        // Passem tot el dibuix a la pantalla real
        g.drawImage(imatgeOffscreen, 0, 0, null); // Enviem el llenç invisible a la vista de l'usuari
    }

    // Agafa les 10 millors puntuacions de la base de dades i les ensenya
    private void mostrarTop10() { // Mètode per la llista de guanyadors
        try { // Connectem amb la base de dades SQL
            final Connection connexio = Conexio.connectar(); // Obrim la connexió
            if (connexio != null) { // Si la connexió funciona
                final String sql = "SELECT u.nombre, p.puntuacion FROM puntuaciones p JOIN usuarios u ON p.id_usuario = u.id_usuario ORDER BY p.puntuacion DESC LIMIT 10"; // Consulta SQL
                final PreparedStatement pst = connexio.prepareStatement(sql); // Preparem la frase per a la DB
                final ResultSet rs = pst.executeQuery(); // Executem i guardem el resultat

                String colPos = "POS";
                String colJug = idioma.equals("Català") ? "JUGADOR" : "JUGADOR";
                String colPts = idioma.equals("Català") ? "PUNTS" : "PUNTOS";

                String taula = String.format("%-5s %-15s %-10s\n", colPos, colJug, colPts); // Capçalera de la taula
                int pos = 1; // Comptador de posició
                while (rs.next()) { // Mentre hi hagi files de resultat
                    taula += String.format("%-5d %-15s %-10d\n", pos++, rs.getString("nombre"), rs.getLong("puntuacion")); // Afegim línia a la taula
                }
                JTextArea area = new JTextArea(taula); // Fem una àrea de text per ensenyar-ho
                area.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Font per a que les columnes quedin rectes
                JOptionPane.showMessageDialog(this, new JScrollPane(area), "TOP 10", JOptionPane.INFORMATION_MESSAGE); // Mostrem la finestra emergent
                connexio.close(); // Tanquem la connexió amb la base de dades
            }
        } catch (final Exception e) { } // Si la DB falla, simplement no ensenya la taula
    }

    // Aqui posem tots els getters i setters 
   
    public double getPilotaX() { // Ens dona la X de la bola
        return pilotaX;
    }

    /**
     * Canvia la X de la bola
     * @param pilotaX
     */
    public void setPilotaX(final double pilotaX) { 
        this.pilotaX = pilotaX;
    }

    public double getPilotaY() { // Ens dona la Y de la bola
        return pilotaY;
    }

    /**
     * Canvia la Y de la bola
     * @param pilotaY
     */
    public void setPilotaY(final double pilotaY) { 
        this.pilotaY = pilotaY;
    }

    public double getVelX() { // Ens dona la velocitat lateral
        return velX;
    }

    /**
     * Canvia la velocitat lateral
     * @param velX
     */
    public void setVelX(final double velX) { 
        this.velX = velX;
    }

    public double getVelY() { // Ens dona la velocitat vertical
        return velY;
    }

    /**
     * Canvia la velocitat vertical
     * @param velY
     */
    public void setVelY(final double velY) { 
        this.velY = velY;
    }

    public int getRaquetaX() { // Ens dona on està la pala
        return raquetaX;
    }

    /**
     * Movem la pala a una X nova
     * @param raquetaX
     */
    public void setRaquetaX(int raquetaX) { 
        // Limitem la X perquè la raqueta no surti per l'esquerra ni per la dreta 
        if (raquetaX < 0) {
            this.raquetaX = 0;
        } else if (raquetaX > 400 - getRaquetaAmple()) {
            this.raquetaX = 400 - getRaquetaAmple();
        } else {
            this.raquetaX = raquetaX;
        }
    }

    public int getRaquetaAmple() { // Ens diu quant medeix la pala
        return raquetaAmple;
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