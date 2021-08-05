// Yang Zou  yza497   301385615


package ca.cmpt213.a4.onlinehangman.controllers;

import ca.cmpt213.a4.onlinehangman.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ca.cmpt213.a4.onlinehangman.model.Game;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import ca.cmpt213.a4.onlinehangman.controllers.GameNotFoundException;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//The controller has 5 functionalities:
//1).@GetMapping("/helloworld"): Display the helloworld messages.
//2).@GetMapping("/welcome"): Display the welcome meaasges, coder information and the rules of the game.
//3).@GetMapping("/game"): Display the game page for player to play.
//4).@PostMapping ("/game"): Handle the user input to change the game progress.If game ended,
// show gameover page and a button to back to welcome page.
//5).@GetMapping("/game/{id}"): Display the “game.html” webpage with the game indicated by id
//refer to the localhost:8080/game entry for what to include. If id is invalid,
// throw an error page with status=404, and messages that guid the play to gamenotfound page to see more messages.
//6).@GetMapping("/gamenotfound"): Require player to type to see the game not found page,
// displays the not found information, and a button to back to welcome page.

@Controller
public class HangmanController {

    //The gutFile function get a path to a file, return a  list contains all messages in the file.
    public static List<String> getFile(String path) throws Exception {
        FileReader fileReader =new FileReader(path);
        BufferedReader bufferedReader =new BufferedReader(fileReader);
        List<String> list =new ArrayList<String>();
        String str=null;
        while((str=bufferedReader.readLine())!=null) {
            if(str.trim().length()>2) {
                list.add(str);
            }
        }
        return list;
    }

    public static List<Game> database=new ArrayList<Game>();//Contaons all game data.
    private Message promptMessage; //a resusable String object to display a prompt message at the screen
    public static int ID=0;//use to increment the game id.


    //works like a constructor, but wait until dependency injection is done, so it's more like a setup
    @PostConstruct
    public void hangmanControllerInit() {
        promptMessage = new Message("Initializing...");
    }


    //Display the helloworld messages.
    @GetMapping("/helloworld")
    public String showHelloworldPage(Model model) {

        promptMessage.setMessage("You are at the helloworld page!");
        model.addAttribute("promptMessage", promptMessage);

        // take the user to helloworld.html
        return "helloworld";
    }


    //Display the welcome meaasges, coder information and the rules of the game.
    @GetMapping("/welcome")
    public String showWelcomePage(Model model){
        String creatorInfo="My name: Yang Zou</p>ID: yza497</p>Email: 18979193009@163.com";
        String rule ="You have maximum 7 chances to guess a word. You lose, you die.";
        model.addAttribute("creatorInfo", creatorInfo);
        model.addAttribute("rule", rule);


        return "welcome";
    }



    //Display the game page for player to play.
    @GetMapping("/game")
    public String GameStart(Model model,HttpSession httpSession) throws Exception {
        ID=ID+1;
        int totalCount=0;
        int wrongCount=0;
        String guess=" ";

        //Get all words in the file
        File f = new File(Paths.get("commonWords.txt").toAbsolutePath().toString());
        String address = f.getAbsolutePath();
        List<String>text=getFile(address);

        //Randomly select a word to play
        int min=0;
        int max=text.size()-1;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;

        //theWord is the word to guess, charArr is the process
        char[] theWord=text.get(s).toCharArray();
        char[] charArr=new char[theWord.length];


        int index=0;
        for(char c:charArr){
            charArr[index]='_';
            index++;
        }


        //Create the Game object to pass the value to html page
        Game newGame = new Game();
        newGame.setCharArr(charArr);
        newGame.setGuess(guess);
        newGame.setId(ID);
        newGame.stat= Game.Status.Active;
        newGame.setTheWord(text.get(s));
        newGame.setTotalCount(totalCount);
        newGame.setWrongCount(wrongCount);
        httpSession.setAttribute("newGames", newGame);
        model.addAttribute("newGame", newGame);


        //Load the new game into database
        if(database.size()<ID) {
            database.add(ID - 1, newGame);
        }
        else{
            database.set(ID - 1, newGame);
        }

        return "game";
    }



