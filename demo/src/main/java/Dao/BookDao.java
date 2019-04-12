package Dao;

import java.util.List;

import entity.Book;

public interface BookDao {
	
	  public boolean addBook(Book book);
	  
	  public Book findById(Integer Bid);
	  
	  public List<Book> findAll();
}
