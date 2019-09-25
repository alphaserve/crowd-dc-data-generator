package com.alphaserve.crowd.datagenerator.controller.servlets;

import com.alphaserve.crowd.datagenerator.controller.authenticator.AuthHelper;
import com.alphaserve.crowd.datagenerator.controller.retriever.ParameterRetriever;
import com.alphaserve.crowd.datagenerator.model.services.generator.Generator;
import com.alphaserve.crowd.datagenerator.model.services.statscounter.StatsCounterService;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class DataGenerateServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DataGenerateServlet.class);

    private final TemplateRenderer templateRenderer;
    private final AuthHelper authHelper;
    private final ParameterRetriever parameterRetriever;
    private final Generator generator;

    public DataGenerateServlet(TemplateRenderer templateRenderer,
                               AuthHelper authHelper,
                               ParameterRetriever parameterRetriever,
                               Generator generator) {
        this.templateRenderer = templateRenderer;
        this.authHelper = authHelper;
        this.parameterRetriever = parameterRetriever;
        this.generator = generator;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!authHelper.remoteUserIsAuthorized()) {
            resp.sendRedirect(req.getContextPath());
            return;
        }

        resp.setContentType("text/html");
        Map context = ImmutableMap.builder()
                .put("generationInProgress", generator.generationInProgress())
                .build();
        templateRenderer.render("templates/generate-data.vm", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!authHelper.remoteUserIsAuthorized()) {
            resp.sendRedirect(req.getContextPath());
            return;
        }

        if(generator.generationInProgress()) {
            resp.sendRedirect(req.getContextPath()+req.getServletPath());
        }

        Map<String, String> formParamsMap = parameterRetriever.retrieveParameters(req);

        generator.startGeneration(formParamsMap);

        //TODO replace with redirect to generation progress page
        resp.sendRedirect(req.getContextPath()+req.getServletPath());
    }
}