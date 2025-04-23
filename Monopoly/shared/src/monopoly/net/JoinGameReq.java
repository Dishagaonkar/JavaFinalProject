package monopoly.net;

/** Sent once, right after the socket connects. */
public record JoinGameReq(String playerName) implements Message {}
