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
 *
 * Classe VentanaJoc, aqui es munta tota la part visual: carreguem els dibuixos,
 * les pales i la pilota. Fem que el joc canvi de nivell cada 20 segons pujant
 * la velocitat un 10%. Posem musica de fons i efectes de so per a cada rebot
 * perque sembli un joc de veritat. Si prems la P o la Q sortira pausa, i si perds
 * sortira game over amb les 10 millors puntuacions.
 *
 */
public class VentanaJoc extends JFrame { // Fem que aquesta classe sigui una finestra

	private static final long serialVersionUID = 1L;
	private static final String FITXER_PARTIDA = "partida_guardada.dat";

	private final Bola pilota1 = new Bola(50, 50, 3, 3, true); // Declarem les dades de la pilota 1.
	private final Bola pilota2 = new Bola(100, 50, 3.5, 3.5, false); // Declarem les dades de la pilota 2.

	// Posem un limit perque el joc sigui jugable al nivell 20
	private final double VELOCITAT_MAXIMA = 12.0;

	// Atributs de les raquetes i del comptador.
	private final Raqueta raquetaSuperior = new Raqueta(100, 40, 100, 20); // Raqueta del jugador de dalt
	private final Raqueta raquetaInferior = new Raqueta(100, 530, 100, 20); // Raqueta del jugador de baix
	private long punts = 0; // El marcador de punts
	private final long tempsInici; // Per saber a quina hora hem comencat a jugar
	private int nivell = 1; // El nivell actual
	private int comptadorTempsNivell = 0; // Un rellotge intern per saber quan pujar de nivell
	
	private final Partida partida; // L'objecte partida amb tota la info dels jugadors
	private boolean estaPausat = false; // Si el joc esta aturat o no
	private final String idioma; // Per saber si parlem en catala o castella
	private boolean pilota2JaActivada = false; // Ens diu si la segona pilota ja ha sortit al nivell 8

	// Variable per controlar el rellotge del joc
	private Timer temporitzadorJoc; // El motor que fa que les coses es moguin soles

	private final RecursosJoc recursos = new RecursosJoc(); // Imatges i sons del joc
	private final ConfigManager config = new ConfigManager();
	private final GestorObstacles gestorObstacles = new GestorObstacles(); // Obstacles del mapa
	
	private final PuntuacionsRepository puntuacionsRepository; // Classe que parla amb la base de dades
	private final DesarPuntuacions desarPuntuacions; // Classe per gestionar el guardat d'equips

	// Es el que s'executa quan obrim el joc

	/**
	 * El constructor que rep la partida i l'utiliti d'hibernate
	 * * @param partida L'objecte amb tota la configuració de la sessió
	 * @param hibernate La connexió activa a la base de dades
	 */
	public VentanaJoc(Partida partida, HibernateUtil hibernate) {

		this.partida = partida;
		this.idioma = "Català"; // Podries usar partida.getIdioma() si cal

		// Inicialitzem les eines de base de dades sense fer servir static
		this.puntuacionsRepository = new PuntuacionsRepository(hibernate);
		this.desarPuntuacions = new DesarPuntuacions(puntuacionsRepository);

		// Carreguem els recursos primer de tot per poder utilitzar el so del menu
		recursos.carregarRecursos(); // Anem a buscar els fitxers de la carpeta resources.

		this.nivell = partida.getNivell(); // Guardem el nivell triat
		this.punts = partida.getPunts(); // Recuperem els punts si venim d'una partida guardada

		// Apliquem el 10% de velocitat pero respectant el limit maxim
		for (int i = 1; i < nivell; i++) {
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA);
		}

		// Ajustem la finestra
		this.setSize(500, 600); // Mida de la finestra
		this.setResizable(false); // No deixem que s'estiri la pantalla
		this.setLocationRelativeTo(null); // Que surti al mig de la pantalla
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Si tanquem la X, s'atura tot
		this.setFocusable(true); // Fem que la finestra pugui escoltar el teclat

		generarObstacles(); // Posem els obstacles pel mapa
		reproduirMusica(); // Posem aixo per a que comenci la musica
		reproduirIniciPartida(); // Fem sonar el so de inici de partida

		this.tempsInici = System.currentTimeMillis() - this.punts; // Mirem el rellotge respectant els punts guardats

