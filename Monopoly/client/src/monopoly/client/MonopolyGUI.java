package monopoly.client;

import monopoly.client.ClientConnection;

import monopoly.model.BoardSpace;
import monopoly.model.MonopolyBoard;
import monopoly.model.Player;
import monopoly.model.Property;

import monopoly.net.BuyPropertyReq;
import monopoly.net.GameStatePush;
import monopoly.net.JoinGameReq;
import monopoly.net.Message;
import monopoly.net.RollDiceReq;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/** Swing front-end for the networked Monopoly game. */
public class MonopolyGUI extends JFrame {

    /* ───── state from server ───── */
    private MonopolyBoard board;
    private List<Player>  players = List.of();

    /* ───── UI ───── */
    private final JTextArea log   = new JTextArea();
    private final JTextArea stats = new JTextArea(5, 14);
    private final List<JButton> btns = new ArrayList<>();
    private final JButton rollBtn = new JButton("Roll Dice");

    /* ───── client internals ───── */
    private final String           myName;
    private final ClientConnection conn;

    /* =================================================================== */
    public MonopolyGUI(String name, String host, int port) throws Exception {
        this.myName = name;

        /* connection */
        conn = new ClientConnection(host, port, this::handle);
        conn.send(new JoinGameReq(name));

        /* frame basics */
        setTitle("Monopoly – " + name);
        setSize(950, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* board edge panels */
        JPanel north = new JPanel(new GridLayout(1,10));
        JPanel south = new JPanel(new GridLayout(1,10));
        JPanel west  = new JPanel(new GridLayout(10,1));
        JPanel east  = new JPanel(new GridLayout(10,1));

        for (int i = 20; i <= 29; i++) btns.add(addButton(north));
        for (int i =  9; i >=  0; i--) btns.add(addButton(south));
        for (int i = 19; i >= 10; i--) btns.add(addButton(west));
        for (int i = 30; i <= 39; i++) btns.add(addButton(east));

        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
        add(west,  BorderLayout.WEST);
        add(east,  BorderLayout.EAST);

        /* log + sidebar */
        log.setEditable(false);
        add(new JScrollPane(log), BorderLayout.CENTER);

        stats.setEditable(false);
        stats.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(stats), BorderLayout.EAST);

        /* roll button */
        rollBtn.addActionListener(e -> conn.send(new RollDiceReq()));
        add(rollBtn, BorderLayout.PAGE_END);

        setVisible(true);
    }

    /* =================================================================== */
    private JButton addButton(JPanel parent) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(80, 80));
        parent.add(b);
        return b;
    }

    /* =================================================================== */
    private void handle(Message msg) {
        if (msg instanceof GameStatePush gs) {
            board   = gs.board();
            players = gs.players();

            boolean myTurn =
                players.get(gs.currentTurn()).getName().equals(myName);
            rollBtn.setEnabled(myTurn);

            SwingUtilities.invokeLater(() -> {
                drawBoard();
                updateStats();
                log.append("\n" + gs.lastEvent());
            });

            if (myTurn) maybePromptBuy(gs.currentTurn());
        }
    }

    /* =================================================================== */
    private void maybePromptBuy(int meIdx) {
        Player me = players.get(meIdx);
        BoardSpace sq = board.getBoard().get(me.getPosition());
        if (!(sq instanceof Property p)) return;
        if (p.isOwned() || me.getMoney() < p.getRent()) return;

        int choice = JOptionPane.showConfirmDialog(
                this,
                myName + ": Buy " + p.getName() +
                " for $" + p.getRent() + "?",
                "Buy Property",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION)
            conn.send(new BuyPropertyReq(me.getPosition()));
    }

    /* =================================================================== */
    private void updateStats() {
        StringBuilder sb = new StringBuilder();
        for (Player p : players)
            sb.append(p.getName())
              .append(" ($").append(p.getMoney()).append(")\n");
        stats.setText(sb.toString());
    }

    /* =================================================================== */
    private void drawBoard() {
        if (board == null) return;

        Color[] pawnColors = {
            Color.ORANGE, Color.CYAN, Color.PINK,
            Color.GREEN,  Color.MAGENTA, Color.YELLOW
        };

        for (int i = 0; i < board.getBoard().size(); i++) {
            final int pos = i;

            JButton    btn = btns.get(i);
            BoardSpace sq  = board.getBoard().get(i);

            StringBuilder html = new StringBuilder("<html><center>")
                                     .append(sq.getName());

            /* property border + owner */
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

            /* players on this square */
            boolean someoneHere = false;
            for (Player pl : players)
                if (pl.getPosition() == pos) {
                    someoneHere = true;
                    html.append("<br><b>").append(pl.getName()).append("</b>");
                }
            if (someoneHere) {
                int firstIdx = players.stream()
                                      .filter(p -> p.getPosition() == pos)
                                      .findFirst()
                                      .map(players::indexOf)
                                      .orElse(0);
                btn.setBackground(pawnColors[firstIdx % pawnColors.length]);
            }

            btn.setText(html.append("</center></html>").toString());
        }
    }

    /* =================================================================== */
    public static void main(String[] args) throws Exception {
        String name = JOptionPane.showInputDialog("Enter player name");
        String host = (args.length > 0) ? args[0] : "localhost";
        int    port = (args.length > 1) ? Integer.parseInt(args[1]) : 5100;
        new MonopolyGUI(name == null ? "Player" : name, host, port);
    }
}
