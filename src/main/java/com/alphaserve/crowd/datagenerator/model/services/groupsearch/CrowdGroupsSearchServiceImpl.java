package com.alphaserve.crowd.datagenerator.model.services.groupsearch;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class CrowdGroupsSearchServiceImpl implements GroupSearchService {
    private final CrowdDirectoryService crowdDirectoryService;
    private final DirectoryManager directoryManager;

    private final static Logger log = LoggerFactory.getLogger(CrowdGroupsSearchServiceImpl.class);

    @Inject
    public CrowdGroupsSearchServiceImpl(CrowdDirectoryService crowdDirectoryService,
                                        DirectoryManager directoryManager) {
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryManager = directoryManager;
    }

    @Override
    public List<Group> findAllGroups() {
        EntityQuery<Group> query = getAllGroupsQuery();
        List<Directory> directories = getDirectories();

        return directories.stream()
                .map(directory -> {
                    try {
                        return directoryManager.searchGroups(directory.getId(), query);
                    } catch (DirectoryNotFoundException | OperationFailedException e) {
                        e.printStackTrace();
                        return Collections.EMPTY_LIST;
                    }
                }).flatMap(Collection<Group>::stream)
                .collect(Collectors.toList());
    }

    private List<Directory> getDirectories() {
        return crowdDirectoryService.findAllDirectories();
    }

    private EntityQuery<Group> getAllGroupsQuery() {
        return QueryBuilder.queryFor(Group.class, EntityDescriptor.group()).returningAtMost(-1);
    }
}
