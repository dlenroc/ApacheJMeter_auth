package org.apache.jmeter.protocol.http.auth.gui.control;

import org.apache.jmeter.protocol.http.auth.sampler.AuthSampler;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Base64;

public class BasicAuthConfigPanel extends JPanel {
    private JTextField username;
    private JTextField password;

    public BasicAuthConfigPanel() {
        init();
    }

    /**
     * Do a sampling and return its results.
     *
     * @param sampler
     */
    public static void sample(AuthSampler sampler) {
        String username = sampler.getPropertyAsString("BasicAuth.username");
        String password = sampler.getPropertyAsString("BasicAuth.password");
        String user = username + ":" + password;

        sampler.setAuthorizationHeader("Basic " + Base64.getEncoder().encodeToString(user.getBytes()));
    }

    /**
     * Initialize the GUI components and layout.
     */
    private void init() {
        this.setBorder(new TitledBorder(null, "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagLayout gbl_basicAuth = new GridBagLayout();
        gbl_basicAuth.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_basicAuth.rowHeights = new int[]{0, 0, 0};
        gbl_basicAuth.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_basicAuth.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_basicAuth);

        JLabel basicAuthUsernameLabel = new JLabel("Username");
        GridBagConstraints gbc_basicAuthUsernameLabel = new GridBagConstraints();
        gbc_basicAuthUsernameLabel.anchor = GridBagConstraints.EAST;
        gbc_basicAuthUsernameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_basicAuthUsernameLabel.gridx = 1;
        gbc_basicAuthUsernameLabel.gridy = 0;
        this.add(basicAuthUsernameLabel, gbc_basicAuthUsernameLabel);

        username = new JTextField();
        basicAuthUsernameLabel.setLabelFor(username);
        username.setColumns(10);
        GridBagConstraints gbc_basicAuthUsername = new GridBagConstraints();
        gbc_basicAuthUsername.fill = GridBagConstraints.HORIZONTAL;
        gbc_basicAuthUsername.insets = new Insets(0, 0, 5, 5);
        gbc_basicAuthUsername.gridx = 2;
        gbc_basicAuthUsername.gridy = 0;
        this.add(username, gbc_basicAuthUsername);

        JLabel basicAuthPasswordLabel = new JLabel("Password");
        GridBagConstraints gbc_basicAuthPasswordLabel = new GridBagConstraints();
        gbc_basicAuthPasswordLabel.anchor = GridBagConstraints.EAST;
        gbc_basicAuthPasswordLabel.insets = new Insets(0, 0, 0, 5);
        gbc_basicAuthPasswordLabel.gridx = 1;
        gbc_basicAuthPasswordLabel.gridy = 1;
        this.add(basicAuthPasswordLabel, gbc_basicAuthPasswordLabel);

        password = new JTextField();
        basicAuthPasswordLabel.setLabelFor(password);
        password.setColumns(10);
        GridBagConstraints gbc_basicAuthPassword = new GridBagConstraints();
        gbc_basicAuthPassword.insets = new Insets(0, 0, 0, 5);
        gbc_basicAuthPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_basicAuthPassword.gridx = 2;
        gbc_basicAuthPassword.gridy = 1;
        this.add(password, gbc_basicAuthPassword);
    }

    /**
     * Clear the TestElement of all data.
     */
    public void clear() {
        username.setText("");
        password.setText("");
    }

    /**
     * Modifies a given TestElement to mirror the data in the org.apache.jmeter.protocol.http.gui components.
     *
     * @param el
     */
    public void configureTestElement(TestElement el) {
        el.setProperty("BasicAuth.username", username.getText(), "");
        el.setProperty("BasicAuth.password", password.getText(), "");
    }

    /**
     * Configures GUI from el
     *
     * @param el
     */
    public void configure(TestElement el) {
        username.setText(el.getPropertyAsString("BasicAuth.username", ""));
        password.setText(el.getPropertyAsString("BasicAuth.password", ""));
    }
}
