package edu.up.cs301.stadiumcheckers;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;

public class SCMainActivity extends GameMainActivity {
    @Override
    public GameConfig createDefaultConfig() {
        return null;
    }

    @Override
    public LocalGame createLocalGame(GameState gameState) {
        return null;
    }
}
