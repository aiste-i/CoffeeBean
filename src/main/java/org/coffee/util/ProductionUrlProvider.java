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

        // Get the protocol (http or https)
        String protocol = request.getScheme();

        // Get the server name (domain)
        String serverName = request.getServerName();

        // Get the server port (usually 80 for HTTP or 443 for HTTPS)
        int serverPort = request.getServerPort();

        // Construct the base URL
        String port = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;

        return protocol + "://" + serverName + port;
    }
}
