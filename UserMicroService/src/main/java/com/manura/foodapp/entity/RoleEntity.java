package com.manura.foodapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity(name="roles")
@Getter
@Setter
public class RoleEntity implements Serializable {
    private static final long serialVersionUID = 7L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public RoleEntity(String role) {
        this.role = role;
    }

    public RoleEntity() {

    }

    String role;

    @ManyToMany(cascade= CascadeType.PERSIST , fetch=FetchType.EAGER)
    @JoinTable(name="roles_authorities",
            joinColumns = @JoinColumn(name="roles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="authorities_id", referencedColumnName = "id"))
    private Collection<AuthorityEntity> authorities;

    @ManyToMany(mappedBy="role")
    private Collection<UserEntity> users;

}
