package com.manura.foodapp.FoodService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 89984844849448L;
    
    @Id
    private Long id;
    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private Boolean emailVerify;
    private String address;
    private Date passwordChangedAt;
    private List<String> roles = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();
    private String pic;
}
