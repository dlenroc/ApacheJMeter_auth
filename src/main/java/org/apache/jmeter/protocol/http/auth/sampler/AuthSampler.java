package org.apache.jmeter.protocol.http.auth.sampler;

import org.apache.jmeter.protocol.http.auth.gui.control.BasicAuthConfigPanel;
import org.apache.jmeter.protocol.http.auth.gui.control.BearerTokenConfigPanel;
import org.apache.jmeter.protocol.http.auth.gui.control.OAuth1ConfigPanel;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.AuthSamplerToHTTPSamplerProxy;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;

import java.net.MalformedURLException;
import java.net.URL;

public class AuthSampler extends AuthSamplerToHTTPSamplerProxy {
    private URL url;

    /**
     * Change sampler url
     *
     * @param url URL
     */
    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set authorization header
     *
     * @param string Authorization header
     */
    public void setAuthorizationHeader(String string) {
        // Get header manager
        HeaderManager headerManager;
        try {
            headerManager = (HeaderManager) getHeaderManager().clone();
        } catch (Exception ignore) {
            headerManager = new HeaderManager();
        }

        // Remove existing authentication header
        headerManager.removeHeaderNamed(HEADER_AUTHORIZATION);

        // Add authentication header to sampler
        Header header = new Header();
        header.setName(HEADER_AUTHORIZATION);
        header.setValue(string);
        headerManager.add(header);

        // Set header manager with added authentication header
        setHeaderManager(headerManager);
    }

    /**
     * Do a sampling and return its results.
     *
     * @param url                  URL
     * @param method               Request method
     * @param areFollowingRedirect Following redirection
     * @param depth                Depth
     * @return HTTPSampleResult
     */
    @Override
    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int depth) {
        this.url = url;

        switch (getPropertyAsString("Authentication.type", "No Auth")) {
            case "Bearer Token": // Bearer Token
                BearerTokenConfigPanel.sample(this);
                break;
            case "Basic Auth": // Basic Authentication
                BasicAuthConfigPanel.sample(this);
                break;
            case "OAuth 1.0": // OAuth 1.0
                OAuth1ConfigPanel.sample(this);
                break;
        }

        return super.sample(this.url, method, areFollowingRedirect, depth);
    }
}
