package top.guinguo.modules.weibo.model;

/**
 * Created by guin_guo on 2018/03/13.
 */
public class Label {
    private String text;
    private Double score;

    public Label(String text, Double score) {
        this.text = text;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
