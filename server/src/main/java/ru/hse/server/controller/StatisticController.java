package ru.hse.server.controller;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.server.exception.AccessException;
import ru.hse.server.service.StatisticService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistic")
public class StatisticController {
    private final static int NONE_AUTHORIZED_STATUS = 401;

    private static final Logger logger = LoggerFactory.getLogger(StatisticController.class);

    private final StatisticService statisticService;

    @GetMapping(value = "/test/group", produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity getTestByGroupStatistic(@RequestParam Long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        try {
            var statistic = statisticService.getGroupTestPercent(userId, authToken);
            return ResponseEntity.ok(statistic);
        } catch (AccessException accessException) {
            logger.error("incorrect token or user", accessException);
            return ResponseEntity.status(NONE_AUTHORIZED_STATUS).body("token and userId mismatch");
        } catch (EntityExistsException entityExistsException) {
            logger.error("incorrect userId={}", userId, entityExistsException);
            return ResponseEntity.badRequest().body("incorrect userId");
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error");
        }
    }

    @GetMapping(value = "/test/date")
    public ResponseEntity getTestByDayStatistic(@RequestParam Long userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        try {
            var statistic = statisticService.getDayTestCount(userId, authToken);
            return ResponseEntity.ok(statistic);
        } catch (AccessException accessException) {
            logger.error("incorrect token or user", accessException);
            return ResponseEntity.status(NONE_AUTHORIZED_STATUS).body("token and userId mismatch");
        } catch (EntityExistsException entityExistsException) {
            logger.error("incorrect userId={}", userId, entityExistsException);
            return ResponseEntity.badRequest().body("incorrect userId");
        } catch (Exception e) {
            logger.error("unexpected error", e);
            return ResponseEntity.internalServerError().body("unexpected error");
        }
    }
}
