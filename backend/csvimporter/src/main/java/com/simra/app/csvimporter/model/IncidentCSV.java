package main.java.com.simra.app.csvimporter.model;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.opencsv.bean.CsvBindByName;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * The Incident csv model.
 */
public class IncidentCSV extends ApplicationFileVersion implements MongoDocument {

    @CsvBindByName
    private int key;

    @CsvBindByName
    private String lat;

    @CsvBindByName
    private String lon;

    @CsvBindByName
    private String ts;

    @CsvBindByName
    private int bike = 0;

    @CsvBindByName
    private int childCheckBox = 0;

    @CsvBindByName
    private int trailerCheckBox = 0;

    @CsvBindByName
    private int pLoc = 0;

    @CsvBindByName
    private int incident = 0;

    @CsvBindByName
    private int i1 = 0;

    @CsvBindByName
    private int i2 = 0;

    @CsvBindByName
    private int i3 = 0;

    @CsvBindByName
    private int i4 = 0;

    @CsvBindByName
    private int i5 = 0;

    @CsvBindByName
    private int i6 = 0;

    @CsvBindByName
    private int i7 = 0;

    @CsvBindByName
    private int i8 = 0;

    @CsvBindByName
    private int i9 = 0;

    @CsvBindByName
    private int scary = 0;

    @CsvBindByName
    private String desc;

    @CsvBindByName
    private int i10 = 0;

    /**
     * Gets key.
     *
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * Gets lat.
     *
     * @return the lat
     */
    public String getLat() {
        return lat;
    }

    /**
     * Sets lat.
     *
     * @param lat the lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * Gets lon.
     *
     * @return the lon
     */
    public String getLon() {
        return lon;
    }

    /**
     * Sets lon.
     *
     * @param lon the lon
     */
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * Gets ts.
     *
     * @return the ts
     */
    public String getTs() {
        return ts;
    }

    /**
     * Sets ts.
     *
     * @param ts the ts
     */
    public void setTs(String ts) {
        this.ts = ts;
    }

    /**
     * Gets bike.
     *
     * @return the bike
     */
    public int getBike() {
        return bike;
    }

    /**
     * Sets bike.
     *
     * @param bike the bike
     */
    public void setBike(int bike) {
        this.bike = bike;
    }

    /**
     * Gets child check box.
     *
     * @return the child check box
     */
    public int getChildCheckBox() {
        return childCheckBox;
    }

    /**
     * Sets child check box.
     *
     * @param childCheckBox the child check box
     */
    public void setChildCheckBox(int childCheckBox) {
        this.childCheckBox = childCheckBox;
    }

    /**
     * Gets trailer check box.
     *
     * @return the trailer check box
     */
    public int getTrailerCheckBox() {
        return trailerCheckBox;
    }

    /**
     * Sets trailer check box.
     *
     * @param trailerCheckBox the trailer check box
     */
    public void setTrailerCheckBox(int trailerCheckBox) {
        this.trailerCheckBox = trailerCheckBox;
    }

    /**
     * Gets loc.
     *
     * @return the loc
     */
    public int getpLoc() {
        return pLoc;
    }

    /**
     * Sets loc.
     *
     * @param pLoc the p loc
     */
    public void setpLoc(int pLoc) {
        this.pLoc = pLoc;
    }

    /**
     * Gets incident.
     *
     * @return the incident
     */
    public int getIncident() {
        return incident;
    }

    /**
     * Sets incident.
     *
     * @param incident the incident
     */
    public void setIncident(int incident) {
        this.incident = incident;
    }

    /**
     * Gets i 1.
     *
     * @return the i 1
     */
    public int getI1() {
        return i1;
    }

    /**
     * Sets i 1.
     *
     * @param i1 the 1
     */
    public void setI1(int i1) {
        this.i1 = i1;
    }

    /**
     * Gets i 2.
     *
     * @return the i 2
     */
    public int getI2() {
        return i2;
    }

    /**
     * Sets i 2.
     *
     * @param i2 the 2
     */
    public void setI2(int i2) {
        this.i2 = i2;
    }

    /**
     * Gets i 3.
     *
     * @return the i 3
     */
    public int getI3() {
        return i3;
    }

    /**
     * Sets i 3.
     *
     * @param i3 the 3
     */
    public void setI3(int i3) {
        this.i3 = i3;
    }

    /**
     * Gets i 4.
     *
     * @return the i 4
     */
    public int getI4() {
        return i4;
    }

    /**
     * Sets i 4.
     *
     * @param i4 the 4
     */
    public void setI4(int i4) {
        this.i4 = i4;
    }

    /**
     * Gets i 5.
     *
     * @return the i 5
     */
    public int getI5() {
        return i5;
    }

    /**
     * Sets i 5.
     *
     * @param i5 the 5
     */
    public void setI5(int i5) {
        this.i5 = i5;
    }

    /**
     * Gets i 6.
     *
     * @return the i 6
     */
    public int getI6() {
        return i6;
    }

    /**
     * Sets i 6.
     *
     * @param i6 the 6
     */
    public void setI6(int i6) {
        this.i6 = i6;
    }

    /**
     * Gets i 7.
     *
     * @return the i 7
     */
    public int getI7() {
        return i7;
    }

    /**
     * Sets i 7.
     *
     * @param i7 the 7
     */
    public void setI7(int i7) {
        this.i7 = i7;
    }

    /**
     * Gets i 8.
     *
     * @return the i 8
     */
    public int getI8() {
        return i8;
    }

    /**
     * Sets i 8.
     *
     * @param i8 the 8
     */
    public void setI8(int i8) {
        this.i8 = i8;
    }

    /**
     * Gets i 9.
     *
     * @return the i 9
     */
    public int getI9() {
        return i9;
    }

    /**
     * Sets i 9.
     *
     * @param i9 the 9
     */
    public void setI9(int i9) {
        this.i9 = i9;
    }

    /**
     * Gets i 10.
     *
     * @return the i 10
     */
    public int getI10() {
        return i10;
    }

    /**
     * Sets i 10.
     *
     * @param i10 the 10
     */
    public void setI10(int i10) {
        this.i10 = i10;
    }

    /**
     * Gets scary.
     *
     * @return the scary
     */
    public int getScary() {
        return scary;
    }

    /**
     * Sets scary.
     *
     * @param scary the scary
     */
    public void setScary(int scary) {
        this.scary = scary;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * To document object document.
     *
     * @return the document
     */
    public Document toDocumentObject(){
        Document document= new Document();

        Document contentCompositeId= new Document();
        contentCompositeId.put("rideId",this.getFileId());
        contentCompositeId.put("key", this.key);
        document.put("_id",contentCompositeId);
        document.put("key", this.key);
        document.put("bike", this.bike);
        document.put("childCheckBox", this.childCheckBox);
        document.put("trailerCheckBox", this.trailerCheckBox);
        document.put("pLoc", this.pLoc);
        document.put("incident", this.incident);
        document.put("i1", this.i1);
        document.put("i2", this.i2);
        document.put("i3", this.i3);
        document.put("i4", this.i4);
        document.put("i5", this.i5);
        document.put("i6", this.i6);
        document.put("i7", this.i7);
        document.put("i8", this.i8);
        document.put("i9", this.i9);
        document.put("i10", this.i10);
        document.put("scary", this.scary);
        document.put("description", this.desc);

        List<Double> places = Arrays.asList(Double.parseDouble(this.lon),Double.parseDouble(this.lat));

        Point geoPoint = new Point(new Position(places));
        document.put("timestamp", this.ts);
        document.put("location", geoPoint);

        return document;
    }
}
