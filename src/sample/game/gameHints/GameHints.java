package sample.game.gameHints;

//游戏提示
public class GameHints
{
    //信息创建时间
    public long createTime;
    //信息内容
    public String content;

    public GameHints(long createTime, String content)
    {
        this.createTime = createTime;
        this.content = content;
    }
}
