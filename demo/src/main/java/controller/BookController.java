package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import entity.Book;
import service.BookService;

@RestController
public class BookController {
	
	@Autowired
	private BookService bookService;
	@RequestMapping(value="/book/add")
	public boolean add(@RequestBody Book book) {
		return bookService.add(book);
	}
	@RequestMapping(value="/book/get/{Bid}",method=RequestMethod.GET)
	public Book get(@PathVariable("Bid") Integer  Bid) {
		return bookService.findById(Bid);
	}
	@RequestMapping(value="/book/list",method=RequestMethod.GET)
	public List<Book> list(){
		return bookService.list();
	}
}
