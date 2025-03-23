package Utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class LoadSave {
    public static final String LEVEL_ATLAS="Mossy - TileSet.png";
    public static final String PLAYER_ATLAS="player_sprites.png";
    public static final String LEVEL_ONE_DATA="NEWMP.png";
    public static final String PLAYING_BG_IMG="BACKGR.png";
    public static final String BIG_CLOUD="big_clouds.png";
    public static final String SMALL_CLOUD="small_clouds.png";
    public static final String BULLET_SPRITE="BulletSprite.png";
    public static final String ENEMIGO1_SPRITE="ENEMIGO1.png";

    
    public static BufferedImage GetSpriteAtlas(String name) {
        BufferedImage img=null;
        InputStream is = LoadSave.class.getResourceAsStream("/recursos/"+name);
        try {
            img = ImageIO.read(is);
         } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try{
                is.close();
            }catch (IOException e) {
                e.printStackTrace();
            } }
        return img;
    }
    
    public static int[][]GetLevelData(){
        BufferedImage img=LoadSave.GetSpriteAtlas(LEVEL_ONE_DATA);
        int [][] lvlData=new int[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color=new Color(img.getRGB(i, j));
                int valor=color.getRed();
                if(valor>=48)
                    valor=0;
                lvlData[j][i]=valor;
            }            
        }
        return lvlData;
    }    
    
}
