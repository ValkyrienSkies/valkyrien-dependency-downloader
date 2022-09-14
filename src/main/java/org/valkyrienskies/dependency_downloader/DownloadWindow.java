package org.valkyrienskies.dependency_downloader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.*;
/*
 * Created by JFormDesigner on Sun Sep 11 21:13:19 EDT 2022
 */


/**
 * @author unknown
 */
public class DownloadWindow extends JFrame {

    public final Object lock = new Object();
    public boolean shouldContinue = false;
    public boolean shouldDownload = false;

    private final List<DependencyCheckbox> checkboxes = new ArrayList<>();

    public DownloadWindow(Iterable<DependencyToDownload> deps) {
        initComponents();
        createCheckboxes(deps);
        this.setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("valkyrien_icon.png")));
        pack();
    }

    private static class DependencyCheckbox extends JCheckBox {
        private final DependencyToDownload toDownload;

        private DependencyCheckbox(DependencyToDownload toDownload) {
            this.toDownload = toDownload;
            boolean isOptional = toDownload.getDependency().isOptional();
            setText(toDownload.getDependency().getName() + (isOptional ? " (optional)" : ""));
            setSelected(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        public DependencyToDownload getToDownload() {
            return toDownload;
        }
    }

    public List<DependencyToDownload> getSelected() {
        return checkboxes.stream()
            .filter(JCheckBox::isSelected)
            .map(DependencyCheckbox::getToDownload)
            .collect(Collectors.toList());
    }

    private void createCheckboxes(Iterable<DependencyToDownload> deps) {
        for (DependencyToDownload dep : deps) {
            DependencyCheckbox checkbox = new DependencyCheckbox(dep);
            checkboxes.add(checkbox);
            selectDependencies.add(checkbox);
        }
        selectDependencies.setMinimumSize(selectDependencies.getPreferredSize());
    }

    private void cancel() {
        this.dispose();
        synchronized (lock) {
            shouldContinue = true;
            lock.notify();
        }

    }

    private void cancel(ActionEvent e) {
        cancel();
    }

    private void onDownload(ActionEvent e) {
        synchronized (lock) {
            shouldDownload = true;
            shouldContinue = true;
            lock.notify();
        }
    }

    private void thisWindowClosing(WindowEvent e) {
        cancel();
    }

    private void cancelAndExit(ActionEvent e) {
        System.exit(0);
    }

    private void doNotAskCheckboxStateChanged(ChangeEvent e) {
        if (doNotAskCheckbox.isSelected()) {
            downloadButton.setEnabled(false);
            cancelAndExitButton.setEnabled(false);
        } else {
            downloadButton.setEnabled(true);
            cancelAndExitButton.setEnabled(true);
        }
    }



    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        selectDependencies = new JPanel();
        downloadButton = new JButton();
        downloadProgress = new JProgressBar();
        cancel = new JButton();
        cancelAndExitButton = new JButton();
        doNotAskCheckbox = new JCheckBox();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Valkyrien Dependency Downloader");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(new EmptyBorder(15, 15, 15, 15));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {197, 187};
            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0, 60, 0, 0};
            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0};
            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0};

            //---- label1 ----
            label1.setText("<html>You are missing required dependencies. Please select the dependencies you would like to automatically download:</html>");
            label1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            label1.setPreferredSize(new Dimension(300, 32));
            panel1.add(label1, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

            //======== selectDependencies ========
            {
                selectDependencies.setBorder(new TitledBorder(null, "Select Dependencies", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                    new Font("Segoe UI", Font.PLAIN, 12)));
                selectDependencies.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                selectDependencies.setLayout(new BoxLayout(selectDependencies, BoxLayout.Y_AXIS));
            }
            panel1.add(selectDependencies, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 10, 0), 0, 0));

            //---- downloadButton ----
            downloadButton.setText("Download");
            downloadButton.setBackground(new Color(54, 159, 214));
            downloadButton.setForeground(Color.white);
            downloadButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            downloadButton.addActionListener(e -> onDownload(e));
            panel1.add(downloadButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

            //---- downloadProgress ----
            downloadProgress.setVisible(false);
            downloadProgress.setStringPainted(true);
            downloadProgress.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            panel1.add(downloadProgress, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

            //---- cancel ----
            cancel.setText("<html>Cancel & Continue</html>");
            cancel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            cancel.setBackground(Color.white);
            cancel.addActionListener(e -> cancel(e));
            panel1.add(cancel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 10), 0, 0));

            //---- cancelAndExitButton ----
            cancelAndExitButton.setText("<html>Cancel & Close Game</html>");
            cancelAndExitButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            cancelAndExitButton.setBackground(Color.white);
            cancelAndExitButton.addActionListener(e -> cancelAndExit(e));
            panel1.add(cancelAndExitButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));

            //---- doNotAskCheckbox ----
            doNotAskCheckbox.setText("Do not ask again");
            doNotAskCheckbox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            doNotAskCheckbox.addChangeListener(e -> doNotAskCheckboxStateChanged(e));
            panel1.add(doNotAskCheckbox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 10), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        setSize(475, 285);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JPanel selectDependencies;
    public JButton downloadButton;
    public JProgressBar downloadProgress;
    private JButton cancel;
    private JButton cancelAndExitButton;
    public JCheckBox doNotAskCheckbox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
