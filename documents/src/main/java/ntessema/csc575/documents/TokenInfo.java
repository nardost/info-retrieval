package ntessema.csc575.documents;

import ntessema.csc575.commons.DocumentException;

import java.util.LinkedList;
import java.util.List;

public class TokenInfo {

    /*
     * The token member might be redundant.
     */
    private String token;
    private double idf = 0.0;
    private List<TokenOccurrence> occurrence = new LinkedList<>();

    TokenInfo(String token) {
        if(token == null) {
            throw new DocumentException("Null token not allowed in TokenInfo constructor.");
        }
        this.token = token;
    }

    String getToken() {
        return token;
    }

    double getIdf() {
        return idf;
    }

    void setIdf(double idf) {
        if(idf < 0.0) {
            throw new DocumentException("IDF cannot be negative");
        }
        this.idf = idf;
    }

    List<TokenOccurrence> getOccurrence() {
        return occurrence;
    }

    void setOccurrence(List<TokenOccurrence> occurrence) {
        if(occurrence == null) {
            throw new DocumentException("Cannot assign a null occurrence list.");
        }
        this.occurrence = occurrence;
    }
}
