import React, { useState, useEffect } from 'react';
import ApiService from '../services/ApiService';
import '../styles/ProductForm.css';

/**
 * ProductForm Component
 *
 * Form for creating and updating products
 * Handles form validation and submission
 */
const ProductForm = ({ editingProduct, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    id: '',
    name: '',
    description: '',
    categoryId: '',
    price: '',
    stockQuantity: '',
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState([]);

  /**
   * Categories list (can be fetched from API in future)
   */
  const availableCategories = [
    { id: 'ELEC-001', name: 'Electronics' },
    { id: 'CLOTH-001', name: 'Clothing' },
    { id: 'HOME-001', name: 'Home & Garden' },
    { id: 'BOOK-001', name: 'Books' },
    { id: 'SPORT-001', name: 'Sports' },
    { id: 'HEALTH-001', name: 'Health & Beauty' },
  ];

  /**
   * Populate form when editing a product
   */
  useEffect(() => {
    if (editingProduct) {
      setFormData({
        id: editingProduct.id,
        name: editingProduct.name,
        description: editingProduct.description || '',
        categoryId: editingProduct.category || '',
        price: editingProduct.price,
        stockQuantity: editingProduct.stockQuantity,
      });
    } else {
      resetForm();
    }
  }, [editingProduct]);

  /**
   * Validate form data
   */
  const validateForm = () => {
    const newErrors = {};

    if (!formData.id.trim()) newErrors.id = 'Product ID is required';
    else if (!/^[A-Z0-9-]+$/.test(formData.id)) {
      newErrors.id = 'Product ID must contain only uppercase letters, numbers, and hyphens';
    }

    if (!formData.name.trim()) newErrors.name = 'Product name is required';
    else if (formData.name.length > 100) newErrors.name = 'Name cannot exceed 100 characters';

    if (formData.description && formData.description.length > 1000) {
      newErrors.description = 'Description cannot exceed 1000 characters';
    }

    if (!formData.categoryId) newErrors.categoryId = 'Category is required';

    if (!formData.price) newErrors.price = 'Price is required';
    else if (parseFloat(formData.price) <= 0) newErrors.price = 'Price must be greater than 0';
    else if (parseFloat(formData.price) > 999999.99) newErrors.price = 'Price cannot exceed 999999.99';

    if (formData.stockQuantity === '') newErrors.stockQuantity = 'Stock quantity is required';
    else if (parseInt(formData.stockQuantity) < 0) newErrors.stockQuantity = 'Stock cannot be negative';
    else if (parseInt(formData.stockQuantity) > 1000000) {
      newErrors.stockQuantity = 'Stock cannot exceed 1,000,000';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Handle form input change
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  /**
   * Handle form submission
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const payload = {
        id: formData.id,
        name: formData.name,
        description: formData.description || null,
        categoryId: formData.categoryId,
        price: parseFloat(formData.price),
        stockQuantity: parseInt(formData.stockQuantity),
      };

      if (editingProduct) {
        // Update product
        await ApiService.updateProduct(editingProduct.id, payload);
        alert('Product updated successfully!');
      } else {
        // Create new product
        await ApiService.createProduct(payload);
        alert('Product created successfully!');
      }

      onSubmit();
      resetForm();
    } catch (error) {
      alert('Error saving product: ' + ApiService.getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  };

  /**
   * Reset form to initial state
   */
  const resetForm = () => {
    setFormData({
      id: '',
      name: '',
      description: '',
      categoryId: '',
      price: '',
      stockQuantity: '',
    });
    setErrors({});
  };

  return (
    <div className="product-form-container">
      <h2>{editingProduct ? '✏️ Edit Product' : '➕ Create New Product'}</h2>

      <form onSubmit={handleSubmit} className="product-form">
        {/* Product ID */}
        <div className="form-group">
          <label htmlFor="id">Product ID *</label>
          <input
            type="text"
            id="id"
            name="id"
            value={formData.id}
            onChange={handleChange}
            placeholder="e.g., PROD-001"
            disabled={!!editingProduct}
            className={errors.id ? 'input-error' : ''}
          />
          {errors.id && <span className="error-text">❌ {errors.id}</span>}
        </div>

        {/* Product Name */}
        <div className="form-group">
          <label htmlFor="name">Product Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="e.g., Laptop Pro 15"
            maxLength="100"
            className={errors.name ? 'input-error' : ''}
          />
          {errors.name && <span className="error-text">❌ {errors.name}</span>}
        </div>

        {/* Product Description */}
        <div className="form-group">
          <label htmlFor="description">Description (Optional)</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter product description..."
            maxLength="1000"
            rows="3"
            className={errors.description ? 'input-error' : ''}
          />
          {errors.description && <span className="error-text">❌ {errors.description}</span>}
        </div>

        {/* Category */}
        <div className="form-group">
          <label htmlFor="categoryId">Category *</label>
          <select
            id="categoryId"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            className={errors.categoryId ? 'input-error' : ''}
          >
            <option value="">-- Select a Category --</option>
            {availableCategories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
          {errors.categoryId && <span className="error-text">❌ {errors.categoryId}</span>}
        </div>

        {/* Price */}
        <div className="form-group">
          <label htmlFor="price">Price ($) *</label>
          <input
            type="number"
            id="price"
            name="price"
            value={formData.price}
            onChange={handleChange}
            placeholder="e.g., 999.99"
            min="0.01"
            max="999999.99"
            step="0.01"
            className={errors.price ? 'input-error' : ''}
          />
          {errors.price && <span className="error-text">❌ {errors.price}</span>}
        </div>

        {/* Stock Quantity */}
        <div className="form-group">
          <label htmlFor="stockQuantity">Stock Quantity *</label>
          <input
            type="number"
            id="stockQuantity"
            name="stockQuantity"
            value={formData.stockQuantity}
            onChange={handleChange}
            placeholder="e.g., 50"
            min="0"
            max="1000000"
            className={errors.stockQuantity ? 'input-error' : ''}
          />
          {errors.stockQuantity && <span className="error-text">❌ {errors.stockQuantity}</span>}
        </div>

        {/* Form Actions */}
        <div className="form-actions">
          <button
            type="submit"
            disabled={loading}
            className="btn-submit"
          >
            {loading ? '⏳ Saving...' : (editingProduct ? '💾 Update Product' : '➕ Create Product')}
          </button>
          <button
            type="button"
            onClick={() => {
              onCancel();
              resetForm();
            }}
            className="btn-cancel"
            disabled={loading}
          >
            ❌ Cancel
          </button>
        </div>

        <p className="form-note">* Fields marked with asterisk are required</p>
      </form>
    </div>
  );
};

export default ProductForm;
