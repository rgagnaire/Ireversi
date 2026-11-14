package ui;

import java.util.EnumSet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import utils.game.Game;
import utils.game.GameSettings;
import utils.game.SpecialTileType;

/** Point d'entree : lance une partie locale a deux joueurs sur le meme ordinateur. */
public class MainUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameSettings settings = new GameSettings();
            settings.setSpecialTilePercent(15); // pour voir les cases speciales dans la demo
            settings.setSpecialTileTypes(EnumSet.allOf(SpecialTileType.class));
            Game game = new Game(settings, "Joueur 1", "Joueur 2");

            JFrame frame = new JFrame("MyReversi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new GameUI(game));
            frame.pack();
            frame.setLocationRelativeTo(null); // centre la fenetre
            frame.setVisible(true);
        });
    }
}