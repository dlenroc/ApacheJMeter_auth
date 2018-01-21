package org.apache.jmeter.protocol.http.auth.gui.control;

import javax.swing.*;
import java.awt.*;

public class NoAuthConfigPanel extends JPanel {
    public NoAuthConfigPanel() {
        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[]{281, 0};
        gbl.rowHeights = new int[]{16, 0};
        gbl.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        this.setLayout(gbl);

        JLabel label = new JLabel("This request does not use any authorization.");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(label, gbc);
    }
}
