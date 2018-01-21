package org.apache.jmeter.protocol.http.auth.sampler;

import org.apache.jmeter.protocol.http.auth.gui.AuthConfigGui;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;

public class AuthSamplerGui extends HttpTestSampleGui {
    private final AuthConfigGui authTypes = new AuthConfigGui();

    /**
     * Create GUI (Default HTTP Sampler + Authentication panel)
     */
    public AuthSamplerGui() {
        super();
        ((JTabbedPane) ((JSplitPane) getComponent(0)).getRightComponent()).add("Authentication", authTypes);
    }

    public String getStaticLabel() {
        return "Auth Request";
    }

    /**
     * Create and configure test element
     */
    @Override
    public TestElement createTestElement() {
        HTTPSamplerBase sampler = new AuthSampler();
        this.modifyTestElement(sampler);
        return sampler;
    }

    /**
     * Clear all fields
     */
    @Override
    public void clearGui() {
        super.clearGui();
        authTypes.clear();
    }

    /**
     * Save all fields
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        authTypes.configure(element);
    }

    /**
     * Restore GUI fields value
     */
    @Override
    public void modifyTestElement(TestElement sampler) {
        super.modifyTestElement(sampler);
        authTypes.configureTestElement(sampler);
        sampler.setProperty(TestElement.GUI_CLASS, this.getClass().getName());
        sampler.setProperty(TestElement.TEST_CLASS, sampler.getClass().getName());

        // Hide empty http sampler parameters
        if (sampler.getPropertyAsString("HTTPSampler.domain").equals(""))
            sampler.removeProperty("HTTPSampler.domain");

        if (sampler.getPropertyAsString("HTTPSampler.port").equals(""))
            sampler.removeProperty("HTTPSampler.port");

        if (sampler.getPropertyAsString("HTTPSampler.protocol").equals(""))
            sampler.removeProperty("HTTPSampler.protocol");

        if (sampler.getPropertyAsString("HTTPSampler.contentEncoding").equals(""))
            sampler.removeProperty("HTTPSampler.contentEncoding");

        if (sampler.getPropertyAsString("HTTPSampler.path").equals(""))
            sampler.removeProperty("HTTPSampler.path");

        if (sampler.getPropertyAsString("HTTPSampler.embedded_url_re").equals(""))
            sampler.removeProperty("HTTPSampler.embedded_url_re");

        if (sampler.getPropertyAsString("HTTPSampler.connect_timeout").equals(""))
            sampler.removeProperty("HTTPSampler.connect_timeout");

        if (sampler.getPropertyAsString("HTTPSampler.response_timeout").equals(""))
            sampler.removeProperty("HTTPSampler.response_timeout");

        if (sampler.getPropertyAsString("HTTPSampler.method").equals(""))
            sampler.removeProperty("HTTPSampler.method");
    }
}
