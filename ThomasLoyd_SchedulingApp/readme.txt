Thomas Loyd created this application as a project for the C195 course at Western Governors University.

Both the username and the password needed to access the database is test. This is the only username and password pair stored into the database.
To test requirement A for a second language, alter line 30 of SchedulingApp to "private final static Locale L = Locale.CHINESE;"
The log file for requirement J is in the log file within the same directory as this file.
When modifying a customer, the customer's country is locked to maintain the integrity of the database.

Below is where each of the projects requirements is found in the provided code.

Requirement A: Create a log-in form that can determine the user’s location and translate log-in and error control messages (e.g., “The username and password did not match.”) 
into two languages.

SchedulingApp lines 30, 78, and 127

Requirement B: Provide the ability to enter and maintain customer records in the database, including name, address, and phone number.

AddCustomerController line 117, and MainScreenController lines 413, 437, and 464.

Requirement C: Write lambda expression(s) to schedule and maintain appointments, capturing the type of appointment and a link to the specific customer record in the database.

AddAppointmentController lines 75, 84, 97, 127, 155, and 279, and MainScreenController lines 479, 501, and 564

Requirement D: Provide the ability to view the calendar by month and by week.

MainScreenController lines 96, 105, 112, and 335

Requirement E: Provide the ability to automatically adjust appointment times based on user time zones and daylight-saving time.

MainScreenController lines 297-299 and 358-362, 

Requirement F: Write exception controls to prevent each of the following. You may use the same mechanism of exception control more than once, but you must incorporate at 
least two different mechanisms of exception control.

•  scheduling an appointment outside business hours - AddAppointmentController line 229 - if-then-else statements

•  scheduling overlapping appointments - AddAppointmentController line 97 - if-then-else statements

•  entering nonexistent or invalid customer data - AddCustomerController line 250 - if-then-else statements and a try-catch statement

•  entering an incorrect username and password - LoginController line 44 - if-then statements and a try-catch statement


Requirement G: Use lambda expressions to create standard pop-up and alert messages.

SchedulingApp line 37, AddCustomerController line 53, MainScreenController line 77, and AddAppointmentController line 75.

Requirement H: Write code to provide reminders and alerts 15 minutes in advance of an appointment, based on the user’s log-in.

MainScreenController line 118

Requirement I: Provide the ability to generate each of the following reports:

•  number of appointment types by month - MainScreenController line 255

•  the schedule for each consultant - MainScreenController line 291

•  one additional report of your choice - MainScreenController line 313 - Retrieves information on inactive customers

Requirement J:  Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file.

LoginController lines 118-157
