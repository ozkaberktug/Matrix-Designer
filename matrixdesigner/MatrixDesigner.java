package matrixdesigner;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

public class MatrixDesigner extends JFrame {
    
    private JLabel status;
    private Board board;
    private AssetBoard assetboard;
    private final String howtoText = "<html>If you have questions, contact with me:<br>Email: bkozkan@hotmail.com<br>Github: www.github.com/ozkaberktug</html>";
    private final String aboutText = "This program written by ozkaberktug in, 11 June 2019. (v1.01)";
    private JButton penButton;
    private JButton eraseButton;
   
    public MatrixDesigner() {
        super();
        initUI();
    }
           
    private void initUI() {
        initMenuBar();
        initElements();
        setTitle("Matrix Designer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(this);
    }
    
    private void initElements() {
        setLayout(new GridBagLayout());

        GridBagConstraints toolsGBC = new GridBagConstraints();
        GridBagConstraints statusGBC = new GridBagConstraints();
        GridBagConstraints splitpaneGBC = new GridBagConstraints();
        splitpaneGBC.fill = GridBagConstraints.BOTH;
        splitpaneGBC.gridx = 0;
        splitpaneGBC.gridy = 1;
        splitpaneGBC.weightx = splitpaneGBC.weighty = 1;
        splitpaneGBC.insets = new Insets(10, 10, 0, 10);
        toolsGBC.gridx = 0;
        toolsGBC.gridy = 0;
        toolsGBC.gridwidth = 2;
        toolsGBC.fill = GridBagConstraints.BOTH;
        toolsGBC.insets = new Insets(10, 10, 0, 10);
        toolsGBC.weightx = 1;
        statusGBC.gridx = 0;
        statusGBC.gridy = 2;
        statusGBC.gridwidth = 2;
        statusGBC.fill = GridBagConstraints.BOTH;
        statusGBC.insets = new Insets(10, 10, 10, 10);
        statusGBC.weightx = 1;
        
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        penButton = new JButton(new ImageIcon(this.getClass().getResource("pen.png")));
        eraseButton = new JButton(new ImageIcon(this.getClass().getResource("eraser.png")));
        penButton.setToolTipText("Pen");
        eraseButton.setToolTipText("Erase");
        penButton.addActionListener((e) -> { board.setDrawingMode(Board.PEN); });
        eraseButton.addActionListener((e) -> { board.setDrawingMode(Board.ERASER); });
        tools.add(penButton);
        tools.add(eraseButton);
        
        status = new JLabel("Ready");
        assetboard = new AssetBoard(status);
        board = new Board(status, assetboard);
        JScrollPane assetboardScrollPane = new JScrollPane(assetboard);
        assetboardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        assetboardScrollPane.setBorder(null);
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, board, assetboardScrollPane);
        splitpane.setOneTouchExpandable(true);
        
        add(splitpane, splitpaneGBC);
        add(tools, toolsGBC);
        add(status, statusGBC);
    }
    
    private void initMenuBar() {
        JMenuBar mbar = new JMenuBar();
        JMenu fileMenu = new JMenu("File..");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenu helpMenu = new JMenu("Help..");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        JMenuItem howtoMenuItem = new JMenuItem("How to use?");
        JMenu projectMenu = new JMenu("Project..");
        JMenuItem importMenuItem = new JMenuItem("Import Assets");
        JMenu exportMenu = new JMenu("Export Level..");
        JMenuItem toPNGMenuItem = new JMenuItem("to PNG");
        JMenuItem toMatrixMenuItem = new JMenuItem("to Matrix");
        
        mbar.add(fileMenu);
        mbar.add(projectMenu);
        mbar.add(helpMenu);
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        helpMenu.add(howtoMenuItem);
        helpMenu.add(aboutMenuItem);
        projectMenu.add(importMenuItem);
        projectMenu.add(exportMenu);
        exportMenu.add(toPNGMenuItem);
        exportMenu.add(toMatrixMenuItem);
        setJMenuBar(mbar);
        
        exitMenuItem.addActionListener((e) -> { System.exit(0); });
        openMenuItem.addActionListener((e) -> { open(); });
        saveMenuItem.addActionListener((e) -> { save(); });
        newMenuItem.addActionListener((e) -> { clear(); });
        aboutMenuItem.addActionListener((e) -> { JOptionPane.showMessageDialog(this, aboutText); });
        howtoMenuItem.addActionListener((e) -> { JOptionPane.showMessageDialog(this, howtoText); });
        importMenuItem.addActionListener((e) -> { assetboard.importAssets(); });
        toPNGMenuItem.addActionListener((e) -> { board.export(Board.EXPORT_TO_PNG); });
        toMatrixMenuItem.addActionListener((e) -> { board.export(Board.EXPORT_TO_MATRIX); });
        
        fileMenu.setMnemonic(KeyEvent.VK_F);
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        howtoMenuItem.setMnemonic(KeyEvent.VK_H);
        projectMenu.setMnemonic(KeyEvent.VK_P);
        importMenuItem.setMnemonic(KeyEvent.VK_I);
        exportMenu.setMnemonic(KeyEvent.VK_E);
        
    }
    
    private void clear() {
        assetboard.clear();
        board.clear();
        status.setText("Ready");
    }
    private void save() {
        status.setText("Saving...");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Object[][] assetData = assetboard.getData();
            Hashtable<String, Node> boardData = board.getData();
            try {
                BufferedWriter fw = new BufferedWriter(new FileWriter(file));
                fw.write("#1\n");
                for(int i=0; i<AssetBoard.MAX_ASSETS; i++)
                    if(assetData[i][0] != null && assetData[i][1] != null)
                        fw.write((String)assetData[i][0]+"?"+((Integer)assetData[i][1]).toString()+"\n");
                fw.write("#2\n");
                for(Enumeration<Node> en=boardData.elements(); en.hasMoreElements();) {
                    Node node = en.nextElement();
                    fw.write(node.r+","+node.c+","+node.val+"\n");
                }
                fw.close();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "File I/O error!", "Error!", JOptionPane.ERROR_MESSAGE);
                status.setText("Save failed!");
                return;
            }
            status.setText("Saved file: "+file.getAbsolutePath());
        }
        
    }
    private void open() {
        status.setText("Loading...");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Object[][] assetData = new Object[AssetBoard.MAX_ASSETS][2];
            Hashtable<String, Node> boardData = new Hashtable<>();
            try {
                BufferedReader fr = new BufferedReader(new FileReader(file));
                String line;
                int i=0;
                if ( !(line = fr.readLine()).equals("#1") ) throw new Exception();
                while ( !(line = fr.readLine()).equals("#2")) {
                    String[] parsed = line.split("\\?");
                    assetData[i][0] = parsed[0];
                    assetData[i][1] = Integer.parseInt(parsed[1]);
                    i++;
                }
                while ( (line = fr.readLine()) != null ) {
                    String[] parsed = line.split(",");
                    Node n = new Node(Integer.parseInt(parsed[0]),Integer.parseInt(parsed[1]),Integer.parseInt(parsed[2]));
                    boardData.put(parsed[0]+","+parsed[1], n);
                }
                assetboard.putData(assetData);
                board.putData(boardData);
                fr.close();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "File I/O error!", "Error!", JOptionPane.ERROR_MESSAGE);
                status.setText("Load failed!");
                return;
            }
            status.setText("Loaded file: "+file.getAbsolutePath());
        }
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MatrixDesigner app = new MatrixDesigner();
            app.setVisible(true);
        });
    }

}
