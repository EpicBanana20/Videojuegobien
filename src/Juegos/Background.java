package Juegos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import Utilz.LoadSave;

public class Background {
    private BufferedImage backgroundImg;
    private int[] small_cloudsPos;
    private Random rnd = new Random();
    private int backgroundWidth;

    public Background() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        small_cloudsPos = new int[8];
        for (int i = 0; i < small_cloudsPos.length; i++) {
            small_cloudsPos[i] = (int) (90 * Juego.SCALE) + rnd.nextInt((int) (100 * Juego.SCALE));
        }
        // Asegurarnos de que el fondo sea suficientemente ancho
        backgroundWidth = (int) (Juego.GAME_WIDTH * 1);
    }

    public void draw(Graphics g, int xLvlOffset) {
        // Dibujamos el fondo fijo al límite del nivel
        int maxOffset = Juego.NIVEL_ACTUAL_ANCHO - Juego.GAME_WIDTH;
        float parallaxFactor = (maxOffset > -240) ? (float)xLvlOffset / maxOffset : 0;
        int bgOffset = (int)(parallaxFactor * (backgroundWidth - Juego.GAME_WIDTH));
        
        // Nos aseguramos de que el fondo abarque todo el ancho de la pantalla
        g.drawImage(backgroundImg, -bgOffset, 0, backgroundWidth, Juego.GAME_HEIGHT, null);
    }
}