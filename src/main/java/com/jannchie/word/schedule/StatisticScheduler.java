package com.jannchie.word.schedule;

import com.jannchie.word.model.ReciteRecord;
import com.jannchie.word.model.User;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * @author Jannchie
 */
@Component
public class StatisticScheduler {
    private MongoTemplate mongoTemplate;

    @Autowired
    StatisticScheduler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    public void recordStatisticUserRecite() {
        Query q = new Query();
        q.fields().include("username");
        List<User> userList = mongoTemplate.find(q, User.class);
        for (User user : userList) {
            User.ReciteStatistic urs = new User.ReciteStatistic();
            mongoTemplate.aggregate(
                    Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("username").is(user.getUsername())),
                            Aggregation.bucket("skillExp").withBoundaries(0, 100, Integer.MAX_VALUE)
                    ), ReciteRecord.class, Document.class
            ).forEach((e) -> {
                Integer id = (Integer) e.get("_id");
                Integer count = (Integer) e.get("count");
                if (id == 100) {
                    urs.setMastered(count);
                } else if (id == 0) {
                    urs.setReciting(count);
                }
            });
            boolean needRecord =
                    user.getReciteStatistics() == null || user.getReciteStatistics().isEmpty()
                            || (!user.getReciteStatistics().get(0).getMastered().equals(urs.getMastered())
                            && !user.getReciteStatistics().get(0).getReciting().equals(urs.getReciting()));
            if (needRecord) {
                urs.setUpdateDate(Calendar.getInstance().getTime());
                mongoTemplate.updateFirst(
                        Query.query(Criteria.where("username").is(user.getUsername())),
                        new Update().push("reciteStatistics").atPosition(0).value(urs),
                        User.class);
            }
        }
    }
}
