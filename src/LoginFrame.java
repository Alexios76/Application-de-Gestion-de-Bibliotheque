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
    private JLabel loginTitle;

    public LoginFrame() {
        // Paramètres de la fenêtre de connexion
        setTitle("Système de gestion de bibliothèque - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialisation des composants
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Se connecter");
        loginTitle = new JLabel("Connexion", SwingConstants.CENTER);

        // Style des composants
        loginButton.setBackground(Color.LIGHT_GRAY);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));

        // Configuration du layout
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);

        // Ajout des composants à la fenêtre
        add(loginTitle, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);


        // Ajustement de la taille de la fenêtre au contenu
        pack();
        setLocationRelativeTo(null); // Centrage de la fenêtre sur l'écran
        setVisible(true);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());
                if (authentifier(email, password)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Connexion réussie !");
                    // Passe à la fenêtre principale ici
                    new MainPageFrame().setVisible(true);
                    LoginFrame.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Email ou mot de passe incorrect.",
                            "Erreur de Connexion", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private boolean authentifier(String email, String password) {
        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "SELECT * FROM users WHERE EMAIL = ? AND PASSWORD = ?";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, email);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                return rs.next(); // Si le résultat est non vide, les identifiants sont corrects
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
