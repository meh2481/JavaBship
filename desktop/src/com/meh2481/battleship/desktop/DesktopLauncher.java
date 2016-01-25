package com.meh2481.battleship.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.meh2481.battleship.MyBattleshipGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Battleship";
        config.width = 640;
        config.height = 640;
        config.resizable = false;
        config.addIcon("icon_128.png", Files.FileType.Internal);
        config.addIcon("icon_64.png", Files.FileType.Internal);
        config.addIcon("icon_32.png", Files.FileType.Internal);
        config.addIcon("icon_16.png", Files.FileType.Internal);
        new LwjglApplication(new MyBattleshipGame(), config);
    }
}
