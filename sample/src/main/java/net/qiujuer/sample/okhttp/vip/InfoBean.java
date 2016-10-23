package net.qiujuer.sample.okhttp.vip;

/**
 * Created by qiujuer
 * on 2016/10/23.
 */
public class InfoBean {
    private long id;
    private int type;
    private String href;
    private String pubDate;

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "InfoBean{" +
                "id=" + id +
                ", type=" + type +
                ", href='" + href + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
