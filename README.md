# Author_Blog_App

Author Blog application is a platform for Authors to post their blogs and store the details of the book alongwith the description. Authors can also view other users (Authors) using this application and manage their account settings such as change the status and upload the photo to the Firebase cloud storage. The Author is logged out from the application on clicking the Logout button.

Components Used:
Firebase Authentication: Firebase Authentication is used to authenticate the users for Register and Login activity. The registered users only are allowed to login to the application. Already registered users are prevented from registering to the application again. This authentication is handled by Google Firebase cloud. The users created are stored in Firebase Authentication under Users list.

Firebase Database: Firebase Database is used to store the registered user information such as email Id, user name, status etc. Whenever user is logged into the application, the Id is generated and updated in firebase database on successful login. The communication between Authors (messages and chat) is also stored in Firebase Database under “chats” and “messages” section.

Firebase Storage: Firebase Storage is used to store the images uploaded by Author.

SQLite Database: The blogs posted by Author is stored in the SQLite database.

Shared Preference: Book details entered by the author is stored in Shared Preferences.


Features:
1.	Login
•	User can login to the application with existing account.
•	Firebase authentication is provided for login
•	The Author not having existing account i.e; unauthorized users are not allowed to log in to the application.

2.	Register
•	The User can register new account with the application
•	Firebase authentication is provided for registering account
•	The User already registered with the application is prevented from registering the account again
•	The list of Users (Authors) registered for the account is maintained in Firebase database

3.	Logout
•	The Author can logout from the application by clicking on Logout button and return back to the Login page

4.	View all Authors
•	The Authors can view all the other Authors using the application 
•	Authors can also view the Name and Status of other Authors 

5.	Manage Account
•	The Authors can manage their own account 
•	They can upload the photos to Firebase cloud storage
•	Authors can also change their status

6.	Chat with other Authors
•	Author can send messages to other users of this application
•	User’s last seen can also be viewed by another user
•	Also, the online status of Author can be viewed

7.	Other Features
•	Image Cropping 
•	Image Compressing


<img width="256" alt="Home page" src="https://user-images.githubusercontent.com/44592616/56477345-8784b900-6459-11e9-8058-8d660f5057c9.PNG">       <img width="259" alt="Login" src="https://user-images.githubusercontent.com/44592616/56477381-b9961b00-6459-11e9-9d40-97c2c155f9df.PNG">


