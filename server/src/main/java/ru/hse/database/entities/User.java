package ru.hse.database.entities;

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

    @Column(name = "password")
    private String passwordHash;

    @Column(name = "meta_info")
    private String metaInfo;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groupsAdmin = new ArrayList<>();

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
