package com.alphaserve.crowd.datagenerator.model.services.generator;

import java.util.Map;

public interface Generator {
    void startGeneration(Map<String, String> formParamsMap);

    boolean generationInProgress();
}