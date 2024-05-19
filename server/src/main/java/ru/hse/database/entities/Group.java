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

    @OneToMany(mappedBy = "groupHost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin")
    private User admin;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> usersSet = new HashSet<User>();

    public Group() {
    }

    public Group(String groupName, String passwordHash, User adminUser) {
        this.groupName = groupName;
        this.passwordHash = passwordHash;
        this.admin = adminUser;
        this.usersSet.add(adminUser);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Group.class) {
            Group tmp = (Group) obj;
            return (Objects.equals(tmp.getGroupId(), getGroupId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getGroupId());
    }
}
