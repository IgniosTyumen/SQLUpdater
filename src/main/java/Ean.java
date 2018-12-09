public class Ean {
     String ean;
     Integer article = 0;

    @Override
    public String toString() {
        return "Ean{" +
                "ean='" + ean + '\'' +
                ", article=" + article +
                '}';
    }

    public Ean(String ean, Integer article) {
        this.ean = ean;
        this.article = article;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Integer getArticle() {
        return article;
    }

    public void setArticle(Integer article) {
        this.article = article;
    }
}
