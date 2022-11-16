package sample.game.controller;

import javafx.scene.input.KeyEvent;
import sample.game.audio.AudioProcessor;
import sample.webSocket.WebSocketController;

public class KeyReleased extends Controller
{
    @Override
    public void handle(KeyEvent keyEvent)
    {
        String key = keyEvent.getText();
        switch (key)
        {
            case "w":
                WebSocketController.w = false;
                break;
            case "s":
                WebSocketController.s = false;
                break;
            case "a":
                WebSocketController.a = false;
                break;
            case "d":
                WebSocketController.d = false;
                break;
            case " ":
                WebSocketController.space = false;
                break;
            default:
                break;
        }

    }
}
