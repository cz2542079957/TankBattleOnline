package sample.game.controller;

import javafx.scene.input.KeyEvent;
import sample.game.audio.AudioProcessor;
import sample.webSocket.WebSocketController;
import sample.webSocket.WebSocketService;

public class KeyPressed extends Controller
{
    //暂时废弃 用来切换坦克运动音效的
    public static boolean isMoving = false;

    @Override
    public void handle(KeyEvent keyEvent)
    {
        String key = keyEvent.getText();
        switch (key)
        {
            case "w":
                resetKey();
                WebSocketController.w = true;
                break;
            case "s":
                resetKey();
                WebSocketController.s = true;
                break;
            case "a":
                resetKey();
                WebSocketController.a = true;
                break;
            case "d":
                resetKey();
                WebSocketController.d = true;
                break;
            case " ":
                WebSocketController.space = true;
                break;
            default:
                break;
        }

    }

    public void resetKey()
    {
        WebSocketController.w = WebSocketController.s = WebSocketController.a = WebSocketController.d = false;
    }
}
