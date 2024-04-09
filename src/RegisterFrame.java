import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterFrame() {
        // Paramètres de la fenêtre de création de compte
        setTitle("Créer un compte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialisation des composants
        surnameField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        registerButton = new JButton("S'inscrire");
        backButton = new JButton("Retour");

        // Style des boutons
        registerButton.setBackground(Color.LIGHT_GRAY);
        backButton.setBackground(Color.LIGHT_GRAY);

        // Configuration du layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Prénom :"));
        panel.add(surnameField);
        panel.add(new JLabel("Nom :"));
        panel.add(nameField);
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Mot de passe :"));
        panel.add(passwordField);

        // Espacement vertical entre les composants
        panel.add(Box.createVerticalStrut(10));

        // Ajout des boutons avec espacement
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10)); // Espacement vertical
        panel.add(backButton);

        // Ajout du panel à la fenêtre
        add(panel, BorderLayout.CENTER);

        // Ajustement de la taille de la fenêtre au contenu
        setSize(300, 300); // Agrandir un peu la taille de la fenêtre
        setLocationRelativeTo(null); // Centrage de la fenêtre sur l'écran
        setVisible(true);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String surname = surnameField.getText();
                String name = nameField.getText();
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                if (creerUtilisateur(surname, name, email, password)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Compte créé avec succès !");
                    // Redirige vers la page de connexion
                    new LoginFrame().setVisible(true);
                    RegisterFrame.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Erreur lors de la création du compte.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retourne à la page de connexion
                new LoginFrame().setVisible(true);
                RegisterFrame.this.dispose();
            }
        });
    }

    private boolean creerUtilisateur(String surname, String name, String email, String password) {
        Connexion connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        if (cn != null) {
            try {
                String sql = "INSERT INTO users (SURNAME, NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = cn.prepareStatement(sql);
                pst.setString(1, surname);
                pst.setString(2, name);
                pst.setString(3, email);
                pst.setString(4, password);

                int rowsInserted = pst.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterFrame::new);
    }
}
