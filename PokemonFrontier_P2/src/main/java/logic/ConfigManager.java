package logic;

import java.io.*;
import java.util.Properties;

/**
 * Classe ConfigManager, controla les properties per emmagatzemar-les
 * @author Daner Coria, André Medinas, Candela Cabello, Izan Perez i Adrià Chenovart
 */
public class ConfigManager {

	//Declaració i inicialització de final String per guardar el nom del fitxer config.properties
    private static final String CONFIG_FILE = "config.properties";

    //Instància d'objecte de la classe Properties
    private Properties props = new Properties();

    /**
     * Constructor per defecte
     */
    public ConfigManager() {
    		//Sobrecàrrega de mètode cargar()
        cargar();
    }

    /**
     * Mètode que carrega el fitxer
     */
    private void cargar() {
    		//Declaració i inicialització de File que s'ha d'anomenar CONFIG_FILE
        File archivo = new File(CONFIG_FILE);

        // Estructura condicional o avalua si no existeix, crea un de nou
        if (!archivo.exists()) {
        		//Amb el mètode setter modifiquem, per defecte l'idioma en català
            props.setProperty("idioma", "Catala");
            //Amb el mètode setter modifiquem, per defecte el color de la puntuació en blanc
            props.setProperty("colorPuntuacio", "WHITE");
            //Amb el mètode setter modifiquem, per defecte el volum a 70
            props.setProperty("volumen", "70");
            //Sobrecàrrega de mètode guardar()
            guardar();
        }

        // cargar archivo
        try (InputStream is = new FileInputStream(archivo)) {

            props.load(is);

        } catch (IOException e) {

            System.out.println("Error cargando config.properties");
            e.printStackTrace();
        }
    }

    /**
     * Mètode que guarda la configuració del joc per la pròxima partida
     */
    public void guardar() {

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {

            props.store(out, "Configuracion del juego");

        } catch (IOException e) {

            System.out.println("Error guardando config.properties");
            e.printStackTrace();
        }
    }

   

    public String getIdioma() {
        return props.getProperty("idioma");
    }

    public String getColorPuntuacio() {
        return props.getProperty("colorPuntuacio");
    }

    public int getVolumen() {
        return Integer.parseInt(props.getProperty("volumen"));
    }



    public void setColorPuntuacio(String color) {

        props.setProperty("colorPuntuacio", color);

        guardar();
    }
    
    public void setVolumen(String volumen) {

        props.setProperty("volumen", volumen);

        guardar();
    }
}

