package ntessema.csc575.crawler;

import java.io.IOException;

import static ntessema.csc575.crawler.Crawler.crawl;

/**
 * This class is the main crawler that
 * downloads selected BBC Radio programs.
 */
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
          "The-Why-Factor",
          "The-Forum",
          "Thinking-Allowed",
          "In-Our-Time",
          "Start-the-Week",
          "The-Fifth-Floor",
          "Crossing-Continents",
          "The-History-Hour",
          "Free-Thinking",
          "The-Documentary",
          "Last-Word",
          "The-Essay",
          "Heart-and-Soul",
          "Great-Lives",
          "Night-Waves",
          "Meridian",
          "Discovery",
          "A-History-of-Ideas",
          "Outlook",
          "Omnibus",
          "Letter-from-America",
          "The-Reith-Lectures",
          "World-Book-Club"
        };
        crawl(programsSelectedForDownload);
    }
}
