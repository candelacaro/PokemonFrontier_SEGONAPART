package gui; // La carpeta on guardem el nostre codi

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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import logic.Bola;
import logic.GestorObstacles;
import logic.Partida;
import logic.Raqueta;
import logic.db.DesarPuntuacions;
import logic.db.HibernateUtil;
import logic.db.Puntuacio;
import logic.db.PuntuacionsRepository;
import logic.ConfigManager;

/**
 * Classe VentanaJoc: Interfície gràfica i motor del joc. Implementa moviment
 * fluid, pujada de nivell progressiva, gestió de base de dades i sistema de
 * pausa/guardat.
 */
public class VentanaJoc extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String FITXER_PARTIDA = "partida_guardada.dat";

	// Entitats del joc
	private final Bola pilota1 = new Bola(50, 50, 3, 3, true);
	private final Bola pilota2 = new Bola(100, 50, 3.5, 3.5, false);
	private final Raqueta raquetaSuperior = new Raqueta(100, 40, 100, 20);
	private final Raqueta raquetaInferior = new Raqueta(100, 530, 100, 20);

	// Control de moviment fluid (Smooth Movement)
	private boolean teclaA = false, teclaD = false, teclaEsquerra = false, teclaDreta = false;
	private final double VELOCITAT_RAQUETA = 7.0; // Píxels per frame
	private final double VELOCITAT_MAXIMA_PILOTA = 12.0;

	// Estat de la partida
	private long punts = 0;
	private final long tempsInici;
	private int nivell = 1;
	private int comptadorTempsNivell = 0;
	private final Partida partida;
	private boolean estaPausat = false;
	private final String idioma;
	private boolean pilota2JaActivada = false;

	// Recursos i lògica externa
	private Timer temporitzadorJoc;
	private final RecursosJoc recursos = new RecursosJoc();
	private final ConfigManager config = new ConfigManager();
	private final GestorObstacles gestorObstacles = new GestorObstacles();
	private final PuntuacionsRepository puntuacionsRepository;
	private final DesarPuntuacions desarPuntuacions;

	public VentanaJoc(Partida partida, HibernateUtil hibernate) {
		this.partida = partida;
		this.idioma = "Català"; // Es podria obtenir de partida.getIdioma()

		this.puntuacionsRepository = new PuntuacionsRepository(hibernate);
		this.desarPuntuacions = new DesarPuntuacions(puntuacionsRepository);

		recursos.carregarRecursos();

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

		generarObstacles();
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

				if (getEstaPausat()) {
					if (codi == KeyEvent.VK_S)
						preguntarDesarPartida();
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

	// --- LÒGICA DE MOVIMENT ---

	public void moureTot() {
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

		mourePilota(pilota1);
		mourePilota(pilota2);

		comprovarFinalPartida();
	}

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

	private void mourePilota(final Bola pilota) {
		if (!pilota.isActiva())
			return;

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

	private void actualitzarNivell() {
		setComptadorTempsNivell(getComptadorTempsNivell() + 10);
		if (getComptadorTempsNivell() >= 20000 && getNivell() < 20) {
			setNivell(getNivell() + 1);
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			setComptadorTempsNivell(0);
			generarObstacles();
		}
	}

	// --- GRÀFICS (PAINT) ---

	@Override
	public void paint(final Graphics g) {
		final Image imatgeOffscreen = createImage(getWidth(), getHeight());
		if (imatgeOffscreen == null)
			return;

		Graphics2D g2d = (Graphics2D) imatgeOffscreen.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		dibuixarFons(g2d);
		dibuixarObstacles(g2d);
		dibuixarPilotes(g2d);
		dibuixarRaquetes(g2d);
		dibuixarTextos(g2d);
		dibuixarPausa(g2d);

		g.drawImage(imatgeOffscreen, 0, 0, null);
	}

	private void dibuixarTextos(final Graphics2D g2d) {
		// Aplicar color des de configuració
		String col = config.getColorPuntuacio().toUpperCase();
		switch (col) {
		case "RED":
			g2d.setColor(Color.RED);
			break;
		case "BLUE":
			g2d.setColor(Color.BLUE);
			break;
		case "GREEN":
			g2d.setColor(Color.GREEN);
			break;
		default:
			g2d.setColor(Color.WHITE);
		}

		g2d.setFont(new Font("Arial", Font.BOLD, 14));
		String labelPunts = idioma.equals("Català") ? "Punts: " : "Puntos: ";
		g2d.drawString("J1: " + partida.getNickName1(), 20, 50);
		g2d.drawString("J2: " + partida.getNickName2(), 20, 70);
		g2d.drawString(labelPunts + getPunts(), 20, 90);
		g2d.drawString("Nivell: " + getNivell(), 410, 50);
	}

	// --- MÈTODES AUXILIARS I SISTEMA ---

	public void iniciarJoc() {
		requestFocusInWindow();
		temporitzadorJoc = new Timer(10, e -> {
			moureTot();
			repaint();
		});
		temporitzadorJoc.start();
	}

	private void preguntarDesarPartida() {
		String msg = idioma.equals("Català") ? "Vols desar la partida?" : "¿Quieres guardar la partida?";
		int res = JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			guardarPartida();
	}

	private void guardarPartida() {
		try {
			partida.setNivell(getNivell());
			partida.setPunts(getPunts());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FITXER_PARTIDA));
			oos.writeObject(partida);
			oos.close();
			pararITornarAlMenu();
			JOptionPane.showMessageDialog(this, "Guardat correctament");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error en guardar");
		}
	}

	private void comprovarFinalPartida() {
		if (!pilota1.isActiva() && !pilota2.isActiva()) {
			if (temporitzadorJoc != null)
				temporitzadorJoc.stop();
			recursos.pararMusica();
			sonarMort();

			desarPuntuacions.guardarPuntuacionEquip(partida, getPunts());
			mostrarTop10();
			pararITornarAlMenu();
		}
	}

	private void pararITornarAlMenu() {
		if (temporitzadorJoc != null)
			temporitzadorJoc.stop();
		recursos.pararMusica();
		new MenuInici().setVisible(true);
		this.dispose();
	}

	private void mostrarTop10() {
		List<Puntuacio> top = puntuacionsRepository.obtenirTop10();
		StringBuilder sb = new StringBuilder("TOP 10\n\n");
		int i = 1;
		for (Puntuacio p : top) {
			sb.append(String.format("%d. %-15s %d\n", i++, p.getNomJugador(), p.getPunts()));
		}
		JTextArea area = new JTextArea(sb.toString());
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JOptionPane.showMessageDialog(this, new JScrollPane(area));
	}

	// --- DIBUIX SECUNDARI ---
	private void dibuixarFons(Graphics2D g2d) {
		ImageIcon f = recursos.getFonsActual(getNivell());
		if (f != null)
			g2d.drawImage(f.getImage(), 0, 0, 500, 600, null);
	}

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

	private void dibuixarPilotes(Graphics2D g2d) {
		ImageIcon img = recursos.getImgPilota();
		if (pilota1.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota1.getX(), (int) pilota1.getY(), 30, 30, null);
		if (pilota2.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota2.getX(), (int) pilota2.getY(), 30, 30, null);
	}

	private void dibuixarRaquetes(Graphics2D g2d) {
		ImageIcon img = recursos.getImgRaqueta();
		g2d.drawImage(img.getImage(), raquetaSuperior.getX(), raquetaSuperior.getY() - 10, raquetaSuperior.getAmple(),
				40, null);
		g2d.drawImage(img.getImage(), raquetaInferior.getX(), 520, raquetaInferior.getAmple(), 40, null);
	}

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

			String msgPausa = idioma.equals("Català") ? "Prem 'Q' per continuar" : "Pulsa 'Q' para continuar";
			String msgGuardar = idioma.equals("Català") ? "Prem 'S' per desar la partida"
					: "Pulsa 'S' para guardar la partida";
			String msgMenu = idioma.equals("Català") ? "Prem 'Esc' per sortir al menu"
					: "Pulsa 'Esc' para salir al menu";
			g2d.setFont(new Font("Arial", Font.PLAIN, 15));
			g2d.drawString(msgPausa, 125, 340);
			g2d.drawString(msgGuardar, 125, 360);
			g2d.drawString(msgMenu, 125, 380);
		}
	}

	// --- GETTERS I SETTERS ---
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