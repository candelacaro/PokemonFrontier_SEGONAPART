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
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class VentanaJoc extends JFrame {

	private static final long serialVersionUID = 1L;

	// Declaració i inicialització de final String del nom de FITXER_PARTIDA
	private static final String FITXER_PARTIDA = "partida_guardada.dat";

	// Declaració i inicialització de finals per els arguments de cada pilota
	// Pilota 1
	private final int COORDENADA_X_PILOTA1 = 50;
	private final int COORDENADA_Y_PILOTA1 = 50;
	private final double VELOCITAT_PILOTA1 = 3;
	// Pilota 2
	private final int COORDENADA_X_PILOTA2 = 100;
	private final int COORDENADA_Y_PILOTA2 = 50;
	private final double VELOCITAT_PILOTA2 = 3.5;

	// Instància d'objectes del joc
	// Instància de pilota1
	private final Bola pilota1 = new Bola(COORDENADA_X_PILOTA1, COORDENADA_Y_PILOTA1, VELOCITAT_PILOTA1,
			VELOCITAT_PILOTA1, true);
	// Instància de pilota2
	private final Bola pilota2 = new Bola(COORDENADA_X_PILOTA2, COORDENADA_Y_PILOTA2, VELOCITAT_PILOTA2,
			VELOCITAT_PILOTA2, false);
	// Instància de la raqueta superior
	private final Raqueta raquetaSuperior = new Raqueta(100, 40, 100, 20);
	// Instància de la raqueta inferior
	private final Raqueta raquetaInferior = new Raqueta(100, 530, 100, 20);

	// Declaració i inicialització de booleans per el control de moviment fluid
	private boolean teclaA = false;
	private boolean teclaD = false;
	private boolean teclaEsquerra = false;
	private boolean teclaDreta = false;

	// Declaració i inicialització de finals que controlen la velocitat
	private final double VELOCITAT_RAQUETA = 7.0; // Píxels per frame
	private final double VELOCITAT_MAXIMA_PILOTA = 12.0;

	// Declaració i incialització de variables que controlen l'estat de la partida
	private long punts = 0;
	private final long tempsInici;
	private int nivell = 1;// Es comença pel nivell 1
	private int comptadorTempsNivell = 0;
	private final Partida partida;
	private boolean estaPausat = false;
	private final String idioma;
	private boolean pilota2JaActivada = false;

	// Declaració i inicialització de finals per la mostra de text a la finestra
	private static final String LLENGUATGE_CATALA = "Catala";
	private static final String PUNTUACIO_CATALA = "Punts: ";
	private static final String PUNTUACIO_CASTELLA = "Puntos: ";
	private static final String PIRMER_JUGADOR = "J1: ";
	private static final String SEGON_JUGADOR = "J2: ";
	private static final String NIVELL_ACTUAL = "Nivell: ";
	private static final String DEU_MILLORS_PUNTUACIONS = "TOP 10";
	private static final String MISSATGE_DE_PAUSA = "PAUSE";

	// Declaració i inicialització de finals dels texts traduits
	private static final String GUARDAR_PARTIDA_CATALA = "Vols desar la partida?";
	private static final String GUARDAR_PARTIDA_CASTELLA = "¿Quieres guardar la partida?";
	private static final String BOTO_PER_DESAR_PARTIDA = "Save";
	private static final String PARTIDA_GUARDADA_CATALA = "Guardat correctament";
	private static final String PARTIDA_GUARDADA_CASTELLA = "Guardado correctamente";
	private static final String ERROR_PARTIDA_CATALA = "Error en desar";
	private static final String ERROR_PARTIDA_CASTELLA = "Error al guardar";
	private static final String ERROR_PUNTUACIO = "Error en desar la puntuació: ";
	private static final String CANVIAR_COLOR_CATALA = "Canviar color";
	private static final String CANVIAR_COLOR_CASTELLA = "Cambiar color";
	private static final String SELECCIO_COLOR = "Selecciona color: ";
	private static final String CONFIGURACIO_CATALA = "Configuració";
	private static final String CONFIGURACIO_CASTELLA = "Configuración";
	private static final String OPCIO_CONTINUAR_CATALA = "Prem 'Q' per continuar";
	private static final String OPCIO_CONTINUAR_CASTELLA = "Pulsa 'Q' para continuar";
	private static final String OPCIO_GUARDAR_CATALA = "Prem 'S' per desar la partida";
	private static final String OPCIO_GUARDAR_CASTELLA = "Pulsa 'S' para guardar la partida";
	private static final String OPCIO_SORTIR_CATALA = "Prem 'Esc' per sortir al menú";
	private static final String OPCIO_SORTIR_CASTELLA = "Pulsa 'Esc' para salir al menú";

	// Declaració i inicialització de finals per les posicions per les possibles
	// opcions del joc
	private static final int POSICIO_HORIZONTAL_PAUSA = 130;
	private static final int POSICIO_VERTICAL_PAUSA = 300;
	private static final int POSICIO_HORIZONTAL_OPCIONS = 125;
	private static final int POSICIO_VERTICAL_CONTINUAR = 340;
	private static final int POSICIO_VERTICAL_GUARDAR = 360;
	private static final int POSICIO_VERTICAL_SORTIR = 380;

	// Declaració i inicialització de finals per les mides de la finestra
	private static final int AMPLADA_FINESTRA = 500;
	private static final int ALÇADA_FINESTRA = 600;

	// Declaració i inicialització de finals per les mides dels obstacles
	private final int AMPLADA_OBSTACLE = 40;
	private final int ALÇADA_OBSTACLE = 40;
	
	// Declaració i inicialització de finals pels colors de la tabla de puntuació
	private static final String COLOR_VERMELL = "RED", COLOR_VERD = "GREEN", COLOR_BLAU = "BLUE", COLOR_NEGRE = "BLACK",
			COLOR_GROC = "YELLOW";

	// Declaració i inicialització de finals per les posicions de la mostra de text
	// en pantalla
	private static final int POSICIO_HORIZONTAL_DADES_PARTIDA = 20;
	private static final int POSICIO_VERTICAL_DADES_PARTIDA = 50;
	private static final int POSICIO_HORIZONTAL_NIVELL = 410;
	private static final int POSICIO_VERTICAL_SEGON_JUGADOR = 70;
	private static final int POSICIO_VERTICAL_PUNTUACIO = 90;

	// Declaració i inicialització de finals de la pujada de dificultat (dues boles)
	private static final int SUPERACIO_NIVELL_VUIT = 8;

	// Declaració i inicialització de final pels limits de colisions de la pilota
	private static final int COLISIO_PARET_ESQUERRA = 0;
	private static final int COLISIO_PARET_DRETA = 460;
	private static final int COMPROVADOR_DE_COLISIO_DE_RAQUETA = 0;
	private static final int COLISIO_PILOTA_RAQUETA = 30;
	private static final int LIMIT_PILOTA_RAQUETA_SUPERIOR = 600;
	private static final int LIMIT_PILOTA_RAQUETA_INFERIOR = 0;

	// Declaració i inicialització de finals del increment de nivell i velocitats
	private static final int INCREMENT_TEMPS_MILISEGONS = 10;
	private static final int SALT_DE_NIVELL = 20000;
	private static final int INCREMENT_DE_NIVELL = 1;
	private static final int COMPTADOR = 0;
	private static final int LIMIT_DE_NIVELL = 20;

	// Declaració i inicialització de finals de la tipografia
	private static final String TIPOGRAFIA_FONT_ARIAL = "Arial";
	private static final String TIPOGRAFIA_FONT_MONOSPACED = "Monospaced";
	private static final String TIPOGRAFIA_FONT_IMPACT = "Impact";
	private static final int FONT_ARIAL = 14;
	private static final int FONT_MONOSPACED = 16;
	private static final int FONT_IMPACT = 50;
	private static final int FONT_ARIAL_PAUSA = 15;

	// Declaració i inicialització de variables per els recursos i lògica externa
	private Timer temporitzadorJoc;
	// Instància d'objecte de la classe RecursosJoc
	private final RecursosJoc recursos = new RecursosJoc();
	// Instància d'objecte de la classe ConfigManager
	private ConfigManager config = new ConfigManager();
	// Instància d'objecte de la classe GestorObstacles
	private final GestorObstacles gestorObstacles = new GestorObstacles();
	// Declaració d'objecte de la classe PuntuacionsRepository
	private final PuntuacionsRepository puntuacionsRepository;

	/**
	 * Constructor que inicialitza la finestra del joc, guarda la partida i connecta
	 * amb l'Hibernate
	 * 
	 * @param partida,   instància de la classe partida
	 * @param hibernate, connexió amb bbdd
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
		this.setSize(AMPLADA_FINESTRA, ALÇADA_FINESTRA);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setFocusable(true);

		// Generem obstacles inicials
		generarObstacles();

		// Reproduïm música i so inicial
		reproduirMusica();
		reproduirIniciPartida();

		/*
		 * Calculem el temps d'inici restant els punts per mantenir la cronologia si es
		 * carrega partida
		 */
		this.tempsInici = System.currentTimeMillis() - this.punts;

		// Gestió d'entrades de teclat (Events)
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Declaració i inicialització de variable de llegeix quina tecla s'està prenent
				int codi = e.getKeyCode();

				// Sistema de Pausa, si es pren a la tecla Q es pausa el joc
				if (codi == KeyEvent.VK_Q) {
					setEstaPausat(!getEstaPausat());
				}
				// Accions disponibles quan el joc està pausat
				if (getEstaPausat()) {
					// Guardar partida, si e spren la tecla S
					if (codi == KeyEvent.VK_S) {
						// Crida del mètode
						preguntarDesarPartida();
					}
					// Tornar al menú, si prenem la tecla ESC
					if (codi == KeyEvent.VK_ESCAPE) {
						// Crida del mètode
						sortirAlMenuSenseGuardar();
					}
				} else {
					// Marquem tecles com premudes per al moviment fluid
					if (codi == KeyEvent.VK_A) {
						teclaA = true;
					}
					if (codi == KeyEvent.VK_D) {
						teclaD = true;
					}
					if (codi == KeyEvent.VK_LEFT) {
						teclaEsquerra = true;
					}
					if (codi == KeyEvent.VK_RIGHT) {
						teclaDreta = true;
					}
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
	 * Mètode que actualitza tota la lògica del joc.
	 */
	public void moureTot() {
		// Si està pausat no es mou res
		if (getEstaPausat()) {
			return;
		}

		// Moviment de les raquetes (Fluïdesa basada en estat de tecles)
		actualitzarPosicioRaquetes();

		// Actualització de punts i nivell
		setPunts(System.currentTimeMillis() - tempsInici);
		actualitzarNivell();

		// Dificultat extra al nivell 8
		if (getNivell() >= SUPERACIO_NIVELL_VUIT && !pilota2.isActiva() && !pilota2JaActivada) {
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
	 * Mètode que actualitza la posició de les raquetes.
	 */
	private void actualitzarPosicioRaquetes() {
		// Raqueta Superior (A - D)
		if (teclaA && raquetaSuperior.getX() > COMPROVADOR_DE_COLISIO_DE_RAQUETA) {
			raquetaSuperior.setX((int) (raquetaSuperior.getX() - VELOCITAT_RAQUETA));
		}

		if (teclaD && raquetaSuperior.getX() < getWidth() - raquetaSuperior.getAmple()) {
			raquetaSuperior.setX((int) (raquetaSuperior.getX() + VELOCITAT_RAQUETA));
		}

		// Raqueta Inferior (Fletxes)
		if (teclaEsquerra && raquetaInferior.getX() > COMPROVADOR_DE_COLISIO_DE_RAQUETA)
			raquetaInferior.setX((int) (raquetaInferior.getX() - VELOCITAT_RAQUETA));
		if (teclaDreta && raquetaInferior.getX() < getWidth() - raquetaInferior.getAmple())
			raquetaInferior.setX((int) (raquetaInferior.getX() + VELOCITAT_RAQUETA));
	}

	/**
	 * Mètode que tanca la partida actual i torna al menú principal sense desar cap
	 * dada ni puntuació.
	 */
	private void sortirAlMenuSenseGuardar() {
		// Aturem el motor del joc si no es nul
		if (temporitzadorJoc != null) {
			temporitzadorJoc.stop();
		}

		// Aturem la música de fons
		recursos.pararMusica();

		// Tornem a obrir el menú principal
		new MenuInici().setVisible(true);

		// Tanquem i alliberem la memòria d'aquesta finestra de joc
		this.dispose();
	}

	/**
	 * Mètode que mou una pilota i comprova col·lisions.
	 */
	private void mourePilota(final Bola pilota) {
		// Si la pilota està desactivada no es mou
		if (!pilota.isActiva())
			return;
		// Actualitza la posició
		pilota.moure();

		// Rebots laterals
		if (pilota.getX() < COLISIO_PARET_ESQUERRA || pilota.getX() > COLISIO_PARET_DRETA) {
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
			pilota.setY(raquetaInferior.getY() - COLISIO_PILOTA_RAQUETA);
			sonarRaqueta();
		}

		// Col·lisió Obstacles
		if (gestorObstacles.comprovarXoc(rPilota)) {
			pilota.setVelY(-pilota.getVelY());
			sonarRebot();
		}

		// Mort de la pilota (surt per dalt o baix)
		if (pilota.getY() > LIMIT_PILOTA_RAQUETA_SUPERIOR || pilota.getY() < LIMIT_PILOTA_RAQUETA_INFERIOR)
			pilota.setActiva(false);
	}

	/**
	 * Mètode que augmenta la dificultat del joc.
	 */
	private void actualitzarNivell() {
		// Incrementem el comptador
		setComptadorTempsNivell(getComptadorTempsNivell() + INCREMENT_TEMPS_MILISEGONS);
		// Cada 20 segons puja el nivell

		if (getComptadorTempsNivell() >= SALT_DE_NIVELL && getNivell() < LIMIT_DE_NIVELL) {
			setNivell(getNivell() + INCREMENT_DE_NIVELL);
			// Augmentem velocitat
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA_PILOTA);
			// Reiniciem comptador
			setComptadorTempsNivell(COMPTADOR);
			// Generem nous obstacles
			generarObstacles();
		}
	}

	/**
	 * Mètode que dibuixa tots els elements del joc
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
	 * Mètode que mostra informació del joc (punts, jugadors, nivell).
	 */
	private void dibuixarTextos(final Graphics2D g2d) {

		// Font del text
		g2d.setFont(new Font(TIPOGRAFIA_FONT_ARIAL, Font.BOLD, FONT_ARIAL));

		// Text segons idioma
		String labelPunts = idioma.equals(LLENGUATGE_CATALA) ? PUNTUACIO_CATALA : PUNTUACIO_CASTELLA;

		// Informació dels jugadors
		g2d.drawString(PIRMER_JUGADOR + partida.getNickName1(), POSICIO_HORIZONTAL_DADES_PARTIDA,
				POSICIO_VERTICAL_DADES_PARTIDA);
		g2d.drawString(SEGON_JUGADOR + partida.getNickName2(), POSICIO_HORIZONTAL_DADES_PARTIDA,
				POSICIO_VERTICAL_SEGON_JUGADOR);

		// Punts i nivell
		g2d.drawString(labelPunts + getPunts(), POSICIO_HORIZONTAL_DADES_PARTIDA, POSICIO_VERTICAL_PUNTUACIO);
		g2d.drawString(NIVELL_ACTUAL + getNivell(), POSICIO_HORIZONTAL_NIVELL, POSICIO_VERTICAL_DADES_PARTIDA);
	}

	/**
	 * Mètode que inicia el timer principal del joc.
	 */
	public void iniciarJoc() {

		// Donem focus a la finestra
		requestFocusInWindow();

		// Timer que actualitza joc cada 10ms
		temporitzadorJoc = new Timer(INCREMENT_TEMPS_MILISEGONS, e -> {
			moureTot();
			repaint();
		});

		// Engeguem el joc
		temporitzadorJoc.start();
	}

	/**
	 * Mètode que pregunta si es vol desar la partida.
	 */
	private void preguntarDesarPartida() {
		String msg = idioma.equals(LLENGUATGE_CATALA) ? GUARDAR_PARTIDA_CATALA : GUARDAR_PARTIDA_CASTELLA;
		int res = JOptionPane.showConfirmDialog(this, msg, BOTO_PER_DESAR_PARTIDA, JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.YES_OPTION)
			guardarPartida();
	}

	/**
	 * Mètode que desa la partida al fitxer.
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
			JOptionPane.showMessageDialog(this, config.t(PARTIDA_GUARDADA_CATALA, PARTIDA_GUARDADA_CASTELLA));
		} catch (Exception e) { // Missatge d'error
			JOptionPane.showMessageDialog(this, config.t(ERROR_PARTIDA_CATALA, ERROR_PARTIDA_CASTELLA));
		}
	}

	/**
	 * Mètode que comprova si la partida ha acabat.
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
				System.err.println(ERROR_PUNTUACIO + e.getMessage());
			}
			// Mostrem ranking al final
			SwingUtilities.invokeLater(() -> {
				mostrarTop10();
			});
		}
	}

	/**
	 * Mètode que torna al menú principal.
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
		//Tanquem la finestra del joc
		this.setFocusable(false);
	}

	/**
	 * Mètode que mostra el Top 10 de puntuacions.
	 */
	private void mostrarTop10() {
		// Recàrrega de configuració
		config = new ConfigManager();
		// Obtenim dades de BD
		List<Puntuaciones> top = puntuacionsRepository.obtenirTop10();

		// Declaració i inicialitzió de StringBuilder per mostrar text
		StringBuilder sb = new StringBuilder("TOP 10\n\n");

		// Declaració i inicialització de variable int
		int i = 1;

		// Construïm llista de ranking
		for (Puntuaciones p : top) {
			// Es canvia p.getNomJugador() per p.getUsuarios().getNombre() per seguretat
			String nomMostrat = (p.getUsuarios() != null) ? p.getUsuarios().getNombre() : "Anònim";
			sb.append(String.format("%d. %-15s %d\n", i++, nomMostrat, p.getPuntuacion()));
		}
		// Zona de text
		JTextArea area = new JTextArea(sb.toString());
		area.setFont(new Font(TIPOGRAFIA_FONT_MONOSPACED, Font.BOLD, FONT_MONOSPACED));
		area.setEditable(false);
		area.setOpaque(true);

		// Color segons configuració
		Color fondo = convertirColor(config.getColorPuntuacio());

		area.setBackground(fondo);
		area.setForeground(Color.WHITE);
		
		/*Crea un panell de desplaçament (JScrollPane) que contindrà el component 'area' (probablement un JTextArea).
		 Això permet que, si el text és molt llarg, apareguin barres de desplaçament.*/
		JScrollPane scroll = new JScrollPane(area);

		// Crea un nou panell (JPanel) per organitzar els elements de la interfície.
		JPanel panel = new JPanel();
		
		/*Defineix un disseny de tipus BorderLayout per al panell. 
		Aquest disseny permet dividir el panell en 5 zones: Nord, Sud, Est, Oest i Centre.*/
		panel.setLayout(new BorderLayout());

		//Estableix el color de fons del panell principal utilitzant la variable 'fondo'.
		panel.setBackground(fondo);

		/*El Viewport és la "finestra" a través de la qual veiem el component scrollable (l'àrea de text).
		 El fem opac perquè es pugui visualitzar el color que li assignarem. */
		scroll.getViewport().setOpaque(true);
		//Establim el color de fons de la vista del scroll (el viewport) perquè coincideixi amb la resta.
		scroll.getViewport().setBackground(fondo);
		//Fem que el propi JScrollPane sigui opac.
		scroll.setOpaque(true);
		
		//Finalment, assignem el color de fons al JScrollPane per mantenir l'estètica uniforme de la interfície.
		scroll.setBackground(fondo);

		// Botó canvi color
		JButton btnColor = new JButton(config.t(CANVIAR_COLOR_CATALA, CANVIAR_COLOR_CASTELLA));

		//Botó per canviar de color la mostra del ranking
		btnColor.addActionListener(e -> {
			//Emmagatzemem les opcions en una llista
			String[] opciones = { COLOR_NEGRE, COLOR_VERMELL, COLOR_BLAU, COLOR_VERD };
			//Declarem i inicialitzem una variable per la mostra del panell de canvi de color
			String seleccion = (String) JOptionPane.showInputDialog(panel, config.t(SELECCIO_COLOR, SELECCIO_COLOR),
					config.t(CONFIGURACIO_CATALA, CONFIGURACIO_CASTELLA), JOptionPane.QUESTION_MESSAGE, null, opciones,
					config.getColorPuntuacio());
			//Si la selecció no es nul·la s'executa
			if (seleccion != null) {

				// Desem l'archiu
				config.setColorPuntuacio(seleccion);

				//Recarreguem configuració
				config = new ConfigManager();
				
				//Declarem i iniccialitzem un nou color segons la selecció
				Color nuevo = convertirColor(seleccion);
				
				//Mostrem el panel amb el nou fons de color
				panel.setBackground(nuevo);
				//Establim el fons principal amb la variable nuevo
				scroll.setBackground(nuevo);
				scroll.getViewport().setBackground(nuevo);

				area.setBackground(nuevo);
				area.setForeground(Color.WHITE);

				panel.repaint();
			}
		});
		//Afegim el botó al panell
		panel.add(btnColor, BorderLayout.NORTH);
		//El centrem
		panel.add(scroll, BorderLayout.CENTER);

		// Finestra ranking
		JFrame frame = new JFrame(DEU_MILLORS_PUNTUACIONS);
		frame.setSize(AMPLADA_FINESTRA, ALÇADA_FINESTRA);
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
	 * Mètode que converteix el nom del color guardat a un objecte Color.
	 */
	private Color convertirColor(String c) {
		//Si la seleccció es nul·la per defecte en gris
		if (c == null) {
			return Color.DARK_GRAY;
		}
		//Switch per actuar segons quina opció es escollida
		switch (c.toUpperCase()) {
		case COLOR_VERMELL:
			return Color.RED;
		case COLOR_BLAU:
			return Color.BLUE;
		case COLOR_VERD:
			return Color.GREEN;
		case COLOR_GROC:
			return Color.YELLOW;
		default:
			return Color.BLACK;
		}
	}

	/**
	 * Mètode que dibuixa el fons del joc.
	 */

	private void dibuixarFons(Graphics2D g2d) {
		ImageIcon f = recursos.getFonsActual(getNivell());
		if (f != null)
			g2d.drawImage(f.getImage(), 0, 0, AMPLADA_FINESTRA, ALÇADA_FINESTRA, null);
	}

	/**
	 * Mètode que dibuixa els obstacles visibles.
	 */
	private void dibuixarObstacles(Graphics2D g2d) {
		//En una array de tipus rectangle, afegim la llistaObstacles
		Rectangle[] obs = gestorObstacles.getLlistaObstacles();
		//En una array de tipus boolean la igualem a visible de la classe gestorObstacles
		boolean[] vis = gestorObstacles.getVisible();
		//Recorrem el total d'obstacles
		for (int i = 0; i < gestorObstacles.getTotalObstacles(); i++) {
			
			if (vis[i]) {
				//Declarem l'obstacle actual segons el nivell
				ImageIcon img = recursos.getObstacleActual(getNivell());
				//Dibuixem l'obstacle
				g2d.drawImage(img.getImage(), obs[i].x, obs[i].y, AMPLADA_OBSTACLE, ALÇADA_OBSTACLE, null);
			}
		}
	}

	/**
	 * Mètode que dibuixa les pilotes actives.
	 */
	private void dibuixarPilotes(Graphics2D g2d) {
		//Accedim a la imatge de la pilota de la classe recursos
		ImageIcon img = recursos.getImgPilota();
		//Si la pilota és activa la dibuixem
		if (pilota1.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota1.getX(), (int) pilota1.getY(), 30, 30, null);
		if (pilota2.isActiva())
			g2d.drawImage(img.getImage(), (int) pilota2.getX(), (int) pilota2.getY(), 30, 30, null);
	}

	/**
	 * Mètode que dibuixa les raquetes dels jugadors.
	 */
	private void dibuixarRaquetes(Graphics2D g2d) {
		//Accedim a la imatge de la raqueta de la classe recursos
		ImageIcon img = recursos.getImgRaqueta();
		//Dibuixem la raqueta
		g2d.drawImage(img.getImage(), raquetaSuperior.getX(), raquetaSuperior.getY() - 10, raquetaSuperior.getAmple(),
				40, null);
		g2d.drawImage(img.getImage(), raquetaInferior.getX(), 520, raquetaInferior.getAmple(), 40, null);
	}

	/**
	 * Mètode que mostra pantalla de pausa.
	 */
	private void dibuixarPausa(final Graphics2D g2d) {
		// Si el joc esta pausat, enfosquim la pantalla.
		if (getEstaPausat()) {
			// Dibuixem un rectangle negre semitransparent que ocupa tota la finestra
			g2d.setColor(new Color(0, 0, 0, 150)); // El 150 es el nivell de transparencia
			g2d.fillRect(0, 0, 500, 600);

			// Posem el text de pausa ben gran al centre
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(TIPOGRAFIA_FONT_IMPACT, Font.BOLD, FONT_IMPACT));
			g2d.drawString(MISSATGE_DE_PAUSA, POSICIO_HORIZONTAL_PAUSA, POSICIO_VERTICAL_PAUSA);
			// Missatges
			String msgPausa = config.t(OPCIO_CONTINUAR_CATALA, OPCIO_CONTINUAR_CASTELLA);
			String msgGuardar = config.t(OPCIO_GUARDAR_CATALA, OPCIO_GUARDAR_CASTELLA);
			String msgMenu = config.t(OPCIO_SORTIR_CATALA, OPCIO_SORTIR_CASTELLA);
			g2d.setFont(new Font(TIPOGRAFIA_FONT_ARIAL, Font.PLAIN, FONT_ARIAL_PAUSA));
			g2d.drawString(msgPausa, POSICIO_HORIZONTAL_OPCIONS, POSICIO_VERTICAL_CONTINUAR);
			g2d.drawString(msgGuardar, POSICIO_HORIZONTAL_OPCIONS, POSICIO_VERTICAL_GUARDAR);
			g2d.drawString(msgMenu, POSICIO_HORIZONTAL_OPCIONS, POSICIO_VERTICAL_SORTIR);
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