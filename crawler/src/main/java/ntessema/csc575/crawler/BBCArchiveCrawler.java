package ntessema.csc575.crawler;

import java.io.IOException;

import static ntessema.csc575.crawler.Crawler.crawl;

public class BBCArchiveCrawler {

    public static void main(String[] args) throws IOException {
        /*
         * The number of available BBC programs is to great to deal with
         * in this course project. So, I had to limit the programs to only
         * a subset of the entire program list.
         *
         * The base URLs were themselves obtained by crawling the a to z program list.
         */
        final String[] programsSelectedForDownload = new String [] {
                "A-History-of-Ideas"
        };
        crawl(programsSelectedForDownload);
    }
}
