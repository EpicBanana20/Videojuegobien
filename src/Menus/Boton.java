package Menus;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Boton {
    private int x, y, index;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;
    private BufferedImage[] imgs;
    
    // Estados del botón
    private static final int ESTADO_NORMAL = 0;
    private static final int ESTADO_HOVER = 1;
    private static final int ESTADO_PRESIONADO = 2;
    
    public static final int B_WIDTH_DEFAULT = 140;
    public static final int B_HEIGHT_DEFAULT = 25;
    public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * 4f);
    public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * 7f);

    public Boton(int x, int y, int rowIndex, BufferedImage[] imgs) {
        this.x = x;
        this.y = y;
        this.imgs = imgs;
        this.index = ESTADO_NORMAL;
        initBounds();
    }

    public void initBounds() {
        bounds = new Rectangle(x - B_WIDTH / 2, y, B_WIDTH, B_HEIGHT);
    }

    public void draw(Graphics g) {
        g.drawImage(imgs[index], x - B_WIDTH / 2, y, B_WIDTH, B_HEIGHT, null);
    }

    public void update() {
        if (mousePressed)
            index = ESTADO_PRESIONADO;
        else if (mouseOver)
            index = ESTADO_HOVER;
        else
            index = ESTADO_NORMAL;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }

    // Getters y Setters
    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}