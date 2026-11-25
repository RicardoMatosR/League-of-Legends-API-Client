package model;

import java.io.Serializable;

@lombok.Data
public class Invocador implements Serializable {
    private String puuid; //idInvocador
    private String gameName; //Nombre de invocador
    private String tagLine; //Server
    private String queueType; //Si es soloq o flex
    private String tier; //Division (DIAMOND)
    private String rank; //Numero de division (II)
    private long leaguePoints; //LPs
    private long wins;
    private long losses;
    private double wr;
    private long championID1; //Champ con mas maestria
    private long championID2; //Champ con mas maestria
    private long championID3; //Champ con mas maestria
    private long championPoints1; //Puntos con ese champ
    private long championPoints2; //Puntos con ese champ
    private long championPoints3; //Puntos con ese champ
    private String championName; //Champ jugado en la ultima partida
    private long kills;
    private long deaths;
    private long assists;
    private boolean win; //Si se ha ganado o perdido la ultima partida
}
