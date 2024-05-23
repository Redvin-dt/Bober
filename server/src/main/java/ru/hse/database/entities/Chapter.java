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

    @Column(name = "text")
    private String textOfChapter = "";

    @Column(name = "test_data")
    private String testData = "";

    @Column(name = "meta_info")
    private String metaInfo = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_host")
    private Group groupHost;

    @OneToMany(mappedBy = "chapterHost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();

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
