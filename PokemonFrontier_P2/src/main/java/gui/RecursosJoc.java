package gui;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

/**
 * Aquesta classe carrega tots els fitxers de so i imatges del joc.
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class RecursosJoc {

    // Declarem les variables privades per les musiques del nostre codi.
    private Clip musica; // El reproductor per a la musica de fons
    private Clip soRebot; // El reproductor per els rebots amb la paret
    private Clip soRaqueta; // El reproductor per quan toques la pala
    private Clip soMenu; // El reproductor per als sons de la interficie
    private Clip soPerdre; // El reproductor per quan la pilota s'escapa
    private Clip soIniciPartida; // Variable per al so d'inici

    // Declarem les variables privades per les imatges del nostre codi.
    private ImageIcon imgFons; // Imatge del primer gimnas
    private ImageIcon imgFons2; // Imatge del segon gimnas
    private ImageIcon imgFons3; // Imatge del tercer gimnas
    private ImageIcon imgFons4; // Imatge de l'ultim gimnas
    private ImageIcon imgPilota; // El dibuix de la Pokeball que rebota
    private ImageIcon imgObstacle; // Sera Gastly
    private ImageIcon imgRaqueta; // El dibuix de la pala que controlem
    private ImageIcon imgObstacle2; // Sera Hunter
    private ImageIcon imgObstacle3; // Sera Gengar
    private ImageIcon imgObstacle4; // Sera Megagengar
    
    //Declaració i inicialització de finals per el canvi de fons segon el nivell
    private final int NIVELL_AMB_PRIMER_FONS = 4;
    private final int NIVELL_AMB_SEGON_FONS = 9;
    private final int NIVELL_AMB_TERCER_FONS = 14;

    /**
     * Mètode que carrega tots els fitxers de so i imatges
     */
    public void carregarRecursos() {
        try { // Intentem carregar tot per si algun fitxer falta
            // Carreguem les imatges
            imgFons = carregarImatge("/Images/fondopoke.png"); // Carrega el gimnas inicial
            imgFons2 = carregarImatge("/Images/gimnasio2.png"); // Carrega el gimnas 2
            imgFons3 = carregarImatge("/Images/gimnasio3.png"); // Carrega el gimnas 3
            imgFons4 = carregarImatge("/Images/gimnasio4.png"); // Carrega el gimnas 4
            imgPilota = carregarImatge("/Images/pokemonverda.png"); // Carrega la pokeball
            imgRaqueta = carregarImatge("/Images/ca\u00f1araqueta.png"); // Carrega la canya
            imgObstacle = carregarImatge("/Images/gastly.png"); // Imatge dels primers obstacles
            imgObstacle2 = carregarImatge("/Images/hunter.png"); // Imatge per els segons obstacles
            imgObstacle3 = carregarImatge("/Images/pokemonobstaculo.png"); // Imatge per els tercers obstacles
            imgObstacle4 = carregarImatge("/Images/megagengarshiny.png"); // Imatge per els ultims obstacles.

            soRebot = carregarClip("/Sound/rebotBola.wav"); // Busca el fitxer del rebot de la pilota.
            soRaqueta = carregarClip("/Sound/sonidoraqueta.wav"); // Busca el fitxer del toc de la raqueta (canya)
            soPerdre = carregarClip("/Sound/partidaPerduda.wav"); // Busca el so de quan perdis.
            soIniciPartida = carregarClip("/Sound/iniciPartida.wav"); // Busca l'audio de comencament de la partida.
            soMenu = carregarClip("/Sound/menuClick.wav"); // Busca el so del menu
        } catch (Exception e) { // Si hi ha un error carregant
            System.out.println("Error carregant recursos: " + e.getMessage()); // Ens avisa per consola
        }
    }

    /**
     * Mètode que carrega les imatges
     * @param ruta, la ruta de la imatge
     * @return la imatge
     */
    private ImageIcon carregarImatge(final String ruta) {
        final URL url = getClass().getResource(ruta);
        if (url == null) {
        	return null;
        }
        return new ImageIcon(url);
    }

    /**
     * Mètode que càrrega els audios
     * @param ruta, la ruta del so
     * @return la reproducció del so
     * @throws Exception
     */
    private Clip carregarClip(final String ruta) throws Exception {
    		//Declaració i inicialització de final URL per accedir a la ruta
        final URL url = getClass().getResource(ruta);
        //Estructura condicional on avalua si la url és nul·la
        if (url == null) {
        	//Retornem null
        	return null;
        }
        
        //Precarreguem l'audio a la memòria
        final Clip clip = AudioSystem.getClip();
        //Obrim l'audio precarregat
        clip.open(AudioSystem.getAudioInputStream(url));
        //Retornem l'audio
        return clip;
    }

    /**
     * Mètode que posa la música de Pokemon en bucle
     */
    public void reproduirMusica() {
        try { // Intentem reproduir-la
            final URL url = getClass().getResource("/Sound/jocActiu.wav"); // Agafem la canço de la partida
            if (url != null) { // Si el fitxer hi es
                final AudioInputStream ais = AudioSystem.getAudioInputStream(url); // Creem el flux d'entrada
                musica = AudioSystem.getClip(); // Agafem el clip
                musica.open(ais); // L'obrim
                musica.loop(Clip.LOOP_CONTINUOUSLY); // Posem aixo perque aixi mai es pari la musica.
                musica.start(); // I comencara la musica.
            }
        } catch (final Exception e) { } // Si falla la musica, el joc segueix en silenci
    }

    /**
     * Mètode per reproduir el so d'inici de partida
     */
    public void reproduirIniciPartida() {
        reproduirClip(soIniciPartida);
    }

    /**
     * Mètode que fa el soroll quan la pilota rebota a la paret
     */
    public void sonarRebot() {
        reproduirClip(soRebot);
    }

    /**
     * Mètode que fa el soroll quan la pilota toca la teva raqueta
     */
    public void sonarRaqueta() {
        reproduirClip(soRaqueta);
    }

    /**
     * Mètode que fa el soroll quan perds la partida
     */
    public void sonarMort() {
        reproduirClip(soPerdre);
    }

    /**
     * Mètode que fa el soroll quan cliques al menu
     */
    public void reproduirClic() {
        reproduirClip(soMenu);
    }

    /**
     * Mètode que reprodueix els audios
     * @param clip, l'audio del argument
     */
    private void reproduirClip(final Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Mètode que atura la música
     */
    public void pararMusica() {
        if (musica != null) musica.stop(); // Parem la musica de fons
    }

    /**
     * Mètode que carrega els fons per cada nivell
     * @param nivell, el nivell que s'estigui jugant
     * @return, returna el fons que correspon al nivell
     */
    public ImageIcon getFonsActual(final int nivell) {
        if (nivell <= NIVELL_AMB_PRIMER_FONS) return imgFons; // Posem el fons del gimnas 1
        if (nivell <= NIVELL_AMB_SEGON_FONS) return imgFons2; // Posem el fons del gimnas 2
        if (nivell <= NIVELL_AMB_TERCER_FONS) return imgFons3; // Posem el fons del gimnas 3
        return imgFons4; // Posem el fons final
    }

    /**
     * Mètode que carrega els diferents osbtacles
     * @param nivell, el nivell que s'estigui jugant
     * @return l'obstacle que correspongui al nivell
     */
    public ImageIcon getObstacleActual(final int nivell) {
        if (nivell <= NIVELL_AMB_PRIMER_FONS) {
        	return imgObstacle; // gastly.png
        }
        if (nivell <= NIVELL_AMB_SEGON_FONS) {
        	return imgObstacle2; // hunter.png
        }
        if (nivell <= 19) {
        	return imgObstacle3; // pokemonobstaculo.png
        }
        return imgObstacle4; // megagengarshiny.png
    }

    /**
     * Mètode getter que accedeix a la imatge de la pilota
     * @return la imatge de la pilota
     */
    public ImageIcon getImgPilota() {
        return imgPilota;
    }

    /**
     * Mètode getter que accedeic a la imatge de la raqueta
     * @return la imatge de la raqueta
     */
    public ImageIcon getImgRaqueta() {
        return imgRaqueta;
    }
}
