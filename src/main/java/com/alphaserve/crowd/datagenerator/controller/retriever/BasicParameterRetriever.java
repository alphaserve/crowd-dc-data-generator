package com.alphaserve.crowd.datagenerator.controller.retriever;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Named
public class BasicParameterRetriever implements ParameterRetriever {

    @Override
    public Map<String, String> retrieveParameters(HttpServletRequest req) {
        Map<String, String> resultMap = new HashMap<>();

        for(GenerateDataFormParameterKeys key : GenerateDataFormParameterKeys.values()) {
            String parameter = req.getParameter(key.toString());
            resultMap.put(key.toString(), parameter);
        }

        return resultMap;
    }
}
