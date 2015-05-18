package TwitterStream;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.List;

import net.sf.json.JSONException;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
public class team2 {

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

        String strdbName = "data2";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
              .setOAuthConsumerKey("vpIOG6O2CbVEG2q4GiCWy6NuL")
              .setOAuthConsumerSecret("7Ua1o6yAridAllwqLDL17QQmKSukmCVyww4Dr2GCBK2xsS30ah")
              .setOAuthAccessToken("1569890539-3PjAVjMnI7RxHb44ui5Zr86GaphEehAwhx8c3uv")
              .setOAuthAccessTokenSecret("rGz9iakj6O1Wvwcjl6v6bfPp2ttZw34JcsHVZpDzqumfd");

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
                    query2.count(100);
                        try {
                            ConfigurationBuilder cb2 = new ConfigurationBuilder();
                            cb2.setDebugEnabled(true);
                            cb2.setOAuthConsumerKey("j2qXsyQmIR2WOubpmchf42zVo");
                            cb2.setOAuthConsumerSecret("kz8YEVl815peRH9IVOaUhBof2ee3oC7TLhqShJVImEglP3salc");
                            cb2.setOAuthAccessToken("1569890539-8JNSEJPoupYZvVGZYl8bVbPvL29WSRc90N1hzRX");
                            cb2.setOAuthAccessTokenSecret("WXp36ZzTpH7vdSs0EtayLb4q4iQCi4FF6V41SpU6L2WmB");
                            TwitterFactory tf = new TwitterFactory(cb2.build());                           
                            Twitter twitter = tf.getInstance();                       
                            QueryResult result = twitter.search(query2);
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
                    // TODO 自动生成的方法存根
                    
                }
        };
       
       FilterQuery query = new FilterQuery();
        String[] lang = {"en"};
        query.language(lang);
        
        double[][] locations = {{-83.0786055,42.255192},{-82.97392875,42.47657}};
        //query.track(track);
        query.locations(locations);
        twitterStream.addListener(listener);
        twitterStream.filter(query);
       
    }

}