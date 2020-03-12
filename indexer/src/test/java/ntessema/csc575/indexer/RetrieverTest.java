package ntessema.csc575.indexer;

import ntessema.csc575.commons.ConfigurationManager;
import ntessema.csc575.commons.Document;
import ntessema.csc575.query.Query;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class RetrieverTest {

    private Retriever retriever;

    private Integer queryIndex;
    private String queryString;

    public RetrieverTest(Integer index, String query) {
        this.queryIndex = index;
        this.queryString = query;
    }

    @Parameters
    public static Collection<Object[]> queries() {
        /*
         * All the 83 queries in the time benchmark corpus.
         */
        return Stream.of(new Object[][] {
                { 1, "KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP SUPPRESSING THE BUDDHISTS." },
                { 2, "EFFORTS OF AMBASSADOR HENRY CABOT LODGE TO GET VIET NAM'S PRESIDENT DIEM TO CHANGE HIS POLICIES OF POLITICAL REPRESSION." },
                { 3, "NUMBER OF TROOPS THE UNITED STATES HAS STATIONED IN SOUTH VIET NAM AS COMPARED WITH THE NUMBER OF TROOPS IT HAS STATIONED IN WEST GERMANY." },
                { 4, "U.S. POLICY TOWARD THE NEW REGIME IN SOUTH VIET NAM WHICH OVERTHREW PRESIDENT DIEM." },
                { 5, "PERSONS INVOLVED IN THE VIET NAM COUP." },
                { 6, "CEREMONIAL SUICIDES COMMITTED BY SOME BUDDHIST MONKS IN SOUTH VIET NAM AND WHAT THEY ARE SEEKING TO GAIN BY SUCH ACTS." },
                { 7, "REJECTION BY PRINCE NORODOM SIHANOUK, AN ASIAN NEUTRALIST LEADER, OF ALL FURTHER U.S. AID TO HIS NATION." },
                { 8, "U.N. TEAM SURVEY OF PUBLIC OPINION IN NORTH BORNEO AND SARAWAK ON THE QUESTION OF JOINING THE FEDERATION OF MALAYSIA." },
                { 9, "OPPOSITION OF INDONESIA TO THE NEWLY-CREATED MALAYSIA." },
                { 10, "GROWING CONTROVERSY IN SOUTHEAST ASIA OVER THE PROPOSED CREATION OF A FEDERATION OF MALAYSIA." },
                { 11, "ARRANGEMENTS FOR INDONESIA TO TAKE OVER THE ADMINISTRATION OF WEST IRIAN, WHICH HAS BEEN UNDER UNITED NATIONS ADMINISTRATION." },
                { 12, "CONTROVERSY BETWEEN INDONESIA AND MALAYA ON THE PROPOSED FEDERATION OF MALAYSIA, WHICH WOULD UNITE FIVE TERRITORIES." },
                { 13, "PRECARIOUS TRUCE IN LAOS WHICH WAS BROUGHT UP BY BRITAIN BEFORE THE 14 NATIONS THAT AGREED ON THE TRUCE IN GENEVA LAST YEAR." },
                { 14, "MAPHILINDO, A WORD FORMED FROM PARTS OF THE NAMES OF THREE COUNTRIES, WHICH IS BEING USED IN DISCUSSIONS OF INTERNATIONAL RELATIONS IN THE FAR EAST." },
                { 15, "ELECTION OF PARK CHUNG HEE AS PRESIDENT OF SOUTH KOREA." },
                { 16, "EFFORTS OF THE THREE-NATION INTERNATIONAL CONTROL COMMISSION FOR INDO-CHINA TO TRY TO STOP THE FIGHTING THAT HAS FLARED IN LAOS." },
                { 17, "WITHDRAWAL BY THE SULTANATE OF BRUNEI FROM THE PROPOSED FEDERATION OF MALAYSIA." },
                { 18, "RUSSIA'S REFUSAL TO CONTRIBUTE FUNDS FOR THE SUPPORT OF UNITED NATIONS PEACEKEEPING FORCES." },
                { 19, "AGREEMENT BY THE UNITED ARAB REPUBLIC AND SAUDI ARABIA TO WITHDRAW THEIR FORCES FROM YEMEN, WHICH INVOLVES OBSERVERS FROM THE UNITED NATIONS EXPEDITIONARY FORCE BEING SENT TO YEMEN." },
                { 20, "THE UNITED STATES HAS WARNED IT WOULD LIMIT ITS UNITED NATIONS PAYMENTS TO THE LEVEL OF ITS REGULAR ASSESSMENT IF NATIONS NOW IN ARREARS FAIL TO PAY UP. WHAT ISSUES ARE INVOLVED IN THESE NATIONS' BEING IN ARREARS." },
                { 21, "SECURITY COUNCIL CONSIDERATION OF THE COMPLAINTS BY 32 AFRICAN NATIONS AGAINST SOUTH AFRICA AND PORTUGAL." },
                { 22, "ALTERNATIVES WHICH HAVE BEEN OFFERED IF U.N. FORCES ARE WITHDRAWN FROM THE CONGO." },
                { 23, "WHAT COUNTRIES HAVE NEWLY JOINED THE UNITED NATIONS." },
                { 24, "UNITED NATIONS EFFORTS TO GET PORTUGAL TO FREE ITS AFRICAN COLONIES." },
                { 25, "U.N. CONSIDERATION OF THE CONFLICT BETWEEN ISRAEL AND ITS ARAB NEIGHBORS." },
                { 26, "EFFECT IN THE U.N. OF A MAJORITY COMPRISED OF UNDERDEVELOPED NATIONS." },
                { 27, "BRITISH PROPOSAL FOR NEW HIGH LEVEL NEGOTIATIONS WITH RUSSIA OR A FOUR-POWER SUMMIT MEETING." },
                { 28, "STRENGTHS IN POPULAR VOTES OF THE VARIOUS POLITICAL PARTIES IN THE RECENT ITALIAN ELECTIONS." },
                { 29, "TALKS BETWEEN SECRETARY OF STATE RUSK AND PRESIDENT TITO OF YUGOSLAVIA CONCERNING MOST-FAVORED-NATION TREATMENT FOR YUGOSLAV EXPORTS TO THE UNITED STATES." },
                { 30, "PARLIAMENTARY VOTE OF CONFIDENCE WON BY PRIME MINISTER MACMILLAN ON THE PROFUMO SCANDAL AND WHAT EFFECT THE PROFUMO SCANDAL MAY HAVE ON LABOR'S CHANCES IN THE NEXT BRITISH ELECTIONS." },
                { 31, "LEADERS WHICH FIGURE IN DISCUSSIONS OF THE FUTURE OF THE WEST GERMAN CHANCELLORSHIP." },
                { 32, "TALKS HELD IN EAST GERMANY BY PREMIER KHRUSHCHEV WITH THE LEADERS OF FOUR EAST EUROPEAN SATELLITE COUNTRIES." },
                { 33, "GOVERNMENT CRISIS WHICH WAS PRODUCED BY THE CONTROVERSY INVOLVING THE WALLOONS AND THE FLEMINGS." },
                { 34, "DIFFICULTIES IRELAND'S MINORITY GOVERNMENT IS FACING BECAUSE OF THE GROWING UNREST OVER ITS AGRICULTURAL POLICIES." },
                { 35, "TALKS BETWEEN PORTUGAL AND THE U.S. ON THE TROUBLES PORTUGAL HAS BEEN HAVING WITH ITS AFRICAN POSSESSIONS." },
                { 36, "SHAKE-UPS IN THE CZECH REGIME HEADED BY PRESIDENT ANTONIN NOVOTNY." },
                { 37, "WHEAT DEALS INVOLVING THE UNITED STATES, RUSSIA AND CANADA." },
                { 38, "SPECULATION THAT THE NUMBER OF U.S. FORCES IN EUROPE MIGHT BE REDUCED." },
                { 39, "COALITION GOVERNMENT TO BE FORMED IN ITALY BY THE LEFT-WING SOCIALISTS, THE REPUBLICANS, SOCIAL DEMOCRATS, AND CHRISTIAN DEMOCRATS." },
                { 40, "RESULTS OF THE POLITICAL POLLS IN BRITAIN REGARDING WHICH PARTY IS IN THE LEAD, THE LABOR PARTY OR THE CONSERVATIVES." },
                { 41, "WHAT TWO PROPOSALS DID DE GAULLE REJECT THIS YEAR AFFECTING ALLIED RELATIONS." },
                { 42, "PRESIDENT DE GAULLE'S BELIEF THAT FRANCE'S NUCLEAR FORCE SHOULD BE THE KEYSTONE OF A EUROPEAN DEFENSE SYSTEM." },
                { 43, "EFFORTS BY KHRUSHCHEV TO FIND A CLOSER RELATIONSHIP WITH YUGOSLAVIA." },
                { 44, "BONN'S OPPOSITION TO PROPOSALS FOR AN EAST-WEST NONAGGRESSION PACT." },
                { 45, "BACKGROUND OF THE NEW PRIME MINISTER OF GREAT BRITAIN." },
                { 46, "PRESIDENT DE GAULLE'S POLICY ON BRITISH ENTRY INTO THE COMMON MARKET." },
                { 47, "PROPOSALS FOR A UNIFIED EUROPE INDEPENDENT OF THE U.S." },
                { 48, "WHAT IS THE \"HOT LINE\" PROPOSAL." },
                { 49, "BACKGROUND OF THE NEW CHANCELLOR OF WEST GERMANY, LUDWIG ERHARD." },
                { 50, "MEETING OF MINISTERS IN PARIS IN APRIL OF THOSE COUNTRIES THAT ARE MEMBERS OF BOTH NATO AND SEATO." },
                { 51, "SUCCESSOR TO PREMIER KHRUSHCHEV." },
                { 52, "HINT BY PREMIER KHRUSHCHEV, WHO IS BOTH THE COMMUNIST PARTY SECRETARY AND PREMIER, THAT HE MAY RETIRE FROM ONE OF HIS TWO POSTS." },
                { 53, "PREMIER KHRUSHCHEV ONCE AGAIN PRESSING FOR A NONAGGRESSION PACT BETWEEN NATO AND THE WARSAW PACT NATIONS." },
                { 54, "COMMUNIST CHINA'S REACTION TO THE SOVIET UNION'S SIGNING OF THE NUCLEAR TEST BAN TREATY WITH THE WEST." },
                { 55, "SUGGESTION MADE BY PRESIDENT KENNEDY FOR A NATO NUCLEAR MISSILE FLEET MANNED BY INTERNATIONAL CREWS." },
                { 56, "SUGGESTION BY PRESIDENT KENNEDY THAT THE U.S. AND THE SOVIET UNION COOPERATE ON A MOON PROJECT." },
                { 57, "PROVISIONS OF THE TEST BAN TREATY." },
                { 58, "OTHER NATIONS POSSESSING U.S. POLARIS MISSILES FOR THEIR NUCLEAR SUBMARINE FLEETS." },
                { 59, "DISPUTE BETWEEN THE U.S. AND THE SOVIET UNION OVER INSPECTION SYSTEMS AT THE GENEVA DISARMAMENT CONFERENCES." },
                { 60, "SIGNING OF THE TEST BAN TREATY." },
                { 61, "NATIONS WORKING ON NUCLEAR WEAPONS DEVELOPMENT." },
                { 62, "MOSCOW'S SUPPORT OF THE KURD'S STRUGGLE FOR AUTONOMY." },
                { 63, "PRESIDENT NASSER'S RULING OUT ARAB UNION SO LONG AS THE PRESENT GOVERNING PARTY IN SYRIA REMAINS IN CONTROL." },
                { 64, "PRIME MINISTER NEHRU'S COMING UNDER ATTACK LAST WEEK IN THE FIRST NO-CONFIDENCE MOTION ENTERTAINED BY THE INDIAN PARLIAMENT SINCE HIS GOVERNMENT TOOK OFFICE." },
                { 65, "BORDER DISPUTE BETWEEN ISRAEL AND SYRIA." },
                { 66, "AGREEMENT BETWEEN SYRIA AND IRAQ ON FULL ECONOMIC UNITY AND CLOSER ECONOMIC COOPERATION." },
                { 67, "CONTINUING CONFLICT BETWEEN INDIA AND PAKISTAN OVER KASHMIR." },
                { 68, "INDIAN FEARS OF ANOTHER COMMUNIST CHINESE INVASION." },
                { 69, "THE BAATH (RENAISSANCE) PARTY FOUNDED BY MICHEL AFLAK, WHICH HAS GAINED CONTROL OF SYRIA AND IRAQ AND AIMS TO UNITE ALL ARAB COUNTRIES." },
                { 70, "KING WHO SIGNED AWAY HIS POWER OF STATE GIVING FREE REIN TO HIS HALF-BROTHER FEISAL'S REFORM RULE." },
                { 71, "SOMALIA IS INVOLVED IN BORDER DISPUTES WITH ITS NEIGHBORS. WHAT MILITARY AID IS BEING SUPPLIED TO SOMALIA BY RUSSIA." },
                { 72, "ECONOMIC STEPS TAKEN AGAINST SOUTH AFRICA AS A PROTEST AGAINST THAT NATION'S APARTHEID POLICY." },
                { 73, "INCREASING CRITICISM OF PREMIER BEN BELLA IN ALGERIA FROM OTHER LEADERS WHO HELPED BRING ABOUT ALGERIAN INDEPENDENCE." },
                { 74, "TAKE OVER AGAIN BY MOISE TSHOMBE OF THE KATANGA PROVINCE IN THE CONGO." },
                { 75, "PREMIER CYRILLE ADOULA'S ORDERING THE ENTIRE SOVIET EMBASSY STAFF THROWN OUT OF HIS COUNTRY ON SPYING CHARGES." },
                { 76, "CONFERENCE ON AFRICAN UNITY TO BE HELD IN ADDIS ABABA ON MAY 22 BY THE HEADS OF STATE OF 31 INDEPENDENT AFRICAN NATIONS." },
                { 77, "SPAIN'S RELAXATION OF CONTROLS OVER SOME OF ITS AFRICAN TERRITORIES." },
                { 78, "FEDERATION OF EAST AFRICA TO BE FORMED BY KENYA, TANGANYIKA AND UGANDA WHEN KENYA GAINS ITS INDEPENDENCE ON DEC. 12 FROM BRITAIN." },
                { 79, "ALGERIAN VOTE ON A NEW CONSTITUTION GIVING FULL POWERS TO PREMIER BEN BELLA'S POLITICAL PARTY." },
                { 80, "EFFECTS OF THE SINO-SOVIET DISPUTE ON THE NEW NATIONS OF AFRICA AND ASIA OR ON AREAS OF FERMENT IN LATIN AMERICA." },
                { 81, "COMMUNIST CHINESE TRADE WITH THE SOVIET UNION, THE REST OF THE COMMUNIST WORLD, AND WITH THE NON-COMMUNIST WORLD." },
                { 82, "ALBANIAN BACKING OF COMMUNIST CHINA IN THE IDEOLOGICAL SPLIT WITH THE SOVIET UNION." },
                { 83, "RED CHINESE BLAMING OF ECONOMIC TROUBLES AND THE TREATY-BREAKING WITHDRAWAL OF RUSSIAN TECHNICAL ASSISTANCE FOR SERIOUS DELAYS IN ITS DEVELOPMENT PROGRAM." }

        }).collect(Collectors.toList());
    }

    @Before
    public void init() {
        retriever = new Retriever();
    }

    @Test @Ignore
    public void test_retriever() {

        /*
         * Run this only if the corpus is the benchmark corpus (time).
         * I used this to run all the 83 benchmark queries against the time
         * magazine corpus. The results are formatted in a usable form and
         * printed to the console. I then copied and used the text for
         * Precision and Recall computation.
         */
        if (ConfigurationManager.getConfiguration("corpus").equals("time")) {
            try {
                Document queryDocument = Query.createQueryFromString(queryString);
                if (queryDocument == null) {
                    System.out.println("Query is null");
                    return;
                }
                Map<DocumentReference, Double> results = retriever.retrieve(queryDocument);
                if (results == null) {
                    System.out.println("No documents returned.");
                    return;
                }
                /*
                 * We will consider only a certain number of results.
                 */
                final int MAX_NUMBER_OF_RESULTS = 20;
                StringBuilder sb = new StringBuilder();
                sb.append("RESULT_DOCUMENTS[");
                sb.append(queryIndex);
                sb.append("]");
                sb.append(" = ");
                sb.append("{");
                int count = 1;
                for (Map.Entry<DocumentReference, Double> docRef : results.entrySet()) {
                    /*
                     * We will consider only a certain number of results.
                     */
                    if (count > MAX_NUMBER_OF_RESULTS) {
                        break;
                    }
                    DocumentReference reference = docRef.getKey();
                    double score = docRef.getValue();
                    sb.append(" ");

                    sb.append(reference.getPath().getFileName().toString().replace(".txt", ""));

                    //sb.append("(" + String.format("%.3f", score) + ")");
                    sb.append(",");
                    count++;
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.replace(sb.length() - 1, sb.length(), " ");
                }
                sb.append("}");
                System.out.println(sb.toString());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (URISyntaxException use) {
                use.printStackTrace();
            }
        }
    }
}
