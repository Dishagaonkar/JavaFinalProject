package monopoly2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.border.LineBorder; 


public class MonopolyGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private GameController game;
    private JTextArea infoArea;
    private JTextArea playerStatsArea;
    private List<JButton> boardButtons;

    public MonopolyGUI() {
        game = new GameController();
        boardButtons = new ArrayList<>();

        setTitle("Monopoly");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        List<BoardSpace> board = game.getBoard().getBoard();

        JPanel topPanel = new JPanel(new GridLayout(1, 10));
        for (int i = 20; i <= 29; i++) {
            JButton btn = makeButton(i);
            boardButtons.add(btn);
            topPanel.add(btn);
        }

        JPanel bottomPanel = new JPanel(new GridLayout(1, 10));
        for (int i = 9; i >= 0; i--) {
            JButton btn = makeButton(i);
            boardButtons.add(btn);
            bottomPanel.add(btn);
        }

        JPanel leftPanel = new JPanel(new GridLayout(10, 1));
        for (int i = 19; i >= 10; i--) {
            JButton btn = makeButton(i);
            boardButtons.add(btn);
            leftPanel.add(btn);
        }

        JPanel rightPanel = new JPanel(new GridLayout(10, 1));
        for (int i = 30; i <= 39; i++) {
            JButton btn = makeButton(i);
            boardButtons.add(btn);
            rightPanel.add(btn);
        }

        JPanel centerPanel = new JPanel(new BorderLayout());

        infoArea = new JTextArea(10, 30);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(infoArea);

        JButton rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(e -> {
            String result = game.takeTurn();
            updateBoard();
            showInfo(result);
        });

        centerPanel.add(rollButton, BorderLayout.NORTH);
        centerPanel.add(logScroll, BorderLayout.CENTER);

        playerStatsArea = new JTextArea(8, 20);
        playerStatsArea.setEditable(false);
        playerStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(playerStatsArea);

        JPanel centerWithStats = new JPanel(new BorderLayout());
        centerWithStats.add(centerPanel, BorderLayout.CENTER);
        centerWithStats.add(statsScroll, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(centerWithStats, BorderLayout.CENTER);

        updateBoard();
        setVisible(true);
    }

    private JButton makeButton(int boardIndex) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(80, 80));
        return btn;
    }

    private void updateBoard() {
        List<BoardSpace> board = game.getBoard().getBoard();

        for (int i = 0; i < board.size(); i++) {
            BoardSpace space = board.get(i);
            JButton btn = boardButtons.get(i);

            StringBuilder label = new StringBuilder("<html><center>" + space.getName());

            if (space instanceof Property p) {
                Player owner = p.getOwner();
                if (p.getColorGroup() != null) {
                    btn.setBorder(new LineBorder(p.getColorGroup(), 4));
                    btn.setOpaque(true);
                    btn.setBackground(Color.WHITE);
                }
                if (owner != null) {
                    label.append("<br>Owner: ").append(owner.getName());
                }
            } else {
                btn.setBorder(UIManager.getBorder("Button.border"));
                btn.setBackground(null);
            }

            for (Player pl : game.getPlayers()) {
                if (pl.getPosition() == i) {
                    label.append("<br><b>").append(pl.getName()).append("</b>");
                }
            }

            btn.setText(label.append("</center></html>").toString());
        }
    }

    private void showInfo(String result) {
        infoArea.setText(infoArea.getText() + "\n\n" +result);

        StringBuilder stats = new StringBuilder();
        for (Player p : game.getPlayers()) {
            stats.append(p.toString()).append("\n");
        }
        playerStatsArea.setText(stats.toString());
    }

    public static void main(String[] args) {
        new MonopolyGUI();
    }
}
