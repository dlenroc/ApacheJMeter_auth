package org.apache.jmeter.protocol.http.auth.gui.control;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import org.apache.jmeter.protocol.http.auth.sampler.AuthSampler;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.util.EncoderCache;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OAuth1ConfigPanel extends JPanel {
    private static String[] storeMethods = {
            "Headers",
            "URL"
    };
    private static String[] signatureMethods = {
            "HMAC-SHA1",
            "RSA-SHA1",
            "PLAINTEXT"
    };

    private JComboBox<String> signatureMethod;
    private JComboBox<String> storeMethod;
    private JTextField consumerKey;
    private JTextField consumerSecret;
    private JTextField token;
    private JTextField secret;
    private JTextField timestamp;
    private JTextField nonce;
    private JTextField version;
    private JTextField realm;

    public OAuth1ConfigPanel() {
        init();
    }

    /**
     * Do a sampling and return its results.
     *
     * @param sampler
     */
    synchronized public static void sample(AuthSampler sampler) {
        String storeTo = sampler.getPropertyAsString("OAuth1.store_to", storeMethods[0]);
        String signatureMethod = sampler.getPropertyAsString("OAuth1.signature_method", signatureMethods[0]);
        String consumerKey = sampler.getPropertyAsString("OAuth1.consumer_key", "");
        String consumerSecret = sampler.getPropertyAsString("OAuth1.consumer_secret", "");
        String token = sampler.getPropertyAsString("OAuth1.token", "");
        String secret = sampler.getPropertyAsString("OAuth1.secret", "");
        String timestamp = sampler.getPropertyAsString("OAuth1.timestamp", "");
        String nonce = sampler.getPropertyAsString("OAuth1.nonce", "");
        String version = sampler.getPropertyAsString("OAuth1.version", "");
        String realm = sampler.getPropertyAsString("OAuth1.realm");

        try {
            // Get OAuth parameters
            final OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, null);
            final OAuthAccessor accessor = new OAuthAccessor(consumer);

            if (!token.isEmpty())
                accessor.accessToken = token;

            if (!secret.isEmpty())
                accessor.tokenSecret = secret;

            // Create OAuthMessage
            OAuthMessage message = new OAuthMessage(sampler.getMethod(), sampler.getUrl().toExternalForm(), null);

            // Convert body/arguments to OAuth parameters
            if (sampler.getMethod().toUpperCase().matches("(POST|PUT|PATCH)"))
                if (sampler.getPostBodyRaw()) {
                    for (int i = sampler.getHeaderManager().getHeaders().size() - 1; i >= 0; --i) {
                        Header header = (Header) sampler.getHeaderManager().getHeaders().get(i).getObjectValue();
                        if (header != null && header.getName().equalsIgnoreCase("Content-Type")) {
                            if (header.getValue().toLowerCase().contains("x-www-form-urlencoded"))
                                message.addParameters(OAuth.decodeForm(sampler.getArguments().getArgument(0).getValue()));
                            break;
                        }
                    }
                } else {
                    for (JMeterProperty property : sampler.getArguments()) {
                        HTTPArgument arg = (HTTPArgument) property.getObjectValue();
                        String name = arg.getName();
                        String value = arg.getValue();
                        if (!arg.isAlwaysEncoded()) {
                            String urlContentEncoding = sampler.getContentEncoding();
                            if (urlContentEncoding == null || urlContentEncoding.length() == 0) {
                                urlContentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
                            }
                            name = URLDecoder.decode(name, urlContentEncoding);
                            value = URLDecoder.decode(value, urlContentEncoding);
                        }

                        message.addParameter(name, value);
                    }
                }

            message.addParameter(OAuth.OAUTH_SIGNATURE_METHOD, signatureMethod); // ex: HMAC-SHA1

            if (!timestamp.isEmpty())
                message.addParameter(OAuth.OAUTH_TIMESTAMP, timestamp);

            if (!nonce.isEmpty())
                message.addParameter(OAuth.OAUTH_NONCE, nonce);

            if (!version.isEmpty())
                message.addParameter(OAuth.OAUTH_VERSION, version);

            if (accessor.accessToken != null && accessor.accessToken.length() > 0)
                message.addParameter(OAuth.OAUTH_TOKEN, accessor.accessToken);

            // Sign the message
            message.addRequiredParameters(accessor);

            // Add Header/Url
            if (storeTo.equalsIgnoreCase(storeMethods[0])) {
                sampler.setAuthorizationHeader(message.getAuthorizationHeader(realm.isEmpty() ? null : realm));
            } else {
                List<Map.Entry<String, String>> oAuthParams = new ArrayList<>(message.getParameters());
                oAuthParams.removeIf(stringStringEntry -> !stringStringEntry.getKey().startsWith("oauth_"));

                sampler.setUrl(OAuth.addParameters(message.URL, oAuthParams));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the GUI components and layout.
     */
    private void init() {
        GridBagLayout gbl_oauth1Panel = new GridBagLayout();
        gbl_oauth1Panel.columnWidths = new int[]{0, 0};
        gbl_oauth1Panel.rowHeights = new int[]{0, 0, 0};
        gbl_oauth1Panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_oauth1Panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_oauth1Panel);

        JPanel oauth1BasicPanel = new JPanel();
        oauth1BasicPanel.setBorder(new TitledBorder(null, "Basic", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_oauth1BasicPanel = new GridBagConstraints();
        gbc_oauth1BasicPanel.anchor = GridBagConstraints.NORTH;
        gbc_oauth1BasicPanel.insets = new Insets(0, 0, 5, 0);
        gbc_oauth1BasicPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1BasicPanel.gridx = 0;
        gbc_oauth1BasicPanel.gridy = 0;
        this.add(oauth1BasicPanel, gbc_oauth1BasicPanel);
        GridBagLayout gbl_oauth1BasicPanel = new GridBagLayout();
        gbl_oauth1BasicPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_oauth1BasicPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_oauth1BasicPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_oauth1BasicPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        oauth1BasicPanel.setLayout(gbl_oauth1BasicPanel);

        JLabel oauth1StoreMethodLabel = new JLabel("Add authorization data to");
        GridBagConstraints gbc_oauth1StoreMethodLabel = new GridBagConstraints();
        gbc_oauth1StoreMethodLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1StoreMethodLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1StoreMethodLabel.gridx = 1;
        gbc_oauth1StoreMethodLabel.gridy = 0;
        oauth1BasicPanel.add(oauth1StoreMethodLabel, gbc_oauth1StoreMethodLabel);

        storeMethod = new JComboBox<>();
        oauth1StoreMethodLabel.setLabelFor(storeMethod);
        storeMethod.setModel(new DefaultComboBoxModel<>(storeMethods));
        GridBagConstraints gbc_oauth1StoreMethod = new GridBagConstraints();
        gbc_oauth1StoreMethod.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1StoreMethod.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1StoreMethod.gridx = 2;
        gbc_oauth1StoreMethod.gridy = 0;
        oauth1BasicPanel.add(storeMethod, gbc_oauth1StoreMethod);

        JLabel oauth1ConsumerKeyLabel = new JLabel("Consumer Key");
        GridBagConstraints gbc_oauth1ConsumerKeyLabel = new GridBagConstraints();
        gbc_oauth1ConsumerKeyLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1ConsumerKeyLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1ConsumerKeyLabel.gridx = 1;
        gbc_oauth1ConsumerKeyLabel.gridy = 1;
        oauth1BasicPanel.add(oauth1ConsumerKeyLabel, gbc_oauth1ConsumerKeyLabel);

        consumerKey = new JTextField();
        oauth1ConsumerKeyLabel.setLabelFor(consumerKey);
        GridBagConstraints gbc_oauth1ConsumerKey = new GridBagConstraints();
        gbc_oauth1ConsumerKey.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1ConsumerKey.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1ConsumerKey.gridx = 2;
        gbc_oauth1ConsumerKey.gridy = 1;
        oauth1BasicPanel.add(consumerKey, gbc_oauth1ConsumerKey);
        consumerKey.setColumns(10);

        JLabel oauth1ConsumerSecretLabel = new JLabel("Consumer Secret");
        GridBagConstraints gbc_oauth1ConsumerSecretLabel = new GridBagConstraints();
        gbc_oauth1ConsumerSecretLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1ConsumerSecretLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1ConsumerSecretLabel.gridx = 1;
        gbc_oauth1ConsumerSecretLabel.gridy = 2;
        oauth1BasicPanel.add(oauth1ConsumerSecretLabel, gbc_oauth1ConsumerSecretLabel);

        consumerSecret = new JTextField();
        oauth1ConsumerSecretLabel.setLabelFor(consumerSecret);
        GridBagConstraints gbc_oauth1ConsumerSecret = new GridBagConstraints();
        gbc_oauth1ConsumerSecret.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1ConsumerSecret.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1ConsumerSecret.gridx = 2;
        gbc_oauth1ConsumerSecret.gridy = 2;
        oauth1BasicPanel.add(consumerSecret, gbc_oauth1ConsumerSecret);
        consumerSecret.setColumns(10);

        JLabel oauth1TokenLabel = new JLabel("Access Token");
        GridBagConstraints gbc_oauth1TokenLabel = new GridBagConstraints();
        gbc_oauth1TokenLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1TokenLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1TokenLabel.gridx = 1;
        gbc_oauth1TokenLabel.gridy = 3;
        oauth1BasicPanel.add(oauth1TokenLabel, gbc_oauth1TokenLabel);

        token = new JTextField();
        oauth1TokenLabel.setLabelFor(token);
        GridBagConstraints gbc_oauth1Token = new GridBagConstraints();
        gbc_oauth1Token.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1Token.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Token.gridx = 2;
        gbc_oauth1Token.gridy = 3;
        oauth1BasicPanel.add(token, gbc_oauth1Token);
        token.setColumns(10);

        JLabel oauth1SecretLabel = new JLabel("Token Secret");
        GridBagConstraints gbc_oauth1SecretLabel = new GridBagConstraints();
        gbc_oauth1SecretLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1SecretLabel.insets = new Insets(0, 0, 0, 5);
        gbc_oauth1SecretLabel.gridx = 1;
        gbc_oauth1SecretLabel.gridy = 4;
        oauth1BasicPanel.add(oauth1SecretLabel, gbc_oauth1SecretLabel);

        secret = new JTextField();
        oauth1SecretLabel.setLabelFor(secret);
        GridBagConstraints gbc_oauth1Secret = new GridBagConstraints();
        gbc_oauth1Secret.insets = new Insets(0, 0, 0, 5);
        gbc_oauth1Secret.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Secret.gridx = 2;
        gbc_oauth1Secret.gridy = 4;
        oauth1BasicPanel.add(secret, gbc_oauth1Secret);
        secret.setColumns(10);

        JPanel oauth1AdvancedPanel = new JPanel();
        oauth1AdvancedPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Advanced (optional)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GridBagConstraints gbc_oauth1AdvancedPanel = new GridBagConstraints();
        gbc_oauth1AdvancedPanel.fill = GridBagConstraints.BOTH;
        gbc_oauth1AdvancedPanel.gridx = 0;
        gbc_oauth1AdvancedPanel.gridy = 1;
        this.add(oauth1AdvancedPanel, gbc_oauth1AdvancedPanel);
        GridBagLayout gbl_oauth1AdvancedPanel = new GridBagLayout();
        gbl_oauth1AdvancedPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
        gbl_oauth1AdvancedPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_oauth1AdvancedPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_oauth1AdvancedPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        oauth1AdvancedPanel.setLayout(gbl_oauth1AdvancedPanel);

        JLabel oauth1SignatureMethodLabel = new JLabel("Signature Method");
        GridBagConstraints gbc_oauth1SignatureMethodLabel = new GridBagConstraints();
        gbc_oauth1SignatureMethodLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1SignatureMethodLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1SignatureMethodLabel.gridx = 1;
        gbc_oauth1SignatureMethodLabel.gridy = 0;
        oauth1AdvancedPanel.add(oauth1SignatureMethodLabel, gbc_oauth1SignatureMethodLabel);

        signatureMethod = new JComboBox<>();
        oauth1SignatureMethodLabel.setLabelFor(signatureMethod);
        signatureMethod.setModel(new DefaultComboBoxModel<>(signatureMethods));
        GridBagConstraints gbc_oauth1SignatureMethod = new GridBagConstraints();
        gbc_oauth1SignatureMethod.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1SignatureMethod.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1SignatureMethod.gridx = 2;
        gbc_oauth1SignatureMethod.gridy = 0;
        oauth1AdvancedPanel.add(signatureMethod, gbc_oauth1SignatureMethod);

        JLabel oauth1TimestampLabel = new JLabel("Timestamp\n");
        GridBagConstraints gbc_oauth1TimestampLabel = new GridBagConstraints();
        gbc_oauth1TimestampLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1TimestampLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1TimestampLabel.gridx = 1;
        gbc_oauth1TimestampLabel.gridy = 1;
        oauth1AdvancedPanel.add(oauth1TimestampLabel, gbc_oauth1TimestampLabel);

        timestamp = new JTextField();
        oauth1TimestampLabel.setLabelFor(timestamp);
        GridBagConstraints gbc_oauth1Timestamp = new GridBagConstraints();
        gbc_oauth1Timestamp.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1Timestamp.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Timestamp.gridx = 2;
        gbc_oauth1Timestamp.gridy = 1;
        oauth1AdvancedPanel.add(timestamp, gbc_oauth1Timestamp);
        timestamp.setColumns(10);

        JLabel oauth1NonceLabel = new JLabel("Nonce");
        GridBagConstraints gbc_oauth1NonceLabel = new GridBagConstraints();
        gbc_oauth1NonceLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1NonceLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1NonceLabel.gridx = 1;
        gbc_oauth1NonceLabel.gridy = 2;
        oauth1AdvancedPanel.add(oauth1NonceLabel, gbc_oauth1NonceLabel);

        nonce = new JTextField();
        oauth1NonceLabel.setLabelFor(nonce);
        GridBagConstraints gbc_oauth1Nonce = new GridBagConstraints();
        gbc_oauth1Nonce.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1Nonce.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Nonce.gridx = 2;
        gbc_oauth1Nonce.gridy = 2;
        oauth1AdvancedPanel.add(nonce, gbc_oauth1Nonce);
        nonce.setColumns(10);

        JLabel oauth1VersionLabel = new JLabel("Version");
        GridBagConstraints gbc_oauth1VersionLabel = new GridBagConstraints();
        gbc_oauth1VersionLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1VersionLabel.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1VersionLabel.gridx = 1;
        gbc_oauth1VersionLabel.gridy = 3;
        oauth1AdvancedPanel.add(oauth1VersionLabel, gbc_oauth1VersionLabel);

        version = new JTextField();
        oauth1VersionLabel.setLabelFor(version);
        GridBagConstraints gbc_oauth1Version = new GridBagConstraints();
        gbc_oauth1Version.insets = new Insets(0, 0, 5, 5);
        gbc_oauth1Version.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Version.gridx = 2;
        gbc_oauth1Version.gridy = 3;
        oauth1AdvancedPanel.add(version, gbc_oauth1Version);
        version.setColumns(10);

        JLabel oauth1RealmLabel = new JLabel("Realm");
        GridBagConstraints gbc_oauth1RealmLabel = new GridBagConstraints();
        gbc_oauth1RealmLabel.anchor = GridBagConstraints.EAST;
        gbc_oauth1RealmLabel.insets = new Insets(0, 0, 0, 5);
        gbc_oauth1RealmLabel.gridx = 1;
        gbc_oauth1RealmLabel.gridy = 4;
        oauth1AdvancedPanel.add(oauth1RealmLabel, gbc_oauth1RealmLabel);

        realm = new JTextField();
        oauth1RealmLabel.setLabelFor(realm);
        GridBagConstraints gbc_oauth1Realm = new GridBagConstraints();
        gbc_oauth1Realm.insets = new Insets(0, 0, 0, 5);
        gbc_oauth1Realm.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauth1Realm.gridx = 2;
        gbc_oauth1Realm.gridy = 4;
        oauth1AdvancedPanel.add(realm, gbc_oauth1Realm);
        realm.setColumns(10);
    }

    /**
     * Clear the TestElement of all data.
     */
    public void clear() {
        signatureMethod.setSelectedIndex(0);
        storeMethod.setSelectedIndex(0);
        consumerKey.setText("");
        consumerSecret.setText("");
        token.setText("");
        secret.setText("");
        timestamp.setText("");
        nonce.setText("");
        version.setText("");
        realm.setText("");
    }

    /**
     * Modifies a given TestElement to mirror the data in the org.apache.jmeter.protocol.http.gui components.
     *
     * @param el
     */
    public void configureTestElement(TestElement el) {
        el.setProperty("OAuth1.store_to", storeMethod.getSelectedItem().toString(), storeMethods[0]);
        el.setProperty("OAuth1.signature_method", signatureMethod.getSelectedItem().toString(), signatureMethods[0]);
        el.setProperty("OAuth1.consumer_key", consumerKey.getText(), "");
        el.setProperty("OAuth1.consumer_secret", consumerSecret.getText(), "");
        el.setProperty("OAuth1.token", token.getText(), "");
        el.setProperty("OAuth1.secret", secret.getText(), "");
        el.setProperty("OAuth1.timestamp", timestamp.getText(), "");
        el.setProperty("OAuth1.nonce", nonce.getText(), "");
        el.setProperty("OAuth1.version", version.getText(), "");
        el.setProperty("OAuth1.realm", realm.getText(), "");
    }

    /**
     * Configures GUI from el
     *
     * @param el
     */
    public void configure(TestElement el) {
        storeMethod.setSelectedItem(el.getPropertyAsString("OAuth1.store_to", storeMethods[0]));
        signatureMethod.setSelectedItem(el.getPropertyAsString("OAuth1.signature_method", signatureMethods[0]));
        consumerKey.setText(el.getPropertyAsString("OAuth1.consumer_key", ""));
        consumerSecret.setText(el.getPropertyAsString("OAuth1.consumer_secret", ""));
        token.setText(el.getPropertyAsString("OAuth1.token", ""));
        secret.setText(el.getPropertyAsString("OAuth1.secret", ""));
        timestamp.setText(el.getPropertyAsString("OAuth1.timestamp", ""));
        nonce.setText(el.getPropertyAsString("OAuth1.nonce", ""));
        version.setText(el.getPropertyAsString("OAuth1.version", ""));
        realm.setText(el.getPropertyAsString("OAuth1.realm", ""));
    }
}
