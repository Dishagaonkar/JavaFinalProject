package monopoly.client;

import monopoly.client.ClientConnection;
import monopoly.model.BoardSpace;
import monopoly.model.Chance;
import monopoly.model.CommunityChest;
import monopoly.model.MonopolyBoard;
import monopoly.model.Player;
import monopoly.model.Property;
import monopoly.net.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MonopolyGUI extends JFrame {
    private MonopolyBoard board;
    private List<Player> players = List.of();

    private final JTextArea log = new JTextArea();
    private final JTextArea stats = new JTextArea(5, 14);
    private final List<JButton> btns = new ArrayList<>();
    private final JButton rollBtn = new JButton("Roll Dice");
    private final Set<Integer> declinedBuys = new HashSet<>();


    private final String myName;
    private final ClientConnection conn;

    public MonopolyGUI(String name, String host, int port, ClientConnection conn) throws Exception {
        this.conn = conn;
        this.myName = name;
        this.conn.setMessageHandler(this::handle);

        setTitle("Monopoly – " + name);
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel north = new JPanel(new GridLayout(1, 10));
        JPanel south = new JPanel(new GridLayout(1, 10));
        JPanel west = new JPanel(new GridLayout(10, 1));
        JPanel east = new JPanel(new GridLayout(10, 1));

        for (int i = 20; i <= 29; i++) btns.add(addButton(north));        // TOP
        for (int i = 30; i <= 39; i++) btns.add(addButton(east));         // RIGHT
        for (int i = 9; i >= 0; i--) btns.add(addButton(south));          // BOTTOM
        for (int i = 19; i >= 10; i--) btns.add(addButton(west));         // LEFT

        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
        add(west, BorderLayout.WEST);
        add(east, BorderLayout.EAST);

        stats.setEditable(false);
        stats.setFont(new Font("Monospaced", Font.PLAIN, 12));
        log.setEditable(false);

        JPanel center = new JPanel(new BorderLayout());
        center.add(rollBtn, BorderLayout.NORTH);
        center.add(new JScrollPane(log), BorderLayout.CENTER);
        center.add(new JScrollPane(stats), BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        rollBtn.addActionListener(e -> conn.send(new RollDiceReq()));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton addButton(JPanel parent) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(80, 80));
        parent.add(b);
        return b;
    }

        private void handle(Message msg) {
        if (msg instanceof GameStatePush gs) {
            board = gs.board();
            players = gs.players();
            String lastEvent = gs.lastEvent();
            boolean myTurn = players.get(gs.currentTurn()).getName().equals(myName);

            if (myTurn && lastEvent.contains("rolled")) {
    declinedBuys.clear();  // ✅ Clear once per actual new roll/turn
}
            rollBtn.setEnabled(myTurn);

            SwingUtilities.invokeLater(() -> {
                drawBoard();
                updateStats();
                log.append("\n" + gs.lastEvent());
                checkBankruptcy();
            });

            if (myTurn) {
                
    maybePromptBuy(gs.currentTurn());

    lastEvent = gs.lastEvent();
    if (lastEvent.contains("drew a card")) {
        JOptionPane.showMessageDialog(this, lastEvent, "Card Drawn", JOptionPane.INFORMATION_MESSAGE);
    }

            }
        }
    }

    




    private void maybePromptBuy(int meIdx) {
    Player me = players.get(meIdx);
    int pos = me.getPosition();

    // Skip if this property was already declined this turn
    if (declinedBuys.contains(pos)) return;

    BoardSpace sq = board.getBoard().get(pos);
    if (!(sq instanceof Property p)) return;
    if (p.isOwned() || me.getMoney() < p.getRent()) return;

    int choice = JOptionPane.showConfirmDialog(
        this,
        myName + ": Buy " + p.getName() + " for $" + p.getRent() + "?",
        "Buy Property",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        conn.send(new BuyPropertyReq(pos));
    } else {
        declinedBuys.add(pos); // ✅ Remember that we declined this spot
        conn.send(new DeclinePropertyReq(pos));
    }
}

    private void updateStats() {
        StringBuilder sb = new StringBuilder();
        for (Player p : players)
            sb.append(p.getName()).append(" ($").append(p.getMoney()).append(")\n");
        stats.setText(sb.toString());
    }

    //  private void maybeTriggerCard(int meIdx) {
    //     Player me = players.get(meIdx);
    //     BoardSpace sq = board.getBoard().get(me.getPosition());
    //     if (sq instanceof CommunityChest || sq instanceof Chance) {
    //         String[] cards = {
    //             "Bank error in your favor. Collect $200.",
    //             "Doctor's fees. Pay $50.",
    //             "You have won second prize in a beauty contest. Collect $10.",
    //             "Pay hospital fees of $100.",
    //         };
    //         int cardIndex = new Random().nextInt(cards.length);
    //         String result = cards[cardIndex];
    //         JOptionPane.showMessageDialog(this, "🃏 " + result);
    //         log.append("\n[Card]: " + result);

    //         switch (cardIndex) {
    //             case 0 -> me.adjustMoney(200);
    //             case 1 -> me.adjustMoney(-50);
    //             case 2 -> me.adjustMoney(10);
    //             case 3 -> me.adjustMoney(-100);
    //         }

    //         drawBoard();
    //         updateStats();
    //     }
    // }

    private void checkBankruptcy() {
        for (Player p : players) {
            if (p.getName().equals(myName) && p.getMoney() < 0) {
                JOptionPane.showMessageDialog(this, "You are bankrupt and have lost the game.", "Bankruptcy", JOptionPane.ERROR_MESSAGE);
                rollBtn.setEnabled(false);
                break;
            }
        }
    }


    private int getButtonIndexForTile(int tile) {
        if (tile >= 20 && tile <= 29) return tile - 20;             // Top row
        if (tile >= 30 && tile <= 39) return 10 + (tile - 30);      // Right side
        if (tile >= 0 && tile <= 9)    return 20 + (9 - tile);      // Bottom row
        if (tile >= 10 && tile <= 19) return 30 + (19 - tile);      // Left side
        return -1;
    }

    private void drawBoard() {
        if (board == null || btns.size() != 40) return;

        Color[] pawnColors = { Color.ORANGE, Color.CYAN, Color.PINK, Color.GREEN, Color.MAGENTA, Color.YELLOW };

        for (int i = 0; i < board.getBoard().size(); i++) {
            final int tile = i;
            int btnIndex = getButtonIndexForTile(tile);
            if (btnIndex < 0 || btnIndex >= btns.size()) continue;

            JButton btn = btns.get(btnIndex);
            BoardSpace sq = board.getBoard().get(tile);

            StringBuilder html = new StringBuilder("<html><center>").append(sq.getName());

            if (sq instanceof Property p && p.getColorGroup() != null) {
                btn.setBorder(new LineBorder(p.getColorGroup(), 4));
                btn.setBorderPainted(true);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                btn.setBackground(Color.WHITE);

                if (p.isOwned())
                    html.append("<br>Owner: ").append(p.getOwner().getName());
            } else {
                btn.setBorder(UIManager.getBorder("Button.border"));
                btn.setBackground(null);
            }

            boolean someoneHere = false;
            for (Player pl : players)
                if (pl.getPosition() == tile) {
                    someoneHere = true;
                    html.append("<br><b>").append(pl.getName()).append("</b>");
                }
            if (someoneHere) {
                int firstIdx = players.stream().filter(p -> p.getPosition() == tile).findFirst().map(players::indexOf).orElse(0);
                btn.setBackground(pawnColors[firstIdx % pawnColors.length]);
            }

            btn.setText(html.append("</center></html>").toString());
        }
    }

    public static void main(String[] args) throws Exception {
        int option = JOptionPane.showOptionDialog(null,
        "Choose an option:",
        "Login or Register",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        new String[]{"Login", "Register"},
        "Login");
    
    String username = JOptionPane.showInputDialog("Enter username:");
    String password = JOptionPane.showInputDialog("Enter password:");
    String name = JOptionPane.showInputDialog("Enter player name");
    String host = (args.length > 0) ? args[0] : "localhost";
    int port = (args.length > 1) ? Integer.parseInt(args[1]) : 5100;
    // String username = JOptionPane.showInputDialog("Enter username:");
        // String password = JOptionPane.showInputDialog("Enter password:");
        // String name = JOptionPane.showInputDialog("Enter player name");
        // String host = (args.length > 0) ? args[0] : "localhost";
        // int port = (args.length > 1) ? Integer.parseInt(args[1]) : 5100;
    
        ClientConnection[] conn = new ClientConnection[1];  // mutable wrapper
    
        // temporary login handler
    conn[0] = new ClientConnection(host, port, message -> {
    if (message instanceof RegisterRes regRes) {
        if (regRes.isSuccess()) {
            JOptionPane.showMessageDialog(null, "✅ Registration successful! Now logging in.");
            conn[0].send(new LoginReq(username, password));
        } else {
            JOptionPane.showMessageDialog(null, "❌ Registration failed. Username may already exist.");
        }
    }
    
    else if (message instanceof LoginRes res) {
        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(null, "⚠️ Login failed. Starting game anyway.");
        } else {
            System.out.println("✅ Login successful.");
            conn[0].send(new JoinGameReq(name));
        }
    
        SwingUtilities.invokeLater(() -> {
            try {
                MonopolyGUI gui = new MonopolyGUI(name == null ? "Player" : name, host, port, conn[0]);
                conn[0].setMessageHandler(gui::handle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    });
    
        if (option == 1) {
    conn[0].send(new RegisterReq(username, password));
    } else {
    conn[0].send(new LoginReq(username, password));
    }
    }
    
    
}





