package com.manura.foodapp.entity;

import java.io.Serializable;
import javax.persistence.Column;


public class UserEntity implements Serializable{

    private static final long serialVersionUID = 6994849949494494L;
    
    private String firstName;
    private String lastName;
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    @Column(name="pic")
    private String pic;
}
