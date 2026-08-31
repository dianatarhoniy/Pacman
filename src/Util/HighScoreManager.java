package Util;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class HighScoreManager {


    private static final Path FILE = Paths.get(
            System.getProperty("user.home"), ".pacman-scores");
    private final List<Entry> list = new ArrayList<>();

    public HighScoreManager() { load(); }

    public void addWithDialog(JFrame owner, int score) {
        String name = JOptionPane.showInputDialog(
                owner, "Your score: " + score + "\nName:",
                "New high score", JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.isBlank()) return;
        list.add(new Entry(name.strip(), score));
        list.sort(Comparator.comparingInt(e -> -e.score));
        store();
    }

    public void showDialog(JFrame owner) {
        load();

        AbstractTableModel tm = new AbstractTableModel() {
            public int    getRowCount()    { return list.size(); }
            public int    getColumnCount() { return 2; }
            public String getColumnName(int c){ return c==0? "Name":"Score"; }
            public Object getValueAt(int r,int c){
                return c==0 ? list.get(r).name : list.get(r).score;
            }
        };

        JTable tbl = new JTable(tm);
        tbl.setFillsViewportHeight(true);
        JScrollPane scroller = new JScrollPane(tbl);
        scroller.setPreferredSize(new java.awt.Dimension(250, 300));

        JOptionPane.showMessageDialog(owner, scroller,
                "Best scores", JOptionPane.PLAIN_MESSAGE);
    }

    private void load() {
        list.clear();
        if (!Files.exists(FILE)) return;
        try (BufferedReader br = Files.newBufferedReader(FILE)) {
            String ln;
            while ((ln = br.readLine()) != null) {
                int i = ln.lastIndexOf('\t');
                if (i > 0)
                    list.add(new Entry(ln.substring(0, i),
                            Integer.parseInt(ln.substring(i + 1))));
            }
        } catch (Exception ignored) { }
        list.sort(Comparator.comparingInt(e -> -e.score));
    }

    private void store() {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE)) {
            for (Entry e : list)
                bw.write(e.name + "\t" + e.score + "\n");
        } catch (IOException ignored) { }
    }

    private record Entry(String name, int score) { }
}