package com.manura.foodapp.dto;

import java.io.Serializable;

public class ReviewDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6281447606623393682L;
    private Long id;
    private String comment;
    private String email;

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
