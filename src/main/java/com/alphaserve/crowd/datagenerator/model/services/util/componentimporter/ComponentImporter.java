package com.alphaserve.crowd.datagenerator.model.services.util.componentimporter;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.inject.Inject;

/**
 * This class only purpose is to mark dependencies for Atlassian Spring Scanner,
 * so they can be used elsewhere without need to add @Scanned and @ComponentImport there (e.g. in Servlets)
 */
@Scanned
public class ComponentImporter {
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final CrowdService crowdService;
    @ComponentImport
    private final CrowdDirectoryService crowdDirectoryService;
    @ComponentImport
    private final DirectoryManager directoryManager;

    @Inject
    public ComponentImporter(TemplateRenderer templateRenderer,
                             UserManager userManager,
                             CrowdService crowdService,
                             CrowdDirectoryService crowdDirectoryService,
                             DirectoryManager directoryManager) {
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.crowdService = crowdService;
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryManager = directoryManager;
    }
}
