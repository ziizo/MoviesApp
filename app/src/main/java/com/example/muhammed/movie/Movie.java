package com.example.muhammed.movie;

/**
 * Created by muhammed on 8/13/16.
 */
public class Movie {

    String ImgPath;
    String OriginalTitle;
    String OverView;
    String Date;
    String Rate;
    String Id;

  public Movie(){

  }

    public Movie(String imgPath, String originalTitle, String overView, String date, String rate, String id) {
        ImgPath = imgPath;
        OriginalTitle = originalTitle;
        OverView = overView;
        Date = date;
        Rate = rate;
        Id = id;
    }

    public String getId() { return Id;}

    public void setId(String id) {Id = id;}

    public String getOriginalTitle() {
        return OriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        OriginalTitle = originalTitle;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public void setImgPath(String imgPath) {
        ImgPath = imgPath;
    }

    public String getOverView() {
        return OverView;
    }

    public void setOverView(String overView) {
        OverView = overView;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }


}
