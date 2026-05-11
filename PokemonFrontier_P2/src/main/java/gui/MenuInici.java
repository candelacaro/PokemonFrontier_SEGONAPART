package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;
import java.io.*;
import logic.ConfigManager;
import logic.Partida;
import logic.db.HibernateUtil;

/**
 * Aquesta classe es el menú i el primer que es veu del joc; és el primer que
 * veu l'usuari, posem una música de fons de la temàtica del nostre joc. Hem
 * posat 6 botons: el de Jugar, l'idioma, volum, continuar, regles i sortir. Una
 * finestra emergent explica les regles del joc per informar a l'usuari amb
 * abans de començar. Quan l'usuari clica a jugar, tanquem el menu i obrim la
 * finestra de joc.
 * 
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià
 *         Chenovart
 */
public class MenuInici extends JFrame {

	// Declaració i inicialització de final
	private static final long serialVersionUID = 1L;
	// Declaració i inicialització de final String, per fer-la servir per la base de
	// dades
	private static final String FITXER_PARTIDA = "partida_guardada.dat";

	// Declaració i inicialització de Clip, el reproductor per a la musica de fons
	// del menú
	private Clip musicaMenu;

	// Declaració i inicialització d'atribut privat final on s'instància un objecte
	// de la classe ConfigManager()
	private final ConfigManager config = new ConfigManager();
	// Declaració i inicialització d'atribut privat final, afegim la instància
	// d'Hibernate per passar-la a la finestra de joc
	private final HibernateUtil hibernate = new HibernateUtil();

	// Declaració i inicialització de finals per als càlculs del control del volum
	private static final double PERCENTATGE_VOLUM = 100.0;
	private static final double BASE_DEL_LOGARITME = 10.0;
	private static final double ESCALA_DECIBELS = 20.0;
	private static final int VOLUM_CERO = 0;

	// Declaració i incialització de finals per les opcions de volum
	private static final String VOLUM_APAGAT = "0";
	private static final String VOLUM_BAIX = "25";
	private static final String VOLUM_MITJA = "50";
	private static final String VOLUM_MIG_ALT = "75";
	private static final String VOLUM_MAXIM = "100";

	// Declaració i inicialització de finals per les traduccions
	private static final String LLENGUATGE_CASTELLA = "Castellano";
	private static final String LLENGUATGE_CATALA = "Catala";
	private static final String MISSATGE_VOLUM = "Volumen inicial: ";
	private static final String MISSATGE_IDIOMA = "Idioma inicial: ";
	private static final String SELECCIO_VOLUMEN = "Selecciona volumen: ";
	private static final String CONFIGURACIO_CATALA = "Configuració";
	private static final String CONFIGURACIO_CASTELLA = "Configuración";
	private static final String VOLUMEN_GUARDAT_CATALA = "Volum guardat: ";
	private static final String VOLUMEN_GUARDAT_CASTELLA = "Volumen guardado: ";
	private static final String SELECCIO_IDIOMA = "Selecciona idioma: ";
	private static final String IDIOMA_SELECCIONAT = "Idioma";
	private static final String IDIOMA_GUARDAT_CASTELLA = "Idioma guardado";
	private static final String IDIOMA_GUARDAT_CATALA = "Idioma guardat";
	private static final String SELECCIO_NIVELL_CATALA = "Selecciona nivell: ";
	private static final String SELECCIO_NIVELL_CASTELLA = "Selecciona nivel: ";
	private static final String NIVELL_INICIAL_CATALA = "Nivell incial";
	private static final String NIVELL_INICIAL_CASTELLA = "Nivel inicial";

	// Declaració i inicialització de finals per les opcions del menú
	private static final String OPCIO_JUGAR_CASTELLA = "JUGAR";
	private static final String OPCIO_CONTINUAR_CASTELLA = "CONTINUAR";
	private static final String OPCIO_REGLES_CASTELLA = "REGLAS";
	private static final String OPCIO_IDOMA = "IDIOMA";
	private static final String OPCIO_VOLUMEN = "VOLUMEN";
	private static final String OPCIO_SORTIR_CASTELLA = "SALIR";
	private static final String OPCIO_JUGAR_CATALA = "JUGAR";
	private static final String OPCIO_CONTINUAR_CATALA = "CONTINUAR";
	private static final String OPCIO_REGLES_CATALA = "REGLES";
	private static final String OPCIO_SORTIR_CATALA = "SORTIR";

