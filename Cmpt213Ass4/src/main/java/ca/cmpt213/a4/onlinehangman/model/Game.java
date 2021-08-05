//Yang Zou  yza497   301385615

package ca.cmpt213.a4.onlinehangman.model;


//The Game class contains all data needed in the game:
//Has 7 fields:
//char[] charArr: The process of the game initially implemented as "______"
//String theWord: The word player need to choose
//int id: The id of the game
//Status stat: Status of the game, initially set as "Active"
//int totalCount: The total num of guesses
//int wrongCount: The num of wrong guesses
//char guess: The guess made by the player

public class Game {

    public enum Status{
        Active,
        Won,
        Lost,
    }


    public char[] charArr;
    public String theWord;
    public int id;
    public Status stat;
    public int totalCount;
    public int wrongCount;
    public String guess;

    public Game(){
    }

    public char[] getCharArr() {
        return charArr;
    }

    public void setCharArr(char[] charArr) {
        this.charArr = charArr;
    }

    public String getTheWord() {
        return theWord;
    }

    public void setTheWord(String theWord) {
        this.theWord = theWord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStat() {
        return stat;
    }

    public void setStat(Status stat) {
        this.stat = stat;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }


}
