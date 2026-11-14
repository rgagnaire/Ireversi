package utils.game;

/** Les effets de cases speciales qu'un createur de partie peut activer. */
public enum SpecialTileType {
    BOMB,   // bombe : retourne tous les autres disques du carre 5x5
    LOCK,   // verrou : le disque pose ici ne peut plus etre retourne
    BONUS   // +N : donne N points bonus a celui qui y joue
}