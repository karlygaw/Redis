package kz.narxoz.redis.dist01redis.service;

import kz.narxoz.redis.dist01redis.models.Book;
import kz.narxoz.redis.dist01redis.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CacheService cacheService;

    private final Map<Long, Integer> popularBooks = new HashMap<>();

    public Book getBook(Long id) {
        final String cacheKey = "book:" + id;

        Book cachedBook = (Book) cacheService.getCachedObject(cacheKey);

        if (cachedBook != null) {
            incrementPopularity(id);
            return cachedBook;
        }

        Optional<Book> book = bookRepository.findById(id);
        book.ifPresent(b -> {
            cacheService.cacheObject(cacheKey, b, 10, TimeUnit.MINUTES);
            incrementPopularity(id);
        });

        return book.orElse(null);
    }

//    public void updateBook(Book book) {
//        bookRepository.save(book);
//        cacheService.cacheObject("book:" + book.getId(), book, 10, TimeUnit.MINUTES);
//    }

    public void updateBook(Book book) {
        Optional<Book> existingBook = bookRepository.findById(book.getId());
        if (existingBook.isPresent()) {
            bookRepository.save(book);
            cacheService.cacheObject("book:" + book.getId(), book, 10, TimeUnit.MINUTES);
        } else {
            throw new IllegalArgumentException("Книга с ID " + book.getId() + " не найдена!");
        }
    }


    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
        cacheService.deleteCachedObject("book:" + bookId);
    }

    public List<Book> getPopularBooks() {
        List<Long> bookIds = popularBooks.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        return bookRepository.findAllById(bookIds);
    }

    private void incrementPopularity(Long id) {
        popularBooks.put(id, popularBooks.getOrDefault(id, 0) + 1);
    }
}
