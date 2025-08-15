import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


/* ---------------- Prototype: Player ---------------- */
class Player {
    private final UUID id; // unique id for each instance
    private String name;
    private int health;
    private int experience;
    private int level;

    public Player(String name, int health, int experience, int level) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.health = health;
        this.experience = experience;
        this.level = level;
    }

    // Copy-constructor (preferred deep-copy approach)
    public Player(Player other) {
        this.id = UUID.randomUUID(); // new identity for clone
        this.name = other.name + " (clone)";
        this.health = other.health;
        this.experience = other.experience;
        this.level = other.level;
    }

    // Factory-style clone for convenience
    public Player clonePrototype() {
        return new Player(this);
    }

    // Operations
    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    public void heal(int amount) {
        health += amount;
    }

    public void gainExperience(int xp) {
        experience += xp;
        // auto level-up logic (simple)
        while (experience >= xpForNextLevel()) {
            experience -= xpForNextLevel();
            levelUp();
        }
    }

    public void levelUp() {
        level += 1;
        health += 10; // bonus health on level-up
    }

    private int xpForNextLevel() { return 100 + (level - 1) * 50; }

    // Getters and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHealth() { return health; }
    public int getExperience() { return experience; }
    public int getLevel() { return level; }

    public String summary() {
        return String.format("%s | HP:%d | XP:%d | Lvl:%d", name, health, experience, level);
    }
}

/* ---------------- Manager to hold clones ---------------- */
class PlayerManager {
    private final List<Player> players = new ArrayList<>();

    public void setOriginal(Player p) {
        // ensure original is first in the list (index 0)
        if (players.isEmpty()) players.add(p);
        else players.set(0, p);
    }

    public Player getOriginal() { return players.isEmpty() ? null : players.get(0); }

    public void addClone(Player p) { players.add(p); }

    public List<Player> getAll() { return Collections.unmodifiableList(players); }

    public void clearClonesKeepOriginal() {
        if (players.isEmpty()) return;
        Player orig = players.get(0);
        players.clear();
        players.add(orig);
    }

    public void removeAt(int index) {
        if (index <= 0 || index >= players.size()) return; // don't remove original via this method
        players.remove(index);
    }
}

/* ---------------- GUI ---------------- */
class GameFrame extends JFrame {
    private final PlayerManager manager = new PlayerManager();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> playerList = new JList<>(listModel);

    public GameFrame() {
        setTitle("🎮 Player Prototype Lab");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new HeaderPanel(), BorderLayout.NORTH);

        // default original player
        Player original = new Player("Hero", 100, 0, 1);
        manager.setOriginal(original);

