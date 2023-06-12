package org.example;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PDF-Coalesce");
        JLabel label = new JLabel("PDF TOOL", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        ImageIcon icon1 = new ImageIcon("merge.png");
        ImageIcon icon2 = new ImageIcon("split.png");
        ImageIcon icon3 = new ImageIcon("compress.png");
        ImageIcon icon4 = new ImageIcon("extract.png");
        ImageIcon icon5 = new ImageIcon("view.png");
        JButton mergeButton = new JButton("Merge",icon1);
        mergeButton.setPreferredSize(new Dimension(200, 200));
        JButton splitButton = new JButton("Split",icon2);
        splitButton.setPreferredSize(new Dimension(200, 200));

        JButton compressButton = new JButton("Compress",icon3);
        compressButton.setPreferredSize(new Dimension(200, 200));

        JButton extractButton = new JButton("Extract",icon4);
        extractButton.setPreferredSize(new Dimension(200, 200));

        JButton viewButton = new JButton("View",icon5);
        viewButton.setPreferredSize(new Dimension(200, 200));

        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            PDFMergerGUI frame = new PDFMergerGUI();
                            System.out.println("main function is called and frame is created...\n");
                            frame.setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                new PDFMergerGUI();
            }
        });

        splitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PDFSplitterGUI gui = new PDFSplitterGUI();
                gui.setVisible(true);
            }
        });

        compressButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            PDFCompressor frame = new PDFCompressor();
                            System.out.println("main function is called and frame is created...\n");
                            frame.setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        extractButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PDFExtractGUI gui = new PDFExtractGUI();
                gui.setVisible(true);
            }
        });
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(() -> new PDFViewerGUI());
            }
        });

        panel.add(mergeButton);
        panel.add(splitButton);
        panel.add(compressButton);
        panel.add(extractButton);
        panel.add(viewButton);


        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}

class PDFMergerGUI extends JFrame {

    private JPanel contentPane;
    private JFileChooser fileChooser1;
    private JFileChooser fileChooser2;
    private JButton btnMerge;
    private JButton btnDownload;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private File file1;
    private File file2;
    private File mergedFile;

    public PDFMergerGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1000, 1450);
        setTitle("PDF Merger");

        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblFile1 = new JLabel("Select PDF 1:");
        contentPane.add(lblFile1, gbc);

        fileChooser1 = new JFileChooser();
        fileChooser1.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        gbc.gridx = 1;
        contentPane.add(fileChooser1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblFile2 = new JLabel("Select PDF 2:");
        contentPane.add(lblFile2, gbc);

        fileChooser2 = new JFileChooser();
        fileChooser2.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        gbc.gridx = 1;
        contentPane.add(fileChooser2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        btnMerge = new JButton("Merge");
        btnMerge.setEnabled(true);
        btnMerge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mergePDFs();
            }
        });
        contentPane.add(btnMerge, gbc);

        gbc.gridx = 1;
        lblStatus = new JLabel(" ");
        contentPane.add(lblStatus, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth= 100;
    }

    private void mergePDFs() {
        file1 = fileChooser1.getSelectedFile();
        file2 = fileChooser2.getSelectedFile();
        mergedFile = new File("merged.pdf");

        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(mergedFile));
            document.open();
            PdfReader reader1 = new PdfReader(file1.getAbsolutePath());
            PdfReader reader2 = new PdfReader(file2.getAbsolutePath());
            copy.addDocument(reader1);
            copy.addDocument(reader2);
            reader1.close();
            reader2.close();
            document.close();
        } catch (IOException | DocumentException e) {
            JOptionPane.showMessageDialog(null, "Invalid input!");
            e.printStackTrace();
        }
        System.out.println("completing merging...\n");
        downloadMergedPDF();
    }

    private void downloadMergedPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(mergedFile);
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                InputStream in = new FileInputStream(mergedFile);
                OutputStream out = new FileOutputStream(file);
                System.out.println("before throw");
                if (in == null) {
                    JOptionPane.showMessageDialog(null, "File not found!");
                    throw new IOException("File not found");
                }
                System.out.println("After throw");
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                in.close();
                out.close();
                JOptionPane.showMessageDialog(null, "PDF merged and downloaded successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Download error!");
                System.out.println("control is here in exception. If any exception occurs");
                e.printStackTrace();
            }
        }
        System.out.println("function ends successfully\n");
    }
}

