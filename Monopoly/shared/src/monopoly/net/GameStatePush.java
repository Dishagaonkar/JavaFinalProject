package monopoly.net;

import monopoly.model.MonopolyBoard;
import monopoly.model.Player;

import java.util.List;

/**
 * Broadcast from the server after every state-changing action.
 * All fields are immutable snapshots, so clients can redraw safely.
 */
public record GameStatePush(
        MonopolyBoard board,
        List<Player>   players,
        String         lastEvent) implements Message {}
