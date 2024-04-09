import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel loginTitle;
    private int userId; // Ajout de la variable pour stocker l'ID de l'utilisateur connecté

    public LoginFrame() {
        // Paramètres de la fenêtre de connexion
        setTitle("Système de gestion de bibliothèque - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialisation des composants
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Se connecter");
        registerButton = new JButton("Créer un compte");
        loginTitle = new JLabel("Connexion", SwingConstants.CENTER);

        // Style des composants
        loginButton.setBackground(Color.LIGHT_GRAY);
        registerButton.setBackground(Color.LIGHT_GRAY);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));

        // Configuration du layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(loginTitle);
        panel.add(Box.createVerticalStrut(20)); // Espacement vertical
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20)); // Espacement vertical
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(5)); // Espacement vertical
        panel.add(registerButton);

        // Ajout des composants à la fenêtre
        add(panel, BorderLayout.CENTER);

        // Ajustement de la taille de la fenêtre au contenu
        pack();
        setLocationRelativeTo(null); // Centrage de la fenêtre sur l'écran
        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ouvrirAffichageEmprunts(); // Utilisation de la méthode pour gérer l'authentification et l'ouverture d'AffichageEmprunts
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ouvre la fenêtre de création de compte
                new RegisterFrame().setVisible(true);
                LoginFrame.this.dispose();
            }
        });
    }

    private void ouvrirAffichageEmprunts() {
        if (authentifier(emailField.getText(), String.valueOf(passwordField.getPassword()))) {
            JOptionPane.showMessageDialog(LoginFrame.this, "Connexion réussie !");
            // Redirection en fonction du type de compte
            if (isAdmin(emailField.getText())) {
                new AdminGestionLivres().setVisible(true);
            } else {
                new AffichageLivres(userId).setVisible(true); // Passage de l'ID de l'utilisateur
            }
            LoginFrame.this.dispose();
        } else {
            JOptionPane.showMessageDialog(LoginFrame.this, "Email ou mot de passe incorrect.",
                    "Erreur de Connexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authentifier(String email, String password) {
        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "SELECT ID FROM users WHERE EMAIL = ? AND PASSWORD = ?";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, email);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    userId = rs.getInt("ID"); // Stockage de l'ID de l'utilisateur connecté
                    return true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private boolean isAdmin(String email) {
        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "SELECT ADMIN FROM users WHERE EMAIL = ?";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    return rs.getInt("ADMIN") == 1; // Si ADMIN vaut 1, l'utilisateur est un admin
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
