import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class EmpruntLivre extends JFrame {

    private JPanel mainPanel;
    private int bookID;
    private Connexion connexion;
    private JComboBox<String> userComboBox;
    private JTextField leasingDateField;
    private JTextField returnDateField;

    public EmpruntLivre(int bookID, String bookTitle, Connexion connexion) {
        super("Emprunt du livre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.bookID = bookID;
        this.connexion = connexion;

        this.mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Emprunt du livre : " + bookTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panneau d'affichage
        JPanel formPanel = new JPanel(new GridLayout(3, 2));

        // Sélection de l'utilisateur
        JLabel userLabel = new JLabel("Utilisateur :");
        formPanel.add(userLabel);

        userComboBox = new JComboBox<>();
        loadUserList();
        formPanel.add(userComboBox);

        // Date d'emprunt
        JLabel leasingDateLabel = new JLabel("Date d'emprunt (aaaa-mm-jj) :"); // au format aaaa-mm-jj
        formPanel.add(leasingDateLabel);

        leasingDateField = new JTextField();
        formPanel.add(leasingDateField);

        // Date de retour
        JLabel returnDateLabel = new JLabel("Date de retour (aaaa-mm-jj) :"); // au format aaaa-mm-jj
        formPanel.add(returnDateLabel);

        returnDateField = new JTextField();
        formPanel.add(returnDateField);

        this.mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Réaliser l'emprunt");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userID = getUserIDFromComboBox();
                    String leasingDate = leasingDateField.getText();
                    String returnDate = returnDateField.getText();
                    if (userID != -1 && !leasingDate.isEmpty() && !returnDate.isEmpty()) {
                        addBorrowRecord(userID, bookID, leasingDate, returnDate);
                        updateBookAvailability(bookID, 0);
                        JOptionPane.showMessageDialog(null, "Emprunt réalisé avec succès !");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs !");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erreur lors de la réalisation de l'emprunt !");
                }
            }
        });
        this.mainPanel.add(confirmButton, BorderLayout.SOUTH);

        getContentPane().add(this.mainPanel);

        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void loadUserList() {
        String sql = "SELECT ID, CONCAT(SURNAME, ' ', NAME) AS FULL_NAME FROM users";
        try {
            ResultSet rs = connexion.query(sql);
            while (rs.next()) {
                userComboBox.addItem(rs.getString("FULL_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Affichage du prénom et nom de l'utilisateur emprunteur
    private int getUserIDFromComboBox() {
        String selectedUserName = (String) userComboBox.getSelectedItem();
        String[] parts = selectedUserName.split(" ");
        String surname = parts[0];
        String name = parts[1];
        String sql = "SELECT ID FROM users WHERE SURNAME = ? AND NAME = ?";
        try {
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            preparedStatement.setString(1, surname);
            preparedStatement.setString(2, name);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Ajout de l'emprunt à la base  de données
    private void addBorrowRecord(int userID, int bookID, String leasingDate, String returnDate) throws SQLException {
        String sql = "INSERT INTO borrow (USER_ID, BOOK_ID, LEASING_DATE, RETURN_DATE) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, bookID);
            preparedStatement.setString(3, leasingDate);
            preparedStatement.setString(4, returnDate);
            preparedStatement.executeUpdate();
        }
    }

    // Changement de la disponibilité du livre
    private void updateBookAvailability(int bookID, int newAvailability) throws SQLException {
        String sql = "UPDATE books SET availability = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql)) {
            preparedStatement.setInt(1, newAvailability);
            preparedStatement.setInt(2, bookID);
            preparedStatement.executeUpdate();
        }
    }
}
