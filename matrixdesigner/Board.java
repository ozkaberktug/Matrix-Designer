package matrixdesigner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class Node {
    public int r, c, val;
    public Node(int r, int c, int val) {
        this.r = r;
        this.c = c;
        this.val = val;
    }
}

public class Board extends JPanel {
    
    public static final int EXPORT_TO_PNG = 1;
    public static final int EXPORT_TO_MATRIX = 2;
    public static final int PEN = 1;
    public static final int ERASER = 2;
    
    private final JLabel status;
    private final AssetBoard assetboard;
    private final MAdapter madapter;
    private int selectedTool;
    private int cellSize;
    private Rectangle panelRect;
    private Hashtable<String,Node> nodes;
    
    public Board(JLabel status, AssetBoard assetboard) {
        super();
        this.status = status;
        this.assetboard = assetboard;
        madapter = new MAdapter();
        setBorder(BorderFactory.createLoweredBevelBorder());
        setBackground(Color.white);
        setMinimumSize(new Dimension(600,450));
        addMouseWheelListener(madapter);
        addMouseListener(madapter);
        addMouseMotionListener(madapter);
        cellSize = 40;
        nodes = new Hashtable<>();
        panelRect = new Rectangle(0, 0, getWidth(), getHeight());
        selectedTool = 0;
    }
    
    public Hashtable getData() {
        return nodes;
    }
    public void putData(Hashtable data) {
        nodes = data;
        panelRect = new Rectangle(0, 0, getWidth(), getHeight());
        selectedTool = 0;
        cellSize = 40;
        repaint();
    }
    
