import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminNouvAuteur extends JFrame {

    private Connexion connexion;

    public AdminNouvAuteur() {
        super("Ajouter un nouvel auteur");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel titleLabel = new JLabel("Créer un nouvel auteur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Champs de saisie
        JTextField surnameField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField styleField = new JTextField(20);
        JTextArea biographyArea = new JTextArea(5, 20);
        JScrollPane biographyScrollPane = new JScrollPane(biographyArea);
        JTextField birthDateField = new JTextField(20);
        JTextField deathDateField = new JTextField(20);

        // Bouton pour enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String surname = surnameField.getText();
                String name = nameField.getText();
                String style = styleField.getText();
                String biography = biographyArea.getText();
                int birthDate = Integer.parseInt(birthDateField.getText());
                int deathDate = Integer.parseInt(deathDateField.getText());

                // Requête SQL
                String sql = "INSERT INTO authors (SURNAME, NAME, STYLE, BIOGRAPHY, BIRTH_DATE, DEATH_DATE) VALUES (?, ?, ?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = cn.prepareStatement(sql);
                    preparedStatement.setString(1, surname);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, style);
                    preparedStatement.setString(4, biography);
                    preparedStatement.setInt(5, birthDate);
                    preparedStatement.setInt(6, deathDate);

                    preparedStatement.executeUpdate();

                    // Affichage d'un message si ça a marché
                    JOptionPane.showMessageDialog(null, "Nouvel auteur ajouté avec succès !");

                    // Remise à zéro des champs de saisie
                    surnameField.setText("");
                    nameField.setText("");
                    styleField.setText("");
                    biographyArea.setText("");
                    birthDateField.setText("");
                    deathDateField.setText("");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Affichage d'un message d'erreur si d'échec
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout de l'auteur !");
                }
            }
        });

        mainPanel.add(createLabelAndTextField("Prénom:", surnameField));
        mainPanel.add(createLabelAndTextField("Nom:", nameField));
        mainPanel.add(createLabelAndTextField("Style:", styleField));
        mainPanel.add(createLabelAndTextArea("Biographie:", biographyScrollPane));
        mainPanel.add(createLabelAndTextField("Date de naissance:", birthDateField));
        mainPanel.add(createLabelAndTextField("Date de mort:", deathDateField));
        mainPanel.add(saveButton);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(new JScrollPane(mainPanel));

        // Taille de la fenêtre
        setSize(400, 500);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLabelAndTextField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    // Méthode utilitaire pour créer un label et un champ de texte associé
    private JPanel createLabelAndTextArea(String labelText, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminNouvAuteur::new);
    }
}
