import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class AdminNouvLivre extends JFrame {

    private Connexion connexion;
    private JComboBox<String> authorComboBox;

    public AdminNouvLivre() {
        super("Créer un nouveau livre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel titleLabel = new JLabel("Créer un nouveau livre");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel);

        // Champs de saisie
        JTextField titleField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextField releaseDateField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        JTextField nbPagesField = new JTextField(20);

        // Menu déroulant pour sélectionner l'auteur
        authorComboBox = new JComboBox<>();
        populateAuthorComboBox(); // Remplir le menu déroulant avec les auteurs existants

        // Bouton pour enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authorIds.isEmpty() || authorIds.get(authorComboBox.getSelectedIndex()) == null) {
                    JOptionPane.showMessageDialog(null, "Veuillez sélectionner un auteur");
                    return;
                }

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

                int authorId = authorIds.get(authorComboBox.getSelectedIndex());

                // Requête SQL
                String sql = "INSERT INTO books (TITLE, AUTHOR_ID, GENRE, RELEASE_DATE, DESCRIPTION, NB_PAGES) VALUES (?, ?, ?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = cn.prepareStatement(sql);
                    preparedStatement.setString(1, title);
                    preparedStatement.setInt(2, authorId);
                    preparedStatement.setString(3, genre);
                    preparedStatement.setInt(4, releaseDate);
                    preparedStatement.setString(5, description);
                    preparedStatement.setInt(6, nbPages);

                    preparedStatement.executeUpdate();

                    // Affichage d'un message si ça a marché
                    JOptionPane.showMessageDialog(null, "Nouveau livre ajouté avec succès !");

                    // Fermer la fenêtre actuelle de AdminNouvLivre
                    dispose();
                    // Créer une nouvelle instance de AdminGestionLivres pour revenir à cette page
                    new AdminGestionLivres();
                    

                    // Remise à zéro des champs de saisie
                    titleField.setText("");
                    genreField.setText("");
                    releaseDateField.setText("");
                    descriptionArea.setText("");
                    nbPagesField.setText("");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Affichage d'un message d'erreur si d'échec
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du livre !");
                }
            }
        });


        mainPanel.add(createLabelAndTextField("Titre:", titleField));
        mainPanel.add(createLabelAndTextField("Genre:", genreField));
        mainPanel.add(createLabelAndTextField("Date de parution:", releaseDateField));
        mainPanel.add(createLabelAndTextArea("Description:", descriptionScrollPane));
        mainPanel.add(createLabelAndTextField("Nombre de pages:", nbPagesField));
        mainPanel.add(new JLabel("Auteur:"));
        mainPanel.add(authorComboBox);
        mainPanel.add(saveButton);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(new JScrollPane(mainPanel));

        // Taille de la fenêtre
        setSize(400, 600);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Déclaration de authorIds
    private List<Integer> authorIds;

    // Modifiez la méthode populateAuthorComboBox() pour remplir à la fois le menu déroulant et la liste des ID des auteurs
    private void populateAuthorComboBox() {
        authorIds = new ArrayList<>();
        String sql = "SELECT ID, CONCAT(NAME, ' ', SURNAME) AS AUTHOR_NAME FROM authors";
        try {
            ResultSet rs = connexion.query(sql);
            while (rs.next()) {
                int authorId = rs.getInt("ID");
                authorComboBox.addItem(rs.getString("AUTHOR_NAME"));
                authorIds.add(authorId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        SwingUtilities.invokeLater(AdminNouvLivre::new);
    }
}
