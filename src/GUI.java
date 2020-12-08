import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GUI extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;
    String path;
    String savePath;

    public GUI() {
        setSize(WIDTH, HEIGHT);

        GridLayout layout = new GridLayout(0,3);
        var panel = new JPanel();
        var buttonPanel = new JPanel();

        var label = new JLabel("Chosen file path:");
        var pathText = new JTextField(20);
        var browse = new JButton("Browse files");
        var process = new JButton("Process file");
        browse.setAlignmentX(Component.CENTER_ALIGNMENT);
        process.setAlignmentX(Component.CENTER_ALIGNMENT);
        process.setEnabled(false);
        pathText.setEditable(false);


        panel.add(label);
        panel.add(pathText);
        buttonPanel.add(browse);
        buttonPanel.add(process);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        panel.add(buttonPanel, BorderLayout.EAST);
        panel.setLayout(layout);
        add(panel, BorderLayout.NORTH);
        panel.setVisible(true);

        browse.addActionListener(event -> {
            process.setEnabled(false);
            var chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileFilter(new FileNameExtensionFilter("MP4, AVI", "mp4", "avi"));
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                path = chooser.getSelectedFile().getPath();
                pathText.setText(path);
                process.setEnabled(true);
            }
        });

        var savePanel = new JPanel();

        var saveLabel = new JLabel("File save location:");
        var savePathText = new JTextField(40);
        savePathText.setEditable(false);
        var browseSaveLoc = new JButton("Choose location");
        var saveButton = new JButton("Save video");
        var filenameLabel = new JLabel("Filename:");

        saveButton.setEnabled(false);

        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_HHmm");

        String formattedDateTime = currentDateTime.format(formatter);
        String defaultFileName = "Analysis_" + formattedDateTime;
        var filenameField = new JTextField(defaultFileName, 40);

        browseSaveLoc.addActionListener(event -> {
                    var chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("."));
                    chooser.setFileFilter(new FileNameExtensionFilter("MP4, AVI", "mp4", "avi"));
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = chooser.showSaveDialog(this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        savePathText.setText(chooser.getSelectedFile().getPath());
                        saveButton.setEnabled(true);
                    }
                });



        saveButton.addActionListener(event -> {
            savePath = savePathText.getText() + "\\" + filenameField.getText();
            System.out.println(savePath);
            program.saveVideoAs(savePath);
            program.deleteTempVideo();
            savePanel.setVisible(false);
            process.setText("Process file");
            pathText.setText("");
        });

        savePanel.setLayout(layout);


        savePanel.add(saveLabel);
        savePanel.add(savePathText);
        savePanel.add(browseSaveLoc);
        savePanel.add(filenameLabel);
        savePanel.add(filenameField);
        savePanel.add(saveButton);
        savePanel.setVisible(false);

        add(savePanel, BorderLayout.SOUTH);


        process.addActionListener(event -> {
            process.setText("Processing...");
            savePanel.setVisible(false);
            program.setColorArray();
            try {
                program.setPath(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                program.processVideo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.setText("Processed!");
            savePanel.setVisible(true);
        });



    }


}
