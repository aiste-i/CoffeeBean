package org.coffee.util;

import org.coffee.annotations.Production;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@Production
@RequestScoped
public final class ProductionUrlProvider implements UrlProvider {

    public String getBaseUrl() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();


        String protocol = request.getScheme();


        String serverName = request.getServerName();


        int serverPort = request.getServerPort();


        String port = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;

        return protocol + "://" + serverName + port;
    }
}