		// Aixo serveix perque si prems la P, la Q, la S, Esc, A/D o les fletxes, el joc reaccioni
		this.addKeyListener(new KeyAdapter() { // Escoltador per al teclat
			@Override
			public void keyPressed(final KeyEvent e) { // Quan es prem una tecla.
				int codi = e.getKeyCode();
				
				// Soporte para P y Q para pausar
				if (codi == KeyEvent.VK_P || codi == KeyEvent.VK_Q) { 
					setEstaPausat(!getEstaPausat()); // Canviem de pausa a joc o viceversa
				}

				if (getEstaPausat() && codi == KeyEvent.VK_S) { // Si esta pausat i premem la S
					preguntarDesarPartida(); // Preguntem si vol guardar la partida
				}

				if (getEstaPausat() && codi == KeyEvent.VK_ESCAPE) { // Si esta pausat i premem Esc
					sortirAlMenuSenseGuardar(); // Tornem al menu sense guardar puntuacio
				}

				// Movem la raqueta superior amb A i D, i la inferior amb les fletxes
				if (!getEstaPausat()) {
					if (codi == KeyEvent.VK_A) {
						setRaquetaSuperiorX(getRaquetaSuperiorX() - 45); // Movem 45 pixels a l'esquerra
					}
					if (codi == KeyEvent.VK_D) {
						setRaquetaSuperiorX(getRaquetaSuperiorX() + 45); // Movem 45 pixels a la dreta
					}
					if (codi == KeyEvent.VK_LEFT) {
						setRaquetaX(getRaquetaX() - 45); // Movem 45 pixels a l'esquerra
					}
					if (codi == KeyEvent.VK_RIGHT) {
						setRaquetaX(getRaquetaX() + 45); // Movem 45 pixels a la dreta
					}
				}
			}
		});
	}

	private void preguntarDesarPartida() {
		// Quan el joc esta pausat, preguntem si vol desar la partida.
		String pregunta = idioma.equals("Català") ? "Vols desar la partida per continuar-la despres?"
				: "Quieres guardar la partida para continuarla despues?";
		String titol = idioma.equals("Català") ? "Desar partida" : "Guardar partida";

		int resposta = JOptionPane.showConfirmDialog(this, pregunta, titol, JOptionPane.YES_NO_OPTION);

		if (resposta == JOptionPane.YES_OPTION) {
			guardarPartida();
		}
	}

	private void guardarPartida() {
		try {
			// Guardem el nivell actual dins de l'objecte Partida abans de serialitzar-lo.
			partida.setNivell(getNivell());
			partida.setPunts(getPunts()); // Guardem tambe els punts actuals

			ObjectOutputStream sortida = new ObjectOutputStream(new FileOutputStream(FITXER_PARTIDA));
			sortida.writeObject(partida); // Serializable converteix l'objecte en dades de fitxer.
			sortida.close();

			if (temporitzadorJoc != null) {
				temporitzadorJoc.stop();
			}
			recursos.pararMusica();

			String msgExito = idioma.equals("Català") ? "Partida guardada" : "Partida guardada";
			JOptionPane.showMessageDialog(this, msgExito);
			new MenuInici().setVisible(true); // Tornem al menu principal.
			this.dispose(); // Tanquem la finestra de joc actual.
		} catch (Exception e) {
			String msgError = idioma.equals("Català") ? "No s'ha pogut guardar la partida" : "No se ha podido guardar la partida";
			JOptionPane.showMessageDialog(this, msgError);
		}
	}

	private void sortirAlMenuSenseGuardar() {
		// Sortim de la partida pausada sense desar partida ni puntuacio a la base de dades.
		if (temporitzadorJoc != null) {
			temporitzadorJoc.stop();
		}
		recursos.pararMusica();

		new MenuInici().setVisible(true); // Tornem al menu principal.
		this.dispose(); // Tanquem la finestra de joc actual.
	}

	// Posem la musica de Pokemon en bucle
	public void reproduirMusica() { // Metode per la canco de fons
		recursos.reproduirMusica();
	}

	// Metode per reproduir el so d'inici de partida
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

	// Fa el soroll quan cliques al menu
	public void reproduirClic() { // Clic botons
		recursos.reproduirClic();
	}

	// Crea 12 obstacles en llocs aleatoris de la pantalla
	public void generarObstacles() { // Posa els enemics al mapa
		gestorObstacles.generarObstacles();
	}

	// Aqui es calcula cada moviment i cada xoc
	public void moureTot() {
		if (getEstaPausat())
			return; // Si esta en pausa, no fem res

		// Actualitzem els punts segons el temps que portes viu
		setPunts(System.currentTimeMillis() - getTempsInici()); // Calcula quants milisegons portes jugant

		actualitzarNivell(); // Cada 20 segons pugem de nivell

		// Si arribes al nivell 8, surt la segona pilota
		if (getNivell() >= 8 && !pilota2.isActiva() && !pilota2JaActivada) { // Condicio per afegir dificultat
			pilota2.setActiva(true); // Activem la segona pilota
			pilota2JaActivada = true; // Marquem que ja ha entrat al joc
		}

		mourePilota(pilota1); // Logica de la pilota 1
		mourePilota(pilota2); // Logica de la pilota 2

		comprovarFinalPartida(); // Si ja no queden pilotes vives, s'ha acabat el joc
	}

	private void actualitzarNivell() {
		setComptadorTempsNivell(getComptadorTempsNivell() + 10); // Sumem temps al comptador de nivell
		// Fem una condicional per sumar cada 20000 punts un 10% de velocitat.
		if (getComptadorTempsNivell() >= 20000 && getNivell() < 20) { // Si passen 20 segons i no som al top
			// Sumem un nivell per cada 20000 punts
			setNivell(getNivell() + 1); // Pugem el numero del nivell

			// Pugem la velocitat un 10% pero mai passem del limit
			pilota1.augmentarVelocitat(VELOCITAT_MAXIMA);
			pilota2.augmentarVelocitat(VELOCITAT_MAXIMA);

			setComptadorTempsNivell(0); // Resetejem el comptador per als proxims 20 segons
			generarObstacles(); // Posem obstacles nous
		}
	}

	private void mourePilota(final Bola pilota) {
		if (!pilota.isActiva())
			return; // Si la pilota encara esta viva

		pilota.moure(); // Movem la pilota

		if (pilota.getX() < 0 || pilota.getX() > 360) {
			pilota.setVelX(-pilota.getVelX());
			sonarRebot();
		} // Rebota si toca els costats

		Rectangle rPilota = pilota.getRectangle(); // Crea el quadrat de la bola
		Rectangle rRaquetaSuperior = raquetaSuperior.getRectangle(); // Crea el quadrat de la pala superior
		Rectangle rRaquetaInferior = raquetaInferior.getRectangle(); // Crea el quadrat de la pala inferior

		if (rPilota.intersects(rRaquetaSuperior)) { // Si la pilota xoca amb la pala superior
			pilota.setVelY(Math.abs(pilota.getVelY())); // La fem baixar
			pilota.setY(raquetaSuperior.getY() + raquetaSuperior.getAlt());
			sonarRaqueta(); // Fem soroll
		}

		if (rPilota.intersects(rRaquetaInferior)) { // Si la pilota xoca amb la pala inferior
			pilota.setVelY(-Math.abs(pilota.getVelY())); // La fem pujar
			pilota.setY(raquetaInferior.getY() - 30);
			sonarRaqueta(); // Fem soroll
		}

		if (gestorObstacles.comprovarXoc(rPilota)) { // Si la pilota toca l'obstacle
			pilota.setVelY(-pilota.getVelY());
			sonarRebot(); // El trenquem i rebotem
		}

		if (pilota.getY() > 600 || pilota.getY() < 0)
			pilota.setActiva(false); // Si surt per sota o per dalt, la pilota mor
	}

	private void comprovarFinalPartida() {
		// Si ja no queden pilotes vives, s'ha acabat el joc i mostra l'usuari un game
		// over.
		if (!pilota1.isActiva() && !pilota2.isActiva()) { // Condicio final de derrota
			if (temporitzadorJoc != null)
				temporitzadorJoc.stop(); // Aturem el timer per evitar el bucle
			recursos.pararMusica(); // Parem la musica de fons
			sonarMort(); // Fem que soni l'audio de derrota

			// Adaptem el missatge de derrota a l'idioma
			String titolGameOver = idioma.equals("Català") ? "HAS PERDUT" : "GAME OVER";
			String etiquetaJug = idioma.equals("Català") ? "Jugadors: " : "Jugadores: ";
			String etiquetaPts = idioma.equals("Català") ? "\nPunts: " : "\nPuntos: ";

			// Mostrem la info de la parella que jugava
			String nomsInfo = partida.getNickName1() + " i " + partida.getNickName2();

			// Li mostrem un missatge a l'usuari un game over i li mostrem els noms i els punts que he conseguit.
			JOptionPane.showMessageDialog(this,
					titolGameOver + "\n" + etiquetaJug + nomsInfo + etiquetaPts + getPunts()); // Cartell de final
			
			// Trucada al mètode d'equip per guardar les dades de la parella
			desarPuntuacions.guardarPuntuacionEquip(partida, getPunts()); 
			
			mostrarTop10(); // Ensenyem el ranquing
			new MenuInici().setVisible(true); // Tornem al menu
			this.dispose(); // Tanquem la finestra
		}
	}

	// Inicia el cronometre que fa que le joc es mogui 100 vegades per segon
	public void iniciarJoc() { // El metode que arranca el motor
		requestFocusInWindow(); // Demanem el focus per poder llegir A/D i les fletxes
		temporitzadorJoc = new Timer(10, e -> {
			moureTot();
			repaint();
		}); // Cada 10 milisegons s'executa
		temporitzadorJoc.start(); // Engeguem el rellotge
	}

	// Aqui es on es dibuixa tot el que veus a la pantalla
	@Override
	public void paint(final Graphics g) { // El metode que pinta cada frame

		// Dibuixem primer en una imatge invisible per evitar que la pantalla parpellegi
		final Image imatgeOffscreen = createImage(getWidth(), getHeight()); // Creem el llenc invisible
		if (imatgeOffscreen == null)
			return; // Si no podem crear-lo, ens aturem
		final Graphics gOff = imatgeOffscreen.getGraphics(); // Agafem els "pinzells" del llenc invisible
		Graphics2D g2d = (Graphics2D) gOff; // Fem servir el motor 2D mes modern

		// Millorem la qualitat del dibuix
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Perque les fotos no es vegin pixelades
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Perque les vores siguin suaus

		dibuixarFons(g2d);
		dibuixarObstacles(g2d);
		dibuixarPilotes(g2d);
		dibuixarRaquetes(g2d);
		dibuixarTextos(g2d);
		dibuixarPausa(g2d);

		// Passem tot el dibuix a la pantalla real
		g.drawImage(imatgeOffscreen, 0, 0, null); // Enviem el llenc invisible a la vista de l'usuari
	}

	private void dibuixarFons(final Graphics2D g2d) {
		// Dibuixem el fons de Pokemon segons el nivell
		ImageIcon fonsActual = recursos.getFonsActual(getNivell()); // Variable per saber quina foto de fons toca
		if (fonsActual != null)
			g2d.drawImage(fonsActual.getImage(), 0, 0, 500, 600, null); // Pintem el fons triat
	}

	private void dibuixarObstacles(final Graphics2D g2d) {
		Rectangle[] llistaObstacles = gestorObstacles.getLlistaObstacles();
		boolean[] visible = gestorObstacles.getVisible();

		// Dibuixem els obstacles segons el nivell
		for (int i = 0; i < gestorObstacles.getTotalObstacles(); i++) { // Recorrem la llista d'obstacles
			if (visible[i]) { // Nomes si els obstacles estan visibles
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
		if (pilota1.isActiva() && imgPilota != null)
			g2d.drawImage(imgPilota.getImage(), (int) getPilotaX(), (int) getPilotaY(), 30, 30, null); // Pinta bola 1
		if (pilota2.isActiva() && imgPilota != null)
			g2d.drawImage(imgPilota.getImage(), (int) pilota2.getX(), (int) pilota2.getY(), 30, 30, null); // Pinta bola 2 si n'hi ha
	}

	private void dibuixarRaquetes(final Graphics2D g2d) {
		ImageIcon imgRaqueta = recursos.getImgRaqueta();

		// Dibuixem les dues raquetes del mode multijugador
		if (imgRaqueta != null) {
			g2d.drawImage(imgRaqueta.getImage(), raquetaSuperior.getX(), raquetaSuperior.getY() - 10,
					raquetaSuperior.getAmple(), 40, null); // Pinta la pala superior
			g2d.drawImage(imgRaqueta.getImage(), raquetaInferior.getX(), 520, raquetaInferior.getAmple(), 40, null); // Pinta la pala inferior
		}
	}

	private void dibuixarTextos(final Graphics2D g2d) {
		// Dibuixem els textos d'informacio (Nicknames, Punts, Nivell) adaptats a l'idioma
		String colorText = config.getColorPuntuacio();

		switch (colorText.toUpperCase()) {
		    case "RED": g2d.setColor(Color.RED); break;
		    case "BLUE": g2d.setColor(Color.BLUE); break;
		    case "GREEN": g2d.setColor(Color.GREEN); break;
		    case "YELLOW": g2d.setColor(Color.YELLOW); break;
		    case "BLACK": g2d.setColor(Color.BLACK); break;
		    default: g2d.setColor(Color.WHITE);
		}
		
		g2d.setFont(new Font("Arial", Font.BOLD, 14)); // Tipus de lletra clara

		String etiquetaPunts = idioma.equals("Català") ? "Punts: " : "Puntos: ";
		String etiquetaNivell = idioma.equals("Català") ? "Nivell: " : "Nivel: ";

		g2d.drawString("J1: " + partida.getNickName1(), 20, 50); // Escribim el nick del jugador 1
		g2d.drawString("J2: " + partida.getNickName2(), 20, 70); // Escribim el nick del jugador 2
		g2d.drawString(etiquetaPunts + getPunts(), 20, 90); // Escribim els milisegons de puntuacio
		g2d.drawString(etiquetaNivell + getNivell(), 410, 50); // Escribim el nivell a la dreta
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
			String msgGuardar = idioma.equals("Català") ? "Prem 'S' per desar la partida" : "Pulsa 'S' para guardar la partida";
			String msgMenu = idioma.equals("Català") ? "Prem 'Esc' per sortir al menu" : "Pulsa 'Esc' para salir al menu";
			
			g2d.setFont(new Font("Arial", Font.PLAIN, 15));
			g2d.drawString(msgPausa, 125, 340);
			g2d.drawString(msgGuardar, 125, 360);
			g2d.drawString(msgMenu, 125, 380);
		}
	}

	// Agafa les 10 millors puntuacions de la base de dades i les ensenya
	private void mostrarTop10() { // Metode per la llista de guanyadors
		List<Puntuacio> puntuacions = puntuacionsRepository.obtenirTop10(); // Connectem amb la base de dades SQL
		if (puntuacions.isEmpty())
			return; // Si la DB falla, simplement no ensenya la taula

		String colPos = "POS";
		String colJug = "JUGADOR";
		String colPts = idioma.equals("Català") ? "PUNTS" : "PUNTOS";

		String taula = String.format("%-5s %-15s %-10s\n", colPos, colJug, colPts); // Capcalera de la taula
		int pos = 1; // Comptador de posicio
		for (Puntuacio puntuacio : puntuacions) { // Mentre hi hagi files de resultat
			taula += String.format("%-5d %-15s %-10d\n", pos++, puntuacio.getNomJugador(), puntuacio.getPunts()); // Afegim linia a la taula
		}
		JTextArea area = new JTextArea(taula); // Fem una area de text per ensenyar-ho
		area.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Font per a que les columnes quedin rectes
		JOptionPane.showMessageDialog(this, new JScrollPane(area), "TOP 10", JOptionPane.INFORMATION_MESSAGE); // Mostrem la finestra emergent
	}

	// Aqui posem tots els getters i setters

	public double getPilotaX() { // Ens dona la X de la bola
		return pilota1.getX();
	}

	public void setPilotaX(final double pilotaX) { // Canvia la X de la bola
		pilota1.setX(pilotaX);
	}

	public double getPilotaY() { // Ens dona la Y de la bola
		return pilota1.getY();
	}

	public void setPilotaY(final double pilotaY) { // Canvia la Y de la bola
		pilota1.setY(pilotaY);
	}

	public double getVelX() { // Ens dona la velocitat lateral
		return pilota1.getVelX();
	}

	public void setVelX(final double velX) { // Canvia la velocitat lateral
		pilota1.setVelX(velX);
	}

	public double getVelY() { // Ens dona la velocitat vertical
		return pilota1.getVelY();
	}

	public void setVelY(final double velY) { // Canvia la velocitat vertical
		pilota1.setVelY(velY);
	}

	public int getRaquetaX() { // Ens dona on esta la pala inferior
		return raquetaInferior.getX();
	}

	public void setRaquetaX(int raquetaX) { // Movem la pala inferior a una X nova
		raquetaInferior.setX(raquetaX);
	}

	public int getRaquetaSuperiorX() { // Ens dona on esta la pala superior
		return raquetaSuperior.getX();
	}

	public void setRaquetaSuperiorX(int raquetaX) { // Movem la pala superior a una X nova
		raquetaSuperior.setX(raquetaX);
	}

	public long getPunts() { // Ens dona los punts actuals
		return punts;
	}

	public void setPunts(final long punts) { // Canvia els punts del marcador
		this.punts = punts;
	}

	public long getTempsInici() { // Ens diu quan vam comencar
		return tempsInici;
	}

	public int getNivell() { // Ens diu en quin nivell estem
		return nivell;
	}

	public void setNivell(final int nivell) { // Ens permet canviar el nivell
		this.nivell = nivell;
	}

	public int getComptadorTempsNivell() { // Ens diu quants ms portem al nivell actual
		return comptadorTempsNivell;
	}

	public void setComptadorTempsNivell(final int comptador) { // Ajusta el rellotge de nivell
		this.comptadorTempsNivell = comptador;
	}

	public boolean getEstaPausat() { // Ens diu si el joc esta aturat
		return estaPausat;
	}

	public void setEstaPausat(final boolean pausat) { // Activa o desactiva la pausa
		this.estaPausat = pausat;
	}
}