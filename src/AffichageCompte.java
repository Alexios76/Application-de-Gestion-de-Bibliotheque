import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AffichageCompte extends JFrame {
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton saveButton;

    public AffichageCompte() {

        setupUI();

        loadUserInfo();

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerModifications();
            }
        });
    }

    private void setupUI() {
        setTitle("Votre compte");

        JMenuBar menuBar = new JMenuBar();

        JMenuItem livresBibliothequeItem = new JMenuItem("Livres de la bibliothèque");
        livresBibliothequeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageLivres();
            }
        });
        menuBar.add(livresBibliothequeItem);

        JMenuItem biographieAuteursItem = new JMenuItem("Biographie des auteurs");
        biographieAuteursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageAuteurs();
            }
        });
        menuBar.add(biographieAuteursItem);

        JMenuItem mesEmpruntsItem = new JMenuItem("Mes emprunts");
        mesEmpruntsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageEmprunts();
            }
        });
        menuBar.add(mesEmpruntsItem);

        JMenuItem monCompteItem = new JMenuItem("Mon compte");
        monCompteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageCompte();
            }
        });
        menuBar.add(monCompteItem);

        JMenuItem deconnexionItem = new JMenuItem("Déconnexion");
        deconnexionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });
        menuBar.add(deconnexionItem);

        setJMenuBar(menuBar);

        JLabel titleLabel = new JLabel("Votre compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        surnameField = new JTextField(10);
        nameField = new JTextField(10);
        emailField = new JTextField(10);
        passwordField = new JPasswordField(10);
        saveButton = new JButton("Enregistrer");

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(surnameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email :"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nouveau mot de passe :"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(saveButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadUserInfo() {
        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "SELECT SURNAME, NAME, EMAIL FROM users WHERE ID = ?";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setInt(1, Utilisateur.getId());
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    surnameField.setText(rs.getString("SURNAME"));
                    nameField.setText(rs.getString("NAME"));
                    emailField.setText(rs.getString("EMAIL"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void enregistrerModifications() {
        String surname = surnameField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String newPassword = String.valueOf(passwordField.getPassword());

        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "UPDATE users SET SURNAME = ?, NAME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, surname);
                pst.setString(2, name);
                pst.setString(3, email);
                pst.setString(4, newPassword);
                pst.setInt(5, Utilisateur.getId());

                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    Utilisateur.modification(surname, name, email, newPassword);
                    JOptionPane.showMessageDialog(AffichageCompte.this, "Modifications enregistrées avec succès !");
                } else {
                    JOptionPane.showMessageDialog(AffichageCompte.this, "Erreur lors de l'enregistrement des modifications.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffichageCompte());
    }
}