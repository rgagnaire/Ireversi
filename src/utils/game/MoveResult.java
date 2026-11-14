package utils.game;

/** Ce qui s'est passe quand on a essaye de jouer un coup. */
public enum MoveResult {
    INVALID,          // coup refuse, rien n'a change, c'est toujours le meme joueur
    OK,               // coup joue, c'est maintenant au tour de l'adversaire
    OPPONENT_PASSED,  // coup joue, l'adversaire n'a aucun coup : le meme joueur rejoue
    GAME_OVER         // coup joue, la partie est terminee
}