class PDFCompressor extends JFrame {
    private JFrame frame;
    private File compressFile;
    private JFileChooser compFile;
    private JButton btncompress;
    private JFileChooser filecomp;
    private JPanel contentPane;

    public  PDFCompressor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 700, 500);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        filecomp = new JFileChooser();
        filecomp.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        contentPane.add(filecomp, BorderLayout.WEST);
        btncompress=new JButton("COMPRESS");
        contentPane.add(btncompress, BorderLayout.CENTER);

        btncompress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Starting Compressing i.e. button is clicked...now calling compressPdf function\n");
                compressPDF();
                System.out.println("compression sucess\n");
            }

            private void compressPDF() {
                compressFile=filecomp.getSelectedFile();
                String inputFilePath =compressFile.getAbsolutePath();


                // Output PDF file
                String outputFilePath = "compressed.pdf";

                // Compression level (0-9)
                int compressionLevel = 9;

                try {
                    // Create a Document object
                    Document document = new Document();

                    // Create a PdfWriter object
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilePath));

                    // Set compression level
                    writer.setCompressionLevel(compressionLevel);

                    // Open the document
                    document.open();

                    // Add the input PDF file to the document
                    com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(inputFilePath);
                    int n = reader.getNumberOfPages();
                    for (int i = 1; i <= n; i++) {
                        document.newPage();
                        com.itextpdf.text.pdf.PdfImportedPage page = writer.getImportedPage(reader, i);
                        writer.getDirectContent().addTemplate(page, 0, 0);
                    }

                    // Close the document
                    document.close();

                    // Close the PdfReader object
                    reader.close();
                    JOptionPane.showMessageDialog(null, "PDF compressed and downloaded successfully!");
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Invalid input!");
                }
                System.out.println("Compression successful\n");
            }
        });
    }
}

class PDFExtractGUI extends JFrame implements ActionListener {

    private JPanel mainPanel, filePanel, splitPanel, buttonPanel;
    private JLabel fileLabel, splitLabel1,splitLabel2;
    private JTextField fileField, splitField1, splitField2;
    private JButton browseButton, ExtractButton;
    private JFileChooser fileChooser;
    private File selectedFile;
    private PDDocument splitDocument1, splitDocument2;

    public PDFExtractGUI() {
        setTitle("PDF Extracter");
        setSize(400, 200);

        mainPanel = new JPanel(new GridLayout(3, 1));
        filePanel = new JPanel(new GridLayout(1, 2));
        splitPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        fileLabel = new JLabel("Select PDF file:");
        filePanel.add(fileLabel);

        fileField = new JTextField();
        fileField.setEditable(false);
        filePanel.add(fileField);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        filePanel.add(browseButton);

        splitLabel1 = new JLabel("Enter Start page:");
        splitPanel.add(splitLabel1);

        splitField1 = new JTextField();
        splitPanel.add(splitField1);

        splitLabel2 = new JLabel("Enter End page:");
        splitPanel.add(splitLabel2);

        splitField2 = new JTextField();
        splitPanel.add(splitField2);

        ExtractButton = new JButton("Extract PDF");
        ExtractButton.addActionListener(this);
        buttonPanel.add(ExtractButton);

        mainPanel.add(filePanel);
        mainPanel.add(splitPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        } else if (e.getSource() == ExtractButton) {
            try {
                int start = Integer.parseInt(splitField1.getText());
                int end= Integer.parseInt(splitField2.getText());
                PDDocument document = PDDocument.load(selectedFile);
                int pages = document.getNumberOfPages();
                Splitter splitter = new Splitter();
                splitter.setStartPage(start);
                splitter.setEndPage(end);
                java.util.List<PDDocument> splitpages = splitter.split(document);
                PDDocument newDoc = new PDDocument();
                for (PDDocument mydoc : splitpages) {
                    newDoc.addPage(mydoc.getPage(0));
                }
                String fileName = selectedFile.getName().replace(".pdf", "") + "_extracted" + ".pdf";
                newDoc.save(fileName);
                newDoc.close();
                document.close();
                JOptionPane.showMessageDialog(null, "PDF Ectracted and downloaded successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error splitting PDF.");
                ex.printStackTrace();
            }
        }
    }
}

