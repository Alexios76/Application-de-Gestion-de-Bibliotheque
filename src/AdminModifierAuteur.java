import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminModifierAuteur extends JFrame {

    private Connexion connexion;
    private int authorID;

    public AdminModifierAuteur(int authorID) {
        super("Modifier un auteur");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.authorID = authorID;
        connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel titleLabel = new JLabel("Modifier l'auteur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Champs de saisie pour les détails de l'auteur
        JTextField surnameField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField styleField = new JTextField(20);
        JTextArea biographyArea = new JTextArea(5, 20);
        JScrollPane biographyScrollPane = new JScrollPane(biographyArea);
        JTextField birthDateField = new JTextField(20);
        JTextField deathDateField = new JTextField(20);

        // Bouton pour enregistrer les modifications
        JButton saveButton = new JButton("Enregistrer la modification");
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
                String sql = "UPDATE authors SET SURNAME=?, NAME=?, STYLE=?, BIOGRAPHY=?, BIRTH_DATE=?, DEATH_DATE=? WHERE ID=?";
                try {
                    PreparedStatement preparedStatement = cn.prepareStatement(sql);
                    preparedStatement.setString(1, surname);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, style);
                    preparedStatement.setString(4, biography);
                    preparedStatement.setInt(5, birthDate);
                    preparedStatement.setInt(6, deathDate);
                    preparedStatement.setInt(7, authorID);

                    preparedStatement.executeUpdate();

                    // Affichage d'un message si ça a marché
                    JOptionPane.showMessageDialog(null, "Auteur modifié avec succès !");

                    // Fermer la fenêtre après avoir enregistré les modifications
                    dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Affichage d'un message d'erreur si échec
                    JOptionPane.showMessageDialog(null, "Erreur lors de la modification de l'auteur !");
                }
            }
        });

        // Récupérer les détails de l'auteur sélectionné et les afficher dans les champs appropriés
        String query = "SELECT * FROM authors WHERE ID=?";
        try {
            PreparedStatement preparedStatement = cn.prepareStatement(query);
            preparedStatement.setInt(1, authorID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                surnameField.setText(rs.getString("SURNAME"));
                nameField.setText(rs.getString("NAME"));
                styleField.setText(rs.getString("STYLE"));
                biographyArea.setText(rs.getString("BIOGRAPHY"));
                birthDateField.setText(String.valueOf(rs.getInt("BIRTH_DATE")));
                deathDateField.setText(String.valueOf(rs.getInt("DEATH_DATE")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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
        setSize(400, 600);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Méthode utilitaire pour créer un label et un champ de texte associé
    private JPanel createLabelAndTextField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    // Méthode utilitaire pour créer un label et une zone de texte associée
    private JPanel createLabelAndTextArea(String labelText, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminModifierAuteur(1)); // Remplacez 1 par l'ID de l'auteur à modifier
    }
}
