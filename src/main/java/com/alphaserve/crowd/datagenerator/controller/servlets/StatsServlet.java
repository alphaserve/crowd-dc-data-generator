package com.alphaserve.crowd.datagenerator.controller.servlets;

import com.alphaserve.crowd.datagenerator.model.services.statscounter.StatsCounterService;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class StatsServlet extends HttpServlet {
    private final TemplateRenderer templateRenderer;
    private final StatsCounterService statsCounterService;


    public StatsServlet(TemplateRenderer templateRenderer,
                        StatsCounterService statsCounterService) {
        this.templateRenderer = templateRenderer;
        this.statsCounterService = statsCounterService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map context = ImmutableMap.builder()
                .put("usersCount", statsCounterService.getUsersCount())
                .put("groupsCount", statsCounterService.getGroupsCount())
                .build();

        resp.setContentType("text/html");
        templateRenderer.render("templates/stats.vm", context, resp.getWriter());
    }
}
