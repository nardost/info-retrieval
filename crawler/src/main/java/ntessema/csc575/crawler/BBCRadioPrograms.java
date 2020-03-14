package ntessema.csc575.crawler;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BBCRadioPrograms {
    /*
     * No need to instantiate this class
     */
    private BBCRadioPrograms() {
    }

    public final static Map<String, String> PROGRAMS = Stream.of(new String[][] {
        { "The-Why-Factor", "https://www.bbc.co.uk/programmes/p00xtky9/episodes/player" },
        { "The-Forum", "https://www.bbc.co.uk/programmes/p004kln9/episodes/player" },
        { "Thinking-Allowed", "https://www.bbc.co.uk/programmes/b006qy05/episodes/player" },
        { "In-Our-Time", "https://www.bbc.co.uk/programmes/b006qykl/episodes/player" },
        { "Start-the-Week", "https://www.bbc.co.uk/programmes/b006r9xr/episodes/player" },
        { "The-Fifth-Floor", "https://www.bbc.co.uk/programmes/p00mt9kd/episodes/player" },
        { "Crossing-Continents", "https://www.bbc.co.uk/programmes/b006qt55/episodes/player" },
        { "The-History-Hour", "https://www.bbc.co.uk/programmes/p016tmg1/episodes/player" },
        { "Free-Thinking", "https://www.bbc.co.uk/programmes/b0144txn/episodes/player" },
        { "The Documentary", "https://www.bbc.co.uk/programmes/p00fvhsf/episodes/player" },
        { "Last-Word", "https://www.bbc.co.uk/programmes/b006qpmv/episodes/player" },
        { "The-Essay", "https://www.bbc.co.uk/programmes/b006x3hl/episodes/player" },
        { "Arts-and-Ideas", "https://www.bbc.co.uk/programmes/b0144txn/episodes/player" },
        { "Heart-and-Soul", "https://www.bbc.co.uk/programmes/p002vsn4/episodes/player" },
        { "Great-Lives", "https://www.bbc.co.uk/programmes/b006qxsb/episodes/player" },
        { "Night-Waves", "https://www.bbc.co.uk/programmes/b006tp43/episodes/player" },
        { "Meridian", "https://www.bbc.co.uk/programmes/p03m0hz8/episodes/guide" },
        { "Discovery", "https://www.bbc.co.uk/programmes/p002w557/episodes/player" },
        { "A-History-of-Ideas", "https://www.bbc.co.uk/programmes/b04bwydw/episodes/player" },
        { "Outlook", "https://www.bbc.co.uk/programmes/p002vsxt/episodes/player" }
    }).collect(Collectors.toMap(x -> x[0], x -> x[1]));

}
