package org.coffee.util;

import org.coffee.annotations.Development;

import javax.enterprise.context.RequestScoped;

@Development
@RequestScoped
public final class DevUrlProvider implements UrlProvider{

    public String getBaseUrl() {
        return "http://localhost:8080/coffee-1.0-SNAPSHOT";
    }
}
