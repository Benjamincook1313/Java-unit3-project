package com.javaunit3.springmvc;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class MovieController {

  @Autowired
  BestMovieService bestMovieService;

  @Autowired
  private SessionFactory sessionFactory;

  @RequestMapping("/")
  public String getIndexPage(){
    return "index";
  }

  @RequestMapping("/bestMovie")
  public String getBestMoviePage(Model model){
    Session session = sessionFactory.getCurrentSession();
    session.beginTransaction();
    List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();
    movieEntityList.sort(Comparator.comparingInt(movieEntity -> movieEntity.getVotes().size()));
    MovieEntity movieWithMostVotes = movieEntityList.get(movieEntityList.size() -1);

    List<String> voterNames = new ArrayList<>();
    for(VoteEntity vote : movieWithMostVotes.getVotes()){
      voterNames.add(vote.getVoterName());
    }

    String voterNamesList = String.join(",", voterNames);

    model.addAttribute("BestMovie", movieWithMostVotes.getTitle());
    model.addAttribute("bestMovieVoters", voterNamesList);
    session.getTransaction().commit();

    return "bestMovie";
  }

  @RequestMapping("/voteForBestMovieForm")
  public String voteForBestMoviePage(Model model){

    Session session = sessionFactory.getCurrentSession();
    session.beginTransaction();
    List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();
    session.getTransaction().commit();

    model.addAttribute("movies", movieEntityList);

    return "voteForBestMovie";
  }

  @RequestMapping("/voteForBestMovie")
  public String voteForBestMovie(HttpServletRequest req , Model model){
//    String movieTitle = req.getParameter("movieTitle");

    String voterName = req.getParameter("voterName");
    String movieId = req.getParameter("movieId");

    Session session = sessionFactory.getCurrentSession();
    session.beginTransaction();

    MovieEntity movieEntity = (MovieEntity)
    session.get(MovieEntity.class, Integer.parseInt(movieId));
    VoteEntity newVote = new VoteEntity();
    newVote.setVoterName(voterName);
    movieEntity.addVote(newVote);

    session.update(movieEntity);
    session.getTransaction().commit();


//    model.addAttribute("BestMovieVote", movieTitle);

    return "voteForBestMovie";
  }

  @RequestMapping("/addMovieForm")
  public String addMoviePage(){
    return "addMovie";
  }

  @RequestMapping("/addMovie")
  public String addMovie(HttpServletRequest req){

    String title = req.getParameter("title");
    String maturityRating = req.getParameter("maturityRating");
    String genre = req.getParameter("genre");

    MovieEntity movieEntity = new MovieEntity();
    movieEntity.setTitle(title);
    movieEntity.setMaturityRating(maturityRating);
    movieEntity.setGenre(genre);

    Session session = sessionFactory.getCurrentSession();

    session.beginTransaction();
    session.save(movieEntity);
    session.getTransaction().commit();

    return "addMovie";
  }

}
