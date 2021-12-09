
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextEditor extends JFrame {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int PADDING_NORMAL = 16;
    public static final int PADDING_MIDDLE = 8;
    public static final int PADDING_SMALL = 4;

    public TextEditor() {
        super("Text Editor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JTextField fileName = new JTextField();
        fileName.setName("FilenameField");
        fileName.setPreferredSize(new Dimension(100, 20));

        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");

        ActionListener saveToFileListener = getSaveActionListener(fileName, textArea);
        ActionListener loadFromFileListener = getLoadActionListener(fileName, textArea);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        createMenu(menuBar, saveToFileListener, loadFromFileListener);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.LINE_AXIS));
        toolsPanel.setBorder(new EmptyBorder(PADDING_MIDDLE, PADDING_NORMAL, PADDING_SMALL, PADDING_NORMAL));
        add(toolsPanel, BorderLayout.NORTH);

        JButton saveButton = new JButton("Save");
        saveButton.setName("SaveButton");
        saveButton.addActionListener(saveToFileListener);

        JButton loadButton = new JButton("Load");
        loadButton.setName("LoadButton");
        loadButton.addActionListener(loadFromFileListener);

        toolsPanel.add(fileName);
        //Spacer
        toolsPanel.add(Box.createRigidArea(new Dimension(PADDING_SMALL, 0)));
        toolsPanel.add(saveButton);
        //Spacer
        toolsPanel.add(Box.createRigidArea(new Dimension(PADDING_SMALL, 0)));
        toolsPanel.add(loadButton);

        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setBorder(new EmptyBorder(PADDING_SMALL, PADDING_NORMAL, PADDING_NORMAL, PADDING_NORMAL));
        scrollableTextArea.setName("ScrollPane");
        scrollableTextArea.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollableTextArea, BorderLayout.CENTER);

        JButton button = new JButton("Choose file");
        toolsPanel.add(button);
        button.addActionListener(getFileFromDialog(textArea, fileName));

    }

    private void createMenu(JMenuBar menuBar,
                            ActionListener saveToFileListener,
                            ActionListener loadFromFileListener) {

        JMenu fileMenu = new JMenu("File");

        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menuBar.add(fileMenu);

        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        loadMenuItem.setName("MenuLoad");
        saveMenuItem.setName("MenuSave");
        exitMenuItem.setName("MenuExit");

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        loadMenuItem.addActionListener(loadFromFileListener);
        saveMenuItem.addActionListener(saveToFileListener);
        exitMenuItem.addActionListener(e -> System.exit(0));
    }

    private ActionListener getLoadActionListener(JTextField fileName, JTextArea textArea) {
        return e -> {
            String filePath = fileName.getText();
            String text = null;
            try {
                text = FileManager.readFileAsString(filePath);
            } catch (IOException ex) {
                System.out.println("No such file found!");
            }
            textArea.setText(text);
        };
    }

    private ActionListener getSaveActionListener(JTextField fileName, JTextArea textArea) {
        return e -> FileManager.writeStringToFile(textArea.getText(), fileName.getText());
    }

    private ActionListener getFileFromDialog(JTextArea textArea, JTextField fileName) {
        return e -> {
            JFileChooser fileChooser = new JFileChooser();
            int ret = fileChooser.showDialog(null, "Открыть файл");
            String text = null;
            String path = null;
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    text = new String(Files.readAllBytes(file.toPath()));
                    path = file.getPath();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                textArea.setText(text);
                fileName.setText(path);
            }
        };
    }
}