	// Declaració i inicialització de finals per els jugadors
	private static final String NOM_PRIMER_JUGADOR_CATALA = "Nom Jugador 1:";
	private static final String NOM_PRIMER_JUGADOR_CASTELLA = "Nombre Jugador 1: ";
	private static final String NICKNAME_PRIMER_JUGADOR = "Nickname Jugador 1: ";
	private static final String NICKNAME_SEGON_JUGADOR = "Nickname Jugador 2: ";
	private static final String NOM_SEGON_JUGADOR_CATALA = "Nom Jugador 2:";
	private static final String NOM_SEGON_JUGADOR_CASTELLA = "Nombre Jugador 2: ";

	// Declaració i inicialització de finals per les mides de la finestra
	private static final int AMPLE = 0;
	private static final int ALTURA = 15;
	private static final int VORES = 50;
	private static final int MARGE_INTERN = 10;
	private static final int AMPLE_FINESTRA = 450;
	private static final int ALÇADA_FINESTRA = 500;
	private static final int AMPLE_BOTO = 200;
	private static final int ALÇADA_BOTO = 50;

	// Declarció i inicialització de finals per la tipografia
	private static final String TIPOGRAFIA_FONT = "Arial";
	private static final int TAMANY_FONT = 26;
	private static final int FONT = 14;

	// Declaració i inicialització de final pel titol del menu
	private static final String TITOL_JOC = "Pokemon Frontier - Main Menu";

	private static final int NIVELL_MAXIM = 20;

	/**
	 * Mètode per tancar el menú
	 */
	private void cerrarMenu() {
		// Estructura condicional que avalua si la musica del menu continua sonant
		if (musicaMenu != null) {
			// Aturem la musica amb un stop
			musicaMenu.stop();
			// Tanquem la musica amb un close
			musicaMenu.close();
		}
	}

	/**
	 * Constructor per defecte que mostra tot el menú sencer
	 */
	public MenuInici() {

		// Crida del mètode inicialitzarComponents(), per mostrar l'estètica del menú
		inicialitzarComponents();

		// Mostra de les properties que hi han
		System.out.println(MISSATGE_IDIOMA + config.getIdioma());
		System.out.println(MISSATGE_VOLUM + config.getVolumen());

		// Posem un títol al menú
		this.setTitle(TITOL_JOC);
		// Fem que no es pugui fer més gran la pantalla
		this.setResizable(false);
		// Posicionem la finestra
		this.setLocationRelativeTo(null);
		// Crida del mètode reproduirMusicaMenu() i pasem com a paràmetre la música de
		// l'introducció del joc
		reproduirMusicaMenu("/Sound/introJoc.wav");
	}

	/**
	 * Mètode String per l'idioma
	 * 
	 * @param Catala, idioma en català
	 * @param Castellano, idioma en castellà
	 * @return l'idioma triat
	 */
	private String t(String Catala, String Castellano) {
		// Si escull Castellano retornem Castellano si no Català amb un operador ternari
		return config.getIdioma().equals(LLENGUATGE_CASTELLA) ? Castellano : Catala;
	}

