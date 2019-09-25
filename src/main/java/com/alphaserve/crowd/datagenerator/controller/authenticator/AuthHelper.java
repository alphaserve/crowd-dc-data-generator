package com.alphaserve.crowd.datagenerator.controller.authenticator;

import com.atlassian.sal.api.user.UserManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

@Named
public class AuthHelper {
    private final UserManager userManager;

    @Inject
    public AuthHelper(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean remoteUserIsAuthorized() {
        return Objects.nonNull(userManager.getRemoteUser());
    }

}
