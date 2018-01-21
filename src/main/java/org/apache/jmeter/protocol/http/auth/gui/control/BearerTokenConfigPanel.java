package org.apache.jmeter.protocol.http.auth.gui.control;

import org.apache.jmeter.protocol.http.auth.sampler.AuthSampler;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.lang.Double.MIN_VALUE;

public class BearerTokenConfigPanel extends JPanel {
    private JTextField token;

    public BearerTokenConfigPanel() {
        init();
    }

    /**
     * Do a sampling and return its results.
     *
     * @param sampler
     */
    public static void sample(AuthSampler sampler) {
        sampler.setAuthorizationHeader(sampler.getPropertyAsString("BearerToken.token"));
    }

    /**
     * Initialize the GUI components and layout.
     */
    private void init() {
        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl.rowHeights = new int[]{0, 0};
        gbl.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, MIN_VALUE};
        gbl.rowWeights = new double[]{0.0, MIN_VALUE};
        setLayout(gbl);

        JLabel label = new JLabel("Token");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(label, gbc);

        token = new JTextField();
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.fill = HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(token, gbc);
        token.setColumns(10);

        label.setLabelFor(token);
    }

    /**
     * Clear the TestElement of all data.
     */
    public void clear() {
        token.setText("");
    }

    /**
     * Modifies a given TestElement to mirror the data in the org.apache.jmeter.protocol.http.gui components.
     *
     * @param el
     */
    public void configureTestElement(TestElement el) {
        el.setProperty("BearerToken.token", token.getText(), "");
    }

    /**
     * Configures GUI from el
     *
     * @param el
     */
    public void configure(TestElement el) {
        token.setText(el.getPropertyAsString("BearerToken.token"));
    }
}
