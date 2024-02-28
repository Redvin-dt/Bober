package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    private long groupId;
    @Getter
    @Setter
    @Column(name = "name")
    private String groupName;
    @Getter
    @Setter
    @Column(name = "password")
    private String passwordHash;

    @Setter
    @Getter
    @Column(name = "meta_info")
    private String metaInfo;

    @Getter
    @Setter
    @OneToMany(mappedBy = "groupHost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();


    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin")
    private User admin;

    @Getter
    @Setter
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_groups",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> usersSet = new HashSet<User>();

    public Group() {
    }

    public Group(String name, String passwordHash, User adminUser) {
        this.groupName = name;
        this.passwordHash = passwordHash;
        this.admin = adminUser;
        this.usersSet.add(adminUser);
    }

    public Long getGroupId() {
        return this.groupId;
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
