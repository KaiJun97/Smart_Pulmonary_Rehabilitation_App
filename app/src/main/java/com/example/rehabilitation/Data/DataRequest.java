package com.example.rehabilitation.Data;

import android.app.Application;

public class DataRequest extends Application{
    private User user;
    public DataRequest(){}
    public void logInFunction(String username){
        this.user = new User(username);
    }

    public User getUserDetails(){
        return this.user;
    }
}
