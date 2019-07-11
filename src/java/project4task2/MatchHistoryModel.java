/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;

import java.util.*;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.*;
import java.math.BigInteger;
/**
 *
 * @author Zhexin Chen (zhexinc)
 * The business model that fetches 
 * every single information needed from the 3rd party API
 */
public class MatchHistoryModel {
    
    // initialize the member variables: key, MatchHistory object
    String key = "AD9247C0F7D06E3AEC94803E141CBB47";
    MatchHistory mh= null;
    
    //maps and lists containing the results
    Map<String, Integer[]> heroStat = null;
    //    matchStat Map: Win,hero_id, Level, Kill, Death, Assistant, GPMs
    Map<String, Integer[]>  matchStat = null;
    
    // Gson parser
    Gson gson = null;
    JsonParser parser = new JsonParser();
    
    // convert steam 64 ID to steam 32 ID (short ID)
    String shortID;
    
    public MatchHistoryModel(){
    mh = new MatchHistory();
    heroStat = new HashMap<>();
    matchStat = new HashMap<>();
    gson = new Gson();
    }
    
    // main - used for testing
    public static void main(String[] args) {
            MatchHistoryModel mhm = new MatchHistoryModel();
            mhm.searchUserID("76561198128848282");
            mhm.searchMatchID("76561198128848282");
            mhm.fillMatch("76561198128848282");
            mhm.setModel();
            System.out.println(mhm.mh.getLastLogin());
    }
    
    // searchResult and return all relevant results
    public MatchHistory searchResult(String id){
    this.mh = new MatchHistory();
    try{
    searchUserID(id);
    searchMatchID(id);
    fillMatch(id);
    setModel();
    }
    catch(Exception e){}
    return this.mh;
    }
    
