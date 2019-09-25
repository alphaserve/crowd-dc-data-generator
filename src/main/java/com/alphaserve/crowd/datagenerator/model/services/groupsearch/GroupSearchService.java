package com.alphaserve.crowd.datagenerator.model.services.groupsearch;

import com.atlassian.crowd.model.group.Group;

import java.util.List;

public interface GroupSearchService {

    List<Group> findAllGroups();
}
