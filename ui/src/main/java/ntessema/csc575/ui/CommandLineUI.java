package ntessema.csc575.ui;

import ntessema.csc575.commons.Document;
import ntessema.csc575.commons.DocumentReference;
import ntessema.csc575.indexer.Retriever;
import ntessema.csc575.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;

public class CommandLineUI {
    public static void main(String[] args) {
        Retriever retriever = new Retriever();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean alive = true;
        while(alive) {
            System.out.println("Enter query: ");
            String query;
            try {
                query = in.readLine();
                Document queryDocument = Query.createQueryFromString(query);
                Map<DocumentReference, Double> results = retriever.retrieve(queryDocument);
                if(results == null) {
                    System.out.println("No documents returned.");
                    return;
                }
                queryDocument.getDocumentVector().forEach((term, weight) -> {
                    System.out.println("Query Term: " + term + ", Weight: " + weight);
                });
                StringBuilder sb = new StringBuilder();
                sb.append(query);
                sb.append(":");
                int count = 1;
                for(Map.Entry<DocumentReference, Double> docRef : results.entrySet()) {
                    if(count > 20) break;
                    DocumentReference reference = docRef.getKey();
                    double score = docRef.getValue();
                    sb.append(" ");
                    sb.append(reference.getPath().getFileName().toString().replace(".txt", ""));
                    sb.append("(" + String.format("%.3f", score) + ")");
                    sb.append(",");
                    count++;
                }
                sb.replace(sb.length() - 1, sb.length(), "");
                System.out.println(sb.toString());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch(URISyntaxException use) {
                use.printStackTrace();
            }
        }
    }
}