        // Left: control panel
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(12,12,12,12));
        left.setPreferredSize(new Dimension(360, 0));

        // Original player card
        JPanel origCard = createPlayerCard(original);
        left.add(origCard);
        left.add(Box.createRigidArea(new Dimension(0,10)));

        // Clone controls
        JPanel cloneControls = new JPanel(new GridLayout(0,1,8,8));
        cloneControls.setBorder(BorderFactory.createTitledBorder("Cloning Controls"));
        JButton cloneOneBtn = new JButton("Clone Selected / Original");
        JButton cloneManyBtn = new JButton("Mass Clone (10)");
        JButton clearClonesBtn = new JButton("Clear Clones (keep original)");
        cloneControls.add(cloneOneBtn);
        cloneControls.add(cloneManyBtn);
        cloneControls.add(clearClonesBtn);
        left.add(cloneControls);

        // Batch clone count
        JPanel batchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        batchPanel.add(new JLabel("Count:"));
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
        batchPanel.add(countSpinner);
        left.add(batchPanel);

        // Right: list of clones and actions
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(12,12,12,12));

        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(playerList);
        sp.setBorder(BorderFactory.createTitledBorder("Players (Index 0 = Original)"));
        right.add(sp, BorderLayout.CENTER);

        // Actions for selected
        JPanel actions = new JPanel(new GridLayout(0,1,6,6));
        actions.setBorder(BorderFactory.createTitledBorder("Actions on Selected"));
        JButton damageBtn = new JButton("Take Damage -10");
        JButton healBtn = new JButton("Heal +10");
        JButton xpBtn = new JButton("Gain XP +60");
        JButton lvlBtn = new JButton("Level Up");
        JButton removeBtn = new JButton("Remove Selected (not original)");
        actions.add(damageBtn); actions.add(healBtn); actions.add(xpBtn); actions.add(lvlBtn); actions.add(removeBtn);
        right.add(actions, BorderLayout.EAST);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        // Populate a list with original
        rebuildList();

        // Button actions
        cloneOneBtn.addActionListener(e -> cloneSelectedOrOriginal());
        cloneManyBtn.addActionListener(e -> {
            int c = (Integer) countSpinner.getValue();
            massClone(c);
        });
        clearClonesBtn.addActionListener(e -> {
            manager.clearClonesKeepOriginal(); rebuildList();
        });

        damageBtn.addActionListener(e -> applyToSelected(p -> p.takeDamage(10)));
        healBtn.addActionListener(e -> applyToSelected(p -> p.heal(10)));
        xpBtn.addActionListener(e -> applyToSelected(p -> p.gainExperience(60)));
        lvlBtn.addActionListener(e -> applyToSelected(Player::levelUp));
        removeBtn.addActionListener(e -> {
            int sel = playerList.getSelectedIndex();
            if (sel <= 0) JOptionPane.showMessageDialog(this, "Cannot remove the original (index 0). Select a clone.");
            else { manager.removeAt(sel); rebuildList(); }
        });

        // double-click to edit name
        playerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int idx = playerList.locationToIndex(evt.getPoint());
                    if (idx >= 0) editName(idx);
                }
            }
        });
    }

    private JPanel createPlayerCard(Player p) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder("Original Player"));
        card.add(new JLabel("Name: " + p.getId()+ p.getName()));
        card.add(new JLabel("HP: " + p.getHealth()));
        card.add(new JLabel("XP: " + p.getExperience()));
        card.add(new JLabel("Level: " + p.getLevel()));
        return card;
    }

    private void editName(int index) {
        Player p = manager.getAll().get(index);
        String newName = JOptionPane.showInputDialog(this, "Edit name for " + p.getName(), p.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            p.setName(newName.trim()); rebuildList();
        }
    }

    private void cloneSelectedOrOriginal() {
        int sel = playerList.getSelectedIndex();
        Player toClone = (sel >= 0) ? manager.getAll().get(sel) : manager.getOriginal();
        Player clone = toClone.clonePrototype();
        manager.addClone(clone);
        rebuildList();
        JOptionPane.showMessageDialog(this, "Cloned: " + clone.summary());
    }

    private void massClone(int count) {
        if (count <= 0) return;
        Player base = manager.getOriginal();
        for (int i = 0; i < count; i++) manager.addClone(base.clonePrototype());
        rebuildList();
        JOptionPane.showMessageDialog(this, "Created " + count + " clones of original.");
    }

    private void applyToSelected(java.util.function.Consumer<Player> op) {
        int sel = playerList.getSelectedIndex();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Select a player first."); return; }
        Player p = manager.getAll().get(sel);
        op.accept(p);
        rebuildList();
    }

    private void rebuildList() {
        listModel.clear();
        List<Player> all = manager.getAll();
        if (all.isEmpty()) return;
        // ensure original at index 0 always presently
        if (manager.getOriginal() == null) manager.setOriginal(new Player("Hero", 100, 0, 1));
        for (int i = 0; i < all.size(); i++) {
            Player p = all.get(i);
            String label = String.format("%02d - %s", i, p.summary());
            listModel.addElement(label);
        }
        playerList.setModel(listModel);
        playerList.setSelectedIndex(0);
    }
}

/* ---------------- Attractive Header ---------------- */
class HeaderPanel extends JPanel {
    public HeaderPanel() {
        setPreferredSize(new Dimension(0, 92));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(12, 97, 33), w, h, new Color(20, 180, 120));
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        g2.setColor(Color.white);
        g2.setFont(new Font("Poppins", Font.BOLD, 26));
        g2.drawString("Prototype Lab — Player Cloning Playground", 18, 36);

        g2.setFont(new Font("Inter", Font.PLAIN, 14));
        g2.drawString("Create clones of players and safely experiment with 'what-if' scenarios.", 18, 56);

        g2.setFont(new Font("Serif", Font.BOLD, 36));
        g2.drawString("⚔", w - 90, 46);
    }
}

public class PlayerPrototypeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}