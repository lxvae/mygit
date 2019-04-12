package serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Dao.BookDao;
import entity.Book;
import service.BookService;
@Service
public class BookeServiceImpl implements BookService{
	
	@Autowired
	private BookDao bookDao ;

	@Override
	public Book findById(Integer Bid) {
	
		return bookDao.findById(Bid);
	}

	@Override
	public boolean add(Book book) {
		
		return bookDao.addBook(book);
	}

	@Override
	public List<Book> list() {
		
		return bookDao.findAll();
	}


}
