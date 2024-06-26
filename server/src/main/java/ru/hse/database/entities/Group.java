package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Groups")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long groupId;

    @Column(name = "name")
    private String groupName;

    @Column(name = "password")
    private String passwordHash = "";

    @Column(name = "meta_info")
    private String metaInfo = "";

    @OneToMany(mappedBy = "groupHost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Chapter> chapters = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin")
    private User admin;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> usersSet = new HashSet<User>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "users_invitations", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> invitedUsers = new HashSet<User>();

    public Group() {
    }

    public Group(String groupName, String passwordHash, User adminUser) {
        this.groupName = groupName;
        this.passwordHash = passwordHash;
        this.admin = adminUser;
        this.usersSet.add(adminUser);
    }

    public void addUser(User user) {
        usersSet.add(user);
    }

    public void removeUser(User user) {
        usersSet.remove(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group tmp) {
            return (Objects.equals(tmp.getGroupId(), getGroupId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getGroupId());
    }
}
