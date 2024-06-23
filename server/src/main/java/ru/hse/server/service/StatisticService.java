package ru.hse.server.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.hse.database.entities.Group;
import ru.hse.database.entities.PassedTest;
import ru.hse.database.entities.User;
import ru.hse.server.exception.AccessException;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.repository.GroupRepository;
import ru.hse.server.repository.UserRepository;
import ru.hse.server.utils.ProtoSerializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.hse.server.utils.ProtoSerializer.getProtoFromDateStatisticData;

@Service
public class StatisticService {

    private final AuthService authService;
    private final UserRepository userRepository;

    public StatisticService(@Qualifier("userDatabaseRepository") UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public EntitiesProto.UserStatistic getGroupTestPercent(Long userId, String authToken) throws AccessException, EntityNotFoundException {
        var user = getUserById(userId, authToken);

        Map<Long, Group> groupById = new HashMap<>();
        Map<String, TestAnswers> groupAnswers = new HashMap<>();

        for (var group : user.getGroupsUserSet()) {
            groupById.put(group.getGroupId(), group);
            groupAnswers.put(group.getGroupName(), new TestAnswers());
        }

        for (var passedTest : user.getPassedTests()) {

            var group = groupById.get(passedTest.getGroupId());

            if (!groupAnswers.containsKey(group.getGroupName())) {
                groupAnswers.put(group.getGroupName(), new TestAnswers());
            }

            groupAnswers.get(group.getGroupName()).addTestAnswers(passedTest);
        }

        return ProtoSerializer.getProtoFromStatisticData(groupAnswers);
    }

    public EntitiesProto.UserStatistic getDayTestCount(Long userId, String authToken) throws AccessException, EntityNotFoundException {
        var user = getUserById(userId, authToken);

        Map<String, Integer> testCountByDate = new HashMap<>();

        DateFormat currentFormat = SimpleDateFormat.getDateInstance();

        for (var passedTest : user.getPassedTests()) {

            Date date = passedTest.getTimestamp();

            String dateString = currentFormat.format(date);

            int testCount;
            if (!testCountByDate.containsKey(dateString)) {
                testCountByDate.put(dateString, 0);
                testCount = 0;
            } else {
                testCount = testCountByDate.get(dateString);
            }
            testCount++;
            testCountByDate.put(dateString, testCount);
        }

        return getProtoFromDateStatisticData(testCountByDate);
    }

    private User getUserById(Long userId, String authToken) throws AccessException {
        var user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("can not find user with that id"));

        if (!authService.checkAccessToken(user.getUserLogin(), authToken.substring("Bearer ".length()))){
            throw new AccessException("user and token mismatch");
        }

        return user;
    }

    @Getter
    public static class TestAnswers {
        private long rightAnswers = 0L;
        private long allAnswers = 0L;

        public void addTestAnswers(PassedTest test) {
            this.allAnswers += test.getQuestionsNumber();
            this.rightAnswers += test.getRightAnswers();
        }

        public int getTestPercent() {
            if (allAnswers == 0) {
                return 0;
            }

            float percent = ((float)rightAnswers) / allAnswers * 100;
            return (int) percent;
        }
    }
}
