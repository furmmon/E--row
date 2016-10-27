package rg.e_row.database;

/**
 * Created by furmon on 12/10/2016.
 */

public class Sortie {

   //       Attributs       //

    protected String dbId;
    protected String date;
    protected String distance;
    protected String duree;


    //      GET_SET        //


    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }



    //      ToString        //

    @Override
    public String toString() {
        return "Sortie{" +
                "dbId='" + dbId + '\'' +
                ", distance='" + distance + '\'' +
                ", date='" + date + '\'' +
                ", duree='" + duree + '\'' +
                '}';
    }
}