    /**
     *  this class search the match history of a single account
     * @param id searchID
     */
    public void searchMatchID(String id){
        StringBuilder sb = new StringBuilder();
    try {
			String match_history_url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/"
                                + "?key="+key+"&account_id="+id+"&matches_requested=100";
			URL url = new URL(match_history_url);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while((line=reader.readLine())!=null)
				sb.append(line+" ");
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JsonObject returnData = parser.parse(sb.toString()).getAsJsonObject();
        JsonObject resultData = parser.parse(returnData.get("result").toString()).getAsJsonObject();
        JsonArray matchData = resultData.get("matches").getAsJsonArray();
        int mCount = 0;
        for (JsonElement obj : matchData){
            JsonObject job = parser.parse(obj.toString()).getAsJsonObject();
            if(job.get("lobby_type").getAsInt()==0){
            String matchID = job.get("match_id").getAsString();
            this.matchStat.put(matchID, new Integer[7]);
            mCount +=1;
            }
            if (mCount>=3)
                break;
        }
    }
    
    /**
     * this method search the account information and return all searched information
     * 
     * @param id searchID
     */
    public void searchUserID(String id){
    
        StringBuilder sb = new StringBuilder();
    try {
			String match_history_url = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+key+"&steamids="+id;
			URL url = new URL(match_history_url);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
//			GZIPInputStream gzin = new GZIPInputStream(is);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while((line=reader.readLine())!=null)
				sb.append(line+" ");
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 
        
        JsonObject returnData = parser.parse(sb.toString()).getAsJsonObject();
        JsonObject resultData = parser.parse(returnData.get("response").toString()).getAsJsonObject();
        JsonArray playersData = resultData.get("players").getAsJsonArray();
        JsonElement obj =  playersData.get(0);
            JsonObject job = parser.parse(obj.toString()).getAsJsonObject();
            this.mh.setNickName(job.get("personaname").getAsString());
            this.mh.setLastLogin(Time2String(job.get("lastlogoff").getAsString()));
            this.mh.setAvatarURL(job.get("avatarfull").getAsString());        
    }
    
    /**
     * helper method used to convert unix timestamp to date time string
     * @param unixTime
     * @return the string representation of the unix timestampe
     */
    public static String Time2String(String unixTime){
    Long timestamp = Long.parseLong(unixTime) * 1000;
    String date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US).format(new Date(timestamp));
    return date;
    }
    
    /**
     * the searched results are all id of heroes/ string of numbers /etc
     * convert all the representations to the precise information they should be
     * and fill in the lists/maps that stored such informations
     * @param id searchID
     */
    public void fillMatch(String id){
    Set<String> matchSet = this.matchStat.keySet();
    Iterator<String> matchIter = matchSet.iterator();
    BigInteger id64 = new BigInteger(id);
    BigInteger id32 = id64.subtract(new BigInteger("76561197960265728"));
    String shortID = id32.toString();
    while (matchIter.hasNext()){
    
        StringBuilder sb = new StringBuilder();
        String matchID = matchIter.next();
    try {
			String match_history_url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key="+key+"&match_id="+matchID;
			URL url = new URL(match_history_url);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
//			GZIPInputStream gzin = new GZIPInputStream(is);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while((line=reader.readLine())!=null)
				sb.append(line+" ");
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JsonObject returnData = parser.parse(sb.toString()).getAsJsonObject();
        JsonObject resultData = parser.parse(returnData.get("result").toString()).getAsJsonObject();
        JsonArray playersData = resultData.get("players").getAsJsonArray();
        boolean radiantWin = resultData.get("radiant_win").getAsString().equals("true");
        for (int i =0;i<playersData.size();i++){
        JsonElement obj =  playersData.get(i);
            JsonObject job = parser.parse(obj.toString()).getAsJsonObject();
            
            if (job.get("account_id").getAsString().equals(shortID)){
                boolean playerWin = (job.get("player_slot").getAsInt()<=4 & radiantWin) | (job.get("player_slot").getAsInt()>=5& !radiantWin);

                 //    matchStat Map: Win,hero_id, Level, Kill, Death, Assistant, GPM
                int win = playerWin?1:0;
                int hero_id = job.get("hero_id").getAsInt();
                int level = job.get("level").getAsInt();
                int kill = job.get("kills").getAsInt();
                int death = job.get("deaths").getAsInt();
                int assist = job.get("assists").getAsInt();
                int gpm = job.get("gold_per_min").getAsInt();
                Integer[] gameStat = new Integer[]{win, hero_id, level, kill, death, assist, gpm};
                this.matchStat.put(matchID,gameStat);
            }  
        }
    }
    }
    
    /**
     * this method process the maps/lists that stored information and extract the necessary information to set construct the MatchHistory object
     * the object will then returned to the android app
     */
    public void setModel(){
                Iterator<String> mIter = this.matchStat.keySet().iterator();
            //    matchStat Map: Win,hero_id, Level, Kill, Death, Assistant, GPMs
                JsonArray heroes=parser.parse(((parser.parse(GetHero()).getAsJsonObject()).get("result").toString())).getAsJsonObject().get("heroes").getAsJsonArray();
                Integer[] t1 = this.matchStat.get(mIter.next());
                Integer[] t2 = this.matchStat.get(mIter.next());
                Integer[] t3 = this.matchStat.get(mIter.next());
                for (int i =0;i<heroes.size();i++){
                    JsonElement obj =  heroes.get(i);
                    JsonObject job = parser.parse(obj.toString()).getAsJsonObject();

                    if (job.get("id").getAsInt()==t1[1]){
                        String h_name = job.get("name").getAsString().substring(14);
                        this.mh.setHero1Name(insertBlank(h_name));
                        this.mh.setHero1URL(GetHeroPic(h_name));
                    }
                    
                    if (job.get("id").getAsInt()==t2[1]){
                        String h_name = job.get("name").getAsString().substring(14);
                        this.mh.setHero2Name(insertBlank(h_name));
                        this.mh.setHero2URL(GetHeroPic(h_name));
                    }
                    
                    if (job.get("id").getAsInt()==t3[1]){
                        String h_name = job.get("name").getAsString().substring(14);
                        this.mh.setHero3Name(insertBlank(h_name));
                        this.mh.setHero3URL(GetHeroPic(h_name));
                    };
                            }
                this.mh.setHero1Result(t1[0]==1?"Win":"Lose");
                this.mh.setHero2Result(t2[0]==1?"Win":"Lose");
                this.mh.setHero3Result(t3[0]==1?"Win":"Lose");
                
                this.mh.setHero1Level(""+t1[2]);
                this.mh.setHero2Level(""+t2[2]);
                this.mh.setHero3Level(""+t3[2]);
                
                this.mh.setHero1Kill(""+t1[3]);
                this.mh.setHero2Kill(""+t2[3]);
                this.mh.setHero3Kill(""+t3[3]);
                
                this.mh.setHero1Death(""+t1[4]);
                this.mh.setHero2Death(""+t2[4]);
                this.mh.setHero3Death(""+t3[4]);
                
                this.mh.setHero1Assists(""+t1[5]);
                this.mh.setHero2Assists(""+t2[5]);
                this.mh.setHero3Assists(""+t3[5]);
                
                this.mh.setHero1GPM(""+t1[6]);
                this.mh.setHero2GPM(""+t2[6]);
                this.mh.setHero3GPM(""+t3[6]);
//                this.mh.setHero1Name();            
    }
    
    /**
     * a helper method that gets the hero names
     * @return 
     */
        public String GetHero(){
        StringBuilder sb = new StringBuilder();
        try {
			//cityname = URLEncoder.encode(cityName, "UTF-8");
			String match_history_url = "http://api.steampowered.com/IEconDOTA2_570/GetHeroes/v1"+ "?key="+key;
			URL url = new URL(match_history_url);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
//			GZIPInputStream gzin = new GZIPInputStream(is);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while((line=reader.readLine())!=null)
				sb.append(line+" ");
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    return sb.toString();
    }
        
        /**
         * a helper method get hero picture url according to its name
         * @param heroName
         * @return the hero picture url
         */
       public String GetHeroPic(String heroName){
        return ("http://cdn.dota2.com/apps/dota2/images/heroes/"+heroName+"_full.png");
        }
       
       /**
        * the helper method that replace _ in hero names
        * @param ori original hero name string
        * @return 
        */
       public String insertBlank(String ori){
       byte[] myString = ori.getBytes();
       for (int i=0;i<myString.length;i++){
       if (myString[i] =='_'){
       myString[i] = ' ';
       }
       }
       return new String(myString);
       }
}