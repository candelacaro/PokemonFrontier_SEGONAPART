package gui;

/**
 * Aquesta classe és el menú i el primer que es veu del joc; és el primer que veu l'usuari, posem una musica de fons 
 * de la tematica del nostre joc.
    
   Hem posat 3 botons: el de Jugar, despres de donar-li et preguntara l'idioma, el nom d'usuari i també el nivell.
  
   Una finestra emergent explica les regles del joc per informar a l'usuari amb abans de començar.
   
   Quan l'usuari clica a jugar, tanquem el menú i obrim la finestra de joc.
   
   També tenim el botó de sortir, que si li dones es tancara el programa.
  
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;

// Aquesta classe és la que fabrica la pantalla principal que veus en obrir el joc
public class MenuInici extends JFrame {

    private Clip musicaMenu; // El reproductor per a la música de fons del menú

    public MenuInici() {
        inicialitzarComponents(); // Cridem a la funció que munta tots els botons i textos
        this.setTitle("Pokémon Frontier - Main Menu"); // El títol que surt a dalt de la finestra
        this.setResizable(false); // No deixem que l'usuari estiri la pantalla
        this.setLocationRelativeTo(null); // Fem que el menú surti clavat al mig de la pantalla
        reproduirMusicaMenu("/introJoc.wav"); // Engeguem la música de la introducció
    }

    // Aquí configurem tot el disseny visual del menú
    private void inicialitzarComponents() {
        JPanel panellPrincipal = new JPanel(); // El contenidor on anirà tot
        panellPrincipal.setBackground(new Color(45, 45, 45)); // Un color gris fosc de fons
        panellPrincipal.setLayout(new BoxLayout(panellPrincipal, BoxLayout.Y_AXIS)); // Posem els elements un sota l'altre
        panellPrincipal.setBorder(new EmptyBorder(50, 50, 50, 50)); // Donem una mica de marge als costats

        // El títol del joc ben gran
        JLabel titol = new JLabel("POKÉMON FRONTIER");
        titol.setForeground(Color.WHITE); // Lletra blanca
        titol.setFont(new Font("Arial", Font.BOLD, 26)); // Lletra Arial, negreta i tamany 26
        titol.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrem el text

        // Creem els botons
        JButton btnJugar = crearBotoEstilitzat("JUGAR", new Color(46, 204, 113)); // Botó verd
        JButton btnRegles = crearBotoEstilitzat("REGLAS", new Color(52, 152, 219)); // Botó blau
        JButton btnSortir = crearBotoEstilitzat("SALIR", new Color(231, 76, 60)); // Botó vermell

        // Programem què passa quan cliquem a JUGAR
        btnJugar.addActionListener(e -> {
            reproduirSo("/menuClick.wav"); // Fem el soroll de clic
            accioBotoJugar(); // Anem a la funció per triar idioma i nom
        });

        // Programem què passa quan cliquem a REGLAS
        btnRegles.addActionListener(e -> {
            reproduirSo("/menuClick.wav"); // So de clic
            mostrarRegles(); // Ensenyem la finestra amb les instruccions
        });

        // Programem el botó de sortir
        btnSortir.addActionListener(e -> System.exit(0)); // Tanquem el programa del tot

        // Anem afegint els elements al panell amb espais entremig
        panellPrincipal.add(titol);
        panellPrincipal.add(Box.createRigidArea(new Dimension(0, 40))); // Espai de 40 píxels sota el títol
        panellPrincipal.add(btnJugar);
        panellPrincipal.add(Box.createRigidArea(new Dimension(0, 15))); // Espai de 15 entre botons
        panellPrincipal.add(btnRegles);
        panellPrincipal.add(Box.createRigidArea(new Dimension(0, 15)));
        panellPrincipal.add(btnSortir);

        this.setContentPane(panellPrincipal); // Posem el panell a la finestra
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Si tanquem la X, s'acaba tot
        this.pack(); // Ajustem la finestra al contingut
        this.setSize(400, 500); // Forcem una mida de 400x500
    }

    // Funció per deixar els botons ben polits i iguals
    private JButton crearBotoEstilitzat(String text, Color colorFons) {
        JButton boto = new JButton(text);
        boto.setMaximumSize(new Dimension(200, 50)); // Mida màxima del botó
        boto.setAlignmentX(Component.CENTER_ALIGNMENT); // Ho centrem horitzontalment
        boto.setFocusPainted(false); // Treiem el quadrat que surt al text quan cliques
        boto.setBackground(colorFons); // Posem el color de fons triat
        boto.setForeground(Color.WHITE); // Text blanc
        boto.setFont(new Font("Arial", Font.BOLD, 14)); // Lletra negreta
        boto.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Fa que surti la maneta en passar el ratolí
        boto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Marge intern
        return boto;
    }

    // Aquí gestionem tota la lògica abans de que la pilota comenci a moure's
    private void accioBotoJugar() {
        String[] idiomes = {"Català", "Castellano"}; // Opcions per a la finestra
        int idSeleccionat = JOptionPane.showOptionDialog(this, "Selecciona idioma:", "Configuración",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, idiomes, idiomes);
        
        if (idSeleccionat == -1) return; // Si l'usuari tanca la finestra, no fem res
        reproduirSo("/menuClick.wav");

        // Guardem l'idioma seleccionat per passar-lo a la VentanaJuego
        String idiomaTriat = idiomes[idSeleccionat];

        // Demanem el nom segons l'idioma que hagi triat
        String nom = JOptionPane.showInputDialog(this, (idSeleccionat == 0 ? "Nom d'usuari:" : "Nombre de usuario:"));
        if (nom == null || nom.trim().isEmpty()) return; // Si no posa nom, cancel·lem
        reproduirSo("/menuClick.wav");

        // Parem la música del menú per a que no s'ajunti amb la del joc
        if (musicaMenu != null && musicaMenu.isRunning()) {
            musicaMenu.stop();
        }

        // Ara li passem els 3 arguments que demana el constructor
        VentanaJoc joc = new VentanaJoc(1, nom, idiomaTriat); 
        joc.setVisible(true); // Fem que aparegui la pantalla de joc
        this.dispose(); // Tanquem aquest menú principal
        
        // Lancem el joc en un fil nou perquè tot vagi fluid
        new Thread(() -> joc.iniciarJoc()).start();
    }

    // Una finestra emergent que explica com es juga
    private void mostrarRegles() {
        String textRegles = "REGLES DEL JOC \n\n"
                + "1. Mou la raqueta amb el ratolí o les fletxes per colpejar la pilota.\n"
                + "2. La puntuació equival al temps transcorregut en milisegons.\n"
                + "3. CADA 20 SEGONS es pujarà de nivell.\n"
                + "4. La velocitat augmentarà un 10% en cada canvi de nivell.\n"
                + "5. Si la pilota toca el terra, la partida finalitza.";
        UIManager.put("OptionPane.messageForeground", Color.BLACK); // Text en negre per a que es llegeixi bé
        JOptionPane.showMessageDialog(this, textRegles, "Reglas del Proyecto ABP", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Funció per carregar la música i que no pari de sonar 
     * @param ruta
     */
    private void reproduirMusicaMenu(String ruta) {
        try {
            URL url = getClass().getResource(ruta); // Busquem l'arxiu de so
            if (url != null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(url);
                musicaMenu = AudioSystem.getClip();
                musicaMenu.open(ais);
                musicaMenu.loop(Clip.LOOP_CONTINUOUSLY); // Posem el bucle infinit
                musicaMenu.start(); // Engeguem la música
            }
        } catch (Exception e) {
            System.out.println("Error música menú: " + e.getMessage());
        }
    }

    /**
     * Funció per a sons curts 
     * @param ruta
     */
    private void reproduirSo(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start(); // Sona un cop i s'acaba
            }
        } catch (Exception e) {
            System.out.println("Error sonido: " + e.getMessage());
        }
    }

    // El punt d'inici de tot el programa
    public static void main(String args[]) {
        // Cridem a la interfície visual de forma segura
        java.awt.EventQueue.invokeLater(() -> new MenuInici().setVisible(true));
    }
}