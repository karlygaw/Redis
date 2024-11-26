package kz.narxoz.redis.dist01redis.repository;

import jakarta.transaction.Transactional;
import kz.narxoz.redis.dist01redis.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {
}