package com.example.rehabilitation.Data;

public class User {
    private String username,firstName,lastName;

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    private int uId;

    public User(String username, String firstName, String lastName){
        this.username=username;
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    public User(String username, int uId ){
        this.username=username;
        this.uId=uId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
