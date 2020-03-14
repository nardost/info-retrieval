package ntessema.csc575.documents;

public class BBCDocument {
    private String id;
    private String program;
    private String title;
    private String link;
    private String date;
    private String synopsis;

    public BBCDocument(String id, String program, String title, String link, String date, String content) {
        this.id = id;
        this.program = program;
        this.title = title;
        this.link = link;
        this.date = date;
        this.synopsis = content;
    }

    public String getId() {
        return id;
    }

    public String getProgram() {
        return program;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDate() {
        return date;
    }

    public String getSynopsis() {
        return synopsis;
    }
}
