package monopoly.net;

/**
 * Optional packet if you later add a “Buy / Don’t Buy” prompt
 * on the client side. The index identifies which board square.
 */
public record BuyPropertyReq(int index) implements Message {}
