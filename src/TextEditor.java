import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor extends JFrame implements ActionListener {
    

    JTextArea textArea;
    JScrollPane scrollPane;
    JLabel fontLabel;
    JSpinner fontSizeSpinner;
    JButton fontColorButton;
    JButton bgColorButton;
    JCheckBox boldCheckBox;
    JCheckBox italicCheckBox;
    JCheckBox darkModeCheckBox;
    JLabel statusBar;
    JComboBox<String> fontBox;

    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;

    public TextEditor() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Text Editor");
        this.setSize(700, 700);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));
        textArea.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateStatusBar();
            }
        });

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 500));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        fontLabel = new JLabel("Font:");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(e -> updateFont());

        fontColorButton = new JButton("Text Color");
        fontColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose Text Color", textArea.getForeground());
            if (color != null) textArea.setForeground(color);
        });

        bgColorButton = new JButton("Background");
        bgColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose Background Color", textArea.getBackground());
            if (color != null) textArea.setBackground(color);
        });

        boldCheckBox = new JCheckBox("Bold");
        boldCheckBox.addActionListener(e -> updateFont());

        italicCheckBox = new JCheckBox("Italic");
        italicCheckBox.addActionListener(e -> updateFont());

        darkModeCheckBox = new JCheckBox("Dark Mode");
        darkModeCheckBox.addActionListener(e -> toggleDarkMode());

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox<>(fonts);
        fontBox.setSelectedItem("Arial");
        fontBox.addActionListener(e -> updateFont());

        statusBar = new JLabel(" Words: 0 | Chars: 0");
        statusBar.setPreferredSize(new Dimension(650, 25));

        // Menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);
        this.add(fontLabel);
        this.add(fontBox);
        this.add(fontSizeSpinner);
        this.add(boldCheckBox);
        this.add(italicCheckBox);
        this.add(fontColorButton);
        this.add(bgColorButton);
        this.add(darkModeCheckBox);
        this.add(scrollPane);
        this.add(statusBar);
        this.setVisible(true);
    }

    private void updateFont() {
        int style = Font.PLAIN;
        if (boldCheckBox.isSelected()) style |= Font.BOLD;
        if (italicCheckBox.isSelected()) style |= Font.ITALIC;
        textArea.setFont(new Font((String) fontBox.getSelectedItem(), style, (int) fontSizeSpinner.getValue()));
    }

    private void toggleDarkMode() {
        if (darkModeCheckBox.isSelected()) {
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        }
    }

    private void updateStatusBar() {
        String text = textArea.getText();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        statusBar.setText(" Words: " + words + " | Chars: " + chars);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try (Scanner fileIn = new Scanner(file)) {
                    textArea.setText("");
                    while (fileIn.hasNextLine()) {
                        textArea.append(fileIn.nextLine() + "\n");
                    }
                    updateStatusBar();
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int response = fileChooser.showSaveDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try (PrintWriter fileOut = new PrintWriter(file)) {
                    fileOut.println(textArea.getText());
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Could not save file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}
