# CST2335 - Lab 5 (SQLite Database & Persistent ToDo List)

**Course:** CST2335 - Mobile Graphical Interface Programming  
**Branch:** lab_5  

## Project Overview
This project builds upon Lab 4 to provide permanent data persistence using an Android SQLite database. 

Tasks added to the To Do list are stored in local storage, automatically loaded upon application startup, and removed from the database when deleted via a long-click confirmation dialog.

### Implemented Features
1. **SQLiteOpenHelper (`MyOpener.java`)**:
   - Database: `TodosDB` (Version 1).
   - Table: `TODOS` with columns `_id` (PRIMARY KEY AUTOINCREMENT), `TODO` (TEXT), and `IS_URGENT` (INTEGER).
2. **Persistent Data Loading (`loadDataFromDatabase()`)**:
   - Queries the `TODOS` table on launch.
   - Populates the `ListView` directly from stored records.
3. **Debug Cursor Function (`printCursor(Cursor c)`)**:
   - Prints database version (`db.getVersion()`), column count, column names, total row count, and each row's data into the Android Studio Logcat window.
4. **Data Insertion & Deletion**:
   - `Add` button inserts new tasks with urgency flags into SQLite.
   - Long-clicking any item triggers an `AlertDialog` confirming deletion and removes the record from SQLite using `db.delete()`.
5. **Localization**:
   - Supports both English and French (`values-fr/strings.xml`).

---

## Git Branches
- `main`: Default root branch.
- `lab_5`: Lab assignment working branch as specified in the lab instructions.
