package com.hsl_firebase.alves.pap_firebase_hsl;

public class Users {

    public String name, thumb_image, status;

    public Users(){

    }

    public Users(String name, String thumb_image, String status){
        this.name = name;
        this.thumb_image = thumb_image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
