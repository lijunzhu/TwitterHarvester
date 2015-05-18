package TwitterStream;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.json.JSONException;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
public class team3 {

    public static Database connectCouchDB(String strDBName) {
        Database dbCouchDB = null;
        Session dbCouchDBSession = new Session("localhost", 5984);
        List<String> databases = dbCouchDBSession.getDatabaseNames();
        if (databases.contains(strDBName)) {
            dbCouchDB = dbCouchDBSession.getDatabase(strDBName);
        } else {
            dbCouchDBSession.createDatabase(strDBName);
            dbCouchDB = dbCouchDBSession.getDatabase(strDBName);
        }

        return dbCouchDB;

    }

    public static Document tweetToCouchDocument(Status tweet) {
        
        Document couchDocument = new Document();                        
        couchDocument.setId(String.valueOf(tweet.getId()));     
        couchDocument.put("UserName", tweet.getUser().getName().toString());
        couchDocument.put("Lat", tweet.getGeoLocation().getLatitude());
        couchDocument.put("Lon", tweet.getGeoLocation().getLongitude());
        couchDocument.put("Tweet", tweet.getText().toString());
        couchDocument.put("User", tweet.getUser().toString());
        
        
      return couchDocument;
    }

    public static void main(String[] args) throws IOException, TwitterException {

        String strdbName = "data3";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
              .setOAuthConsumerKey("ZIw8VwCYLs3R1zH2jPb2N9a9a")
              .setOAuthConsumerSecret("wLdZrOY2797VXEtJlelkkuG3Iod5oirQEnvPr14XFpJgnVj69u")
              .setOAuthAccessToken("1569890539-RzvUSs17gCUDq67dm8NypDU6Oxwqj7taIh3mcWX")
              .setOAuthAccessTokenSecret("GId43N1tsQJ8xTbEb4rNykZ6hSW6onCkdwcKuSjoYeVPa");
        
        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());

        TwitterStream twitterStream = twitterStreamFactory.getInstance();

        Database dbInstance = connectCouchDB(strdbName);
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if(status.getGeoLocation() != null) {
                    double latitude  = status.getGeoLocation().getLatitude();
                    double longitude = status.getGeoLocation().getLongitude();
                    long user = status.getUser().getId();                   
                    if (  status.getGeoLocation() != null){
                    System.out.println("User:"+ user);
                    String username = status.getUser().getScreenName();
                    System.out.println(username);              
                    System.out.println("lacation: "+ latitude+","+longitude);
                    long tweetId = status.getId(); 
                    System.out.println(tweetId);
                    String content = status.getText();
                    System.out.println(content +"\n");             
                    try{
                        Document document = tweetToCouchDocument(status);
                        dbInstance.saveDocument(document);
                        System.out.println("Add one Document");
                        }
                        catch(JSONException j){
                            System.out.println("same id");
                        }                                                                                           
                    Query query2 = new Query(username);
                    query2.setCount(100);
                        try {
                            ConfigurationBuilder cb2 = new ConfigurationBuilder();
                            cb2.setDebugEnabled(true);
                            cb2.setOAuthConsumerKey("ZwWBfzOBoEJ4zt18A9ZePsnFK");
                            cb2.setOAuthConsumerSecret("TrYtwUkIg0dEkcSZS0PD8wUr3ylABeDZfrdGIkJmsWAWfUVWSg");
                            cb2.setOAuthAccessToken("3180591786-5sNzdbPk4ED7ur9Fr93nL1Ntw99VaajMMQNDzJ2");
                            cb2.setOAuthAccessTokenSecret("TebFo8st6Cgq1TnnrEFLkOHa6EYDKuqmAYWmeftl4UEhl");
                            TwitterFactory tf = new TwitterFactory(cb2.build());                           
                            Twitter twitter = tf.getInstance();                       
                            QueryResult result = twitter.search(query2);
                            query2.count(100);
                            do{
                                List<Status> tweets = result.getTweets();
                                for (Status tweet : tweets){
                                    if (  tweet.getGeoLocation() != null && tweet.getGeoLocation().getLatitude()!=0){
                                    String username2 = tweet.getUser().getScreenName();
                                    System.out.println(username2);              
                                    System.out.println("lacation: "+ tweet.getGeoLocation().getLatitude()+","+tweet.getGeoLocation().getLongitude());
                                    long tweetId2 = tweet.getId(); 
                                    System.out.println(tweetId2);
                                    String content2 = tweet.getText();
                                    System.out.println(content2 +"\n");
                                    try{
                                    Document document2 = tweetToCouchDocument(tweet);
                                    dbInstance.saveDocument(document2);
                                    System.out.println("Add one Document");}
                                    catch(JSONException j){
                                        System.out.println("same id");
                                    }
                                    }
                                }query2=result.nextQuery();
                                if(query2!=null)
                                    result=twitter.search(query2);
                                    }while(query2!=null);                                   
                        } catch (TwitterException e) {
                            e.printStackTrace();
                        }
                        
                    }
                 
                }
                    }
      
        
            

            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

                 @Override
                  public void onScrubGeo(long arg0, long arg1){
               // TODO Auto-generated method stub
                 }
                 public void onTrackLimitationNotice(int arg0){
                      // TODO Auto-generated method stub

                  }


                @Override
                public void onException(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onStallWarning(StallWarning arg0) {
                    // TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
                    
                }
        };
       
       FilterQuery query = new FilterQuery();
        String[] lang = {"en"};
        query.language(lang);
        
        double[][] locations = {{-83.18328225,42.255192},{-83.0786055,42.47657}};
        //query.track(track);
        query.locations(locations);
        twitterStream.addListener(listener);
        twitterStream.filter(query);
       
    }

}