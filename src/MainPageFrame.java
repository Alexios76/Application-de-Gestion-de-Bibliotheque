import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPageFrame extends JFrame implements ActionListener {
    public MainPageFrame() {
        setTitle("Système de Gestion de Bibliothèque");
        setSize(800, 600); // Dimension de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(this);

        JMenuItem catalogueItem = new JMenuItem("Catalogue");
        catalogueItem.addActionListener(this);

        JMenuItem myBooksItem = new JMenuItem("Mes Livres");
        myBooksItem.addActionListener(this);

        JMenuItem historyItem = new JMenuItem("Historique");
        historyItem.addActionListener(this);

        menuBar.add(exitItem);
        menuBar.add(catalogueItem);
        menuBar.add(myBooksItem);
        menuBar.add(historyItem);

        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        switch (actionCommand) {
            case "Quitter":
                System.exit(0);
                break;
            case "Catalogue":
                JOptionPane.showMessageDialog(this, "Coming soon");
                break;
            case "Mes Livres":
                JOptionPane.showMessageDialog(this, "Coming soon");
                break;
            case "Historique":
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
