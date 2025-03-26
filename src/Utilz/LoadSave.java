package Utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class LoadSave {
    public static final String LEVEL_ATLAS = "tiles/Mossy - TileSet.png";
    public static final String PLAYER_ATLAS = "personajes/player_sprites.png";
    public static final String LEVEL_ONE_DATA = "lvlData/NEWMP.png";
    public static final String PLAYING_BG_IMG = "BACKGR.png";
    public static final String BULLET_SPRITE = "balas/BulletSprite.png";
    
    // HashMap para almacenar las imágenes ya cargadas
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    
    public static BufferedImage GetSpriteAtlas(String name) {
        BufferedImage img = imageCache.get(name);

        if (img == null) {
            InputStream is = LoadSave.class.getResourceAsStream("/recursos/" + name);
            try {
                img = ImageIO.read(is);
                imageCache.put(name, img);
                
                //System.out.println("Cargada imagen: " + name);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Para debug: comenta esta línea en producción
            //System.out.println("Imagen recuperada de caché: " + name);
        }
        
        return img;
    }
    
    // Método para limpiar la caché si es necesario (por ejemplo al cambiar de nivel)
    public static void clearCache() {
        imageCache.clear();
        System.out.println("Caché de imágenes limpiada");
    }
    
    // Método para obtener el tamaño actual de la caché
    public static int getCacheSize() {
        return imageCache.size();
    }
    
    public static int[][] GetLevelData() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getRed();
                if (valor >= 48)
                    valor = 0;
                lvlData[j][i] = valor;
            }
        }
        return lvlData;
    }
}