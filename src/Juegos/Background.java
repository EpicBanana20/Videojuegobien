package Juegos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import Utilz.LoadSave;
import static Utilz.Constantes.Ambiente.*;

public class Background {
    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] small_cloudsPos;
    private Random rnd = new Random();
    private int backgroundWidth;

    public Background() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUD);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUD);
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
        
        drawClouds(g, xLvlOffset);
    }

    private void drawClouds(Graphics g, int xLvlOffset) {
        // Para las nubes usamos parallax más lento
        float cloudParallaxFactor = 0.7f;
        int cloudOffset = (int)(xLvlOffset * cloudParallaxFactor);
        
        // Dibujar nubes grandes con parallax
        for (int i = 0; i < 4; i++) {
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - cloudOffset, 
                      (int) (204 * Juego.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);
        }

        // Dibujar nubes pequeñas con parallax más lento
        float smallCloudParallaxFactor = 0.5f;
        int smallCloudOffset = (int)(xLvlOffset * smallCloudParallaxFactor);
        for (int i = 0; i < small_cloudsPos.length; i++) {
            g.drawImage(smallCloud, 4 * i * SMALL_CLOUD_WIDTH - smallCloudOffset, 
                      small_cloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }
    }
}