package com.porter.collector.controller;

import com.porter.collector.db.GoalDao;
import com.porter.collector.model.*;

import javax.ws.rs.PathParam;
import java.util.List;

public class GoalsController {

    private final GoalDao goalDao;

    public GoalsController(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    public List<Goal> getAllFromUser(SimpleUser user) {
        return goalDao.findAllFromUser(user.id());
    }

    public Goal getById(SimpleUser requesting, @PathParam("id") Long id) throws IllegalAccessException {
        Goal goal = goalDao.findById(id);
        if (goal.userId() != requesting.id()) {
            throw new IllegalAccessException();
        }
        return goal;
    }
}
