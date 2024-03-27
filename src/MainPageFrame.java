import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPageFrame extends JFrame implements ActionListener {
    // Constructeur de MainPageFrame
    public MainPageFrame() {
        // Paramètres de la fenêtre principale
        setTitle("Système de Gestion de Bibliothèque");
        setSize(800, 600); // Dimension de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Barre de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu "Quitter"
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(this);

        // Menu "Catalogue"
        JMenuItem catalogueItem = new JMenuItem("Catalogue");
        catalogueItem.addActionListener(this);

        // Menu "Mes Livres"
        JMenuItem myBooksItem = new JMenuItem("Mes Livres");
        myBooksItem.addActionListener(this);

        // Menu "Historique"
        JMenuItem historyItem = new JMenuItem("Historique");
        historyItem.addActionListener(this);

        // Ajout des éléments de menu à la barre de menu
        menuBar.add(exitItem);
        menuBar.add(catalogueItem);
        menuBar.add(myBooksItem);
        menuBar.add(historyItem);

        // Configuration de la barre de menu sur la fenêtre principale
        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "Quitter":
                System.exit(0); // Fermer l'application
                break;
            case "Catalogue":
                // Afficher le panneau du catalogue ici
                JOptionPane.showMessageDialog(this, "Coming soon");
                break;
            case "Mes Livres":
                // Afficher le panneau de "Mes Livres" ici
                JOptionPane.showMessageDialog(this, "Coming soon");
                break;
            case "Historique":
                // Afficher le panneau d'historique ici
                JOptionPane.showMessageDialog(this, "Coming soon");
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainPageFrame().setVisible(true);
            }
        });
    }
}
