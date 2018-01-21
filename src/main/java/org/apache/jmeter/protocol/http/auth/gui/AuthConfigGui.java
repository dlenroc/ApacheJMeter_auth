package org.apache.jmeter.protocol.http.auth.gui;

import org.apache.jmeter.protocol.http.auth.gui.control.BasicAuthConfigPanel;
import org.apache.jmeter.protocol.http.auth.gui.control.BearerTokenConfigPanel;
import org.apache.jmeter.protocol.http.auth.gui.control.NoAuthConfigPanel;
import org.apache.jmeter.protocol.http.auth.gui.control.OAuth1ConfigPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AuthConfigGui extends JPanel {
    private final CardLayout card = new CardLayout(0, 0);
    private final JComboBox<String> authType = new JComboBox<String>();

    private NoAuthConfigPanel noAuthConfigPanel = new NoAuthConfigPanel();
    private BearerTokenConfigPanel bearerTokenConfigPanel = new BearerTokenConfigPanel();
    private BasicAuthConfigPanel basicAuthConfigPanel = new BasicAuthConfigPanel();
    private OAuth1ConfigPanel oauth1ConfigPanel = new OAuth1ConfigPanel();

    private String[] authenticationTypes = {
            "No Auth",
            "Bearer Token",
            "Basic Auth",
            "OAuth 1.0"
    };

    /**
     * Create GUI and load authentication type panels
     */
    public AuthConfigGui() {
        JPanel authProperties = init();

        // Add Panels
        authProperties.add(noAuthConfigPanel, "No Auth");
        authProperties.add(bearerTokenConfigPanel, "Bearer Token");
        authProperties.add(basicAuthConfigPanel, "Basic Auth");
        authProperties.add(oauth1ConfigPanel, "OAuth 1.0");

        // Authentication Type listener
        authType.addItemListener(event -> card.show(authProperties, event.getItem().toString()));
    }

    /**
     * Draw ComboBox to choose authentication type
     *
     * @return JPanel
     */
    private JPanel init() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        JPanel authPanel = new JPanel();
        authPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_authPanel = new GridBagConstraints();
        gbc_authPanel.insets = new Insets(0, 0, 5, 0);
        gbc_authPanel.fill = GridBagConstraints.BOTH;
        gbc_authPanel.gridx = 0;
        gbc_authPanel.gridy = 0;
        this.add(authPanel, gbc_authPanel);
        GridBagLayout gbl_authPanel = new GridBagLayout();
        gbl_authPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_authPanel.rowHeights = new int[]{0, 0};
        gbl_authPanel.columnWeights = new double[]{1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_authPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        authPanel.setLayout(gbl_authPanel);

        JLabel authTypeLabel = new JLabel("Authorization Type:");
        GridBagConstraints gbc_authTypeLabel = new GridBagConstraints();
        gbc_authTypeLabel.insets = new Insets(0, 0, 0, 5);
        gbc_authTypeLabel.anchor = GridBagConstraints.EAST;
        gbc_authTypeLabel.gridx = 1;
        gbc_authTypeLabel.gridy = 0;
        authPanel.add(authTypeLabel, gbc_authTypeLabel);

        authTypeLabel.setLabelFor(authType);
        authType.setModel(new DefaultComboBoxModel<>(authenticationTypes));
        GridBagConstraints gbc_authType = new GridBagConstraints();
        gbc_authType.insets = new Insets(0, 0, 0, 5);
        gbc_authType.fill = GridBagConstraints.HORIZONTAL;
        gbc_authType.gridx = 2;
        gbc_authType.gridy = 0;
        authPanel.add(authType, gbc_authType);

        JPanel authProperties = new JPanel();
        GridBagConstraints gbc_authProperties = new GridBagConstraints();
        gbc_authProperties.fill = GridBagConstraints.BOTH;
        gbc_authProperties.gridx = 0;
        gbc_authProperties.gridy = 1;
        this.add(authProperties, gbc_authProperties);
        authProperties.setLayout(card);

        return authProperties;
    }

    /**
     * Clear all fields
     */
    public void clear() {
        authType.setSelectedIndex(0);
        bearerTokenConfigPanel.clear();
        basicAuthConfigPanel.clear();
        oauth1ConfigPanel.clear();
    }

    /**
     * Save all fields
     */
    public void configureTestElement(TestElement sampler) {
        sampler.setProperty("Authentication.type", authType.getSelectedItem().toString(), authenticationTypes[0]);
        bearerTokenConfigPanel.configureTestElement(sampler);
        basicAuthConfigPanel.configureTestElement(sampler);
        oauth1ConfigPanel.configureTestElement(sampler);
    }

    /**
     * Restore GUI fields value
     */
    public void configure(TestElement el) {
        authType.setSelectedItem(el.getPropertyAsString("Authentication.type", authenticationTypes[0]));
        bearerTokenConfigPanel.configure(el);
        basicAuthConfigPanel.configure(el);
        oauth1ConfigPanel.configure(el);
    }
}