class PDFSplitterGUI extends JFrame implements ActionListener {

    private JPanel mainPanel, filePanel, splitPanel, buttonPanel;
    private JLabel fileLabel, splitLabel;
    private JTextField fileField, splitField;
    private JButton browseButton, splitButton;
    private JFileChooser fileChooser;
    private File selectedFile;
    private PDDocument splitDocument1, splitDocument2;

    public PDFSplitterGUI() {
        setTitle("PDF Splitter");
        setSize(400, 200);

        mainPanel = new JPanel(new GridLayout(3, 1));
        filePanel = new JPanel(new GridLayout(1, 2));
        splitPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        fileLabel = new JLabel("Select PDF file:");
        filePanel.add(fileLabel);

        fileField = new JTextField();
        fileField.setEditable(false);
        filePanel.add(fileField);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(this);
        filePanel.add(browseButton);

        splitLabel = new JLabel("Enter number of pages to split:");
        splitPanel.add(splitLabel);

        splitField = new JTextField();
        splitPanel.add(splitField);

        splitButton = new JButton("Split PDF");
        splitButton.addActionListener(this);
        buttonPanel.add(splitButton);

        mainPanel.add(filePanel);
        mainPanel.add(splitPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        } else if (e.getSource() == splitButton) {
            try {
                int splitValue = Integer.parseInt(splitField.getText());
                PDDocument document = PDDocument.load(selectedFile);
                int pages = document.getNumberOfPages();
                Splitter splitter = new Splitter();
                splitter.setStartPage(1);
                splitter.setEndPage(splitValue);
                java.util.List<PDDocument> splitpages = splitter.split(document);
                PDDocument newDoc = new PDDocument();
                for (PDDocument mydoc : splitpages) {
                    newDoc.addPage(mydoc.getPage(0));
                }
                String fileName = selectedFile.getName().replace(".pdf", "") + "_part" + (1) + ".pdf";
                newDoc.save(fileName);
                newDoc.close();

                Splitter splitter2 = new Splitter();
                splitter2.setStartPage(splitValue + 1);
                splitter2.setEndPage(pages);
                java.util.List<PDDocument> splitpages2 = splitter2.split(document);
                PDDocument newDoc1 = new PDDocument();
                for (PDDocument mydoc : splitpages2) {
                    newDoc1.addPage(mydoc.getPage(0));
                }
                String fileName2 = selectedFile.getName().replace(".pdf", "") + "_part" + (2) + ".pdf";
                newDoc1.save(fileName2);
                newDoc1.close();
                document.close();
                JOptionPane.showMessageDialog(null, "PDF split successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error splitting PDF.");
                ex.printStackTrace();

            }
        }
    }
}

class PDFViewerGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JButton selectButton;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File selectedFile;

    public PDFViewerGUI() {
        super("PDF Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        selectButton = new JButton("Select PDF");
        selectButton.addActionListener(this);
        panel.add(selectButton);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        add(scrollPane, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                new PDFViewerWorker().execute();
            }
        }
    }

    private class PDFViewerWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            PDDocument document = PDDocument.load(selectedFile);
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document);
                textArea.append("Page " + (i + 1) + ":\n");
                textArea.append(text);
                textArea.append("\n\n");
                renderer.renderPageToGraphics(i, (Graphics2D) textArea.getGraphics(), 72);
            }
            document.close();
            return null;
        }
    }
}

class PDFViewer {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("PDF Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            JScrollPane scrollPane = new JScrollPane();
            panel.add(scrollPane);

            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);

            try {
                PDDocument document = PDDocument.load(new File("E:\\pdf\\s.pdf"));
                PDFRenderer renderer = new PDFRenderer(document);
                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    scrollPane.setViewportView(new PDFPagePanel(renderer, i));
                    frame.validate();
                    Thread.sleep(2);
                }
                document.close();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }
}

class PDFPagePanel extends JPanel {
    private PDFRenderer renderer;
    private int pageIndex;

    public PDFPagePanel(PDFRenderer renderer, int pageIndex) {
        this.renderer = renderer;
        this.pageIndex = pageIndex;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300);
            g.drawImage(image, 0, 0, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}