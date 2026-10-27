# Product Catalog Frontend - React Application

A modern React application for managing products with full CRUD (Create, Read, Update, Delete) operations. This frontend communicates with the Spring Boot backend API.

## Features

✅ **View Products** - Browse all products in a paginated table  
✅ **Create Products** - Add new products with validation  
✅ **Update Products** - Edit existing product information  
✅ **Delete Products** - Remove products from catalog  
✅ **Responsive Design** - Works on desktop, tablet, and mobile  
✅ **Real-time Validation** - Client-side form validation  
✅ **Error Handling** - Comprehensive error messages  

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── ProductList.js       # Display products table
│   │   └── ProductForm.js       # Create/update product form
│   ├── services/
│   │   └── ApiService.js        # API communication
│   ├── styles/
│   │   ├── App.css              # Main app styles
│   │   ├── ProductForm.css      # Form styles
│   │   ├── ProductList.css      # Table styles
│   │   └── index.css            # Global styles
│   ├── App.js                   # Main app component
│   └── index.js                 # React entry point
├── package.json
└── README.md
```

## Installation & Setup

### Prerequisites
- Node.js 14+ and npm installed
- Backend API running on http://localhost:9090/api

### Steps

1. **Navigate to frontend directory**
```bash
cd frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Start the development server**
```bash
npm start
```

The application will open at `http://localhost:3000`

## Configuration

### API Base URL

The default backend API URL is configured in `src/services/ApiService.js`:

```javascript
const API_BASE_URL = 'http://localhost:9090/api/products';
```

To change the API URL, edit this line in `ApiService.js`.

## Available Scripts

### `npm start`
Runs the app in development mode.  
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.  
The page will reload when you make changes.

### `npm run build`
Builds the app for production to the `build` folder.

### `npm test`
Launches the test runner in interactive watch mode.

## Components

### App.js
- Main application component
- Manages form visibility and product editing state
- Coordinates between ProductForm and ProductList

### ProductForm.js
- Handles product creation and updates
- Client-side validation
- Form field validation and error display
- Category selection dropdown

### ProductList.js
- Displays products in a paginated table
- Supports pagination controls
- Edit and delete buttons for each product
- Real-time data refresh

### ApiService.js
- Centralized API communication
- Axios instance with default configuration
- Methods for all CRUD operations
- Error handling utilities

## Key Features

### Form Validation
- Product ID format validation (uppercase alphanumeric)
- Required field validation
- Price and stock quantity constraints
- Field length validation

### Pagination
- Browse through products pages
- Previous/Next navigation
- Page indicator

### Error Handling
- Connection error detection
- User-friendly error messages
- Retry functionality

### Responsive UI
- Two-column layout on desktop
- Single column on tablet/mobile
- Mobile-optimized button sizes and spacing

## API Endpoints Used

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | /products | Get paginated product list |
| GET | /products/{id} | Get product details |
| POST | /products | Create new product |
| PUT | /products/{id} | Update product |
| DELETE | /products/{id} | Delete product |

## Styling

The application uses vanilla CSS for styling with:
- Modern flexbox layouts
- Gradient backgrounds
- Smooth transitions and hover effects
- Mobile-first responsive design
- Accessible color schemes

### Color Palette
- Primary: #667eea (Blue-Purple)
- Secondary: #764ba2 (Purple)
- Success: #27ae60 (Green)
- Error: #e74c3c (Red)
- Neutral: #f5f5f5 (Light Gray)

## Testing the Application

1. **Create a Product**
   - Click "Create New Product"
   - Fill in all required fields
   - Click "Create Product"

2. **View Products**
   - Products appear in the right panel
   - Use pagination to browse pages

3. **Update a Product**
   - Click "Edit" on any product
   - Modify fields
   - Click "Update Product"

4. **Delete a Product**
   - Click "Delete" on any product
   - Confirm deletion
   - Product removed from list

## Categories

Available product categories:
- Electronics
- Clothing
- Home & Garden
- Books
- Sports
- Health & Beauty

## Troubleshooting

### Backend Connection Error
**Problem**: "No response from server"  
**Solution**: Ensure backend is running on http://localhost:9090
```bash
# In backend directory, run:
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9090"
```

### Port Already in Use
**Problem**: Port 3000 is already in use  
**Solution**: Set a different port
```bash
PORT=3001 npm start
```

### CORS Issues
**Problem**: Cross-Origin Request Blocked  
**Solution**: Backend should have CORS enabled (already configured in Spring Boot)

## Future Enhancements

- [ ] User authentication
- [ ] Search functionality
- [ ] Category filtering
- [ ] Product image upload
- [ ] Inventory alerts
- [ ] Export to CSV
- [ ] Dark mode toggle
- [ ] Advanced filters

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

MIT License - See LICENSE file for details

---

**Backend API**: http://localhost:9090/api  
**Swagger UI**: http://localhost:9090/api/swagger-ui/index.html