	/**
	 * Mètode que configura tot el disseny visual del menú
	 */
	private void inicialitzarComponents() {
		// El contenidor on anira tot
		JPanel panellPrincipal = new JPanel();
		// Un color gris fosc de fons
		panellPrincipal.setBackground(new Color(45, 45, 45));
		// Posem els elements un sota l'altre i verticalment amb BoxLayout.Y_AXIS
		panellPrincipal.setLayout(new BoxLayout(panellPrincipal, BoxLayout.Y_AXIS));
		// Donem una mica de marge als costats
		panellPrincipal.setBorder(new EmptyBorder(VORES, VORES, VORES, VORES));

		// El titol del joc ben gran
		JLabel titol = new JLabel("POKEMON FRONTIER");
		// Lletra blanca
		titol.setForeground(Color.WHITE);
		// Lletra Arial, negreta i tamany 26
		titol.setFont(new Font(TIPOGRAFIA_FONT, Font.BOLD, TAMANY_FONT));
		// Centrem el text
		titol.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Creem els botons utilitzant el fortmat RGB
		JButton btnJugar = crearBotoEstilitzat(t(OPCIO_JUGAR_CATALA, OPCIO_JUGAR_CASTELLA), new Color(46, 204, 113));
		JButton btnContinuar = crearBotoEstilitzat(t(OPCIO_CONTINUAR_CATALA, OPCIO_CONTINUAR_CASTELLA), new Color(241, 196, 15));
		JButton btnRegles = crearBotoEstilitzat(t(OPCIO_REGLES_CATALA, OPCIO_REGLES_CASTELLA), new Color(52, 152, 219));
		JButton btnIdioma = crearBotoEstilitzat(OPCIO_IDOMA, new Color(52, 152, 219));
		JButton btnVolumen = crearBotoEstilitzat(OPCIO_VOLUMEN, new Color(155, 89, 182));
		JButton btnSortir = crearBotoEstilitzat(t(OPCIO_SORTIR_CATALA, OPCIO_SORTIR_CASTELLA), new Color(231, 76, 60));

		// Programem que passa quan cliquem a JUGAR
		btnJugar.addActionListener(e -> {
			reproduirSo("/Sound/menuClick.wav"); // Fem el soroll de clic
			accioBotoJugar(); // Anem a la funcio per triar idioma i nom
		});

		// Programem que passa quan cliquem a CONTINUAR
		btnContinuar.addActionListener(e -> {
			reproduirSo("/Sound/menuClick.wav"); // Fem el soroll de clic
			accioBotoContinuar(); // Intentem carregar la partida guardada
		});

		// Programem que passa quan cliquem a REGLAS
		btnRegles.addActionListener(e -> {
			reproduirSo("/Sound/menuClick.wav"); // So de clic
			mostrarRegles(); // Ensenyem la finestra amb les instruccions
		});

		// Botó volum, dona opcions per regular el volum del joc
		btnVolumen.addActionListener(e -> {

			reproduirSo("/Sound/menuClick.wav");// So de clic

			// Mostrem les possibles opcions
			String[] opciones = { VOLUM_APAGAT, VOLUM_BAIX, VOLUM_MITJA, VOLUM_MIG_ALT, VOLUM_MAXIM };

			// Mostrem el missatge en un panell
			String seleccion = (String) JOptionPane.showInputDialog(this, config.t(SELECCIO_VOLUMEN, SELECCIO_VOLUMEN),
					config.t(CONFIGURACIO_CATALA, CONFIGURACIO_CASTELLA), JOptionPane.QUESTION_MESSAGE, null, opciones,
					String.valueOf(config.getVolumen()) // Accedim amb el volum amb mètode getter
			);

			// Si la selecció no és nul·la modifiquem el valor
			if (seleccion != null) {

				// Modifiquem el valor del volum amb el mètode setter
				config.setVolumen(seleccion);

				// Si la música no és nul·la apliquem el volume immediatament
				if (musicaMenu != null) {

					/*
					 * Declaració i inicialització de FloatControl (permet controlar un rang de
					 * valors de punt flotant per a funcions d'àudio, com ara ajustar el volum)
					 */
					FloatControl gainControl = (FloatControl) musicaMenu.getControl(FloatControl.Type.MASTER_GAIN); 
					// control principal de guany (volum) d'una línia d'àudio.
					
					// Declaració i inicialització de variable on passem la selecció a int
					int volumen = Integer.parseInt(seleccion);

					// Si el volum és igual a 0 s'executa
					if (volumen == VOLUM_CERO) {
						// Control de la modificació del volum al mínim
						gainControl.setValue(gainControl.getMinimum());

					} else {
						// Declaració i inicialització de float que calcula el nivell de volum o
						// ganancia en decibelis
						float db = (float) (Math.log(volumen / PERCENTATGE_VOLUM) / Math.log(BASE_DEL_LOGARITME)
								* ESCALA_DECIBELS);

						// Modifiquem el valor amb el mètode setValue
						gainControl.setValue(db);
					}
				}

				// Panell que et mostra el missatge i la selecció
				JOptionPane.showMessageDialog(this,
						config.t(VOLUMEN_GUARDAT_CATALA, VOLUMEN_GUARDAT_CASTELLA) + seleccion);
			}
		});

		// Botó per fer canvi d'idiomes
		btnIdioma.addActionListener(e -> {
			// Reproduim el so
			reproduirSo("/Sound/menuClick.wav");

			// Declaració i inicialització d'Array de String
			String[] idiomes = { LLENGUATGE_CATALA, LLENGUATGE_CASTELLA };

			// Declaració i inicialització de variable per mostrar el panell fent casting
			String seleccion = (String) JOptionPane.showInputDialog(this, config.t(SELECCIO_IDIOMA, SELECCIO_IDIOMA),
					config.t(IDIOMA_SELECCIONAT, IDIOMA_SELECCIONAT), JOptionPane.QUESTION_MESSAGE, null, idiomes,
					config.getIdioma());

			// Estructura condicional on avalua si la selecció no es nul·la
			if (seleccion != null) {
				// Modifiquem l'idioma segons la selecció escollida
				config.setIdioma(seleccion);

				// Panell que et mostra el missatge
				JOptionPane.showMessageDialog(this, config.t(IDIOMA_GUARDAT_CATALA, IDIOMA_GUARDAT_CASTELLA));
				cerrarMenu();

				// Alliberem recursos natius de la finestra
				this.dispose();
				// Tornem a mostrat el menuInici amb els canvis aplicats
				new MenuInici().setVisible(true);
			}
		});

		// Programem el boto de sortir
		btnSortir.addActionListener(e -> System.exit(0)); // Tanquem el programa del tot

		// Anem afegint els elements al panell amb espais entremig
		panellPrincipal.add(btnJugar);
		panellPrincipal.add(Box.createRigidArea(new Dimension(AMPLE, ALTURA)));

		panellPrincipal.add(btnContinuar);
		panellPrincipal.add(Box.createRigidArea(new Dimension(AMPLE, ALTURA)));

		panellPrincipal.add(btnRegles);
		panellPrincipal.add(Box.createRigidArea(new Dimension(AMPLE, ALTURA)));

		panellPrincipal.add(btnIdioma);
		panellPrincipal.add(Box.createRigidArea(new Dimension(AMPLE, ALTURA)));

		panellPrincipal.add(btnVolumen);
		panellPrincipal.add(Box.createRigidArea(new Dimension(AMPLE, ALTURA)));

		panellPrincipal.add(btnSortir);

		this.setContentPane(panellPrincipal); // Posem el panell a la finestra
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Si tanquem la X, s'acaba tot
		this.pack(); // Ajustem la finestra al contingut
		this.setSize(AMPLE_FINESTRA, ALÇADA_FINESTRA); // Forcem una mida de 450x500
	}

