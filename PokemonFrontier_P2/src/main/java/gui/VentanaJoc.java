package gui; // La carpeta on guardem el nostre codi

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import logic.Bola;
import logic.GestorObstacles;
import logic.Partida;
import logic.Raqueta;
import logic.db.HibernateUtil;
import logic.db.PuntuacionsRepository;
import logic.db.classes.Puntuaciones;
import logic.ConfigManager;

/**
 * Classe VentanaJoc: Interfície gràfica i motor del joc. Implementa moviment
 * fluid, pujada de nivell progressiva, gestió de base de dades i sistema de
 * pausa/guardat.
 */
public class VentanaJoc extends JFrame {

	private static final long serialVersionUID = 1L;
	
	//Declaració i inicialització de final String del nom de FITXER_PARTIDA
	private static final String FITXER_PARTIDA = "partida_guardada.dat";

	//Instància d'objectes del joc
	//Instància de pilota1
	private final Bola pilota1 = new Bola(50, 50, 3, 3, true);
	//Instància de pilota2
	private final Bola pilota2 = new Bola(100, 50, 3.5, 3.5, false);
	//Instància de la raqueta superior
	private final Raqueta raquetaSuperior = new Raqueta(100, 40, 100, 20);
	//Instància de la raqueta inferior
	private final Raqueta raquetaInferior = new Raqueta(100, 530, 100, 20);

	//Declaració i inicialització de booleans per el control de moviment fluid
	private boolean teclaA = false; 
	private boolean teclaD = false; 
	private boolean teclaEsquerra = false; 
	private boolean teclaDreta = false;
	
	//Declaració i inicialització de finals que controlen la velocitat
	private final double VELOCITAT_RAQUETA = 7.0; // Píxels per frame
	private final double VELOCITAT_MAXIMA_PILOTA = 12.0;

	//Declaració i incialització de variables que controlen l'estat de la partida
	private long punts = 0;
	private final long tempsInici;
	private int nivell = 1;//Es comença pel nivell 1
	private int comptadorTempsNivell = 0;
	private final Partida partida;
	private boolean estaPausat = false;
	private final String idioma;
	private boolean pilota2JaActivada = false;

	//Declaració i inicialització de variables per els recursos i lògica externa
	private Timer temporitzadorJoc;
	//Instància d'objecte de la classe RecursosJoc
	private final RecursosJoc recursos = new RecursosJoc();
	//Instància d'objecte de la classe ConfigManager
	private ConfigManager config = new ConfigManager();
	//Instància d'objecte de la classe GestorObstacles
	private final GestorObstacles gestorObstacles = new GestorObstacles();
	//Declaració d'objecte de la classe PuntuacionsRepository
	private final PuntuacionsRepository puntuacionsRepository;

