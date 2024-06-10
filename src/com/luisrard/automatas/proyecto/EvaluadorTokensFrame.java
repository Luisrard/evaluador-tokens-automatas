package com.luisrard.automatas.proyecto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EvaluadorTokensFrame extends JFrame {
    private JTextArea textAreaInput;
    private JTextArea textAreaOutput;
    private JButton buttonLoadFile;
    private JButton buttonAnalyze;

    public EvaluadorTokensFrame() {
        setTitle("Evaluador Tokens");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textAreaInput = new JTextArea();
        textAreaOutput = new JTextArea();
        textAreaOutput.setEditable(false);

        buttonLoadFile = new JButton("Cargar Archivo");
        buttonAnalyze = new JButton("Analizar");

        buttonLoadFile.addActionListener(new LoadFileAction());
        buttonAnalyze.addActionListener(new AnalyzeAction());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textAreaInput), new JScrollPane(textAreaOutput));
        splitPane.setDividerLocation(400);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonLoadFile);
        buttonPanel.add(buttonAnalyze);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private class LoadFileAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(EvaluadorTokensFrame.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textAreaInput.read(reader, null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(EvaluadorTokensFrame.this, "Error al cargar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class AnalyzeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = textAreaInput.getText();
            String report = EvaluadorTokens.detectTokensAndGenerateReport(text);
            textAreaOutput.setText(report);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EvaluadorTokensFrame frame = new EvaluadorTokensFrame();
            frame.setVisible(true);
        });
    }
}
