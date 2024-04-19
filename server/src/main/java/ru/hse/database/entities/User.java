package ru.hse.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long userId;

    @Column(name = "login")
    private String userLogin;

    @Column(name = "email")
    private String userEmail;

    @Column(name = "password")
    private String passwordHash;

    @Column(name = "passwordSalt")
    private String passwordSalt;

    @Column(name = "meta_info")
    private String metaInfo = "";

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groupsAdmin = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private Set<Group> groupsUserSet = new HashSet<Group>();

    public User() {
    }

    public User(String login, String email, String passwordHash, String passwordSalt) {
        this.userLogin = login;
        this.userEmail = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != User.class) {
            User tmp = (User) obj;
            return (Objects.equals(tmp.getUserId(), getUserId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getUserId());
    }
}
