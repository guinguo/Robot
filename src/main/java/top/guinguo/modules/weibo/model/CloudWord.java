package top.guinguo.modules.weibo.model;

import com.kennycason.kumo.WordFrequency;

/**
 * Created by guin_guo on 2018/04/01.
 */
public class CloudWord {
    private String text;
    private int size;

    public CloudWord() {
    }

    public CloudWord(String text, int size) {
        this.text = text;
        this.size = size;
    }

    public CloudWord(WordFrequency wf) {
        this.text = wf.getWord();
        this.size = wf.getFrequency();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
