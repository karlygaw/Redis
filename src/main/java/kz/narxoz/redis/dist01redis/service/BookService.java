package kz.narxoz.redis.dist01redis.service;

import kz.narxoz.redis.dist01redis.models.Book;
import kz.narxoz.redis.dist01redis.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CacheService cacheService;


    public Book getBook(Long id) {
        final String cacheKey = "book:" + id;

        Book cachedBook = (Book) cacheService.getCachedObject(cacheKey);

        if (cachedBook != null) {
            return cachedBook;
        }

        Optional<Book> product = bookRepository.findById(id);
        product.ifPresent(p -> cacheService.cacheObject(cacheKey, p, 1, TimeUnit.MINUTES));

        return product.orElse(null);
    }

    public void updateBook(Book book) {
        // Обновляем продукт в базе данных
        bookRepository.save(book);

        // Обновляем продукт в кэше
        cacheService.cacheObject("book:" + book.getId(), book, 1, TimeUnit.MINUTES);
    }

    public void deleteBook(Long bookId) {
        // Удаляем продукт из базы данных
        bookRepository.deleteById(bookId);

        // Удаляем продукт из кэша
        cacheService.deleteCachedObject("book:" + bookId);
    }
}

