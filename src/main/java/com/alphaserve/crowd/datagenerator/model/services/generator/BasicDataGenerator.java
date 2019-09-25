package com.alphaserve.crowd.datagenerator.model.services.generator;

import com.alphaserve.crowd.datagenerator.controller.retriever.GenerateDataFormParameterKeys;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.google.common.base.Stopwatch;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
public class BasicDataGenerator implements Generator {
    private boolean generationIsInProgress;

    private CrowdService crowdService;
    private CrowdDirectoryService crowdDirectoryService;
    private DirectoryManager directoryManager;

    private static final Logger log = LoggerFactory.getLogger(BasicDataGenerator.class);

    private final int USERS_BATCH_SIZE = 1000;
    private final int GROUPS_BATCH_SIZE = 10000;

    @Inject
    public BasicDataGenerator(CrowdService crowdService,
                              CrowdDirectoryService crowdDirectoryService,
                              DirectoryManager directoryManager) {
        this.crowdService = crowdService;
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryManager = directoryManager;
    }

    @Override
    public void startGeneration(Map<String, String> formParamsMap) {
        String usersNumStr = formParamsMap.get(GenerateDataFormParameterKeys.USERS_GEN_NUM.toString());
        String groupsNumStr = formParamsMap.get(GenerateDataFormParameterKeys.GROUPS_GEN_NUM.toString());

        int numberOfUsers = Integer.parseInt(usersNumStr);
        int numberOfGroups = Integer.parseInt(groupsNumStr);

        Directory directory = crowdDirectoryService.findAllDirectories().stream().findFirst().get();
        Long dirId = directory.getId();

        try {
            generationIsInProgress=true;
            log.info("GENERATING USERS");
            generateUsers(numberOfUsers, dirId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try{
            generationIsInProgress=true;
            log.info("GENERATING GROUPS");
            generateGroups(numberOfGroups, dirId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        generationIsInProgress = false;
    }

    private void generateUsers(int numberOfUsersToGenerate, Long dirId) throws Exception {
        final PasswordCredential passCred = new PasswordCredential("password");

        int numberOfBatches = numberOfUsersToGenerate/USERS_BATCH_SIZE;
        int leftoverUsers = numberOfUsersToGenerate%USERS_BATCH_SIZE;

        List<Callable<Void>> tasks = Stream.generate(() -> getCallableToCreateBatchOfUsers(USERS_BATCH_SIZE, dirId, passCred))
                .limit(numberOfBatches)
                .collect(Collectors.toList());

        if(leftoverUsers!=0) { tasks.add(getCallableToCreateBatchOfUsers(leftoverUsers, dirId, passCred)); }

        try {
            ExecutorService executor=Executors.newFixedThreadPool(4);
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private Callable<Void> getCallableToCreateBatchOfUsers(int numberOfUsersToGenerate, Long dirId, PasswordCredential passCred) {
        return () -> {
            Stopwatch timer1 = Stopwatch.createStarted();
            List<UserTemplateWithCredentialAndAttributes> list =
                    Stream.generate(getUsersSupplier(dirId, passCred))
                            .limit(numberOfUsersToGenerate)
                            .collect(Collectors.toList());

            try {
                boolean shouldOverwrite = true;
                directoryManager.addAllUsers(dirId, list, shouldOverwrite);
            } catch (DirectoryPermissionException | DirectoryNotFoundException | OperationFailedException e) {
                e.printStackTrace();
            }

            log.info("TIME ELAPSED WHILE GENERATING USERS BATCH: "+timer1.elapsed(TimeUnit.SECONDS));

            return null;
        };
    }

    Supplier<UserTemplateWithCredentialAndAttributes> getUsersSupplier(Long dirId, PasswordCredential passCred) {
        return () -> new UserTemplateWithCredentialAndAttributes(RandomStringUtils.randomAlphanumeric(13), dirId, passCred);
    }

    /*________________________________________________________________________________________________________________*/

    private void generateGroups(int numberOfGroupsToGenerate, Long dirId) {
        int numberOfBatches = numberOfGroupsToGenerate/GROUPS_BATCH_SIZE;
        int leftoverGroups = numberOfGroupsToGenerate%GROUPS_BATCH_SIZE;

        List<Callable<Void>> tasks = Stream.generate(()->getCallableToCreateBatchOfGroups(GROUPS_BATCH_SIZE, dirId))
                .limit(numberOfBatches)
                .collect(Collectors.toList());
        if(leftoverGroups!=0) {tasks.add(getCallableToCreateBatchOfGroups(leftoverGroups, dirId));}

        try {
            ExecutorService executor=Executors.newFixedThreadPool(2);
            executor.invokeAll(tasks);
            executor.shutdown();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private Callable<Void> getCallableToCreateBatchOfGroups(int numberOfGroupsToGenerate, Long dirId) {
        return () -> {
            Stopwatch timer1 = Stopwatch.createStarted();
            List<GroupTemplate> list =
                    Stream.generate(getGroupsSupplier(dirId))
                            .limit(numberOfGroupsToGenerate)
                            .collect(Collectors.toList());

            try {
                boolean shouldOverwrite = true;
                directoryManager.addAllGroups(dirId, list, shouldOverwrite);
            } catch (DirectoryPermissionException | DirectoryNotFoundException | OperationFailedException | InvalidGroupException e) {
                e.printStackTrace();
            }

            log.info("TIME ELAPSED WHILE GENERATING GROUPS BATCH: "+timer1.elapsed(TimeUnit.SECONDS));

            return null;
        };
    }

    private Supplier<GroupTemplateWithAttributes> getGroupsSupplier(Long dirId) {
        return () -> new GroupTemplateWithAttributes(RandomStringUtils.randomAlphanumeric(42), dirId, GroupType.GROUP);
    }

    @Override
    public boolean generationInProgress() {
        return generationIsInProgress;
    }

}
