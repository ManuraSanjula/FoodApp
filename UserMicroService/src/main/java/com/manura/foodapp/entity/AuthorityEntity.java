package com.manura.foodapp.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name="authorities")
@Getter
@Setter
public class AuthorityEntity  implements Serializable {
    private static final long serialVersionUID = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String name;

    public AuthorityEntity() {

    }

    public AuthorityEntity(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy="authorities")
    private Collection<RoleEntity> roles;

}
