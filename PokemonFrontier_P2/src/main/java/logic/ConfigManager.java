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
        	props.setProperty("idioma", "Catala");

        	// Color de puntuació per defecte
            props.setProperty("colorPuntuacio", "WHITE");

            // Volum per defecte
            props.setProperty("volumen", "70");

            // Guardem la configuració inicial
            guardar();
        }

        // Obrim i carreguem el fitxer properties
        try (InputStream is = new FileInputStream(archivo)) {

            props.load(is);

        } catch (IOException e) {

        	// Mostrem error si no es pot carregar
            System.out.println("Error cargando config.properties");
            e.printStackTrace();
        }
    }

    /**
     * Guarda la configuració actual al fitxer.
     */
    public void guardar() {

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {

        	// Desa totes les propietats al fitxer
            props.store(out, "Configuracion del juego");

        } catch (IOException e) {

        	// Mostrem error si falla el guardat
            System.out.println("Error guardando config.properties");
            e.printStackTrace();
        }
    }

    // Retorna l'idioma seleccionat
    public String getIdioma() {
        return props.getProperty("idioma");
    }

    // Retorna el color de la puntuació
    public String getColorPuntuacio() {
        return props.getProperty("colorPuntuacio");
    }

    // Retorna el volum convertit a enter
    public int getVolumen() {
        return Integer.parseInt(props.getProperty("volumen"));
    }

    // Modifica el color de la puntuació
    public void setColorPuntuacio(String color) {

        props.setProperty("colorPuntuacio", color);

        // Guarda els canvis
        guardar();
    }

    // Modifica el volum
    public void setVolumen(String volumen) {

        props.setProperty("volumen", volumen);

        // Guarda els canvis
        guardar();
    }

    // Modifica l'idioma
    public void setIdioma(String idioma) {

        props.setProperty("idioma", idioma);

        // Guarda els canvis
        guardar();
    }

    /**
     * Retorna el text segons l'idioma seleccionat.
     */
    public String t(String Catala, String Castellano) {

    	// Si l'idioma és castellà retorna el text en castellà,
    	// si no, retorna el text en català
        return "Castellano".equals(getIdioma()) ? Castellano : Catala;
    }
}

