import React, { useState } from 'react';
import ProductForm from './components/ProductForm';
import ProductList from './components/ProductList';
import ErrorBoundary from './components/ErrorBoundary';
import './styles/App.css';

/**
 * App Component
 *
 * Main application component
 * Handles the UI layout and component interaction
 */
function App() {
  const [editingProduct, setEditingProduct] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  /**
   * Handle product edit
   */
  const handleEdit = (product) => {
    setEditingProduct(product);
    setShowForm(true);
  };

  /**
   * Handle form submission (create/update)
   */
  const handleFormSubmit = () => {
    setShowForm(false);
    setEditingProduct(null);
    // Trigger product list refresh
    setRefreshTrigger((prev) => prev + 1);
  };

  /**
   * Handle form cancel
   */
  const handleFormCancel = () => {
    setShowForm(false);
    setEditingProduct(null);
  };

  /**
   * Handle create new product button
   */
  const handleCreateNew = () => {
    setEditingProduct(null);
    setShowForm(true);
  };

  return (
    <ErrorBoundary>
      <div className="app-container">
        {/* Header */}
        <header className="app-header">
          <div className="header-content">
            <h1>📦 Product Catalog Management System</h1>
            <p className="subtitle">React Frontend Application</p>
          </div>
        </header>

        {/* Main Content */}
        <main className="app-main">
          <div className="container">
            {/* Left Column - Product Form */}
            <div className="left-column">
              {showForm ? (
                <ProductForm
                  editingProduct={editingProduct}
                  onSubmit={handleFormSubmit}
                  onCancel={handleFormCancel}
                />
              ) : (
                <div className="form-placeholder">
                  <div className="welcome-message">
                    <h3>👋 Welcome to Product Catalog</h3>
                    <p>Click the "Create New Product" button to get started</p>
                    <button onClick={handleCreateNew} className="btn-create-main">
                      ➕ Create New Product
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Right Column - Product List */}
            <div className="right-column">
              <div className="list-header">
                {!showForm && (
                  <button onClick={handleCreateNew} className="btn-create-secondary">
                    ➕ Create New Product
                  </button>
                )}
              </div>
              <ProductList
                onEdit={handleEdit}
                refreshTrigger={refreshTrigger}
              />
            </div>
          </div>
        </main>

        {/* Footer */}
        <footer className="app-footer">
          <div className="footer-content">
            <p>
              <strong>Backend API:</strong> http://localhost:9090/api/products
            </p>
            <p>
              <strong>Swagger UI:</strong> http://localhost:9090/api/swagger-ui/index.html
            </p>
            <p>© 2026 Product Catalog Management System | Powered by React & Spring Boot</p>
          </div>
        </footer>
      </div>
    </ErrorBoundary>
  );
}

export default App;
