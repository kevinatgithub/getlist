package model;

public class Recipt_model {

    String title,detaile,km;

    Integer image1;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetaile() {
        return detaile;
    }

    public void setDetaile(String detaile) {
        this.detaile = detaile;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public Integer getImage1() {
        return image1;
    }

    public void setImage1(Integer image1) {
        this.image1 = image1;
    }

    public Recipt_model(String title, String detaile, String km, Integer image1) {
        this.title = title;
        this.detaile = detaile;
        this.km = km;
        this.image1 = image1;
    }
}
