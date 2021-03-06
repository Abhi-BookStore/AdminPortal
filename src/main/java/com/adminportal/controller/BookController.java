package com.adminportal.controller;

import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.adminportal.domain.Book;
import com.adminportal.s3.service.S3Services;
import com.adminportal.service.BookService;
import com.adminportal.service.InStockNotificationService;
import com.adminportal.utility.AsyncMethodUtils;
import com.adminportal.utility.MailSenderUtilityService;

@Controller
@RequestMapping("/book")
public class BookController {

	public static final Logger log = LoggerFactory.getLogger(BookController.class);

	@Autowired
	private BookService bookService;
	
	@Autowired
	private InStockNotificationService inStockNotificationService;
	
	@Autowired
	private MailSenderUtilityService mailSenderUtilityService;
	
	@Autowired
	private AsyncMethodUtils asyncMethodUtils;
	
	@Autowired
	private S3Services s3Services;

	@Value("${jsa.s3.bucket}")
	private String bucketName;
	
	public static final String DEFAULT_BOOK_IMAGE="https://s3.ap-south-1.amazonaws.com/bookstore-book-image/BookImages/defaultbook.png";

	public static final String BOOK_IMAGE_PREFIX = "book_";
	
	public static final String BOOK_FOLDER_NAME = "BookImages";
	public static final String SUFFIX = "/";
	
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
		
		if(book.getBookImage().isEmpty()) {
			log.info("$$$$$$$$$$$$$$$$$$$c  book.getImage() is empty "+ book.getBookImage());
			book.setBookImageUrl(DEFAULT_BOOK_IMAGE);
		}else {
			log.info("$$$$$$$$$$$$$$$$$$$c  book.getImage() is not enpty  "+ book.getBookImage());
			MultipartFile bookImageFile = book.getBookImage();
			String bookImageName = BOOK_IMAGE_PREFIX + book.getTitle().trim() + "_" + book.getId()+ ".png";
			
			s3Services.uploadProfileImageToFolderInS3(bookImageFile, bookImageName, BOOK_FOLDER_NAME);

			URL bookImageUrl = s3Services.getObjectAccessibleUrl(BOOK_FOLDER_NAME+SUFFIX+bookImageName);
			log.info("************ bookImageUrl :::::  " + bookImageUrl.toString());
			
			book.setBookImageUrl(bookImageUrl.toString());
		}
		
		bookService.save(book);
		
//		try {
//			byte[] bytes = bookImageFile.getBytes();
//			String name = book.getId() + ".png";
//			log.info("Name of the image: :::::: "+ name);
//
//			BufferedOutputStream stream = new BufferedOutputStream(
//					new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
//			stream.write(bytes);
//			stream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

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
	private String updateBookWithPost( Model model, @ModelAttribute("book") Book book, HttpServletRequest request) {

		bookService.save(book);
		
		MultipartFile bookImageFile = book.getBookImage();

		if(!bookImageFile.isEmpty()) {
			
			String bookImageName = BOOK_IMAGE_PREFIX + book.getTitle().trim() + "_" + book.getId()+ ".png";
			
//			s3Services.uploadProfileImageToS3(bookImageFile, bookImageName);
			s3Services.uploadProfileImageToFolderInS3(bookImageFile, bookImageName, BOOK_FOLDER_NAME);

			URL bookImageUrl = s3Services.getObjectAccessibleUrl(BOOK_FOLDER_NAME+SUFFIX+bookImageName);
			log.info("************ bookImageUrl :::::  " + bookImageUrl.toString());
			
			book.setBookImageUrl(bookImageUrl.toString());			
//			try {
//				byte[] bytes = bookImage.getBytes();
//				String name = book.getId() + ".png";
//				
//				Files.delete(Paths.get("src/main/resources/static/image/book/" + name));
//
//				BufferedOutputStream stream = new BufferedOutputStream(
//						new FileOutputStream(new File("src/main/resources/static/image/book/" + name)));
//				stream.write(bytes);
//				stream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}else {
			book.setBookImageUrl(DEFAULT_BOOK_IMAGE);
		}
		bookService.save(book);
		model.addAttribute("bookImageUrl", book.getBookImageUrl().toString());
		
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@  bookImageUrl" + book.getBookImageUrl().toString());
		
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
	
	@RequestMapping("/deactivateBook")
	private String deactivateBook(Model model,
								@RequestParam("id") Long bookId) {
				
		Book book = bookService.findOne(bookId);
		if(book.isActive() == true) {
			book.setActive(false);
		}
		bookService.save(book);
		return "redirect:/book/bookList";
		
	}
	
	@RequestMapping("/activateBook")
	private String activateBook(Model model,
								@RequestParam("id") Long bookId) {
				
		Book book = bookService.findOne(bookId);
		if(book.isActive() == false) {
			book.setActive(true);
		}
		bookService.save(book);
		return "redirect:/book/bookList";
		
	}

	@Async
	@RequestMapping(value="/addBookQty", method=RequestMethod.POST)
	private String addBookQty(Model model, 
			@ModelAttribute("bookId") Long bookId,
			@ModelAttribute("bookQty") int bookQty
			) {
		
		log.info("Adding qty for book id: "+ bookId);
		
		Book book = bookService.findOne(bookId);
		
		// Adding notifyMe logic here
		if(book.getInStockNumber() == 0 && bookQty > 0) {
			// Async Method calls
			asyncMethodUtils.notifyMeImplementation(bookId, book);
			
		}
		
		if(bookQty > 0) {
			int totalQty = bookQty + book.getInStockNumber();
			book.setInStockNumber(totalQty);
			bookService.save(book);
		}
		
		return "redirect:/book/bookInfo?id="+bookId;
	}

}
