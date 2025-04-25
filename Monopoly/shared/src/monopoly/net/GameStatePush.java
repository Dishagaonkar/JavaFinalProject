package monopoly.net;

import monopoly.model.MonopolyBoard;
import monopoly.model.Player;
import java.util.List;

/** Immutable snapshot of the game after every move. */
public record GameStatePush(
        MonopolyBoard board,
        List<Player>  players,
        String        lastEvent,
        int           currentTurn) implements Message {}
