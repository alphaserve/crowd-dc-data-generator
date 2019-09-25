package com.alphaserve.crowd.datagenerator.model.services.usersearch;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class CrowdUserSearchServiceImpl implements UserSearchService {
    private final CrowdDirectoryService crowdDirectoryService;
    private final DirectoryManager directoryManager;

    private final static Logger log = LoggerFactory.getLogger(CrowdUserSearchServiceImpl.class);

    @Inject
    public CrowdUserSearchServiceImpl(CrowdDirectoryService crowdDirectoryService,
                                      DirectoryManager directoryManager) {
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryManager = directoryManager;
    }

    @Override
    public List<User> findUsers(String nameOrEmail) {
        EntityQuery<User> query = StringUtils.isEmpty(nameOrEmail) || StringUtils.isBlank(nameOrEmail) ?
                getQueryToFindAllUsers() :
                getQueryToFindUsersByNameOrEmail(nameOrEmail);
        List<Directory> directories = getDirectories();

        return directories.stream()
                .map(directory -> {
                    try {
                        return directoryManager.searchUsers(directory.getId(), query);
                    } catch (DirectoryNotFoundException | OperationFailedException e) {
                        e.printStackTrace();
                        return new LinkedList<User>();
                    }
                }).flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsers() {
        return findUsers(StringUtils.EMPTY);
    }

    private List<Directory> getDirectories() {
        return crowdDirectoryService.findAllDirectories();
    }

    private EntityQuery<User> getQueryToFindAllUsers() {
        return QueryBuilder.queryFor(User.class, EntityDescriptor.user()).returningAtMost(-1);
    }

    private EntityQuery<User> getQueryToFindUsersByNameOrEmail(String nameOrEmail) {
        return QueryBuilder
                .queryFor(User.class, EntityDescriptor.user())
                .with(Combine.anyOf(
                        Restriction.on(UserTermKeys.USERNAME).startingWith(nameOrEmail),
                        Restriction.on(UserTermKeys.EMAIL).startingWith(nameOrEmail)
                ))
                .returningAtMost(100);
    }
}
