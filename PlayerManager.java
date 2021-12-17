package lotsofyou;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PlayerManager {
    private final HashMap<Integer, Player> players;

    PlayerManager() {
        players = new HashMap<>();
    }

    synchronized void addPlayer(Player p, int id) {
        players.put(id, p);
    }

    synchronized void removePlayer(int id) {
        players.remove(id);
    }

    synchronized Player getPlayer(int id) {
        return players.get(id);
    }

    synchronized Collection<Player> getPlayers() {
        return players.values();
    }
}
