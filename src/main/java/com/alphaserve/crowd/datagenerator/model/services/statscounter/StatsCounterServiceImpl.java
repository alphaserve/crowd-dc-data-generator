package com.alphaserve.crowd.datagenerator.model.services.statscounter;

import com.alphaserve.crowd.datagenerator.model.services.groupsearch.GroupSearchService;
import com.alphaserve.crowd.datagenerator.model.services.usersearch.UserSearchService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StatsCounterServiceImpl implements StatsCounterService {
    private final UserSearchService userSearchService;
    private final GroupSearchService groupSearchService;

    @Inject
    public StatsCounterServiceImpl(UserSearchService userSearchService,
                                GroupSearchService groupSearchService) {
        this.userSearchService = userSearchService;
        this.groupSearchService = groupSearchService;
    }

    @Override
    public int getUsersCount() {
        return userSearchService.findUsers().size(); //TODO optimize
    }

    @Override
    public int getGroupsCount() {
        return groupSearchService.findAllGroups().size();
    }
}
