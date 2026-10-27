import React, { useState, useEffect } from 'react';
import ApiService from '../services/ApiService';
import '../styles/ProductList.css';

/**
 * ProductList Component
 *
 * Displays a list of products with pagination
 * Allows viewing, editing, and deleting products
 */
const ProductList = ({ onEdit, refreshTrigger }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(5);

  /**
   * Fetch products from API
   */
  const fetchProducts = async (page = 0) => {
    setLoading(true);
    setError(null);
    try {
      const response = await ApiService.getAllProducts(page, pageSize, 'id,asc');
      setProducts(response.data.content || []);
      setCurrentPage(response.data.pageable.pageNumber);
      setTotalPages(response.data.pageable.totalPages);
    } catch (err) {
      setError(ApiService.getErrorMessage(err));
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Load products on component mount and when refresh is triggered
   */
  useEffect(() => {
    fetchProducts(currentPage);
  }, [refreshTrigger]);

  /**
   * Handle delete product
   */
  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        await ApiService.deleteProduct(id);
        alert('Product deleted successfully');
        fetchProducts(currentPage);
      } catch (err) {
        alert('Error deleting product: ' + ApiService.getErrorMessage(err));
      }
    }
  };

  /**
   * Handle page change
   */
  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchProducts(newPage);
    }
  };

  if (error && error.includes('No response from server')) {
    return (
      <div className="error-container">
        <h3>⚠️ Connection Error</h3>
        <p>{error}</p>
        <p>Make sure the backend is running on http://localhost:9090</p>
        <button onClick={() => fetchProducts(currentPage)}>Retry</button>
      </div>
    );
  }

  return (
    <div className="product-list-container">
      <h2>📦 Products Catalog</h2>

      {error && (
        <div className="error-message">
          <p>❌ {error}</p>
          <button onClick={() => fetchProducts(currentPage)}>Retry</button>
        </div>
      )}

      {loading ? (
        <div className="loading">⏳ Loading products...</div>
      ) : products.length === 0 ? (
        <div className="no-products">
          <p>No products found. Create your first product!</p>
        </div>
      ) : (
        <>
          <div className="table-responsive">
            <table className="products-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id} className="product-row">
                    <td className="product-id">{product.id}</td>
                    <td className="product-name">{product.name}</td>
                    <td className="product-category">{product.category}</td>
                    <td className="product-price">${product.price}</td>
                    <td className={`product-stock ${product.stockQuantity === 0 ? 'out-of-stock' : 'in-stock'}`}>
                      {product.stockQuantity === 0 ? '❌ Out of Stock' : `✅ ${product.stockQuantity}`}
                    </td>
                    <td className="product-actions">
                      <button
                        className="btn-edit"
                        onClick={() => onEdit(product)}
                        title="Edit product"
                      >
                        ✏️ Edit
                      </button>
                      <button
                        className="btn-delete"
                        onClick={() => handleDelete(product.id)}
                        title="Delete product"
                      >
                        🗑️ Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination Controls */}
          <div className="pagination">
            <button
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
              className="btn-pagination"
            >
              ← Previous
            </button>
            <span className="page-info">
              Page {currentPage + 1} of {totalPages}
            </span>
            <button
              disabled={currentPage >= totalPages - 1}
              onClick={() => handlePageChange(currentPage + 1)}
              className="btn-pagination"
            >
              Next →
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default ProductList;
