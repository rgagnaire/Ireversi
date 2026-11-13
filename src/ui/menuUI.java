package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menuUI extends JFrame {
    private JTextField usernameField;

    public menuUI() {
        // Configuration de la fenêtre principale
        setTitle("MyReversi - Menu Principal");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setLayout(new BorderLayout());

        // Panel du haut : Titre / Logo
        JLabel titleLabel = new JLabel("MyReversi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel du centre : Saisie du pseudo et boutons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel userLabel = new JLabel("Votre nom d'utilisateur :");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacement
        centerPanel.add(userLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(usernameField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton : Créer une partie
        JButton createButton = new JButton("Créer une partie");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pseudo = usernameField.getText().trim();
                if (pseudo.isEmpty()) {
                    JOptionPane.showMessageDialog(menuUI.java.this, "Veuillez entrer un pseudo valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(menuUI.java.this, "Bienvenue " + pseudo + " ! Redirection vers CreateUI...");
                    // TODO: Ouvrir la fenêtre CreateUI et fermer le menu
                }
            }
        });
        centerPanel.add(createButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bouton : Rejoindre une partie
        JButton joinButton = new JButton("Rejoindre une partie");
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pseudo = usernameField.getText().trim();
                if (pseudo.isEmpty()) {
                    JOptionPane.showMessageDialog(menuUI.java.this, "Veuillez entrer un pseudo valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(menuUI.java.this, "Bienvenue " + pseudo + " ! Redirection vers JoinUI...");
                    // TODO: Ouvrir la fenêtre JoinUI et fermer le menu
                }
            }
        });
        centerPanel.add(joinButton);

        add(centerPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Lancer l'interface graphique sur le thread dédié (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new menuUI().setVisible(true);
            }
        });
    }
}