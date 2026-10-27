import axios from 'axios';

// API Base URL - Configure based on your backend server
const API_BASE_URL = 'http://localhost:9090/api/products';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

/**
 * ApiService - Handles all product-related API calls
 */
const ApiService = {
  /**
   * Get all products with pagination
   * @param {number} page - Page number (0-indexed)
   * @param {number} size - Items per page
   * @param {string} sort - Sort parameter (e.g., 'id,asc')
   * @returns {Promise} Products page object
   */
  getAllProducts: (page = 0, size = 10, sort = 'id,asc') => {
    return api.get('', {
      params: {
        page,
        size,
        sort,
      },
    });
  },

  /**
   * Get product by ID
   * @param {string} id - Product ID
   * @returns {Promise} Product object
   */
  getProductById: (id) => {
    return api.get(`/${id}`);
  },

  /**
   * Get products by category
   * @param {string} categoryId - Category ID
   * @returns {Promise} Array of products
   */
  getProductsByCategory: (categoryId) => {
    return api.get(`/category/${categoryId}`);
  },

  /**
   * Create new product
   * @param {object} productData - Product object
   * @returns {Promise} Created product
   */
  createProduct: (productData) => {
    return api.post('', productData);
  },

  /**
   * Update existing product
   * @param {string} id - Product ID
   * @param {object} productData - Updated product data
   * @returns {Promise} Updated product
   */
  updateProduct: (id, productData) => {
    return api.put(`/${id}`, productData);
  },

  /**
   * Delete product
   * @param {string} id - Product ID
   * @returns {Promise} Delete response
   */
  deleteProduct: (id) => {
    return api.delete(`/${id}`);
  },

  /**
   * Handle API errors
   * @param {object} error - Error object from axios
   * @returns {string} Error message
   */
  getErrorMessage: (error) => {
    if (error.response) {
      // Server responded with error status
      return error.response.data?.message || 'An error occurred';
    } else if (error.request) {
      // Request made but no response
      return 'No response from server. Please check if backend is running.';
    } else {
      // Error in request setup
      return error.message || 'An error occurred';
    }
  },
};

export default ApiService;
