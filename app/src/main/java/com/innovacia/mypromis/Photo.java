package com.innovacia.mypromis;

/**
 * Created by Belal on 10/18/2017.
 */

public class Photo {
    private int id;
    private String sitePhotoDate;
    private String sitePhotoDesc;
    private String sitePhotoName;

    String urlPhoto = "http://www.innovacia.com.my/promise/sitephoto/";
    String strTest, strGapo;



    public Photo(int id, String sitePhotoDate, String sitePhotoDesc, String sitePhotoName) {
        this.id = id;
        this.sitePhotoDate = sitePhotoDate;
        this.sitePhotoDesc = sitePhotoDesc;
        this.sitePhotoName = sitePhotoName;
    }

    public int getId() {
        return id;
    }

    public String getSitePhotoDate() {
        return sitePhotoDate;
    }

    public String getSitePhotoDesc() {
        return sitePhotoDesc;
    }


    public String getSitePhotoName() {
        return urlPhoto + sitePhotoName;
    }
}