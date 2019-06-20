package matrixdesigner;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class AssetBoard extends JPanel {
    
    public static int MAX_ASSETS=1000;
    
    private int selectedAssetID;
    private final JLabel status;
    private Object[][] assets;
    
    public AssetBoard(JLabel status) {
        super();
        this.status = status;
        setBorder(BorderFactory.createTitledBorder("Assets"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        assets = new Object[MAX_ASSETS][2];
        selectedAssetID = -1;
    }
    
    public void importAssets() {
        
        EventQueue.invokeLater(() -> {
            AssetLoader frame = new AssetLoader(this);
            frame.setVisible(true);
        });
         
    }
    
    public Object[][] getData() {
        return assets;
    }
    public void putData(Object[][] data) {
        assets = data;
        selectedAssetID = -1;
        removeAll();
        for(int i=0; i<MAX_ASSETS; i++){
            if(assets[i][0] == null || ((String)assets[i][0]).isEmpty()) continue;
            add(Box.createRigidArea(new Dimension(10,10)));
            String filename = (String)assets[i][0];
            String idStr = ((Integer)assets[i][1]).toString();
            ImageIcon icon = new ImageIcon(filename);
            Image image = icon.getImage();
            if(Math.max(icon.getIconHeight(), icon.getIconWidth()) > 200)
                image = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            else if(Math.min(icon.getIconHeight(), icon.getIconWidth()) < 20)
                image = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            else
                image = image.getScaledInstance(Math.max(icon.getIconHeight(), icon.getIconWidth()), Math.max(icon.getIconHeight(), icon.getIconWidth()), Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            JButton tmp = new JButton(icon);
            tmp.setToolTipText("<html>Path: "+filename+"<br>ID: "+assets[i][1]+"</html>");
            tmp.addActionListener((e)-> { selectedAssetID = Integer.parseInt(idStr); status.setText("Selected Asset ID: "+idStr);});
            add(tmp);
        }
        revalidate();
        repaint();
    }
    
    
    public Image getAssetFromID(int id) {
        for(int i=0; i<MAX_ASSETS; i++){
            if(assets[i][1] == null) continue;
            if((Integer)assets[i][1] == id) 
                return (new ImageIcon((String)assets[i][0]).getImage());
        }
        return null;
    }
    
    public int getSelectedAssetID() {
        return selectedAssetID;
    }
    
    public void clear() {
        assets = new Object[MAX_ASSETS][2];
        selectedAssetID = -1;
        removeAll();
        revalidate();
        repaint();
    }
    
    private class AssetLoader extends JFrame {
        
        private final Object[][] assetsBackup;
        private final JPanel owner;
        private int currentAsset;
        private int firstIndex, lastIndex;
        
        public AssetLoader(JPanel owner) {
            this.owner = owner;
            assetsBackup = new Object[MAX_ASSETS][2];
            initUI();
        }
        
        private void getNextAsset() {
            for(currentAsset=0; currentAsset<MAX_ASSETS; currentAsset++)
                if(assetsBackup[currentAsset][0] == null)
                    return;
        }
        
        private void initUI() {
    // Frame init
            setSize(600,400);
            setResizable(false);
            setTitle("Asset Loader");
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
    // Data backup        
            for(int i=0; i<MAX_ASSETS; i++) {
                assetsBackup[i][0] = assets[i][0];
                assetsBackup[i][1] = assets[i][1];
            }
    // Cancel Button
            JButton cancelButton = new JButton("Cancel");
            GridBagConstraints cancelButtonGBC = new GridBagConstraints();
            cancelButtonGBC.insets = new Insets(10,0,0,30);
            cancelButtonGBC.anchor = GridBagConstraints.LINE_END;
            cancelButtonGBC.gridx = 4;
            cancelButton.addActionListener((e) -> { this.dispose(); });
    // Save Button
            JButton saveButton = new JButton("Save");
            GridBagConstraints saveButtonGBC = new GridBagConstraints();
            saveButtonGBC.insets = new Insets(10,25,0,0);
            saveButtonGBC.anchor = GridBagConstraints.LINE_END;
            saveButtonGBC.gridx = 3;
            saveButton.addActionListener((e)->{ 
                for(int i=0; i<MAX_ASSETS; i++) {
                    assets[i][0] = assetsBackup[i][0];
                    assets[i][1] = assetsBackup[i][1];
                }
                this.dispose();
            });
    // Table
            JTable table = new JTable(new TModel());
            
            JScrollPane tableScroll = new JScrollPane(table);
            GridBagConstraints tableGBC = new GridBagConstraints();
            tableGBC.fill = GridBagConstraints.BOTH;
            tableGBC.gridy = 1;
            tableGBC.gridwidth = 5;
            tableGBC.insets = new Insets(15,20,20,20);
            tableGBC.weightx = tableGBC.weighty = 1;
    // Import File
            JButton importButton = new JButton("Import file..");
            GridBagConstraints importButtonGBC = new GridBagConstraints();
            importButtonGBC.anchor = GridBagConstraints.LINE_START;
            importButtonGBC.insets = new Insets(10,30,0,0);
            importButton.addActionListener((e)->{
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public String getDescription() {
                        return "PNG files";
                    }
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        else 
                            return f.getName().toLowerCase().endsWith(".png");
                    }
                });
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    getNextAsset();
                    if(currentAsset == MAX_ASSETS) {
                        JOptionPane.showMessageDialog(this, "Maximum asset limit is 1000!", "Error!", JOptionPane.ERROR_MESSAGE);
                        table.repaint();
                        return;
                    }
                    File file = fileChooser.getSelectedFile();
                    String valStr = JOptionPane.showInputDialog(this, "Enter Asset ID:");
                    if(valStr == null) return;
                    int val;
                    try {
                        val = Integer.parseInt(valStr);
                    } catch( NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Asset ID!", "Error!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if(val < 1) {
                        JOptionPane.showMessageDialog(this, "Asset ID must be greater than 0!", "Error!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    for(int i=0; i<MAX_ASSETS; i++){
                        if (assetsBackup[i][1] != null)
                            if((int)assetsBackup[i][1]==val) {
                                JOptionPane.showMessageDialog(this, "Asset IDs must be different!", "Error!", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                    }
                            
                    
                    assetsBackup[currentAsset][1] = val;
                    assetsBackup[currentAsset][0] = file.getPath();
                }
                table.repaint();
            });
    // Import Directory
            JButton importDirButton = new JButton("Import directory..");
            GridBagConstraints importDirButtonGBC = new GridBagConstraints();
            importDirButtonGBC.anchor = GridBagConstraints.LINE_START;
            importDirButtonGBC.insets = new Insets(10,5,0,0);
            importDirButtonGBC.gridx = 1;
            importDirButton.addActionListener((e)->{
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    int val = 1;
                    for( File f : file.listFiles(new java.io.FileFilter() { @Override public boolean accept(File f) { if (f.isDirectory()) return true; else return f.getName().toLowerCase().endsWith(".png"); } }) ) {
                        getNextAsset();
                        if(currentAsset == MAX_ASSETS) {
                            JOptionPane.showMessageDialog(this, "Maximum asset limit is 1000!", "Error!", JOptionPane.ERROR_MESSAGE);
                            table.repaint();
                            return;
                        }
                        for(int i=0; i<MAX_ASSETS; i++) {
                            if(assetsBackup[i][1]!=null)
                                if((int)assetsBackup[i][1] == val) {
                                    val++;
                                    i=-1;
                                    continue;
                                }
                        }
                        assetsBackup[currentAsset][1] = val;
                        assetsBackup[currentAsset][0] = f.getPath();
                    }
                }
                table.repaint();
            });
    // Remove File
            JButton removeButton = new JButton("Remove Assets");
            GridBagConstraints removeButtonGBC = new GridBagConstraints();
            removeButtonGBC.anchor = GridBagConstraints.LINE_START;
            removeButtonGBC.insets = new Insets(10,5,0,5);
            removeButtonGBC.gridx = 2;
            removeButton.addActionListener((e)-> {
                for(int i : table.getSelectedRows())
                    assetsBackup[i][0] = assetsBackup[i][1] = null;
                table.repaint();
            });
    // Build GUI
            add(importButton, importButtonGBC);
            add(importDirButton, importDirButtonGBC);
            add(saveButton, saveButtonGBC);
            add(cancelButton, cancelButtonGBC);
            add(removeButton, removeButtonGBC);
            add(tableScroll, tableGBC);
        }
        
        @Override
        public void dispose() {
            
            for(int i=0; i<MAX_ASSETS; i++) {
                if(assets[i][0] != null) {
                    for(int j=i-1; j>=0 && assets[j][0]==null; j--){
                        assets[j][0] = assets[j+1][0];
                        assets[j][1] = assets[j+1][1];
                        assets[j+1][0] = null;
                        assets[j+1][1] = null;
                    }
                }
            }

            owner.removeAll();
            for(int i=0; i < MAX_ASSETS && assets[i][0] != null; i++){
                owner.add(Box.createRigidArea(new Dimension(10,10)));
                
                String filename = (String)assets[i][0];
                String idStr = ((Integer)assets[i][1]).toString();
                
                ImageIcon icon = new ImageIcon(filename);
                Image image = icon.getImage();
                if(Math.max(icon.getIconHeight(), icon.getIconWidth()) > 200)
                    image = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                else if(Math.min(icon.getIconHeight(), icon.getIconWidth()) < 20)
                    image = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                else
                    image = image.getScaledInstance(Math.max(icon.getIconHeight(), icon.getIconWidth()), Math.max(icon.getIconHeight(), icon.getIconWidth()), Image.SCALE_SMOOTH);
                icon = new ImageIcon(image);
                JButton tmp = new JButton(icon);
                
                tmp.setToolTipText("<html>Path: "+filename+"<br>ID: "+assets[i][1]+"</html>");
                tmp.addActionListener((e)-> { selectedAssetID = Integer.parseInt(idStr); status.setText("Selected Asset ID: "+idStr);});
                
                owner.add(tmp);
            }
            
            owner.revalidate();
            owner.repaint();  
            super.dispose();
        }
        
        private class TModel extends AbstractTableModel {
            @Override
            public int getRowCount() {
                return MAX_ASSETS;
            }
            @Override
            public int getColumnCount() {
                return 2;
            }
            @Override
            public Object getValueAt(int row, int col) {
              return assetsBackup[row][col];
            }
            @Override
            public String getColumnName(int col) {
                return (col == 0)?  "Asset's Path": "Asset's ID";
            }
        }

    }
    
}
