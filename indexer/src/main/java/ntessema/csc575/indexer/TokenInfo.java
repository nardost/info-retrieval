package ntessema.csc575.indexer;

import ntessema.csc575.documents.DocumentException;

import java.util.LinkedList;
import java.util.List;

public class TokenInfo {

    /*
     * The token member might be redundant.
     */
    private String token;
    private double idf = 0.0;
    private List<TokenOccurrence> occurrence = new LinkedList<>();

    public TokenInfo(String token) {
        if(token == null) {
            throw new DocumentException("Null token not allowed in TokenInfo constructor.");
        }
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        if(idf < 0.0) {
            throw new DocumentException("IDF cannot be negative");
        }
        this.idf = idf;
    }

    public List<TokenOccurrence> getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(List<TokenOccurrence> occurrence) {
        if(occurrence == null) {
            throw new DocumentException("Cannot assign a null occurrence list.");
        }
        this.occurrence = occurrence;
    }
}
