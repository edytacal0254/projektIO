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
    String videoToPlayPath;

    public GUI() {
        setSize(WIDTH, HEIGHT);

        //tworzę potrzebne elementy interfejsu i dodaję je do niego
        GridLayout layout = new GridLayout(0,3);
        var panel = new JPanel();
        var buttonPanel = new JPanel();

        var label = new JLabel("Chosen file path:");
        var pathText = new JTextField(60);
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

        //Wybór ścieżki przetwarzanego pliku
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
                //Gdy uda się wybrać poprawną ścieżkę odblokowany zostaje przycisk analizy
                process.setEnabled(true);
            }
        });

        //Ponownie dodawanie elementów:
        var savePanel = new JPanel();

        var saveLabel = new JLabel("File save location:");
        var savePathText = new JTextField(40);
        savePathText.setEditable(false);
        var browseSaveLoc = new JButton("Choose location");
        var saveButton = new JButton("Save video");
        var filenameLabel = new JLabel("Filename:");
        var previewVidButton = new JButton("Preview video");

        saveButton.setEnabled(false);
        //Generujemy domyślną nazwę pliku
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_HHmm");
        String formattedDateTime = currentDateTime.format(formatter);
        String defaultFileName = "Analysis_" + formattedDateTime;
        var filenameField = new JTextField(defaultFileName, 40);

        //Wybieramy miejsce zapisu wideo
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


        //zapisujemy wideo
        saveButton.addActionListener(event -> {
            savePath = savePathText.getText() + "\\" + filenameField.getText();
            File filem = new File(savePath + ".mp4");
            File filea = new File(savePath + ".avi");
            if (filem.isFile() || filea.isFile()) {

                int iter = 1;

                filem = new File(savePath + "(" + iter + ").mp4");
                filea = new File(savePath + "(" + iter + ").avi");

                while (filem.isFile() || filea.isFile()) {
                    iter++;

                    filem = new File(savePath + "(" + iter + ").mp4");
                    filea = new File(savePath + "(" + iter + ").avi");
                }


                int select = JOptionPane.showConfirmDialog(savePanel, "Plik o tej nazwie już istnieje. Czy chcesz go zapisać jako " + filenameField.getText() + "(" + iter + ")?", "Plik już istnieje", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                if (select == JOptionPane.OK_OPTION) {
                    savePath = savePath + "(" + iter + ")";
                    System.out.println(savePath);
                    program.saveVideoAs(savePath);
                    savePanel.setVisible(false);
                    process.setEnabled(false);
                    process.setText("Process file");
                    pathText.setText("");
                    savePathText.setText("");
                    filenameField.setText("");
                }
                if (select == JOptionPane.CANCEL_OPTION) {
                    filenameField.setText("");
                }

            }
            else {
                System.out.println(savePath);
                program.saveVideoAs(savePath);
                savePanel.setVisible(false);
                process.setEnabled(false);
                process.setText("Process file");
                pathText.setText("");
                savePathText.setText("");
                filenameField.setText("");
            }
        });


        previewVidButton.addActionListener(event -> {
            program.playVideo();
        });


        //ustawianie elementów
        savePanel.setLayout(layout);
        var filler = new JLabel("coscos");
        var filler1 = new JLabel("coscoscos");

        savePanel.add(filler);
        savePanel.add(filler1);
        filler.setVisible(false);
        filler1.setVisible(false);
        savePanel.add(previewVidButton);
        savePanel.add(saveLabel);
        savePanel.add(savePathText);
        savePanel.add(browseSaveLoc);
        savePanel.add(filenameLabel);
        savePanel.add(filenameField);
        savePanel.add(saveButton);
        savePanel.setVisible(false);

        add(savePanel, BorderLayout.SOUTH);

        //akcja przetwarzania wideo
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
            process.setText("Process again");
            savePanel.setVisible(true);
        });



    }


}
