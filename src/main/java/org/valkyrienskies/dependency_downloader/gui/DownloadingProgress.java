/*
 * Created by JFormDesigner on Sat Sep 17 10:28:53 EDT 2022
 */

package org.valkyrienskies.dependency_downloader.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author unknown
 */
public class DownloadingProgress extends JFrame {
    public DownloadingProgress() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        currentlyDownloading = new JLabel();
        panel2 = new JPanel();
        totalProgress = new JProgressBar();
        label2 = new JLabel();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 133, 0};
            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 35, 0, 0};
            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};

            //---- currentlyDownloading ----
            currentlyDownloading.setText("Currently downloading: ");
            currentlyDownloading.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            panel1.add(currentlyDownloading, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== panel2 ========
            {
                panel2.setBorder(new CompoundBorder(
                    LineBorder.createGrayLineBorder(),
                    new EmptyBorder(5, 5, 5, 5)));
                panel2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
            }
            panel1.add(panel2, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //---- totalProgress ----
            totalProgress.setStringPainted(true);
            totalProgress.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            panel1.add(totalProgress, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //---- label2 ----
            label2.setText("Speed: ? MB/s");
            label2.setInheritsPopupMenu(false);
            label2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            panel1.add(label2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- cancelButton ----
            cancelButton.setText("Cancel Download");
            cancelButton.setBackground(Color.white);
            cancelButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            panel1.add(cancelButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    public JLabel currentlyDownloading;
    private JPanel panel2;
    public JProgressBar totalProgress;
    private JLabel label2;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
