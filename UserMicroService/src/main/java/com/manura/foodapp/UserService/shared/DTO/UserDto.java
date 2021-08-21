package com.manura.foodapp.UserService.shared.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserDto implements Serializable {

    private static final long serialVersionUID = 89984844849448L;
    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean active;
    private Boolean emailVerify;
    private String address;
    private Date passwordChangedAt;
    private List<String> roles = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();
    private String pic;
}
