package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PassedTests")
@Getter
@Setter
public class PassedTest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long passedTestId;

    @Column(name = "test_name")
    private String testName;

    @Column(name = "chapter_name")
    private String chapterName;

    @Column(name = "test_id")
    private Long testId;

    @Column(name = "chapter_id")
    private Long chapterId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "right_answers")
    private Long rightAnswers;

    @Column(name = "question_number")
    private Long questionsNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_timestamp")
    private Timestamp timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_host")
    private User userHost;

    public PassedTest(String testName, String chapterName, Long testId, Long chapterId, Long groupId, User userHost,
                      Long rightAnswers, Long questionsNumber) {
        this.testName = testName;
        this.chapterName = chapterName;
        this.testId = testId;
        this.chapterId = chapterId;
        this.groupId = groupId;
        this.userHost = userHost;
        this.rightAnswers = rightAnswers;
        this.questionsNumber = questionsNumber;
    }

    public PassedTest() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Test tmp) {
            return (Objects.equals(tmp.getTestId(), getTestId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.toIntExact(getTestId());
    }
}
