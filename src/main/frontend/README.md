# News Portal - React Frontend

A modern, responsive React frontend for the News & Ads Portal application built with Vite, React Router, and TailwindCSS.

## Features

- ✅ **Authentication** - JWT-based login with automatic token refresh
- ✅ **News Management** - Full CRUD operations with multi-language support (UZ/RU/EN)
- ✅ **Category Management** - Hierarchical categories with translations
- ✅ **Tag Management** - Tag creation and management
- ✅ **Ads Campaign Management** - Create and manage advertising campaigns
- ✅ **Ads Assignment Management** - Assign ads to placements with filters
- ✅ **Public News Page** - Public-facing news listing
- ✅ **Responsive Design** - Mobile-friendly interface
- ✅ **Modern UI** - Clean, professional design with smooth animations

## Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router v6** - Client-side routing
- **Axios** - HTTP client for API calls
- **TailwindCSS** - Utility-first CSS framework
- **React Query (TanStack Query)** - Server state management
- **Zustand** - Client state management
- **React Hot Toast** - Toast notifications
- **Lucide React** - Modern icon library

## Prerequisites

- Node.js 16+ and npm
- Backend API running on `http://localhost:8080`

## Installation

1. Navigate to the frontend directory:
   ```bash
   cd src/main/frontend-in-react
   ```

2. Copy environment variables:
   ```bash
   cp .env.example .env
   ```

3. Install dependencies (if not already done):
   ```bash
   npm install
   ```

## Running the Application

### Development Mode

```bash
npm run dev
```

The application will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Default Credentials

- **Username**: admin
- **Password**: admin123

## Project Structure

```
src/
├── api/              # API service layer
│   ├── axios.js      # Axios instance with interceptors
│   ├── auth.js       # Authentication APIs
│   ├── news.js       # News APIs
│   ├── categories.js # Category APIs
│   ├── tags.js       # Tag APIs
│   ├── campaigns.js  # Ads Campaign APIs
│   └── assignments.js # Ads Assignment APIs
├── components/       # Reusable components
│   ├── common/       # Buttons, inputs, modals, etc.
│   └── layout/       # Layout components
├── pages/            # Page components
│   ├── auth/         # Login page
│   ├── news/         # News management pages
│   ├── categories/   # Category pages
│   ├── tags/         # Tag pages
│   ├── ads/          # Ads management pages
│   └── public/       # Public-facing pages
├── store/            # Zustand stores
├── App.jsx           # Main app component
└── main.jsx          # Entry point
```

## API Endpoints Connected

### Authentication
- ✅ POST `/admin/auth/login` - Login
- ✅ POST `/admin/auth/register` - Register
- ✅ POST `/admin/auth/refresh` - Refresh token
- ✅ POST `/admin/auth/logout` - Logout

### News Management
- ✅ GET `/admin/news/get-all` - List all news with filters
- ✅ GET `/admin/news/{id}` - Get news by ID
- ✅ POST `/admin/news/add` - Create news
- ✅ PATCH `/admin/news/{id}/status` - Update status
- ✅ DELETE `/admin/news/delete/{id}` - Soft delete
- ✅ POST `/admin/news/{id}/restore` - Restore
- ✅ DELETE `/admin/news/{id}/hard` - Hard delete
- ✅ GET `/admin/news/{id}/history` - Get history

### Categories
- ✅ GET `/admin/categories/get-all` - List all
- ✅ GET `/admin/categories/get-all-by-lang` - By language
- ✅ GET `/admin/categories/{id}` - By ID
- ✅ POST `/admin/categories/add` - Create
- ✅ PUT `/admin/categories/{id}` - Update
- ✅ PATCH `/admin/categories/{id}/toggle-status` - Toggle status
- ✅ DELETE `/admin/categories/{id}` - Delete

### Tags
- ✅ GET `/admin/tags/get-all` - List with pagination
- ✅ GET `/admin/tags/{id}` - By ID
- ✅ POST `/admin/tags/add` - Create
- ✅ PATCH `/admin/tags/{id}` - Update
- ✅ DELETE `/admin/tags/{id}` - Delete

### Ads Campaigns
- ✅ GET `/admin/ads/campaigns` - List all
- ✅ GET `/admin/ads/campaigns/{id}` - By ID
- ✅ POST `/admin/ads/campaigns` - Create
- ✅ PUT `/admin/ads/campaigns/{id}` - Update
- ✅ PATCH `/admin/ads/campaigns/{id}/status` - Update status
- ✅ DELETE `/admin/ads/campaigns/{id}` - Delete

### Ads Assignments
- ✅ GET `/admin/ads/assignments/get-all` - List all
- ✅ GET `/admin/ads/assignments/{id}` - By ID
- ✅ POST `/admin/ads/assignments/add` - Create
- ✅ PUT `/admin/ads/assignments/{id}` - Update
- ✅ DELETE `/admin/ads/assignments/{id}` - Delete

### Public APIs
- ✅ GET `/public/news` - Published news
- ✅ GET `/public/news/{slug}` - News by slug
- ✅ GET `/public/news/categories` - Public categories
- ✅ GET `/public/news/tags` - Public tags
- ✅ GET `/public/ads/{placementCode}` - Get ad

## Features Overview

### Admin Dashboard
- Statistics overview
- Quick actions
- Recent activity

### News Management
- Create/edit news with multi-language support
- Rich filtering (keyword, language, status, category, tags, date range)
- Status workflow (Draft → Review → Published → Unpublished → Archived)
- Soft delete and restore
- View history

### Category Management
- Create categories with translations
- Hierarchical structure support
- Toggle active/inactive status

### Tag Management
- Simple tag CRUD operations
- Grid layout display

### Ads Management
- Campaign creation with date ranges and daily caps
- Assignment management with weight-based selection
- Language and category filters

### Public Pages
- News listing with search and filters
- Responsive design
- Clean, modern interface

## Environment Variables

Create a `.env` file with:

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## Development Notes

- The app uses JWT tokens stored in localStorage
- Automatic token refresh on 401 errors
- Protected routes require authentication
- Toast notifications for user feedback
- Responsive design with mobile-first approach

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

Proprietary
