package com.example.rehabilitation.Data;

import android.app.Application;

public class DataRequest extends Application{
    private User user;
    public DataRequest(){}
    public void logInFunction(String username, int uId){
        this.user = new User(username,uId);
    }

    public User getUserDetails(){
        return this.user;
    }
}
