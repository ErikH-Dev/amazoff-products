package unit;

import entities.Product;
import entities.Vendor;
import interfaces.IProductRepository;
import interfaces.IVendorRepository;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.EntityNotFoundException;
import services.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private IProductRepository productRepository;
    private IVendorRepository vendorRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(IProductRepository.class);
        vendorRepository = mock(IVendorRepository.class);
        productService = new ProductService(productRepository, vendorRepository);
    }

    @Test
    void addProduct_shouldReturnCreatedProduct_whenVendorExists() {
        int oauthId = 1;
        Vendor vendor = new Vendor();
        Product product = new Product();
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().item(vendor));
        when(productRepository.create(product)).thenReturn(Uni.createFrom().item(product));

        Uni<Product> result = productService.create(oauthId, product);

        assertEquals(product, result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
        verify(productRepository, times(1)).create(product);
    }

    @Test
    void addProduct_shouldThrowEntityNotFoundException_whenVendorDoesNotExist() {
        int oauthId = 1;
        Product product = new Product();
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Product> result = productService.create(oauthId, product);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
        verify(productRepository, never()).create(any());
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        int productId = 1;
        Product product = new Product();
        when(productRepository.read(productId)).thenReturn(Uni.createFrom().item(product));

        Uni<Product> result = productService.read(productId);

        assertEquals(product, result.await().indefinitely());
        verify(productRepository, times(1)).read(productId);
    }

    @Test
    void getProductById_shouldReturnNull_whenProductDoesNotExist() {
        int productId = 1;
        when(productRepository.read(productId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Product> result = productService.read(productId);

        assertNull(result.await().indefinitely());
        verify(productRepository, times(1)).read(productId);
    }

    @Test
    void getAllProducts_shouldReturnProducts_whenProductsExist() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.readAll()).thenReturn(Uni.createFrom().item(products));

        Uni<List<Product>> result = productService.readAll();

        assertEquals(products, result.await().indefinitely());
        verify(productRepository, times(1)).readAll();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenVendorAndProductExist() {
        int oauthId = 1;
        Vendor vendor = new Vendor();
        Product product = new Product(1, "Product Name", 100.0, "Product Description");
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().item(vendor));
        when(productRepository.read(product.getId())).thenReturn(Uni.createFrom().item(product));
        when(productRepository.update(product)).thenReturn(Uni.createFrom().item(product));

        Uni<Product> result = productService.update(oauthId, product);

        assertEquals(product, result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
        verify(productRepository, times(1)).read(product.getId());
        verify(productRepository, times(1)).update(product);
    }

    @Test
    void updateProduct_shouldThrowEntityNotFoundException_whenVendorDoesNotExist() {
        int oauthId = 1;
        Product product = new Product(1, "Product Name", 100.0, "Product Description");
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().nullItem());

        Uni<Product> result = productService.update(oauthId, product);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
        verify(productRepository, never()).read(anyInt());
        verify(productRepository, never()).update(any());
    }

    @Test
    void updateProduct_shouldThrowEntityNotFoundException_whenProductDoesNotExist() {
        int oauthId = 1;
        Vendor vendor = new Vendor();
        Product product = new Product(1, "Product Name", 100.0, "Product Description");
        when(vendorRepository.read(oauthId)).thenReturn(Uni.createFrom().item(vendor));
        when(productRepository.read(product.getId())).thenReturn(Uni.createFrom().nullItem());

        Uni<Product> result = productService.update(oauthId, product);

        assertThrows(EntityNotFoundException.class, () -> result.await().indefinitely());
        verify(vendorRepository, times(1)).read(oauthId);
        verify(productRepository, times(1)).read(product.getId());
        verify(productRepository, never()).update(any());
    }

    @Test
    void deleteProduct_shouldReturnNoContent_whenProductExists() {
        int productId = 1;
        when(productRepository.delete(productId)).thenReturn(Uni.createFrom().voidItem());

        Uni<Void> result = productService.delete(productId);

        assertDoesNotThrow(() -> result.await().indefinitely());
        verify(productRepository, times(1)).delete(productId);
    }

    @Test
    void deleteProduct_shouldThrowRuntimeException_whenProductDoesNotExist() {
        int productId = 1;
        when(productRepository.delete(productId)).thenReturn(Uni.createFrom().failure(new RuntimeException("Error")));

        Uni<Void> result = productService.delete(productId);

        assertThrows(RuntimeException.class, () -> result.await().indefinitely());
        verify(productRepository, times(1)).delete(productId);
    }
}
