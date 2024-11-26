package kz.narxoz.redis.dist01redis.service;

import kz.narxoz.redis.dist01redis.models.Product;
import kz.narxoz.redis.dist01redis.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CacheService cacheService;


    public Product getProduct(Long id) {
        final String cacheKey = "product:" + id;

        Product cachedProduct = (Product) cacheService.getCachedObject(cacheKey);

        if (cachedProduct != null) {
            return cachedProduct;
        }

        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(p -> cacheService.cacheObject(cacheKey, p, 1, TimeUnit.MINUTES));

        return product.orElse(null);
    }

    public void updateProduct(Product product) {
        // Обновляем продукт в базе данных
        productRepository.save(product);

        // Обновляем продукт в кэше
        cacheService.cacheObject("product:" + product.getId(), product, 1, TimeUnit.MINUTES);
    }

    public void deleteProduct(Long productId) {
        // Удаляем продукт из базы данных
        productRepository.deleteById(productId);

        // Удаляем продукт из кэша
        cacheService.deleteCachedObject("product:" + productId);
    }
}

