package logic;

import java.io.*;
import java.util.Properties;

/**
 * Classe ConfigManager, controla les properties per emmagatzemar-les
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class ConfigManager {

	// Nom del fitxer on es guarda la configuració
    private static final String CONFIG_FILE = "config.properties";

    // Objecte que emmagatzema les propietats del joc
    private Properties props = new Properties();

    // Declaració i incialització dels text traduits per defecte
    private static final String IDIOMA = "idioma", LLENGUATGE_PER_DEFECTE = "Catala", LLENGUATGE_CASTELLA = "Castellano";
    private static final String COLOR = "colorPuntuacio", COLOR_PER_DEFECTE = "WHITE";
    private static final String VOLUM = "volumen", VOLUM_PER_DEFECTE = "70";
    private static final String ERROR_CARREGAR = "Error cargando config.properties", CONFIGUARCIO = "Configuracion del juego", ERROR_GUARDAR = "Error guardando config.properties";
    
    /**
     * Constructor per defecte.
     * Carrega la configuració en iniciar el joc.
     */
    public ConfigManager() {

    	// Crida al mètode que carrega el fitxer
        cargar();
    }

    /**
     * Mètode que carrega el fitxer de configuració.
     */
    private void cargar() {

    	// Creem el fitxer de configuració
        File archivo = new File(CONFIG_FILE);

        // Si el fitxer no existeix, es creen valors per defecte
        if (!archivo.exists()) {

        	// Idioma per defecte
        	props.setProperty(IDIOMA, LLENGUATGE_PER_DEFECTE);

        	// Color de puntuació per defecte
            props.setProperty(COLOR, COLOR_PER_DEFECTE);

            // Volum per defecte
            props.setProperty(VOLUM, VOLUM_PER_DEFECTE);

            // Guardem la configuració inicial
            guardar();
        }

        // Obrim i carreguem el fitxer properties
        try (InputStream is = new FileInputStream(archivo)) {

            props.load(is);

        } catch (IOException e) {

        	// Mostrem error si no es pot carregar
            System.out.println(ERROR_CARREGAR);
            e.printStackTrace();
        }
    }

    /**
     * Guarda la configuració actual al fitxer.
     */
    public void guardar() {

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {

        	// Desa totes les propietats al fitxer
            props.store(out, CONFIGUARCIO);

        } catch (IOException e) {

        	// Mostrem error si falla el guardat
            System.out.println(ERROR_GUARDAR);
            e.printStackTrace();
        }
    }

    // Retorna l'idioma seleccionat
    public String getIdioma() {
        return props.getProperty(IDIOMA);
    }

    // Retorna el color de la puntuació
    public String getColorPuntuacio() {
        return props.getProperty(COLOR);
    }

    // Retorna el volum convertit a enter
    public int getVolumen() {
        return Integer.parseInt(props.getProperty(VOLUM));
    }

    // Modifica el color de la puntuació
    public void setColorPuntuacio(String color) {

        props.setProperty(COLOR, color);

        // Guarda els canvis
        guardar();
    }

    // Modifica el volum
    public void setVolumen(String volumen) {

        props.setProperty(VOLUM, volumen);

        // Guarda els canvis
        guardar();
    }

    // Modifica l'idioma
    public void setIdioma(String idioma) {

        props.setProperty(IDIOMA, idioma);

        // Guarda els canvis
        guardar();
    }

    /**
     * Retorna el text segons l'idioma seleccionat.
     */
    public String t(String Catala, String Castellano) {

    	// Si l'idioma és castellà retorna el text en castellà,
    	// si no, retorna el text en català
        return LLENGUATGE_CASTELLA.equals(getIdioma()) ? Castellano : Catala;
    }
}

