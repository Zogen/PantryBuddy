# Pantry Buddy

**Pantry Buddy** is an Android application designed to help users manage their pantry inventory, reduce food waste, receive recipe recommendations based on available ingredients, and keep an organized grocery shopping list.

## Features

- **Pantry Tracking**: Easily add, update, and remove pantry items. Track quantities and expiration dates to minimize waste.  
- **Recipe Suggestions**: Get personalized recipe ideas based on current pantry contents using the Edamam Recipe API.  
- **Grocery List Management**: Maintain a dynamic grocery list and transfer purchased items directly to the pantry.  
- **Expiration Alerts**: Visually highlighted expiration status helps users spot items that are near or past their expiration date.  
- **Data Persistence**: All user data is stored locally using an SQLite database for offline access and reliability.  
- **Smart Additions**: Prevents duplicate entries by updating quantities of existing items instead of duplicating them.  
- **API Call Optimization**: Implements caching for recipe suggestions to reduce unnecessary API requests and improve performance.  
- **Responsive & Persistent UI**: All dialogs and screens adapt to different screen sizes and orientations, remain persistent across rotations.  
- **Night Mode Ready**: Supports both light and dark themes with appropriate color contrasts and drawables.  

## System Requirements

- **Minimum Android Version**: Android 5.0 Lollipop (SDK 21)  
- **Development Environment**: Android Studio Arctic Fox or later  
- **Programming Language**: Java  
- **Key Libraries Used**:  
  - [Retrofit](https://square.github.io/retrofit/) – for API integration
  - [Gson](https://github.com/google/gson) – for JSON parsing
  - SQLite for local data storage  

## Architecture

The app follows the **Model-View-Controller (MVC)** design pattern:

- **Model**: Handles data logic and API communication.  
- **View**: Includes the UI components such as Activities, RecyclerViews, Dialogs.  
- **Controller**: Bridges interaction logic between View and Model (e.g., updating the pantry list, fetching recipes).  

## Main Screens

### 🏠 Home Screen

The landing screen features three navigation buttons:
- **Pantry** – Manage pantry inventory  
- **Grocery List** – Manage shopping list  
- **Recipes** – View recipe suggestions  

---

### 🗃️ Pantry Screen

- View and manage pantry items (name, quantity, expiration date).  
- Add new items via a dialog form.  
- Long-press items to edit expiration dates.  
- Expiration highlights:  
  - ⚪ Grey: Safe (>3 days)
  - 🔴 Red: Expires within 3 days
  - ❗ “EXPIRED” label for expired items  
  - No label for items without expiration dates  

---

### 🍲 Recipe Screen

- Automatically suggests recipes based on current pantry contents.  
- Fetches up to 20 relevant recipes via the Edamam API.  
- Each recipe card displays a title and image.  
- Clicking on a recipe reveals ingredients and a link to full instructions.  

---

### 🛒 Grocery List Screen

- Add, remove, and edit grocery list items.  
- Transfer items to pantry when purchased.  
- Intelligent updates: quantities are increased if the item already exists.  

---

## Screenshots

![Home Screen](screenshots/home_screen.png)  
*Home Screen with navigation buttons*

![Pantry View](screenshots/pantry_view.png)  
*Pantry list with expiration highlights*

![Add Item Dialog](screenshots/add_item_dialog.png)  
*Dialog for adding a new pantry item*

![Recipe Suggestions](screenshots/recipe_suggestions.png)  
*List of recipe cards fetched from the API, in night mode*

![Recipe Detail](screenshots/recipe_detail.png)  
*Detailed view showing ingredients and link*

![Grocery List](screenshots/grocery_list.png)  
*Grocery list management view*

---

## Future Improvements

- Notifications for upcoming expirations  
- Barcode scanning for faster item entry  
- Quickly add missing recipe items to shopping list  
- User authentication and cloud sync  

---

## API Setup

To use this project, you’ll need your own **Edamam API credentials** (App ID and App Key). Follow the steps below to set them up securely:

### 1. Get your API credentials

1. Sign up at [Edamam Developer](https://developer.edamam.com/).
2. Create an application.
3. Copy your:
   - **App ID**
   - **App Key**

### 2. Add them to `local.properties`

1. In the root of the project (next to `build.gradle.kts`), create or edit the file named:

```lua
local.properties
```
2. Add your credentials in this format:

```properties
EDAMAM_APP_ID=your_app_id_here
EDAMAM_APP_KEY=your_app_key_here
```
### 3. Sync the project

After saving `local.properties`, sync your project:

- In Android Studio: **File** → **Sync Project with Gradle Files**
- Or via terminal:

```bash
./gradlew clean build
```

---

## License

This project is for academic purposes and is not currently licensed for commercial use.

---

## Credits

This app was developed as part of a university project for an educational course on Android development. API powered by [Edamam](https://developer.edamam.com/).
