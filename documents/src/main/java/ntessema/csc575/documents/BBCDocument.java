package ntessema.csc575.documents;

public class BBCDocument {
    private String id;
    private String program;
    private String title;
    private String link;
    private String date;
    private String content;

    BBCDocument(String id, String program, String title, String link, String date, String content) {
        this.id = id;
        this.program = program;
        this.title = title;
        this.link = link;
        this.date = date;
        this.content = content;
    }

    String getId() {
        return id;
    }

    String getProgram() {
        return program;
    }

    String getTitle() {
        return title;
    }

    String getLink() {
        return link;
    }

    String getDate() {
        return date;
    }

    String getContent() {
        return content;
    }
}
