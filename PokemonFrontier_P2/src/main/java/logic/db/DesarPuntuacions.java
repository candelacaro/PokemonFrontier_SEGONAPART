package logic.db;

import logic.Partida;

public class DesarPuntuacions {

    // Ja no és static final, ara depèn de la instància de repositori que li passem
    private final PuntuacionsRepository repository;

    /**
     * Constructor que rep el repositori configurat.
     * @param repository El repositori que ja té la connexió Hibernate.
     */
    public DesarPuntuacions(PuntuacionsRepository repository) {
        this.repository = repository;
    }

    /**
     * Guardem la mateixa puntuacio per als dos jugadors de l'equip.
     * Ara utilitzem els nicknames definits a la partida.
     */
    public void guardarPuntuacionEquip(Partida partida, long punts) {
        // Guardem la puntuació pel primer jugador (usant el seu nickname)
        repository.guardarPuntuacion(partida.getNickName1(), punts);
        
        // Guardem la puntuació pel segon jugador (usant el seu nickname)
        repository.guardarPuntuacion(partida.getNickName2(), punts);
    }
}