	/**
	 * Funció per deixar els botons ben polits i iguals
	 * 
	 * @param text, text que es mostra
	 * @param colorFons, colors que hi ha
	 * @return el botó amb estètica millorada
	 */
	private JButton crearBotoEstilitzat(String text, Color colorFons) {
		JButton boto = new JButton(text);
		boto.setMaximumSize(new Dimension(AMPLE_BOTO, ALÇADA_BOTO)); // Mida màxima del botó
		boto.setAlignmentX(Component.CENTER_ALIGNMENT); // Ho centrem horitzontalment
		boto.setFocusPainted(false); // Treiem el quadrat que surt al text quan cliques
		boto.setBackground(colorFons); // Posem el color de fons triat
		boto.setForeground(Color.WHITE); // Text blanc
		boto.setFont(new Font(TIPOGRAFIA_FONT, Font.BOLD, FONT)); // Lletra negreta
		boto.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Fa que surti la maneta en passar el ratolí
		boto.setBorder(BorderFactory.createEmptyBorder(MARGE_INTERN, MARGE_INTERN, MARGE_INTERN, MARGE_INTERN)); // Marge
																													// intern
		return boto;
	}

	/**
	 * Mètode que gestiona tota la lògica abans de que la pilota comenci a moure's
	 */
	private void accioBotoJugar() {
		// Declaració i inicialització de variable que accedeix a l'idioma que hi ha a
		// la classe configManager
		String idiomaTriat = config.getIdioma();

		// Crida del mètode reproduirSo amb el so que es fa cada vegada que es fa click
		reproduirSo("/Sound/menuClick.wav");

		// Declaració i inicialització de varible que demana dades del Jugador 1
		String nom1 = JOptionPane.showInputDialog(this,
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? NOM_PRIMER_JUGADOR_CATALA : NOM_PRIMER_JUGADOR_CASTELLA));

