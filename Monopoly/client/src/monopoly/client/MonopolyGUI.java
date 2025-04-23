package monopoly.client;

import monopoly.client.ClientConnection;
import monopoly.net.*;
import monopoly.model.*;

import javax.swing.*;
import java.awt.*;                     // still fine
import java.util.List;                 // explicit, no wildcard
import java.util.ArrayList;

/** Swing front-end for the networked Monopoly game. */
public class MonopolyGUI extends JFrame {

    private MonopolyBoard board;
    private List<Player>  players = List.of();          // now unambiguous
    private final JTextArea log  = new JTextArea();
    private final List<JButton> btns = new ArrayList<>();   // ditto
    private final ClientConnection conn;

    // -------------------------------------------------------------

    public MonopolyGUI(String name, String host, int port) throws Exception {
        conn = new ClientConnection(host, port, this::handle);
        conn.send(new JoinGameReq(name));

        setTitle("Monopoly – " + name);
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // board layout identical to previous version
        JPanel north = new JPanel(new GridLayout(1, 10));
        JPanel south = new JPanel(new GridLayout(1, 10));
        JPanel west  = new JPanel(new GridLayout(10, 1));
        JPanel east  = new JPanel(new GridLayout(10, 1));

        for (int i = 20; i <= 29; i++) btns.add(addButton(north));
        for (int i = 9;  i >=  0; i--) btns.add(addButton(south));
        for (int i = 19; i >= 10; i--) btns.add(addButton(west));
        for (int i = 30; i <= 39; i++) btns.add(addButton(east));

        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
        add(west,  BorderLayout.WEST);
        add(east,  BorderLayout.EAST);

        add(new JScrollPane(log), BorderLayout.CENTER);

        JButton roll = new JButton("Roll Dice");
        roll.addActionListener(e -> conn.send(new RollDiceReq()));
        add(roll, BorderLayout.PAGE_END);

        setVisible(true);
    }

    // -------------------------------------------------------------

    private JButton addButton(JPanel parent) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(70, 70));
        parent.add(b);
        return b;
    }

    private void handle(Message m) {
        if (m instanceof GameStatePush gs) {
            board   = gs.board();
            players = gs.players();
            SwingUtilities.invokeLater(() -> {
                drawBoard();
                log.append("\n" + gs.lastEvent());
            });
        }
    }

    private void drawBoard() {
        if (board == null) return;
        for (int i = 0; i < board.getBoard().size(); i++) {
            JButton     btn = btns.get(i);
            BoardSpace space = board.getBoard().get(i);

            StringBuilder lbl = new StringBuilder("<html><center>")
                    .append(space.getName());

            if (space instanceof Property prop && prop.isOwned()) {
                lbl.append("<br>Owner: ").append(prop.getOwner().getName());
            }

            for (Player p : players)
                if (p.getPosition() == i)
                    lbl.append("<br><b>").append(p.getName()).append("</b>");

            btn.setText(lbl.append("</center></html>").toString());
        }
    }

    // -------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        String name = JOptionPane.showInputDialog("Enter player name");
        String host = (args.length > 0) ? args[0] : "localhost";
        int    port = (args.length > 1) ? Integer.parseInt(args[1]) : 5100; // default to 5100
        new MonopolyGUI(name == null ? "Player" : name, host, port);
    }
}