    //Handle the user input to change the game progress.If game ended,
    // show gameover page and a button to back to welcome page.
    @PostMapping ("/game")
    public String guess(@ModelAttribute("newGame") Game newGame,HttpSession httpSession,Model model) {

        //Get the game object sent from html page
        Game game= (Game) httpSession.getAttribute("newGames");
        char[] charArr=game.charArr;
        String theWord=game.theWord;
        String guess=newGame.guess.equals("")?" ":newGame.guess;
        int totalCount=game.totalCount;
        int wrongCount=game.wrongCount;
        int id=game.id;


        //If the play enter an empty guess, ignore it
        if(guess.equals(" ")){
            newGame.setWrongCount(wrongCount);
            newGame.setTotalCount(totalCount);
            newGame.setTheWord(theWord);
            newGame.setId(id);
            newGame.setGuess(guess);
            newGame.setCharArr(charArr);
            game.stat=Game.Status.Active;
            newGame.stat= Game.Status.Active;
            return "game::game-makeGuess";
        }


        //Increment the total count
        totalCount++;
        game.totalCount++;
        int fcount=0;
        int index=0;
        boolean correct=false;

        //Load the process
        for(char c:theWord.toCharArray()){
            if(guess.charAt(0)==c){
                charArr[index]=c;
                correct=true;
            }
            index++;
        }

        //If guess is wrong, increment the wrongGuess
        if(!correct){
            game.wrongCount++;
            wrongCount++;
        }


        //Count the remaining characters need to guess
        for(char c : charArr){
            if(c=='_'){
                fcount++;
            }
        }


        //Check the status of the game
        String status;
        if(wrongCount==8){
            game.stat= Game.Status.Lost;
            newGame.stat= Game.Status.Lost;
            status="Lost";
        }
        else if(fcount==0){
            game.stat=Game.Status.Won;
            newGame.stat= Game.Status.Won;
            status="Won";
        }
        else{
            game.stat=Game.Status.Active;
            newGame.stat= Game.Status.Active;
            status="Active";
        }



        //Refresh the game data
        newGame.setWrongCount(wrongCount);
        newGame.setTotalCount(totalCount);
        newGame.setTheWord(theWord);
        newGame.setId(id);
        newGame.setGuess(guess);
        newGame.setCharArr(charArr);


        //Load the game data
        httpSession.setAttribute("status", status);
        model.addAttribute("status", status);

        //Refresh the database
        if(database.size()<id) {
            database.add(id - 1, newGame);
        }
        else{
            database.set(id - 1, newGame);
        }

        if(game.stat!= Game.Status.Active){
            model.addAttribute("word", theWord);
            return("gameover");
        }

        return "game::game-makeGuess";
    }



 //Display the “game.html” webpage with the game indicated by id
//refer to the localhost:8080/game entry for what to include. If id is invalid,
// throw an error page with status=404, and messages that guid the play to gamenotfound page to see more messages.
    @GetMapping("/game/{id}")
    public String searchPage(@PathVariable("id") long Id,Model model) {

        if(Id>database.size() || Id<=0){
            throw new GameNotFoundException();
        }

        Game theGame=database.get((int)Id-1);

        String sss="";
        if(theGame.stat==Game.Status.Lost){
            sss="Lost";
        }
        else if(theGame.stat==Game.Status.Won){
            sss="Won";
        }

        model.addAttribute("newGame", theGame);
        model.addAttribute("word", theGame.theWord);
        model.addAttribute("status",sss);

        if(theGame.stat != Game.Status.Active){ return "gameover"; }

        else{return "game"; }


    }




    //When play search a invalid id ,displays the not found information,
    // and a button to back to welcome page.
    @ExceptionHandler(GameNotFoundException.class)
    public ModelAndView gameNotFound(HttpServletResponse response){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("gamenotfound");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return mv;

    }

}