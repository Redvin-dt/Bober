package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    private long userId;
    @Getter
    @Column(name = "login")
    private String userLogin;
    @Getter
    @Column(name = "password")
    private String passwordHash;

    @Setter
    @Getter
    @Column(name = "meta_info")
    private String metaInfo;


    @Getter
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groupsAdmin = new ArrayList<>();

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private Set<Group> groupsUserSet = new HashSet<Group>();

    public User() {
    }

    public User(String name, String passwordHash) {
        this.userLogin = name;
        this.passwordHash = passwordHash;
    }

    public Long getUserId() {
        return this.userId;
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
