/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.google.gson.*;
import java.util.*;

/**
 * This class provides basic functions that read and write to the MongoDB
 * And provides the data needed for the dashboard function
 * @author Zhexin Chen (zhexinc)
 */
public class MongoDB {
    MongoClientURI uri = null;
    MongoDatabase database = null;
    MongoClient mongoClient = null;
    MongoCollection<Document> collection = null;
    
    /**
     * constructor that initialize the connection to the MongoDB
     */
    public MongoDB(){
    uri = new MongoClientURI(
    "mongodb://zhexinc:c6269502@cluster0-shard-00-00-9tgkk.mongodb.net:27017,cluster0-shard-00-01-9tgkk.mongodb.net:27017,cluster0-shard-00-02-9tgkk.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
    mongoClient = new MongoClient(uri);
    database = mongoClient.getDatabase("test");
    collection = database.getCollection("test");
    
    }
    
    // main as the test drive
    public static void main(String[] args){
        
        MongoDB mdb = new MongoDB();
        
        mdb.writeDB("test_data", "I don't know why");
        mdb.writeDB("test_data", "She");
        mdb.readDB();
    }
    
    //write to the DB
    public void writeDB(String key, String content){

    Document doc = new Document(key,content);
    collection.insertOne(doc);


    }
    
    /**
     * read all information stored in MongoDB
     * extract the keys we need
     * and store them in corresponding variables
     * @return the DBDoc object that stores necessary data for operational analytics
     */
    public DBDoc readDB(){
    Gson gson = new Gson();
    JsonParser parser=new JsonParser();
    // iterator
    MongoCursor<Document> cursor = collection.find().iterator();
    List<String> userList = new ArrayList<>();
    Map<String, Integer> heroCount = new HashMap<>();
    int killAll = 0;
    int killCount = 0;   
    int deathAll = 0;
    int deathCount = 0;
    // extract the needed keys
    try {
        while (cursor.hasNext()) {
            JsonObject job = parser.parse(cursor.next().toJson()).getAsJsonObject();
            try{
            killAll += job.get("Kill").getAsInt();
            killCount += 1;            
            }
            catch(Exception e){}
            
            try{
            deathAll += job.get("Death").getAsInt();
            deathCount += 1;     
            }
            catch(Exception e){

            }
            
            try{
            String heroName = job.get("Hero").getAsString();
            if (heroCount.containsKey(heroName)){
            heroCount.put(heroName, heroCount.get(heroName)+1);
            }
            else {
            heroCount.put(heroName, 0);
            }   
            }
            catch(Exception e){}
                        
            try{
            String userData = job.get("User").getAsString();
            userList.add(userData);
            }
            catch(Exception e){
                        }
    }
} finally {
    cursor.close();
}
    
    DBDoc dbd = new DBDoc();
    String pHero = "";
    int mFreq = 0;
    for (String i: heroCount.keySet()){
        if (heroCount.get(i)>mFreq){
        pHero = i;
        mFreq  = heroCount.get(i);
        }
    }
    
    // set statistics
    // set user login
    dbd.setAverageKill(killCount==0?0:killAll/killCount);
    dbd.setAverageDeath(deathCount==0?0:deathAll/deathCount);
    dbd.setHero(pHero+": "+mFreq+" times");
    dbd.setUser1(userList.get(userList.size()-1));
    dbd.setUser2(userList.get(userList.size()-2));
    dbd.setUser3(userList.get(userList.size()-3));
    dbd.setUser4(userList.get(userList.size()-4));
    dbd.setUser5(userList.get(userList.size()-5));
    return dbd;
    }
    
    /**
     * close the mongoClient
     */
    public void close(){
    mongoClient.close();
    }
    
}
