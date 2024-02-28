package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table (name = "Chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    private long chapterId;
    @Getter
    @Setter
    @Column(name = "name")
    private String chapterName;
    @Getter
    @Setter
    @Column(name = "text")
    private String textOfChapter;

    @Getter
    @Setter
    @Column(name = "test_data")
    private String testData;

    @Setter
    @Getter
    @Column(name = "meta_info")
    private String metaInfo;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_host")
    private Group groupHost;

    public Chapter() {
    }

    public Chapter(String name, Group group) {
        this.chapterName = name;
        this.groupHost = group;
    }

    public Long getChapterId() {
        return this.chapterId;
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
