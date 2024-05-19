package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tests")
@Getter
@Setter
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long testId;

    @Column(name = "name")
    private String testName;

    @Column(name = "position")
    private long position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_host")
    private Chapter chapterHost;

    @OneToMany(mappedBy = "testHost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Test(String name, Long position, Chapter chapter) {
        this.testName = name;
        this.position = position;
        this.chapterHost = chapter;
    }

    public Test() {
    }
}