	/**
	 * Mètode
	 * @param partida
	 * @param hibernate
	 */
	public VentanaJoc(Partida partida, HibernateUtil hibernate) {
		this.partida = partida; // Guardem la partida actual
		this.idioma = partida.getIdioma(); // Guardem l'idioma seleccionat
		// Inicialitzem el repositori de puntuacions
		this.puntuacionsRepository = new PuntuacionsRepository(hibernate);
		// Carreguem recursos del joc
		recursos.carregarRecursos();
		// Recuperem nivell i punts guardats
		this.nivell = partida.getNivell();
		this.punts = partida.getPunts();

		// Ajustar velocitat inicial segons el nivell carregat
		for (int i = 1; i < nivell; i++) {
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
		}

		// Configuració de la finestra
		this.setSize(500, 600);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setFocusable(true);
		
		// Generem obstacles inicials
		generarObstacles();
		
		// Reproduïm música i so inicial
		reproduirMusica();
		reproduirIniciPartida();

		// Calculem el temps d'inici restant els punts per mantenir la cronologia si es
		// carrega partida
		this.tempsInici = System.currentTimeMillis() - this.punts;

		// Gestió d'entrades de teclat (Events)
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int codi = e.getKeyCode();

				// Sistema de Pausa
				if (codi == KeyEvent.VK_Q || codi == KeyEvent.VK_P) {
					setEstaPausat(!getEstaPausat());
				}
				// Accions disponibles quan el joc està pausat
				if (getEstaPausat()) {
					// Guardar partida
					if (codi == KeyEvent.VK_S)
						preguntarDesarPartida();
					// Tornar al menú
					if (codi == KeyEvent.VK_ESCAPE)
						sortirAlMenuSenseGuardar();
				} else {
					// Marquem tecles com premudes per al moviment fluid
					if (codi == KeyEvent.VK_A)
						teclaA = true;
					if (codi == KeyEvent.VK_D)
						teclaD = true;
					if (codi == KeyEvent.VK_LEFT)
						teclaEsquerra = true;
					if (codi == KeyEvent.VK_RIGHT)
						teclaDreta = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int codi = e.getKeyCode();
				// Aturem el moviment quan es deixa de prémer la tecla
				if (codi == KeyEvent.VK_A)
					teclaA = false;
				if (codi == KeyEvent.VK_D)
					teclaD = false;
				if (codi == KeyEvent.VK_LEFT)
					teclaEsquerra = false;
				if (codi == KeyEvent.VK_RIGHT)
					teclaDreta = false;
			}
		});
	}

	/**
	 * Actualitza tota la lògica del joc.
	 */
	public void moureTot() {
		// Si està pausat no es mou res

		if (getEstaPausat())
			return;

		// Moviment de les raquetes (Fluïdesa basada en estat de tecles)
		actualitzarPosicioRaquetes();

		// Actualització de punts i nivell
		setPunts(System.currentTimeMillis() - tempsInici);
		actualitzarNivell();

		// Dificultat extra al nivell 8
		if (getNivell() >= 8 && !pilota2.isActiva() && !pilota2JaActivada) {
			pilota2.setActiva(true);
			pilota2JaActivada = true;
		}
		// Movem les pilotes
		mourePilota(pilota1);
		mourePilota(pilota2);
		// Comprovem si la partida ha acabat
		comprovarFinalPartida();
	}
	/**
	 * Actualitza la posició de les raquetes.
	 */	
	private void actualitzarPosicioRaquetes() {
		// Raqueta Superior (A - D)
		if (teclaA && raquetaSuperior.getX() > 0)
			raquetaSuperior.setX((int) (raquetaSuperior.getX() - VELOCITAT_RAQUETA));
		if (teclaD && raquetaSuperior.getX() < getWidth() - raquetaSuperior.getAmple())
			raquetaSuperior.setX((int) (raquetaSuperior.getX() + VELOCITAT_RAQUETA));

		// Raqueta Inferior (Fletxes)
		if (teclaEsquerra && raquetaInferior.getX() > 0)
			raquetaInferior.setX((int) (raquetaInferior.getX() - VELOCITAT_RAQUETA));
		if (teclaDreta && raquetaInferior.getX() < getWidth() - raquetaInferior.getAmple())
			raquetaInferior.setX((int) (raquetaInferior.getX() + VELOCITAT_RAQUETA));
	}

	/**
	 * Tanca la partida actual i torna al menú principal sense desar cap dada ni
	 * puntuació.
	 */
	private void sortirAlMenuSenseGuardar() {
		// 1. Aturem el motor del joc (el rellotge)
		if (temporitzadorJoc != null) {
			temporitzadorJoc.stop();
		}

		// 2. Aturem la música de fons
		recursos.pararMusica();

		// 3. Tornem a obrir el menú principal
		new MenuInici().setVisible(true);

		// 4. Tanquem i alliberem la memòria d'aquesta finestra de joc
		this.dispose();
	}
	/**
	 * Mou una pilota i comprova col·lisions.
	 */
	private void mourePilota(final Bola pilota) {
		// Si la pilota està desactivada no es mou
		if (!pilota.isActiva())
			return;
		// Actualitza la posició
		pilota.moure();

		// Rebots laterals
		if (pilota.getX() < 0 || pilota.getX() > 460) {
			pilota.setVelX(-pilota.getVelX());
			sonarRebot();
		}

		Rectangle rPilota = pilota.getRectangle();

		// Col·lisió Raqueta Superior
		if (rPilota.intersects(raquetaSuperior.getRectangle())) {
			pilota.setVelY(Math.abs(pilota.getVelY()));
			pilota.setY(raquetaSuperior.getY() + raquetaSuperior.getAlt());
			sonarRaqueta();
		}

		// Col·lisió Raqueta Inferior
		if (rPilota.intersects(raquetaInferior.getRectangle())) {
			pilota.setVelY(-Math.abs(pilota.getVelY()));
			pilota.setY(raquetaInferior.getY() - 30);
			sonarRaqueta();
		}

		// Col·lisió Obstacles
		if (gestorObstacles.comprovarXoc(rPilota)) {
			pilota.setVelY(-pilota.getVelY());
			sonarRebot();
		}

		// Mort de la pilota (surt per dalt o baix)
		if (pilota.getY() > 600 || pilota.getY() < 0)
			pilota.setActiva(false);
	}
	/**
	 * Augmenta la dificultat del joc.
	 */
	private void actualitzarNivell() {
		// Incrementem el comptador
		
		setComptadorTempsNivell(getComptadorTempsNivell() + 10);
		// Cada 20 segons puja el nivell
		
		if (getComptadorTempsNivell() >= 20000 && getNivell() < 20) {
			setNivell(getNivell() + 1);
			// Augmentem velocitat
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			// Reiniciem comptador
			setComptadorTempsNivell(0);
			// Generem nous obstacles
			generarObstacles();
		}
	}

	/**
	 * Dibuixa tots els elements del joc (doble buffer).
	 */

	@Override
	public void paint(final Graphics g) {
		// Creem una imatge en memòria per evitar parpelleig
		final Image imatgeOffscreen = createImage(getWidth(), getHeight());
		if (imatgeOffscreen == null)
			return;
		// Convertim a Graphics2D per poder millorar el renderitzat
		Graphics2D g2d = (Graphics2D) imatgeOffscreen.getGraphics();
		
		// Activem suavitzat d'imatge
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Dibuix de tots els elements
		dibuixarFons(g2d);
		dibuixarObstacles(g2d);
		dibuixarPilotes(g2d);
		dibuixarRaquetes(g2d);
		dibuixarTextos(g2d);
		dibuixarPausa(g2d);
		
		// Pintem la imatge final a pantalla
		g.drawImage(imatgeOffscreen, 0, 0, null);
	}
	/**
	 * Mostra informació del joc (punts, jugadors, nivell).
	 */
	private void dibuixarTextos(final Graphics2D g2d) {
		
		// Font del text
		g2d.setFont(new Font("Arial", Font.BOLD, 14));
		
		// Text segons idioma
		String labelPunts = idioma.equals("Catala") ? "Punts: " : "Puntos: ";
	
		// Informació dels jugadors
		g2d.drawString("J1: " + partida.getNickName1(), 20, 50);
		g2d.drawString("J2: " + partida.getNickName2(), 20, 70);
		
		// Punts i nivell
		g2d.drawString(labelPunts + getPunts(), 20, 90);
		g2d.drawString("Nivell: " + getNivell(), 410, 50);
	}

	
	/**
	 * Inicia el timer principal del joc.
	 */
	public void iniciarJoc() {
		
		// Donem focus a la finestra
		requestFocusInWindow();
		
		// Timer que actualitza joc cada 10ms
		temporitzadorJoc = new Timer(10, e -> {
			moureTot();
			repaint();
		});
		
		// Engeguem el joc
		temporitzadorJoc.start();
	}

	/**
	 * Pregunta si es vol guardar la partida.
	 */
	private void preguntarDesarPartida() {
		String msg = idioma.equals("Catala") ? "Vols desar la partida?" : "¿Quieres guardar la partida?";
		int res = JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			guardarPartida();
	}
	/**
	 * Guarda la partida al fitxer.
	 */
	private void guardarPartida() {
		try {
			
			// Actualitzem dades de la partida
			partida.setNivell(getNivell());
			partida.setPunts(getPunts());
			
			// Escriure objecte en fitxer
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FITXER_PARTIDA));
			oos.writeObject(partida);
			oos.close();
			
			// Tornem al menú
			pararITornarAlMenu();
			
			// Missatge d'èxit
			JOptionPane.showMessageDialog(this,
			        config.t("Guardat correctament", "Guardado correctamente"));
		} catch (Exception e) {	// Missatge d'error
			JOptionPane.showMessageDialog(this,
			        config.t("Error en guardar", "Error al guardar"));
		}
	}
	/**
	 * Comprova si la partida ha acabat.
	 */
	private void comprovarFinalPartida() {
		// Si les dues pilotes han mort
	    if (!pilota1.isActiva() && !pilota2.isActiva()) {
			// Aturem el joc
	        if (temporitzadorJoc != null)
	            temporitzadorJoc.stop();
			// Aturem música
	        recursos.pararMusica();
	        sonarMort();

			try {
				// Creem nova puntuació
				Puntuaciones novaPuntuacio = new Puntuaciones();
				novaPuntuacio.setPuntuacion((int) getPunts());
				// Guardem a la BD
				puntuacionsRepository.guardarPuntuacio(novaPuntuacio, partida.getNickName1());
			} catch (Exception e) {
				System.err.println("Error en desar la puntuació: " + e.getMessage());
			}
			// Mostrem ranking al final
	        SwingUtilities.invokeLater(() -> {
	            mostrarTop10();
	        });
	    }
	}
	/**
	 * Torna al menú principal.
	 */
	private void pararITornarAlMenu() {
		// Aturem el timer
	    if (temporitzadorJoc != null)
	        temporitzadorJoc.stop();
		// Aturem música
	    recursos.pararMusica();
		// Obrim menú
	    new MenuInici().setVisible(true);
		// Tanquem finestra actual
	    this.dispose();
	}
	/**
	 * Mostra el Top 10 de puntuacions.
	 */
	private void mostrarTop10() {
		// Recàrrega de configuració
		    config = new ConfigManager();
			// Obtenim dades de BD
		    List<Puntuaciones> top = puntuacionsRepository.obtenirTop10();

		    StringBuilder sb = new StringBuilder("TOP 10\n\n");

	    int i = 1;
	    
		// Construïm llista de ranking
	    for (Puntuaciones p : top) {
			// Es canvia p.getNomJugador() per p.getUsuarios().getNombre() per seguretat
			String nomMostrat = (p.getUsuarios() != null) ? p.getUsuarios().getNombre() : "Anònim";
	        sb.append(String.format("%d. %-15s %d\n", i++, nomMostrat, p.getPuntuacion()));
	    }
		// Zona de text
	    JTextArea area = new JTextArea(sb.toString());
	    area.setFont(new Font("Monospaced", Font.BOLD, 16));
	    area.setEditable(false);
	    area.setOpaque(true);

		// Color segons configuració
	    Color fondo = convertirColor(config.getColorPuntuacio());

	    area.setBackground(fondo);
	    area.setForeground(Color.WHITE);
		// Scroll
	    JScrollPane scroll = new JScrollPane(area);

	    JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());

	    panel.setBackground(fondo);

	    scroll.getViewport().setOpaque(true);
	    scroll.getViewport().setBackground(fondo);
	    scroll.setOpaque(true);
	    scroll.setBackground(fondo);

		// Botó canvi color
	    JButton btnColor = new JButton(
	            config.t("Canviar color", "Cambiar color"));
	    
	    btnColor.addActionListener(e -> {

	    	String[] opciones = {
	    		    "BLACK",
	    		    "RED",
	    		    "BLUE",
	    		    "GREEN"
	    		};

	    	String seleccion = (String) JOptionPane.showInputDialog(
	    	        panel,
	    	        config.t("Selecciona color:", "Selecciona color:"),
	    	        config.t("Configuració", "Configuración"),
	    	        JOptionPane.QUESTION_MESSAGE,
	    	        null,
	    	        opciones,
	    	        config.getColorPuntuacio()
	    	);

	        if (seleccion != null) {

	            // guardar en archivo
	            config.setColorPuntuacio(seleccion);

	            // recargar config
	            config = new ConfigManager();

	            Color nuevo = convertirColor(seleccion);

	            panel.setBackground(nuevo);
	            scroll.setBackground(nuevo);
	            scroll.getViewport().setBackground(nuevo);

	            area.setBackground(nuevo);
	            area.setForeground(Color.WHITE);

	            panel.repaint();
	        }
	    });

	    panel.add(btnColor, BorderLayout.NORTH);
	    panel.add(scroll, BorderLayout.CENTER);

		// Finestra ranking
	    JFrame frame = new JFrame("TOP 10");
	    frame.setSize(400, 450);
	    frame.setLocationRelativeTo(null);
	    frame.setContentPane(panel);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Quan es tanca torna al menú
	    frame.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
	        public void windowClosed(java.awt.event.WindowEvent e) {
	            new MenuInici().setVisible(true);
	        }
	    });

	    frame.setVisible(true);
	}

	/**
	 * Converteix el nom del color guardat a un objecte Color.
	 */
	private Color convertirColor(String c) {
		if (c == null) return Color.DARK_GRAY;

		switch (c.toUpperCase()) {
			case "RED": return Color.RED;
			case "BLUE": return Color.BLUE;
			case "GREEN": return Color.GREEN;
			case "YELLOW": return Color.YELLOW;
			default: return Color.BLACK;
		}
	}
	/**
	 * Dibuixa el fons del joc.
	 */
	
	private void dibuixarFons(Graphics2D g2d) {
		ImageIcon f = recursos.getFonsActual(getNivell());
		if (f != null)
			g2d.drawImage(f.getImage(), 0, 0, 500, 600, null);
	}
	/**
	 * Dibuixa els obstacles visibles.
	 */
	private void dibuixarObstacles(Graphics2D g2d) {
		Rectangle[] obs = gestorObstacles.getLlistaObstacles();
		boolean[] vis = gestorObstacles.getVisible();
		for (int i = 0; i < gestorObstacles.getTotalObstacles(); i++) {
			if (vis[i]) {
				ImageIcon img = recursos.getObstacleActual(getNivell());
				g2d.drawImage(img.getImage(), obs[i].x, obs[i].y, 40, 40, null);
			}
		}
	}
	/**
	 * Dibuixa les pilotes actives.
	 */
	private void dibuixarPilotes(Graphics2D g2d) {
		ImageIcon img = recursos.getImgPilota();
		if (pilota1.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota1.getX(), (int) pilota1.getY(), 30, 30, null);
		if (pilota2.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota2.getX(), (int) pilota2.getY(), 30, 30, null);
	}
	/**
	 * Dibuixa les raquetes dels jugadors.
	 */
	private void dibuixarRaquetes(Graphics2D g2d) {
		ImageIcon img = recursos.getImgRaqueta();
		g2d.drawImage(img.getImage(), raquetaSuperior.getX(), raquetaSuperior.getY() - 10, raquetaSuperior.getAmple(),
				40, null);
		g2d.drawImage(img.getImage(), raquetaInferior.getX(), 520, raquetaInferior.getAmple(), 40, null);
	}
	/**
	 * Mostra pantalla de pausa.
	 */
	private void dibuixarPausa(final Graphics2D g2d) {
		// Si el joc esta pausat, enfosquim la pantalla.
		if (getEstaPausat()) {
			// Dibuixem un rectangle negre semitransparent que ocupa tota la finestra
			g2d.setColor(new Color(0, 0, 0, 150)); // El 150 es el nivell de transparencia
			g2d.fillRect(0, 0, 500, 600);

			// Posem el text de pausa ben gran al centre
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Impact", Font.BOLD, 50));
			g2d.drawString("PAUSA", 130, 300);
			// Missatges
			String msgPausa = config.t("Prem 'Q' per continuar", "Pulsa 'Q' para continuar");
			String msgGuardar = config.t("Prem 'S' per desar la partida", "Pulsa 'S' para guardar la partida");
			String msgMenu = config.t("Prem 'Esc' per sortir al menú", "Pulsa 'Esc' para salir al menú");
			g2d.setFont(new Font("Arial", Font.PLAIN, 15));
			g2d.drawString(msgPausa, 125, 340);
			g2d.drawString(msgGuardar, 125, 360);
			g2d.drawString(msgMenu, 125, 380);
		}
	}

	// getters i setters
	private void generarObstacles() {
		gestorObstacles.generarObstacles();
	}

	private void reproduirMusica() {
		recursos.reproduirMusica();
	}

	private void reproduirIniciPartida() {
		recursos.reproduirIniciPartida();
	}

	private void sonarRebot() {
		recursos.sonarRebot();
	}

	private void sonarRaqueta() {
		recursos.sonarRaqueta();
	}

	private void sonarMort() {
		recursos.sonarMort();
	}

	public long getPunts() {
		return punts;
	}

	public void setPunts(long p) {
		this.punts = p;
	}

	public int getNivell() {
		return nivell;
	}

	public void setNivell(int n) {
		this.nivell = n;
	}

	public boolean getEstaPausat() {
		return estaPausat;
	}

	public void setEstaPausat(boolean p) {
		this.estaPausat = p;
	}

	public int getComptadorTempsNivell() {
		return comptadorTempsNivell;
	}

	public void setComptadorTempsNivell(int c) {
		this.comptadorTempsNivell = c;
	}
}