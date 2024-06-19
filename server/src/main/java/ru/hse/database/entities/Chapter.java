package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "Chapters")
@Getter
@Setter
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long chapterId;

    @Column(name = "name")
    private String chapterName;

    @Column(name = "text_file")
    private String textFile = "";

    @Column(name = "meta_info")
    private String metaInfo = "";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_host")
    private Group groupHost;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "chapterHost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();

    @Column(name = "deadline_ts")
    private Long deadlineTs = (long) -1;

    public Chapter() {
    }

    public Chapter(String name, Group group) {
        this.chapterName = name;
        this.groupHost = group;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Chapter.class) {
            Chapter tmp = (Chapter) obj;
            return (Objects.equals(tmp.getChapterId(), getChapterId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getChapterId());
    }
}
