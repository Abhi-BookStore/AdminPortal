package com.adminportal.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.adminportal.domain.Book;
import com.adminportal.service.BookService;

@Controller
@RequestMapping("/book")
public class BookController {

	public static final Logger log = LoggerFactory.getLogger(BookController.class);

	@Autowired
	private BookService bookService;
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addNewBook(Model model) {
		Book book = new Book();
		model.addAttribute("book", book);
		return "addBook";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addNewBookPost(Model model, @ModelAttribute("book") Book book) {
		bookService.save(book);
		log.info("Saving book.....");
		
		MultipartFile bookImage = book.getBookImage();

		try {
			byte[] bytes = bookImage.getBytes();
			String name = book.getId() + ".png";
			log.info("Name of the image: :::::: "+ name);

			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:bookList";
	}

	@RequestMapping("/bookList")
	private String getBookList(Model model) {
		
		// Fetch all the ids from BookRepository
		// Get book object
		// add the book object to Model
		// render the book object to the View.

		// What if you want to do it in batches ??????????????????????????????
		// Would you still fetch all the books from tables and try to render it on View ?
		// What about pagination concepts working within there ?
		
		List<Book> allBooks =  bookService.findAll();
		model.addAttribute("bookList", allBooks);
				
		return "bookList";
	}
	
	@RequestMapping("/bookInfo")
	public String bookInfo(Model model, @RequestParam("id") Long id) {
				
		Book book = bookService.findOne(id);
		if(book == null) {
			return "redirect:bookList";
			
		}
		model.addAttribute("book", book);
		return "bookInfo";
	}
	
	@RequestMapping("/updateBook")
	private String updateBook(Model model, @RequestParam("id") Long id) {
		
		Book book = bookService.findOne(id);
		if(book == null) {
			return "redirect:bookList";			
		}
		
		model.addAttribute("book", book);
	
		return "updateBook";
	}
	
	@RequestMapping(value="/updateBook", method=RequestMethod.POST)
	private String updateBookWithPost(@ModelAttribute("book") Book book, HttpServletRequest request) {

		bookService.save(book);
		
		MultipartFile bookImage = book.getBookImage();

		if(!bookImage.isEmpty()) {
			try {
				byte[] bytes = bookImage.getBytes();
				String name = book.getId() + ".png";
				
				Files.delete(Paths.get("src/main/resources/static/image/book/" + name));

				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
				stream.write(bytes);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "redirect:/book/bookInfo?id="+book.getId();
	}

	@RequestMapping("/deleteBook")
	public String deleteBook(
			Model model,
			@RequestParam("id") Long bookId
			) {
		
		if(null != bookService.findOne(bookId)) {
			bookService.deleteById(bookId);
		}
		List<Book> bookList = bookService.findAll();
		model.addAttribute("bookList", bookList);
		
		return "bookList";
	}
	

}
