package com.alphaserve.crowd.datagenerator.controller.retriever;


import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface ParameterRetriever {
    Map<String, String> retrieveParameters(HttpServletRequest req);
}

