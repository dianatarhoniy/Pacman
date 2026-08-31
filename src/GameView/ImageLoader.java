package GameView;

import Model.Cell;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<Cell, ImageIcon> originals = new EnumMap<>(Cell.class);
    static {
        originals.put(Cell.PAC_CLOSED,  load("resources/pac_closed.png"));
        originals.put(Cell.FOOD,          load("resources/food.png"));
        originals.put(Cell.GHOST_RED,          load("resources/ghost_red.png"));
        originals.put(Cell.GHOST_PINK,          load("resources/ghost_pink.png"));
        originals.put(Cell.GHOST_GREEN,          load("resources/ghost_green.png"));
        originals.put(Cell.GHOST_YELLOW,          load("resources/ghost_yellow.png"));
        originals.put(Cell.PATH,          load("resources/path.png"));
        originals.put(Cell.PACKMAN_RIGHT, load("resources/pac_right.png"));
        originals.put(Cell.PACKMAN_UP,    load("resources/pac_up.png"));
        originals.put(Cell.PACKMAN_LEFT,  load("resources/pac_left.png"));
        originals.put(Cell.PACKMAN_DOWN,  load("resources/pac_down.png"));
        originals.put(Cell.WALL,          load("resources/wall.png"));
        originals.put(Cell.UPGRADE,          load("resources/upgrade.png"));
    }


    private static final Map<Cell, ImageIcon> scaled = new EnumMap<>(Cell.class);
    private static int TILE = 32;

    public static void setTileSize(int tile) {
        if (!scaled.isEmpty() && tile == TILE) return;
        TILE = tile;
        scaled.clear();
        for (var e : originals.entrySet()) {
            Image img = e.getValue().getImage()
                    .getScaledInstance(TILE, TILE, Image.SCALE_SMOOTH);
            scaled.put(e.getKey(), new ImageIcon(img));
        }
    }

    public static ImageIcon iconFor(Cell cell) {
        return scaled.get(cell);
    }

    private static ImageIcon load(String file) {
        return new ImageIcon(ImageLoader.class.getResource("/" + file));
    }
    public static ImageIcon iconForBackground(String file, int w, int h) {
        ImageIcon raw = new ImageIcon(ImageLoader.class.getResource("/" + file));
        Image img = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}