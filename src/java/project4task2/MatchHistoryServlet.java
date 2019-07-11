/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;



import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;

/**
 *  Unlike task 1, task 2 will also write data to mongoDB
 * @author Zhexin Chen (zhexinc)
 */
@WebServlet(name = "MatchHistoryService", urlPatterns = {"/MatchHistoryService/*","/stat"})
public class MatchHistoryServlet extends HttpServlet {
    
    MatchHistoryModel mhm = new MatchHistoryModel(); 
    
    public void MathHistoryServlet(){
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
        String servletPath = request.getServletPath();
        MongoDB mdb = new MongoDB();
        String ua = request.getHeader("User-Agent");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        System.out.println("Console: doGET visited");
        
        // if the url is to get dashboard
        // forward to the dashboard view
        if (servletPath.equals("/stat")){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        DBDoc documents = mdb.readDB();
        String pHero = documents.getHero();
        String killAvg = documents.getAverageKill()+"";
        String deathAvg = documents.getAverageDeath()+"";
        String user1 = documents.getUser1();
        String user2 = documents.getUser2();
        String user3 = documents.getUser3();
        String user4 = documents.getUser4();
        String user5 = documents.getUser5();
        request.setAttribute("pHero", pHero);
        request.setAttribute("killAvg", killAvg);
        request.setAttribute("deathAvg", deathAvg);
        request.setAttribute("user1",user1);
        request.setAttribute("user2",user2);
        request.setAttribute("user3",user3);
        request.setAttribute("user4",user4);
        request.setAttribute("user5",user5);
        String nextView = "Stat.jsp";
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
        mdb.close();
        }
        
        // else, execute the search function
        else{
        String searchID = (request.getPathInfo()).substring(1);
        mdb.writeDB("User", "User device:"+ua+"<br></br>Time: "+strDate+"<br></br>Search contents:"+searchID);   
        System.out.println(searchID);
        mhm =new MatchHistoryModel();
        MatchHistory searchResult = mhm.searchResult(searchID);
        System.out.println(searchResult.getHero1Name());
        int kills = 0;
        int death = 0;
        String hero1 = "";
        String hero2 = "";
        String hero3 = "";
        try{
        kills += Integer.parseInt(searchResult.getHero1Kill());
        kills += Integer.parseInt(searchResult.getHero2Kill());
        kills += Integer.parseInt(searchResult.getHero3Kill());
        death += Integer.parseInt(searchResult.getHero1Death());
        death += Integer.parseInt(searchResult.getHero2Death());
        death += Integer.parseInt(searchResult.getHero3Death());
        hero1 = hero1+searchResult.getHero1Name();
        hero2 = hero2+searchResult.getHero2Name();
        hero3 = hero3+searchResult.getHero3Name();
        mdb.writeDB("Kill", ""+kills/3);
        mdb.writeDB("Death", ""+death/3);
        mdb.writeDB("Hero", hero1);
        mdb.writeDB("Hero", hero2);
        mdb.writeDB("Hero", hero3);
        mdb.close();
        }
        catch(Exception e){
        }

        Gson gson = new Gson();
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(searchResult));    
        }
}



    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
