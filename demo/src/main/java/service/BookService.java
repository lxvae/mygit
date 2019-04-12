package service;

import java.util.List;

import entity.Book;

public interface BookService {

	public Book findById(Integer Bid);

	public boolean add(Book book);

	public List<Book> list();
}
