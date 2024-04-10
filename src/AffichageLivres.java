import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class AffichageLivres extends JFrame {

    private Connexion connexion;
    private List<JPanel> livrePanels;
    private JCheckBox disponibleCheckBox;
    private JPanel livresPanel;
    private int userID;

    public AffichageLivres() {
        super("Livres disponibles - "+ Utilisateur.getName() + " " + Utilisateur.getSurname());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Barre de navigation
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

        // Reste du code pour la fenêtre principale
        JPanel titleSearchPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Livres disponibles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel searchLabel = new JLabel("Recherche : ");
        JTextField searchField = new JTextField(20);
        searchField.addActionListener(new SearchActionListener());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        titleSearchPanel.add(searchPanel, BorderLayout.CENTER);

        // Case à cocher Disponible
        disponibleCheckBox = new JCheckBox("Disponible");
        disponibleCheckBox.setSelected(false); // Par défaut, non cochée
        disponibleCheckBox.addItemListener(new DisponibleCheckBoxListener());
        titleSearchPanel.add(disponibleCheckBox, BorderLayout.SOUTH);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        livresPanel = new JPanel();
        livresPanel.setLayout(new BoxLayout(livresPanel, BoxLayout.Y_AXIS));

        livrePanels = new ArrayList<>();

        // Affichage initial des livres disponibles
        afficherLivresDisponibles();

        mainPanel.add(new JScrollPane(livresPanel), BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(400, 500);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Méthode pour afficher les livres disponibles
    boolean afficherNotes = false;
    private void afficherLivresDisponibles() {
        // Ancienne requête (marche mais n'affiche pas la note qu'a reçu le livre)
        String sql;
        if (!afficherNotes) {
            sql = "SELECT CONCAT(a.SURNAME, ' ', a.NAME) AS AUTHOR_NAME, a.SURNAME, a.NAME, a.ID AS AUTHOR_ID,\n" +
                    "b.TITLE, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, b.IMAGE\n" +
                    "FROM books b INNER JOIN authors a ON b.AUTHOR_ID = a.ID";
        } else {
            // Requête SQL pour récupérer les livres disponibles avec les informations sur les auteurs
            sql = "SELECT CONCAT(a.SURNAME, ' ', a.NAME) AS AUTHOR_NAME, a.SURNAME, a.NAME, a.ID AS AUTHOR_ID, \n" +
                    "b.TITLE, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, b.IMAGE, AVG(br.GRADE) AS AVG_GRADE\n" +
                    "FROM books b INNER JOIN authors a ON b.AUTHOR_ID = a.ID \n" +
                    "LEFT JOIN borrow AS br ON b.ID = br.BOOK_ID\n" +
                    "GROUP BY br.BOOK_ID";
        }

        if (disponibleCheckBox.isSelected()) {
            // Afficher uniquement les livres disponibles
            sql += " WHERE b.AVAILABILITY = 1";
        }

        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des informations des livres
            while (rs.next()) {
                JPanel livrePanel = createLivrePanel(rs);
                livrePanels.add(livrePanel);
                livresPanel.add(livrePanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createLivrePanel(ResultSet rs) throws SQLException {
        JPanel livrePanel = new JPanel(new BorderLayout());
        livrePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        String authorFirstName = rs.getString("NAME");
        String authorLastName = rs.getString("SURNAME");
        String authorFullName = authorLastName + " " + authorFirstName;
        String imagePath = rs.getString("IMAGE");

        boolean afficherImages = true;

        if (imagePath != null && !imagePath.isEmpty() && afficherImages) {
            try (InputStream inputStream = getClass().getResourceAsStream("/images/" + imagePath)) {
                if (inputStream != null) {
                    BufferedImage originalImage = ImageIO.read(inputStream);
                    int maxSize = 150;
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    int newWidth = width;
                    int newHeight = height;

                    if (width > maxSize || height > maxSize) {
                        if (width > height) {
                            newWidth = maxSize;
                            newHeight = (newWidth * height) / width;
                        } else {
                            newHeight = maxSize;
                            newWidth = (newHeight * width) / height;
                        }
                    }

                    Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel coverLabel = new JLabel(scaledIcon);
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    infoPanel.add(coverLabel, gbc);
                    gbc.gridy++;
                } else {
                    System.out.println("Error loading image: " + imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gbc.gridy++;
        }

        JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
        gbc.gridx = 0;
        infoPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel authorLabel = new JLabel("Auteur: " + authorFullName);
        infoPanel.add(authorLabel, gbc);

        gbc.gridy++;
        JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
        infoPanel.add(genreLabel, gbc);

        gbc.gridy++;
        if (afficherNotes) {
            JLabel noteLabel = new JLabel("Note: " +
                    ((rs.getString("AVG_GRADE") == null) ? "Aucune note" : rs.getFloat("AVG_GRADE")));
            infoPanel.add(noteLabel, gbc);
        }
        gbc.gridy++;
        JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
        infoPanel.add(releaseDateLabel, gbc);

        gbc.gridy++;
        JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
        infoPanel.add(descriptionLabel, gbc);

        gbc.gridy++;
        JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));
        infoPanel.add(nbPagesLabel, gbc);

        // Bouton Détails de l'auteur et commentaires

        JButton detailsButton = new JButton("Détails de l'auteur");
        JButton commentairesButton = new JButton("Commentaires");

        detailsButton.setPreferredSize(new Dimension(140, 25));
        commentairesButton.setPreferredSize(new Dimension(140, 25));

        detailsButton.addActionListener(new DetailsButtonListener(rs.getInt("AUTHOR_ID")));
        commentairesButton.addActionListener(new CommentairesButtonListener(rs.getInt("AUTHOR_ID"), rs.getString("TITLE")));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detailsPanel.add(detailsButton);
        detailsPanel.add(commentairesButton);

        // Ajoute le infoPanel en haut et detailsPanel en bas du livrePanel
        livrePanel.add(infoPanel, BorderLayout.NORTH);
        livrePanel.add(detailsPanel, BorderLayout.SOUTH);

        return livrePanel;
    }

    class DetailsButtonListener implements ActionListener {
        private int authorId;

        public DetailsButtonListener(int authorId) {
            this.authorId = authorId;
        }

        public void actionPerformed(ActionEvent e) {
            new AuteurDetailsWindow(authorId);
        }
    }

    class CommentairesButtonListener implements ActionListener{
        private int bookId;
        private String bookName;
        public CommentairesButtonListener(int bookId, String bookName){
            this.bookId = bookId;
            this.bookName = bookName;
        }
        public void actionPerformed(ActionEvent e){
        // Ouvre la fenêtre des commentaires du livre
            new CommentairesWindow(bookId,bookName);
        }
    }

    class SearchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = ((JTextField) e.getSource()).getText().trim().toLowerCase();

            for (JPanel panel : livrePanels) {
                JPanel infoPanel = (JPanel) panel.getComponent(0);

                if (infoPanel.getComponentCount() > 3 && infoPanel.getComponent(3) instanceof JLabel) {
                    JLabel genreComponent = (JLabel) infoPanel.getComponent(3);
                    String genreText = genreComponent.getText();
                    String[] parts = genreText.split(":");
                    if (parts.length > 1) {
                        String genre = parts[1].trim().toLowerCase();
                        if (genre.contains(searchTerm)) {
                            panel.setVisible(true);
                            continue;
                        }
                    }
                }

                if (infoPanel.getComponentCount() > 1 && infoPanel.getComponent(1) instanceof JLabel) {
                    JLabel authorComponent = (JLabel) infoPanel.getComponent(1);
                    String author = authorComponent.getText();
                    if (author != null && author.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 2 && infoPanel.getComponent(2) instanceof JLabel) {
                    JLabel genreComponent = (JLabel) infoPanel.getComponent(2);
                    String genre = genreComponent.getText();
                    if (genre != null && genre.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 4 && infoPanel.getComponent(4) instanceof JLabel) {
                    JLabel authorFirstNameComponent = (JLabel) infoPanel.getComponent(4);
                    String authorFirstName = authorFirstNameComponent.getText();
                    if (authorFirstName != null && authorFirstName.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 5 && infoPanel.getComponent(5) instanceof JLabel) {
                    JLabel authorLastNameComponent = (JLabel) infoPanel.getComponent(5);
                    String authorLastName = authorLastNameComponent.getText();
                    if (authorLastName != null && authorLastName.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                panel.setVisible(false);
            }
        }
    }
    class DisponibleCheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            livrePanels.clear();
            livresPanel.removeAll();
            getContentPane().validate();
            getContentPane().repaint();
            afficherLivresDisponibles();
        }
    }

    public static void main(String[] args) {
        Utilisateur.connexion(5, "Hubert","Chavasse","hubert@gmail.com",0,0, "test");
        SwingUtilities.invokeLater(() -> new AffichageEmprunts());
    }
}
