import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminModifierLivre extends JFrame {

    private Connexion connexion;
    private int bookID;

    public AdminModifierLivre(int bookID) {
        super("Modifier un livre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.bookID = bookID;
        connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel titleLabel = new JLabel("Modifier le livre");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Champs de saisie pour les détails du livre
        JTextField titleField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextField releaseDateField = new JTextField(20);
        JTextField imageUrlField = new JTextField(20); // Champ pour l'URL de l'image
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        JTextField nbPagesField = new JTextField(20);

        // Bouton pour enregistrer les modifications
        JButton saveButton = new JButton("Enregistrer la modification");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String genre = genreField.getText();
                int releaseDate;
                try {
                    releaseDate = Integer.parseInt(releaseDateField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir une année valide pour la date de parution");
                    return;
                }
                String description = descriptionArea.getText();
                int nbPages;
                try {
                    nbPages = Integer.parseInt(nbPagesField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Veuillez saisir un nombre de pages valide");
                    return;
                }
                String imageUrl = imageUrlField.getText(); // Récupération de l'URL de l'image

                // Requête SQL
                String sql = "UPDATE books SET TITLE=?, GENRE=?, RELEASE_DATE=?, DESCRIPTION=?, NB_PAGES=?, IMAGE=? WHERE ID=?";
                try {
                    PreparedStatement preparedStatement = cn.prepareStatement(sql);
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, genre);
                    preparedStatement.setInt(3, releaseDate);
                    preparedStatement.setString(4, description);
                    preparedStatement.setInt(5, nbPages);
                    preparedStatement.setString(6, imageUrl);
                    preparedStatement.setInt(7, bookID);

                    preparedStatement.executeUpdate();

                    // Affichage d'un message si ça a marché
                    JOptionPane.showMessageDialog(null, "Livre modifié avec succès !");

                    // Fermer la fenêtre après avoir enregistré les modifications
                    dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Affichage d'un message d'erreur si échec
                    JOptionPane.showMessageDialog(null, "Erreur lors de la modification du livre !");
                }
            }
        });

        // Récupérer les détails du livre sélectionné et les afficher dans les champs appropriés
        String query = "SELECT * FROM books WHERE ID=?";
        try {
            PreparedStatement preparedStatement = cn.prepareStatement(query);
            preparedStatement.setInt(1, bookID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                titleField.setText(rs.getString("TITLE"));
                genreField.setText(rs.getString("GENRE"));
                releaseDateField.setText(String.valueOf(rs.getInt("RELEASE_DATE")));
                imageUrlField.setText(rs.getString("IMAGE")); // Affichage de l'URL de l'image
                descriptionArea.setText(rs.getString("DESCRIPTION"));
                nbPagesField.setText(String.valueOf(rs.getInt("NB_PAGES")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        mainPanel.add(createLabelAndTextField("Titre:", titleField));
        mainPanel.add(createLabelAndTextField("Genre:", genreField));
        mainPanel.add(createLabelAndTextField("Date de parution:", releaseDateField));
        mainPanel.add(createLabelAndTextField("URL de l'image:", imageUrlField)); // Champ pour l'URL de l'image
        mainPanel.add(createLabelAndTextArea("Description:", descriptionScrollPane));
        mainPanel.add(createLabelAndTextField("Nombre de pages:", nbPagesField));
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
        SwingUtilities.invokeLater(() -> new AdminModifierLivre(1)); // Remplacez 1 par l'ID du livre à modifier
    }
}
