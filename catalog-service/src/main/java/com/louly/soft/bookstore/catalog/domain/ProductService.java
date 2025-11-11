package com.louly.soft.bookstore.catalog.domain;

import com.louly.soft.bookstore.catalog.ApplicationProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationProperties properties;

    public PagedResult<Product> getProducts(int pageNo) {
        /**
         * when using pagination then always specify the sort option as well
         * this pageNo by default start with index 0
         */
        pageNo = pageNo <= 1 ? 0 : pageNo - 1;
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);
        Page<Product> productPages = productRepository.findAll(pageable).map(ProductMapper::toProduct);

        return new PagedResult<>(
                productPages.getContent(),
                productPages.getTotalElements(),
                productPages.getNumber() + 1, // this getNumber by default start with index 0
                productPages.getTotalPages(),
                productPages.isFirst(),
                productPages.isLast(),
                productPages.hasNext(),
                productPages.hasPrevious());
    }

    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code).map(ProductMapper::toProduct);
    }
}
