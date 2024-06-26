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

    @Column(name = "email")
    private String userEmail;

    @Column(name = "password")
    private String passwordHash;

    @Column(name = "passwordSalt")
    private String passwordSalt;

    @Column(name = "meta_info")
    private String metaInfo = "";

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Group> groupsAdmin = new ArrayList<>();

    @OneToMany(mappedBy = "userHost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PassedTest> passedTests = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private Set<Group> groupsUserSet = new HashSet<Group>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "users_invitations",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private Set<Group> invitations = new HashSet<>();

    public User() {
    }

    public User(String login, String email, String passwordHash) {
        this.userLogin = login;
        this.userEmail = email;
        this.passwordHash = passwordHash;
    }

    public void addInvitation(Group group) {
        invitations.add(group);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User tmp) {
            return (Objects.equals(tmp.getUserId(), getUserId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getUserId());
    }
}
