/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4task2;

/**
 * a helper class that stores all information the android app needs
 * all getters and setters
 * @author Zhexin Chen (zhexinc)
 */

public class DBDoc {
    String hero;
    int averageKill;
    int averageDeath;
    String user1;
    String user2;
    String user3;
    String user4;
    String user5;

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUser3() {
        return user3;
    }

    public void setUser3(String user3) {
        this.user3 = user3;
    }

    public String getUser4() {
        return user4;
    }

    public void setUser4(String user4) {
        this.user4 = user4;
    }

    public String getUser5() {
        return user5;
    }

    public void setUser5(String user5) {
        this.user5 = user5;
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }

    public int getAverageKill() {
        return averageKill;
    }

    public void setAverageKill(int averageKill) {
        this.averageKill = averageKill;
    }

    public int getAverageDeath() {
        return averageDeath;
    }

    public void setAverageDeath(int averageDeath) {
        this.averageDeath = averageDeath;
    }
}
