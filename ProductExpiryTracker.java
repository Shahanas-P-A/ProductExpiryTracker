import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProductExpiryTracker extends JFrame {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expiry_tracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color EXPIRED_COLOR = new Color(231, 76, 60);
    private static final Color CRITICAL_COLOR = new Color(230, 126, 34);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color SAFE_COLOR = new Color(46, 204, 113);
    
    // GUI Components
    private JTextField txtProductName, txtCategory, txtQuantity, txtExpiryDate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnExpiringSoon;
    private JLabel lblTitle, lblStats;
    
    public ProductExpiryTracker() {
        setTitle("Product Expiry Tracker");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeDatabase();
        initComponents();
        loadProducts();
    }
    
    private void initializeDatabase() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "product_name VARCHAR(100) NOT NULL," +
                    "category VARCHAR(50)," +
                    "quantity INT," +
                    "expiry_date DATE NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database initialization failed: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        lblTitle = new JLabel("📦 Product Expiry Tracker");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);
        
        lblStats = new JLabel();
        lblStats.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStats.setForeground(Color.WHITE);
        titlePanel.add(lblStats, BorderLayout.EAST);
        
        // Input panel with styled components
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font inputFont = new Font("Arial", Font.PLAIN, 13);
        
        // Product Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblProductName = new JLabel("Product Name:");
        lblProductName.setFont(labelFont);
        inputPanel.add(lblProductName, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtProductName = createStyledTextField();
        txtProductName.setFont(inputFont);
        inputPanel.add(txtProductName, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(labelFont);
        inputPanel.add(lblCategory, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCategory = createStyledTextField();
        txtCategory.setFont(inputFont);
        inputPanel.add(txtCategory, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setFont(labelFont);
        inputPanel.add(lblQuantity, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtQuantity = createStyledTextField();
        txtQuantity.setFont(inputFont);
        inputPanel.add(txtQuantity, gbc);
        
        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel lblExpiryDate = new JLabel("Expiry Date (YYYY-MM-DD):");
        lblExpiryDate.setFont(labelFont);
        inputPanel.add(lblExpiryDate, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtExpiryDate = createStyledTextField();
        txtExpiryDate.setFont(inputFont);
        inputPanel.add(txtExpiryDate, gbc);
        
        // Button panel with styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAdd = createStyledButton("➕ Add Product", new Color(46, 204, 113));
        btnUpdate = createStyledButton("✏️ Update", new Color(52, 152, 219));
        btnDelete = createStyledButton("🗑️ Delete", new Color(231, 76, 60));
        btnRefresh = createStyledButton("🔄 Refresh", new Color(149, 165, 166));
        btnExpiringSoon = createStyledButton("⚠️ Expiring Soon", new Color(230, 126, 34));
        
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnRefresh.addActionListener(e -> loadProducts());
        btnExpiringSoon.addActionListener(e -> showExpiringSoon());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExpiringSoon);
        
        // Table with custom styling
        String[] columns = {"ID", "Product Name", "Category", "Quantity", "Expiry Date", "Days Until Expiry", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(true);
        table.setGridColor(new Color(189, 195, 199));
        
        // Style table header
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(SECONDARY_COLOR);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Custom cell renderer for status colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = tableModel.getValueAt(row, 6).toString();
                    switch (status) {
                        case "EXPIRED":
                            c.setBackground(new Color(255, 235, 235));
                            break;
                        case "CRITICAL":
                            c.setBackground(new Color(255, 243, 224));
                            break;
                        case "WARNING":
                            c.setBackground(new Color(255, 251, 230));
                            break;
                        case "SAFE":
                            c.setBackground(new Color(232, 248, 245));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                    
                    // Status column color
                    if (column == 6) {
                        setFont(new Font("Arial", Font.BOLD, 12));
                        switch (status) {
                            case "EXPIRED":
                                setForeground(EXPIRED_COLOR);
                                break;
                            case "CRITICAL":
                                setForeground(CRITICAL_COLOR);
                                break;
                            case "WARNING":
                                setForeground(WARNING_COLOR);
                                break;
                            case "SAFE":
                                setForeground(SAFE_COLOR);
                                break;
                        }
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(column == 0 || column == 3 || column == 5 ? CENTER : LEFT);
                return c;
            }
        });
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    populateFields();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        // Assemble panels
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add scroll pane for the top panel
        JScrollPane topScrollPane = new JScrollPane(topPanel);
        topScrollPane.setBorder(null);
        topScrollPane.setPreferredSize(new Dimension(0, 280));
        topScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        topScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(topScrollPane, BorderLayout.CENTER);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(25);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void addProduct() {
        if (!validateInputs()) return;
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "INSERT INTO products (product_name, category, quantity, expiry_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtProductName.getText().trim());
            pstmt.setString(2, txtCategory.getText().trim());
            pstmt.setInt(3, Integer.parseInt(txtQuantity.getText().trim()));
            pstmt.setDate(4, Date.valueOf(txtExpiryDate.getText().trim()));
            
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputs()) return;
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "UPDATE products SET product_name=?, category=?, quantity=?, expiry_date=? WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtProductName.getText().trim());
            pstmt.setString(2, txtCategory.getText().trim());
            pstmt.setInt(3, Integer.parseInt(txtQuantity.getText().trim()));
            pstmt.setDate(4, Date.valueOf(txtExpiryDate.getText().trim()));
            pstmt.setInt(5, id);
            
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        int expired = 0, critical = 0, warning = 0, safe = 0;
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM products ORDER BY expiry_date";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("product_name");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                Date expiryDate = rs.getDate("expiry_date");
                
                LocalDate expiry = expiryDate.toLocalDate();
                long daysUntil = ChronoUnit.DAYS.between(today, expiry);
                String status = getStatus(daysUntil);
                
                // Count statistics
                switch (status) {
                    case "EXPIRED": expired++; break;
                    case "CRITICAL": critical++; break;
                    case "WARNING": warning++; break;
                    case "SAFE": safe++; break;
                }
                
                tableModel.addRow(new Object[]{id, name, category, quantity, expiryDate, daysUntil, status});
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Update statistics
            lblStats.setText(String.format("Total: %d | Expired: %d | Critical: %d | Warning: %d | Safe: %d", 
                expired + critical + warning + safe, expired, critical, warning, safe));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showExpiringSoon() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(30);
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM products WHERE expiry_date <= ? AND expiry_date >= ? ORDER BY expiry_date";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(threshold));
            pstmt.setDate(2, Date.valueOf(today));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("product_name");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                Date expiryDate = rs.getDate("expiry_date");
                
                LocalDate expiry = expiryDate.toLocalDate();
                long daysUntil = ChronoUnit.DAYS.between(today, expiry);
                String status = getStatus(daysUntil);
                
                tableModel.addRow(new Object[]{id, name, category, quantity, expiryDate, daysUntil, status});
            }
            
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expiring products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getStatus(long daysUntil) {
        if (daysUntil < 0) return "EXPIRED";
        else if (daysUntil <= 7) return "CRITICAL";
        else if (daysUntil <= 30) return "WARNING";
        else return "SAFE";
    }
    
    private void populateFields() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        txtProductName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtCategory.setText(tableModel.getValueAt(selectedRow, 2).toString());
        txtQuantity.setText(tableModel.getValueAt(selectedRow, 3).toString());
        txtExpiryDate.setText(tableModel.getValueAt(selectedRow, 4).toString());
    }
    
    private void clearFields() {
        txtProductName.setText("");
        txtCategory.setText("");
        txtQuantity.setText("");
        txtExpiryDate.setText("");
        table.clearSelection();
    }
    
    private boolean validateInputs() {
        if (txtProductName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter product name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Date.valueOf(txtExpiryDate.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid date (YYYY-MM-DD)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ProductExpiryTracker tracker = new ProductExpiryTracker();
            tracker.setVisible(true);
        });
    }
}