import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminGestionLivres extends JFrame {

    private Connexion connexion;
    private JPanel mainPanel;

    public AdminGestionLivres() {
        super("Gestion des livres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        mainPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Gestion des livres");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        // Affichage de la liste des livres
        JPanel booksPanel = new JPanel(new GridLayout(0, 1));
        loadBooks(booksPanel);
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau pour les boutons en bas de la page
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton Actualiser
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booksPanel.removeAll();
                loadBooks(booksPanel);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        buttonPanel.add(refreshButton);

        // Bouton Ajouter un nouveau livre
        JButton createButton = new JButton("Ajouter un nouveau livre");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de création d'un nouveau livre
                dispose(); // Fermer la fenêtre actuelle
                new AdminNouvLivre();
            }
        });
        buttonPanel.add(createButton);

        // Ajout du panneau de boutons au panneau principal
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(400, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Charger la liste des livres dans le panneau
    private void loadBooks(JPanel booksPanel) {
        String sql = "SELECT b.ID, b.TITLE, CONCAT(a.NAME, ' ', a.SURNAME) AS AUTHOR_NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, b.availability " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID";
        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des informations des livres avec leurs boutons respectifs
            while (rs.next()) {
                JPanel livrePanel = new JPanel(new BorderLayout());
                livrePanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations du livre

                JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
                JLabel authorLabel = new JLabel("Auteur: " + rs.getString("AUTHOR_NAME"));
                JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
                JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
                JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
                JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));
                JLabel availabilityLabel = new JLabel("Disponibilité: " + (rs.getInt("availability") == 1 ? "Disponible" : "Non disponible"));

                infoPanel.add(titleLabel);
                infoPanel.add(authorLabel);
                infoPanel.add(genreLabel);
                infoPanel.add(releaseDateLabel);
                infoPanel.add(descriptionLabel);
                infoPanel.add(nbPagesLabel);
                infoPanel.add(availabilityLabel); // Ajout de l'étiquette de disponibilité

                // Boutons
                JButton deleteButton = new JButton("Supprimer");
                JButton editButton = new JButton("Modifier");

                // Récupération de l'ID du livre
                int bookID = rs.getInt("ID");

                // Bouton Supprimer
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce livre ?");
                        if (confirmation == JOptionPane.YES_OPTION) {
                            // Suppression du livre dans la base de données
                            deleteBook(bookID);
                            // Rafraîchir la fenêtre
                            booksPanel.removeAll();
                            loadBooks(booksPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                        }
                    }
                });


                // Bouton Modifier
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Ouvrir la fenêtre de modification du livre
                        new AdminModifierLivre(bookID);
                    }
                });


                infoPanel.add(deleteButton);
                infoPanel.add(editButton);

                livrePanel.add(infoPanel, BorderLayout.CENTER);

                booksPanel.add(livrePanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Suppression d'un livre
    private void deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE ID=?";
        try {
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            preparedStatement.setInt(1, bookID);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Livre supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression du livre !");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminGestionLivres::new);
    }
}
