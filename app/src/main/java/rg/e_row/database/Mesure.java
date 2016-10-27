package rg.e_row.database;

/**
 * Created by furmon on 12/10/2016.
 */

public class Mesure {


    //       Attributs       //

    protected String dbId;
    protected String sortieId;
    protected String cadence;
    protected String vitesse;
    protected String longitude;
    protected String latitude;


    //      GET_SET        //


    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getSortieId() {
        return sortieId;
    }

    public void setSortieId(String sortieId) {
        this.sortieId = sortieId;
    }

    public String getCadence() {
        return cadence;
    }

    public void setCadence(String cadence) {
        this.cadence = cadence;
    }

    public String getVitesse() {
        return vitesse;
    }

    public void setVitesse(String vitesse) {
        this.vitesse = vitesse;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
