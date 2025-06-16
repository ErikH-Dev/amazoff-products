package repositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import entities.ProductDocument;
import exceptions.errors.*;
import exceptions.errors.ProductSearchException;
import interfaces.IProductSearchRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductSearchRepository implements IProductSearchRepository {

    private static final Logger LOG = Logger.getLogger(ProductSearchRepository.class);
    private static final String INDEX_NAME = "products";

    @Inject
    ElasticsearchClient elasticsearchClient;

    public Uni<Void> indexProduct(ProductDocument product) {
        return Uni.createFrom().item(() -> {
            try {
                IndexRequest<ProductDocument> request = IndexRequest.of(i -> i
                        .index(INDEX_NAME)
                        .id(product.productId)
                        .document(product));

                elasticsearchClient.index(request);
                LOG.debugf("Indexed product: %s", product.productId);
                return null;
            } catch (Exception e) {
                LOG.errorf("Failed to index product %s: %s", product.productId, e.getMessage());
                throw new ProductIndexException("Failed to index product " + product.productId, e);
            }
        });
    }

    public Uni<List<ProductDocument>> searchProducts(String query) {
        return Uni.createFrom().item(() -> {
            try {
                Query multiMatchQuery = Query.of(q -> q
                        .multiMatch(m -> m
                                .query(query)
                                .fields("name", "description")));

                SearchRequest request = SearchRequest.of(s -> s
                        .index(INDEX_NAME)
                        .query(multiMatchQuery)
                        .size(50));

                SearchResponse<ProductDocument> response = elasticsearchClient.search(request, ProductDocument.class);

                List<ProductDocument> results = response.hits().hits().stream()
                        .map(hit -> hit.source())
                        .collect(Collectors.toList());

                if (results.isEmpty()) {
                    LOG.warnf("No products found for query '%s'", query);
                }

                return results;

            } catch (Exception e) {
                LOG.errorf("Search failed for query '%s': %s", query, e.getMessage());
                throw new ProductSearchException("Search failed for query: " + query);
            }
        });
    }

    public Uni<Void> deleteProduct(String productId) {
        return Uni.createFrom().item(() -> {
            try {
                DeleteRequest request = DeleteRequest.of(d -> d
                    .index(INDEX_NAME)
                    .id(productId)
                );
                
                elasticsearchClient.delete(request);
                LOG.debugf("Deleted product from Elasticsearch: %s", productId);
                return null;
            } catch (Exception e) {
                LOG.errorf("Failed to delete product %s from Elasticsearch: %s", productId, e.getMessage());
                throw new ProductDeleteException("Failed to delete product " + productId, e);
            }
        });
    }
}