		// Estructura condicional on s'avalua si el nom es posa en blanc o es troba buit
		// no retornem res
		if (nom1 == null || nom1.trim().isEmpty()) {
			return;
		}

		// Declaració i inicialització de varible que demana dades del nickname 1
		String nick1 = JOptionPane.showInputDialog(this,
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? NICKNAME_PRIMER_JUGADOR : NICKNAME_PRIMER_JUGADOR));
		// Estructura condicional on s'avalua si el nom es posa en blanc o es troba buit
		// no retornem res
		if (nick1 == null || nick1.trim().isEmpty()) {
			return;
		}

		// Declaració i inicialització de varible que demana dades del Jugador 2
		String nom2 = JOptionPane.showInputDialog(this,
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? NOM_SEGON_JUGADOR_CATALA : NOM_SEGON_JUGADOR_CASTELLA));
		// Estructura condicional on s'avalua si el nom es posa en blanc o es troba buit no retornem res
		if (nom2 == null || nom2.trim().isEmpty()) {
			return;
		}
		// Declaració i inicialització de varible que demana dades del nickname 2
		String nick2 = JOptionPane.showInputDialog(this,
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? NICKNAME_SEGON_JUGADOR : NICKNAME_SEGON_JUGADOR)); 
		
		// Estructura condicional on s'avalua si el nom es posa en blanc o es troba buit no retornem res
		if (nick2 == null || nick2.trim().isEmpty()) {
			return;
		}

		// Declaració i inicialització de variable que guarda el nivell escollit
		int nivellTriat = seleccionarNivell(idiomaTriat);
		// Estructura condicional on s'avalua si no s'ha triat cap nivell
		if (nivellTriat == -1) {
			return;
		}

		// Crida del mètode reproduirSo amb el so que es fa cada vegada que es fa click
		reproduirSo("/Sound/menuClick.wav");

		// Estructura condicional per parar la música del menu per a que no s'ajunti amb la del joc
		if (musicaMenu != null && musicaMenu.isRunning()) {
			// Fem ús del mètode .stop()
			musicaMenu.stop();
		}

		// Creem l'objecte Partida amb els 6 paràmetres segons el constructor
		Partida novaPartida = new Partida(nom1, nick1, nom2, nick2, idiomaTriat, nivellTriat);

		// Ara passem l'objecte Partida i la instància d'hibernate al constructor de VentanaJoc
		VentanaJoc joc = new VentanaJoc(novaPartida, hibernate);
		joc.setVisible(true); // Fem que aparegui la pantalla de joc
		joc.iniciarJoc(); // Engeguem el joc
		this.dispose(); // Tanquem aquest menu principal
	}

	/**
	 * Mètode que mostra la selecció de nivell inicial de la partida.
	 * 
	 * @param idiomaTriat, idioma escollit per mostrar el missatge
	 * @return el nivell triat o -1 si es cancel·la
	 */
	private int seleccionarNivell(String idiomaTriat) {
		// Declaració i inicialització d'Array de String amb els nivells disponibles
		String[] nivells = new String[NIVELL_MAXIM];
		// Bucle que omple els nivells de l'1 al 20
		for (int i = 0; i < nivells.length; i++) {
			nivells[i] = String.valueOf(i + 1);
		}

		// Declaració i inicialització de variable per mostrar el panell fent casting
		String seleccion = (String) JOptionPane.showInputDialog(this,
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? SELECCIO_NIVELL_CATALA : SELECCIO_NIVELL_CASTELLA),
				(idiomaTriat.equals(LLENGUATGE_CATALA) ? NIVELL_INICIAL_CATALA : NIVELL_INICIAL_CASTELLA),
				JOptionPane.QUESTION_MESSAGE, null, nivells, nivells[0]);

		// Estructura condicional on avalua si la selecció és nul·la
		if (seleccion == null) {
			return -1;
		}

		// Retornem la selecció convertida a int
		return Integer.parseInt(seleccion);
	}

	/**
	 * Mètode per un nou botó per continuar la partida
	 */
	private void accioBotoContinuar() {
		//Declaració i inicialització de variable File i referenciem el nom de FITXER_PARTIDA
		File fitxer = new File(FITXER_PARTIDA);
		
		//Estructura condicional on s'executat si el fitxer referenciat no existeix
		if (!fitxer.exists()) {
			//Mostra de panell informatiu amb missatge
			JOptionPane.showMessageDialog(this,
					config.t("No hi ha cap partida guardada", "No hay ninguna partida guardada"));
			//No retornem res
			return;
		}

		//Declaració i inicialització de variable int, on es mostra un panell preguntar si es vol desar la partida o continuar-la, posem opció de si/no 
		int resposta = JOptionPane.showConfirmDialog(this,
				config.t("Vols continuar la partida desada?", "¿Quieres continuar la partida guardada?"),
				config.t("Continuar partida", "Continuar partida"), JOptionPane.YES_NO_OPTION);

		//Si l'usuari pren a "Si" s'executa
		if (resposta == JOptionPane.YES_OPTION) {
			//Cridem al mètode 
			continuarPartidaGuardada();
		}
	}

	/**
	 * Mètode per continuar una partida existent desada
	 */
	private void continuarPartidaGuardada() {
		// Estructura de control d'excepcions TRY-CATCH
		try {
			// Declaració i inicialització de ObjectInputStream on fa referència a un nou objecte de FITXER_PARTIDA
			ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(FITXER_PARTIDA));
			// Declaració i inicialització d'objecte partida, on és fa un casting i llegeix l'objecte
			Partida partidaGuardada = (Partida) entrada.readObject();
			// Tanquem l'entrada
			entrada.close();

			// Estructura condicional on avalua si la música del menú continua sonant
			if (musicaMenu != null && musicaMenu.isRunning()) {
				// Aturem la musica del menú amb el mètode .stop()
				musicaMenu.stop();
			}

			// Ara passem l'objecte Partida i la instància d'hibernate al constructor de VentanaJoc
			VentanaJoc joc = new VentanaJoc(partidaGuardada, hibernate);
			// Forçem a que es vegi el joc
			joc.setVisible(true);
			// Iniciem el joc
			joc.iniciarJoc();
			// Alliberem recursos de la finestra
			this.dispose();

		} catch (Exception e) {
			// Mostrem un missatge per si ocurreix alguna excepció
			JOptionPane.showMessageDialog(this, "No se ha podido continuar la partida guardada");
		}
	}

	/**
	 * Mètode que crea una finestra emergent que explica com es juga (REGLES DEL
	 * JOC)
	 */
	private void mostrarRegles() {
		// Mostra del text
		String textRegles = config.t(

				"REGLES DEL JOC\n\n" + "1. Juguen dos jugadors en mode cooperatiu.\n"
						+ "2. El jugador de dalt mou la raqueta amb A i D.\n"
						+ "3. El jugador de baix mou la raqueta amb fletxes.\n"
						+ "4. La puntuació és el temps en mil·lisegons.\n" + "5. Cada 20 segons puja de nivell.\n"
						+ "6. La velocitat augmenta un 10% per nivell.\n" + "7. Si la pilota surt, es perd.",
				"REGLAS DEL JUEGO\n\n" + "1. Juegan dos jugadores en modo cooperativo.\n"
						+ "2. El jugador de arriba mueve la raqueta con A y D.\n"
						+ "3. El jugador de abajo mueve la raqueta con flechas.\n"
						+ "4. La puntuación es el tiempo en milisegundos.\n" + "5. Cada 20 segundos sube de nivel.\n"
						+ "6. La velocidad aumenta un 10% por nivel.\n" + "7. Si la pelota sale, se pierde."

		);
		UIManager.put("OptionPane.messageForeground", Color.BLACK);
		// Mostrem el missatge en una finestra emergent
		JOptionPane.showMessageDialog(this, textRegles, "Regles de POKEMON FRONTIER", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Funció per carregar la musica i que no pari de sonar
	 * 
	 * @param ruta, la ruta del so
	 */
	private void reproduirMusicaMenu(String ruta) {
		// Estructura de control d'errors
		try {
			// Declaració i inicialització de URL per accedir a la ruta
			URL url = getClass().getResource(ruta);
			// EStructura condicional on avalua si la ruta no es nul·la
			if (url != null) {
				// Declaració i inicialització de AudioInputStream on accedeix a la url
				AudioInputStream ais = AudioSystem.getAudioInputStream(url);
				// Precarreguem l'audio a la memòria
				musicaMenu = AudioSystem.getClip();
				// Amb el mètode .open() obrim ais
				musicaMenu.open(ais);

				/*
				 * Declaració i inicialització de FloatControl (permet controlar un rang de
				 * valors de punt flotant per a funcions d'àudio, com ara ajustar el volum)
				 */
				FloatControl gainControl = (FloatControl) musicaMenu.getControl(FloatControl.Type.MASTER_GAIN);

				// Declaració i inicialització de variable que té el valor que té Volumen a la
				// classe configManager
				int volumen = config.getVolumen();

				// Si el volum és igual a 0 s'executa
				if (volumen == 0) {
					// Control de la modificació del volum al mínim
					gainControl.setValue(gainControl.getMinimum());

				} else {
					// Declaració i inicialització de float que calcula el nivell de volum o
					// ganancia en decibelis
					float db = (float) (Math.log(volumen / PERCENTATGE_VOLUM) / Math.log(BASE_DEL_LOGARITME)
							* ESCALA_DECIBELS);

					// Modifiquem el valor amb el mètode setValue
					gainControl.setValue(db);
				}
				// Reproduim la música infinitament
				musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
				// Inicialitzem la musica del menú
				musicaMenu.start();
			}
		} catch (Exception e) {
			// Mostra de missatge
			System.out.println("Error musica menu: " + e.getMessage());
		}
	}

	/**
	 * Funció per a sons curts
	 * 
	 * @param ruta, la ruta del so
	 */
	private void reproduirSo(String ruta) {
		// Estructura de control d'errors
		try {
			// Declaració i inicialització de URL per accedir a la ruta
			URL url = getClass().getResource(ruta);
			// EStructura condicional on avalua si la ruta no es nul·la
			if (url != null) {
				// Declaració i inicialització de AudioInputStream on accedeix a la url
				AudioInputStream ais = AudioSystem.getAudioInputStream(url);
				// Precarreguem l'audio a la memòria
				Clip clip = AudioSystem.getClip();
				// Amb el mètode .open() obrim ais
				clip.open(ais);
				// Inicialitzem l'audio
				clip.start();
			}
		} catch (Exception e) {
			// Mostra de missatge
			System.out.println("Error sonido: " + e.getMessage());
		}
	}

	// El punt d'inici de tot el programa
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(() -> new MenuInici().setVisible(true));
	}
}
