package Niveles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Juegos.Juego;
import Utilz.LoadSave;

public class LevelManager {
    @SuppressWarnings("unused")
    private Juego game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Juego game) {
        this.game = game;
        importOutsideSprite();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    private void importOutsideSprite() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[60];
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 10; i++) {
                int index = j * 10 + i;
                levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }

    }

    public void cargarEntidades(Juego game) {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ONE_DATA);
        
        // Iterar a través de cada píxel de la imagen
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valorRojo = color.getRed();
                
                // Convertir la posición del píxel a posición del mundo
                float x = i * Juego.TILES_SIZE;
                float y = j * Juego.TILES_SIZE;
                

                if (valorRojo >= 50 && valorRojo < 60) {
                    game.getPlayer().resetPosition(x, y);
                    System.out.println("Posición inicial del jugador establecida en: " + x + ", " + y);
                }

                else if (valorRojo >= 100 && valorRojo < 110) {
                    // Enemigo Verde
                    game.getAdminEnemigos().crearEnemigoVerde(x, y);
                    System.out.println("Enemigo Verde creado en: " + x + ", " + y);
                }
            }
        }
    }

    public void update() {
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        // Aumentar el área de dibujo añadiendo un margen de tiles extra
        int margenExtra = 5; // Número de tiles adicionales en cada dirección
        
        int filaInicio = Math.max(0, (yLvlOffset / Juego.TILES_SIZE) - margenExtra);
        int filaFin = Math.min(levelOne.getLvlData().length,
                ((yLvlOffset + Juego.GAME_HEIGHT) / Juego.TILES_SIZE) + margenExtra);
    
        int colInicio = Math.max(0, (xLvlOffset / Juego.TILES_SIZE) - margenExtra);
        int colFin = Math.min(levelOne.getLvlData()[0].length,
                ((xLvlOffset + Juego.GAME_WIDTH) / Juego.TILES_SIZE) + margenExtra);
    
        // Dibujar solo las tiles visibles y el margen extra
        for (int j = filaInicio; j < filaFin; j++) {
            for (int i = colInicio; i < colFin; i++) {
                int index = levelOne.getSpriteIndex(j, i);
                g.drawImage(levelSprite[index],
                        Juego.TILES_SIZE * i - xLvlOffset,
                        Juego.TILES_SIZE * j - yLvlOffset,
                        Juego.TILES_SIZE,
                        Juego.TILES_SIZE,
                        null);
            }
        }
    }

    public Level getCurrentLevel() {
        return levelOne;
    }

}
