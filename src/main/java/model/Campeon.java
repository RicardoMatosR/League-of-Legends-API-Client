package model;

import java.io.Serializable;

@lombok.Data
public class Campeon implements Serializable {
    private long championId; //idChampionLast3Games (same that key)
    private String key; //idChampionDataChamps (same that championID)
    private String name;
    private long championPoints; //Puntos de maestria
}
