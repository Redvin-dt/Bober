package ru.hse.database.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Questions")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(value = AccessType.FIELD)
    @Setter(AccessLevel.NONE)
    private long questionId;

    @Column(name = "question")
    private String question;

    @ElementCollection
    @CollectionTable(name="right_answers",
            joinColumns = @JoinColumn(name = "right_answer_id"))
    @Column(name = "right_answer")
    public List<Long> rightAnswers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_host")
    private Test testHost;

    @ElementCollection
    @CollectionTable(name="answers",
            joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "answer")
    public List<String> answers = new ArrayList<>();

    public Question(String question, List<Long> rightAnswers, List<String> answers, Test testHost) {
        this.question = question;
        this.answers = answers;
        this.rightAnswers = rightAnswers;
        this.testHost = testHost;
    }

    public Question() {
    }
}