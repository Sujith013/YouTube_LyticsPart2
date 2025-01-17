package Actors;

import Actors.UserActor.SearchData;
import Actors.UserActor.FetchSearchData;
import Models.YoutubeService;
import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.YouTube;

import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;

import org.junit.*;
import org.mockito.Mock;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class UserActorTest {
    private static ActorSystem system;

    private static String[] VIDEO_KIND = {"youtube#channel","youtube#channel","youtube#playlist","youtube#playlist","youtube#playlist","youtube#playlist","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video","youtube#video"};
    private static String[] VIDEO_ID = {"https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=null","https://www.youtube.com/watch?v=2LkDU0iKaro","https://www.youtube.com/watch?v=PBaFURjVrm0","https://www.youtube.com/watch?v=eIho2S0ZahI","https://www.youtube.com/watch?v=HrCbXNRP7eg","https://www.youtube.com/watch?v=8jPQjjsBbIc","https://www.youtube.com/watch?v=O9pD6LTF4Bk","https://www.youtube.com/watch?v=arj7oStGLkU","https://www.youtube.com/watch?v=PY9DcIMGxMs","https://www.youtube.com/watch?v=-moW9jvvMr4","https://www.youtube.com/watch?v=H14bBuluwB8","https://www.youtube.com/watch?v=gUV5DJb6KGs","https://www.youtube.com/watch?v=lg48Bi9DA54","https://www.youtube.com/watch?v=dIYmzf21d1g","https://www.youtube.com/watch?v=Hu4Yvq-g7_Y"};
    private static String[] VIDEO_TITLE = {"TED","TEDx Talks","The 20 Most-Watched TEDTalks","10 funniest TED Talks","Most Interesting Ted Talks","TED Talks Daily","Networking Doesn't Have to Feel Gross | Daniel Hallak | TED","How to hack your brain for better focus | Sasha Hamdani | TEDxKC","How to Speak So That People Want to Listen | Julian Treasure | TED","The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED","How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED","What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne","Inside the Mind of a Master Procrastinator | Tim Urban | TED","Everything you think you know about addiction is wrong | Johann Hari | TED","A simple way to break a bad habit | Judson Brewer | TED","Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED","How to talk to the worst parts of yourself | Karen Faith | TEDxKC","The science behind dramatically better conversations | Charles Duhigg | TEDxManchester","How to Claim Your Leadership Power | Michael Timms | TED","How to Get Your Brain to Focus | Chris Bailey | TEDxManchester"};
    private static String[] CHANNEL_TITLE = {"https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@Yoong Cheong Sin","https://www.youtube.com/@Bryan Leonardo","https://www.youtube.com/@TED Audio Collective","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TEDx Talks","https://www.youtube.com/@TED","https://www.youtube.com/@TEDx Talks"};
    private static String[] CHANNEL_ID = {"https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCo05wfWBCsmAeL50CPA_Vyw","https://www.youtube.com/channel/UCKpc2NQtnrEx9Srs9EDDLUA","https://www.youtube.com/channel/UCy9b8cNJQmxX8Y2bdE6mQNw","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q","https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug","https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"};
    private static String[] DESCRIPTION = {"The TED Talks channel features the best talks and performances from the TED Conference, where the world's leading thinkers ...","TEDx is an international community that organizes TED-style events anywhere and everywhere -- celebrating locally-driven ideas ...","A list of the 20 most-watched talks on all the platforms we track: TED.com, YouTube, iTunes, embed and download, Hulu and ...","","","Every weekday, TED Talks Daily brings you the latest talks in audio. Join host and journalist Elise Hu for thought-provoking ideas ...","Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...","The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...","Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...","Are you \"too nice\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...","Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","NOTE FROM TED: This talk contains a discussion of suicidal ideation. If you are struggling with suicidal thoughts, please consult a ...","In a world of increasing complexity but decreasing free time, the role of the trusted 'explainer' has never been more important.","When faced with challenges, do you often seek someone else to blame? Leadership expert Michael Timms shows why this ...","The latest research is clear: the state of our attention determines the state of our lives. So how do we harness our attention to focus ..."};
    private static String[] URL = {"https://yt3.ggpht.com/ytc/AIdro_l_fFETDQgTAl5rWb38pxJww-4kszJH_n0G4fKP1BdK-jc=s800-c-k-c0xffffffff-no-rj-mo","https://yt3.ggpht.com/70r5TkYTLC0cpKLAiQEvcWLeIHB8yxoiog0nQIK9MmnZHqkICy0YA-jAaqfT2ChOBwehskjf5g=s800-c-k-c0xffffffff-no-rj-mo","https://i.ytimg.com/vi/iG9CE55wbtY/hqdefault.jpg","https://i.ytimg.com/vi/buRLc2eWGPQ/hqdefault.jpg","https://i.ytimg.com/vi/MB5IX-np5fE/hqdefault.jpg","https://i.ytimg.com/vi/24wBKuU2rfE/hqdefault.jpg","https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg","https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg","https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg","https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg","https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg","https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg","https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg","https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg","https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg","https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg","https://i.ytimg.com/vi/gUV5DJb6KGs/hqdefault.jpg","https://i.ytimg.com/vi/lg48Bi9DA54/hqdefault.jpg","https://i.ytimg.com/vi/dIYmzf21d1g/hqdefault.jpg","https://i.ytimg.com/vi/Hu4Yvq-g7_Y/hqdefault.jpg"};

    private static List<List<String>> expectedVideos = Arrays.asList(Arrays.asList("Networking Doesn't Have to Feel Gross | Daniel Hallak | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=2LkDU0iKaro","https://www.youtube.com/@https://www.youtube.com/@TED","Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...","https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("How to hack your brain for better focus | Sasha Hamdani | TEDxKC","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PBaFURjVrm0","https://www.youtube.com/@https://www.youtube.com/@TEDx Talks","The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...","https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"),Arrays.asList("How to Speak So That People Want to Listen | Julian Treasure | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=eIho2S0ZahI","https://www.youtube.com/@https://www.youtube.com/@TED","Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...","https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=HrCbXNRP7eg","https://www.youtube.com/@https://www.youtube.com/@TED","Are you \"too nice\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...","https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=8jPQjjsBbIc","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=O9pD6LTF4Bk","https://www.youtube.com/@https://www.youtube.com/@TEDx Talks","The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...","https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q"),Arrays.asList("Inside the Mind of a Master Procrastinator | Tim Urban | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=arj7oStGLkU","https://www.youtube.com/@https://www.youtube.com/@TED","Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...","https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("Everything you think you know about addiction is wrong | Johann Hari | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PY9DcIMGxMs","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("A simple way to break a bad habit | Judson Brewer | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=-moW9jvvMr4","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.","https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"),Arrays.asList("Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED","https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=H14bBuluwB8","https://www.youtube.com/@https://www.youtube.com/@TED","Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.","https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg","/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug"));

        private static Map<String, Object> responseData = new HashMap<>(Map.of("data", expectedVideos, "senti", ":|"));

    @Mock
    private YouTube mockYoutube;

    @Mock
    private YouTube.Search mockSearch;

    @Mock
    private YouTube.Search.List mockSearchList;

    @Mock
    private SearchListResponse mockResponse;

    @Mock
    private SearchResult[] mockVideos = new SearchResult[20];

    @Mock
    private SearchResultSnippet[] mockSnippet = new SearchResultSnippet[20];

    @Mock
    private ResourceId[] mockId = new ResourceId[20];

    @Mock
    private ThumbnailDetails[] mockThumbnailDetails = new ThumbnailDetails[20];

    @Mock
    private Thumbnail[] mockThumbnail = new Thumbnail[20];

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void FetchSearchDataTest() throws GeneralSecurityException, IOException {
        UserActor.FetchSearchData inputData = new UserActor.FetchSearchData(YoutubeService.getService(),"sampleQuery","sampleAPI");
        assertNotNull(inputData);
        assertEquals("sampleQuery",inputData.query);
        assertEquals("sampleAPI",inputData.apiKey);
    }

    @Test
    public void searchData() {
        UserActor.SearchData inputData = new UserActor.SearchData(responseData);
        assertNotNull(inputData);
        assertNotNull(inputData.getVideos());
        assertEquals("{\"data\":[[\"Networking Doesn't Have to Feel Gross | Daniel Hallak | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=2LkDU0iKaro\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...\",\"https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to hack your brain for better focus | Sasha Hamdani | TEDxKC\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PBaFURjVrm0\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...\",\"https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"How to Speak So That People Want to Listen | Julian Treasure | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=eIho2S0ZahI\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...\",\"https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=HrCbXNRP7eg\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Are you \\\"too nice\\\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...\",\"https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=8jPQjjsBbIc\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=O9pD6LTF4Bk\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...\",\"https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"Inside the Mind of a Master Procrastinator | Tim Urban | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=arj7oStGLkU\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...\",\"https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Everything you think you know about addiction is wrong | Johann Hari | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PY9DcIMGxMs\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"A simple way to break a bad habit | Judson Brewer | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=-moW9jvvMr4\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.\",\"https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=H14bBuluwB8\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"]],\"senti\":\":|\"}",inputData.getVideos());
    }

    @Test
    public void testHandleFetchSearchDataSuccess() throws IOException {
        mockYoutube = mock(YouTube.class);
        mockSearch = mock(YouTube.Search.class);
        mockResponse = mock(SearchListResponse.class);
        mockSearchList = mock(YouTube.Search.List.class);

        // Mock the YouTube search setup
        when(mockYoutube.search()).thenReturn(mockSearch);
        when(mockSearch.list(Collections.singletonList("snippet"))).thenReturn(mockSearchList);

        // Set up the query and max results
        when(mockSearchList.setQ("ted talks")).thenReturn(mockSearchList);
        when(mockSearchList.setKey("AIzaSyBhVg3undEJFND54BtUkJ17gSJCuK9CAvI")).thenReturn(mockSearchList);
        when(mockSearchList.setMaxResults(20L)).thenReturn(mockSearchList);

        // Mock the response to return our mock video
        when(mockSearchList.execute()).thenReturn(mockResponse);

        // Create the first mock video
        for(int i=0;i<20;i++) {
            mockVideos[i] = mock(SearchResult.class);
            mockSnippet[i] = mock(SearchResultSnippet.class);
            mockId[i] = mock(ResourceId.class);
            mockThumbnail[i] = mock(Thumbnail.class);
            mockThumbnailDetails[i] = mock(ThumbnailDetails.class);

            when(mockVideos[i].getId()).thenReturn(mockId[i]);
            when(mockVideos[i].getSnippet()).thenReturn(mockSnippet[i]);
            when(mockSnippet[i].getThumbnails()).thenReturn(mockThumbnailDetails[i]);
            when(mockThumbnailDetails[i].getHigh()).thenReturn(mockThumbnail[i]);

            when(mockId[i].getKind()).thenReturn(VIDEO_KIND[i]);
            when(mockId[i].getVideoId()).thenReturn(VIDEO_ID[i]);
            when(mockSnippet[i].getTitle()).thenReturn(VIDEO_TITLE[i]);
            when(mockSnippet[i].getChannelId()).thenReturn(CHANNEL_ID[i]);
            when(mockSnippet[i].getChannelTitle()).thenReturn(CHANNEL_TITLE[i]);
            when(mockSnippet[i].getDescription()).thenReturn(DESCRIPTION[i]);
            when(mockThumbnail[i].getUrl()).thenReturn(URL[i]);
        }

        when(mockResponse.getItems()).thenReturn(List.of(mockVideos));

        // Act and Assert
        new TestKit(system) {{
            ActorRef userActor = system.actorOf(UserActor.props());
            FetchSearchData fetchSearchData = new FetchSearchData(mockYoutube, "ted talks", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            userActor.tell(fetchSearchData, getRef());

            SearchData response = expectMsgClass(SearchData.class);
            Assert.assertEquals("{\"data\":[[\"Networking Doesn't Have to Feel Gross | Daniel Hallak | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=2LkDU0iKaro\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Networking doesn't always have to feel like a self-serving transaction, says executive coach Daniel Hallak. Highlighting the ...\",\"https://i.ytimg.com/vi/2LkDU0iKaro/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to hack your brain for better focus | Sasha Hamdani | TEDxKC\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PBaFURjVrm0\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The modern world constantly fragments our attention. In this funny, insightful talk, Dr. Hamdani, a psychiatrist and ADHD expert, ...\",\"https://i.ytimg.com/vi/PBaFURjVrm0/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"How to Speak So That People Want to Listen | Julian Treasure | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=eIho2S0ZahI\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Have you ever felt like you're talking, but nobody is listening? Here's Julian Treasure to help you fix that. As the sound expert ...\",\"https://i.ytimg.com/vi/eIho2S0ZahI/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"The Problem With Being ΓÇ£Too NiceΓÇ¥ at Work | Tessa West | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=HrCbXNRP7eg\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Are you \\\"too nice\\\" at work? Social psychologist Tessa West shares her research on how people attempt to mask anxiety with ...\",\"https://i.ytimg.com/vi/HrCbXNRP7eg/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"How to stay calm when you know you&#39;ll be stressed | Daniel Levitin | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=8jPQjjsBbIc\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/8jPQjjsBbIc/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"What Nobody Tells You About Your Twenties | Livi Redden | TEDxBayonne\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=O9pD6LTF4Bk\",\"https://www.youtube.com/@https://www.youtube.com/@TEDx Talks\",\"The fact of the matter is many of the biggest decisions that leave a long-lasting impact on our lives generally occur in our teens ...\",\"https://i.ytimg.com/vi/O9pD6LTF4Bk/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCsT0YIqwnpJCM-mx7-gSA4Q\"],[\"Inside the Mind of a Master Procrastinator | Tim Urban | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=arj7oStGLkU\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Tim Urban knows that procrastination doesn't make sense, but he's never been able to shake his habit of waiting until the last ...\",\"https://i.ytimg.com/vi/arj7oStGLkU/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Everything you think you know about addiction is wrong | Johann Hari | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=PY9DcIMGxMs\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/PY9DcIMGxMs/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"A simple way to break a bad habit | Judson Brewer | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=-moW9jvvMr4\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized Talk recommendations and more.\",\"https://i.ytimg.com/vi/-moW9jvvMr4/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"],[\"Grit: The Power of Passion and Perseverance | Angela Lee Duckworth | TED\",\"https://www.youtube.com/watch?v=https://www.youtube.com/watch?v=H14bBuluwB8\",\"https://www.youtube.com/@https://www.youtube.com/@TED\",\"Visit http://TED.com to get our entire library of TED Talks, transcripts, translations, personalized talk recommendations and more.\",\"https://i.ytimg.com/vi/H14bBuluwB8/hqdefault.jpg\",\"/channel/https://www.youtube.com/channel/UCAuUUnT6oDeKwE6v1NGQxug\"]],\"senti\":\":-|\"}", response.getVideos());
        }};
    }

    @Test
    public void testHandleFetchSearchDataFailure() throws IOException {
        // Arrange
        mockYoutube = mock(YouTube.class);
        mockSearch = mock(YouTube.Search.class);
        mockResponse = mock(SearchListResponse.class);
        mockSearchList = mock(YouTube.Search.List.class);

        // Mock the YouTube search setup
        when(mockYoutube.search()).thenReturn(mockSearch);
        when(mockSearch.list(Collections.singletonList("snippet"))).thenReturn(mockSearchList);

        // Set up the query and max results
        when(mockSearchList.setQ("")).thenThrow(new NullPointerException("Query error - Query cannot be null"));;

        when(mockSearchList.setKey("")).thenThrow(new NullPointerException("API error - API key not present"));
        when(mockSearchList.setKey("API_KEY")).thenThrow(new IllegalArgumentException("API error - API key too short"));
        when(mockSearchList.setKey("AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEabcdbdefgh")).thenThrow(new IllegalArgumentException("API error - API key too long"));
        when(mockSearchList.setKey("AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVE-@I")).thenThrow(new IllegalArgumentException("API error - illegal characters present in API"));

        // Act and Assert
        new TestKit(system) {{
            ActorRef userActor = system.actorOf(UserActor.props());
            FetchSearchData fetchSearchData = new FetchSearchData(mockYoutube, "ted talks", "");

            try {
                userActor.tell(fetchSearchData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchSearchData = new FetchSearchData(mockYoutube, "ted talks", "API_KEY");

            try {
                userActor.tell(fetchSearchData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchSearchData = new FetchSearchData(mockYoutube, "ted talks", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEabcdbdefgh");

            try {
                userActor.tell(fetchSearchData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchSearchData = new FetchSearchData(mockYoutube, "ted talks", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVE-@I");

            try {
                userActor.tell(fetchSearchData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }

            fetchSearchData = new FetchSearchData(mockYoutube, "", "AIzaSyCRJ3kJeKPXbVQKeAP6HKzsIWxnGfVEkPI");

            try {
                userActor.tell(fetchSearchData, getRef());
                expectNoMessage(); // The actor should handle the error internally without sending a message
            } catch (Exception e) {
                Assert.fail("Actor threw an unexpected exception: " + e.getMessage());
            }
        }};
    }
}
