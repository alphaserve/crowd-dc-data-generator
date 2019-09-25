package com.alphaserve.crowd.datagenerator.controller.retriever;

import javax.inject.Named;

/**
 * List of keys should be monitored in both back-end and templates to assure conformity
 */
public enum GenerateDataFormParameterKeys {
    USERS_GEN_NUM, GROUPS_GEN_NUM;

    @Override
    public String toString() {
        switch (this) {
            case USERS_GEN_NUM: return "users-gen-num";
            case GROUPS_GEN_NUM: return "groups-gen-num";
            default: throw new IllegalArgumentException();
        }
    }
}