    public void export(int type) {
        if(nodes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No element to draw!", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        switch(type) {
        case EXPORT_TO_PNG:
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if(drawImage(file) == true) 
                    status.setText("Save PNG: "+file.getAbsolutePath());
                else
                    status.setText("Exporting failed!");
            }
            break;
        case EXPORT_TO_MATRIX:
            EventQueue.invokeLater(() -> {
                ExportMatrixFrame frame = new ExportMatrixFrame();
                frame.setVisible(true);
            });
            break;
        }
    }
    
    public void setDrawingMode(int type) {
        selectedTool = type;
        switch(type) {
        case PEN:
            status.setText("Pen tool selected.");
            break;
        case ERASER:
            status.setText("Eraser tool selected.");
            break;
        }
    }
    
    public void clear() {
        cellSize = 40;
        nodes.clear();
        panelRect = new Rectangle(0, 0, getWidth(), getHeight());
        selectedTool = 0;
        repaint();
    }
    
    private boolean drawImage(File file) {
        int minRow, minCol, maxRow, maxCol;
        minRow = minCol = Integer.MAX_VALUE;
        maxRow = maxCol = Integer.MIN_VALUE;
        for(Enumeration<Node> en=nodes.elements(); en.hasMoreElements();) {
            Node node = en.nextElement();
            if(minRow > node.r) minRow = node.r;
            if(minCol > node.c) minCol = node.c;
            if(maxRow < node.r) maxRow = node.r;
            if(maxCol < node.c) maxCol = node.c;
        }
        int w = Math.abs(maxCol - minCol + 1)*cellSize;
        int h = Math.abs(maxRow - minRow + 1)*cellSize;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        
        g.setColor(Color.darkGray);
        
        for(int i=0; i<=Math.abs(maxRow-minRow); i++) {
            for(int j=0; j<=Math.abs(maxCol-minCol); j++) {
                Node node = nodes.get((i+minRow)+","+(j+minCol));
                if(node != null) {
                    if(node.val == -1) {
                        g.fillRect((j)*cellSize, (i)*cellSize, cellSize, cellSize);
                    } else {
                        Image im = assetboard.getAssetFromID(node.val);
                        g.drawImage(im, (j)*cellSize, (i)*cellSize, cellSize, cellSize, this);
                    }
                }
            }
        }
        
        g.dispose();
        
        try {
            ImageIO.write(bi, "png", file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "File I/O error!", "Error!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    @Override
    public void paintComponent(Graphics g1D) {
        super.paintComponent(g1D);
        
        Graphics2D g = (Graphics2D) g1D;
        
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0f, new float[] {2}, 0.0f));
        
        panelRect.width = getWidth();
        panelRect.height = getHeight();
        
        /*
        for(int i=panelRect.y/cellSize-1; i<panelRect.y/cellSize+panelRect.height/cellSize+1; i++) 
            for(int j=panelRect.x/cellSize-1; j<panelRect.x/cellSize+panelRect.width/cellSize+1; j++) 
                g.drawRect(j*cellSize-panelRect.x, i*cellSize-panelRect.y, cellSize, cellSize);
        */
        for(int i=panelRect.y/cellSize; i<panelRect.y/cellSize+panelRect.height/cellSize+2; i++) 
            g.drawLine(0, i*cellSize-panelRect.y, panelRect.width, i*cellSize-panelRect.y);
        for(int j=panelRect.x/cellSize; j<panelRect.x/cellSize+panelRect.width/cellSize+2; j++) 
            g.drawLine(j*cellSize-panelRect.x, 0, j*cellSize-panelRect.x, panelRect.height);
        
        for(Enumeration<Node> en=nodes.elements(); en.hasMoreElements();) {
            Node node = en.nextElement();
            Rectangle r = new Rectangle(node.c*cellSize, node.r*cellSize, cellSize, cellSize);
            if(panelRect.intersects(r)) {
                Image im = assetboard.getAssetFromID(node.val);
                if(im == null)
                    g.fillRect(r.x-panelRect.x,r.y-panelRect.y,cellSize, cellSize);
                else
                    g.drawImage(im, r.x-panelRect.x, r.y-panelRect.y, cellSize, cellSize, this);
            }
        }
        
    }
    
    private class MAdapter extends MouseAdapter {
        
        int sx, sy;
        boolean dragged = false;
        int currentButton;
        int dx, dy;
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.getWheelRotation() < 0) {
                if(cellSize+2 <= 100)
                    cellSize += 2;
            } else {
                if(cellSize-2 >= 10)
                    cellSize -= 2;
            }
            panelRect.width = getWidth();
            panelRect.height = getHeight();
            status.setText("Cell Size: "+cellSize+" px");
            repaint();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            currentButton = e.getButton();
            dragged = false;
            if(currentButton == MouseEvent.BUTTON2) {
                sx = e.getX();
                sy = e.getY();
                dragged = true;
            } else if (currentButton == MouseEvent.BUTTON1) {
                int c = panelRect.x + e.getX();
                int r = panelRect.y + e.getY();
                if(c < 0) c = c-cellSize;
                if(r < 0) r = r-cellSize;
                c /= cellSize;
                r /= cellSize;
                switch(selectedTool) {
                case PEN:
                    if(nodes.containsKey(r+","+c)) break;
                    Node n = new Node(r,c,assetboard.getSelectedAssetID());
                    nodes.put(r+","+c, n);
                    break;
                case ERASER:
                    if(!nodes.containsKey(r+","+c)) break;
                    nodes.remove(r+","+c);
                    break;
                default:
                    status.setText("No tool selected!");
                    break;
                }
                
            } else if(currentButton == MouseEvent.BUTTON3) {
                int c = panelRect.x + e.getX();
                int r = panelRect.y + e.getY();
                if(c < 0) c = c-cellSize;
                if(r < 0) r = r-cellSize;
                c /= cellSize;
                r /= cellSize;
                Node n = nodes.get(r+","+c);
                if(n != null)
                    status.setText("["+c+","+r+"]"+" [ID: "+ n.val+"]");
                else
                    status.setText("["+c+","+r+"]");
            }
            repaint();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if(dragged) {
                dx = e.getX() - sx;
                dy = e.getY() - sy;
                sx = e.getX();
                sy = e.getY();
                panelRect.x -= dx;
                panelRect.y -= dy;
            } else if(currentButton == MouseEvent.BUTTON1) {
                int c = panelRect.x + e.getX();
                int r = panelRect.y + e.getY();
                if(c < 0) c = c-cellSize;
                if(r < 0) r = r-cellSize;
                c /= cellSize;
                r /= cellSize;
                switch(selectedTool) {
                case PEN:
                    if(nodes.containsKey(r+","+c)) break;
                    Node n = new Node(r,c,assetboard.getSelectedAssetID());
                    nodes.put(r+","+c, n);
                    break;
                case ERASER:
                    if(!nodes.containsKey(r+","+c)) break;
                    nodes.remove(r+","+c);
                    break;
                default:
                    status.setText("No tool selected!");
                    break;
                }
            }           
            repaint();
        }
    }
    
    private class ExportMatrixFrame extends JFrame {
        
        private JTextArea textArea;
        
        public ExportMatrixFrame() {
            initUI();
            initText();
        }
        
        private void initUI() {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setResizable(true);
            setMinimumSize(new Dimension(400, 400));
            setLocationRelativeTo(null);
            textArea = new JTextArea();
            textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
            JScrollPane textScroll = new JScrollPane(textArea);
            add(textScroll);
        }
        
        private void initText() {
            
            StringBuilder s = new StringBuilder();
            
            int minRow, minCol, maxRow, maxCol;
            minRow = minCol = Integer.MAX_VALUE;
            maxRow = maxCol = Integer.MIN_VALUE;
            for(Enumeration<Node> en=nodes.elements(); en.hasMoreElements();) {
                Node node = en.nextElement();
                if(minRow > node.r) minRow = node.r;
                if(minCol > node.c) minCol = node.c;
                if(maxRow < node.r) maxRow = node.r;
                if(maxCol < node.c) maxCol = node.c;
            }
            
            setTitle("Matrix Size: "+Math.abs(maxRow-minRow+1)+"x"+Math.abs(maxCol-minCol+1));
            
            for(int i=0; i<=Math.abs(maxRow-minRow); i++) {
                for(int j=0; j<=Math.abs(maxCol-minCol); j++) {
                    Node node = nodes.get((i+minRow)+","+(j+minCol));
                    if(node != null) 
                        s.append(String.format(" %3d", node.val));
                    else
                        s.append(String.format(" %3d", 0));
                }
                s.append('\n');
            }
            
            textArea.setText(s.toString());
        }
        
    }
    
}
