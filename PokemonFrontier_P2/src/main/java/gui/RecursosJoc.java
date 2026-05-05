package gui;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

/**
 * Aquesta classe carrega tots els fitxers de so i imatges del joc.
 */
public class RecursosJoc {

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

    // Carreguem tots els fitxers de so i imatges
    public void carregarRecursos() {
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

            soRebot = carregarClip("/rebotBola.wav"); // Busca el fitxer del rebot de la pilota.
            soRaqueta = carregarClip("/sonidoraqueta.wav"); // Busca el fitxer del toc de la raqueta (canya)
            soPerdre = carregarClip("/partidaPerduda.wav"); // Busca el so de quan perdis.
            soIniciPartida = carregarClip("/iniciPartida.wav"); // Busca l'audio de començament de la partida.
            soMenu = carregarClip("/menuClick.wav"); // Busca el so del menú
        } catch (Exception e) { // Si hi ha un error carregant
            System.out.println("Error carregant recursos: " + e.getMessage()); // Ens avisa per consola
        }
    }

    private Clip carregarClip(final String ruta) throws Exception {
        final URL url = getClass().getResource(ruta);
        if (url == null) return null;
        final Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(url));
        return clip;
    }

    //Posem la música de Pokémon en bucle
    public void reproduirMusica() {
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
    public void reproduirIniciPartida() {
        reproduirClip(soIniciPartida);
    }

    // Fa el soroll quan la pilota rebota a la paret
    public void sonarRebot() {
        reproduirClip(soRebot);
    }

    // Fa el soroll quan la pilota toca la teva raqueta
    public void sonarRaqueta() {
        reproduirClip(soRaqueta);
    }

    // Fa el soroll quan perds la partida
    public void sonarMort() {
        reproduirClip(soPerdre);
    }

    // Fa el soroll quan cliques al menú
    public void reproduirClic() {
        reproduirClip(soMenu);
    }

    private void reproduirClip(final Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void pararMusica() {
        if (musica != null) musica.stop(); // Parem la música de fons
    }

    public ImageIcon getFonsActual(final int nivell) {
        if (nivell <= 4) return imgFons; // Posem el fons del gimnàs 1
        if (nivell <= 9) return imgFons2; // Posem el fons del gimnàs 2
        if (nivell <= 14) return imgFons3; // Posem el fons del gimnàs 3
        return imgFons4; // Posem el fons final
    }

    public ImageIcon getObstacleActual(final int nivell) {
        if (nivell <= 4) return imgObstacle; // gastly.png
        if (nivell <= 9) return imgObstacle2; // hunter.png
        if (nivell <= 19) return imgObstacle3; // pokemonobstaculo.png
        return imgObstacle4; // megagengarshiny.png
    }

    public ImageIcon getImgPilota() {
        return imgPilota;
    }

    public ImageIcon getImgRaqueta() {
        return imgRaqueta;
    }
}
