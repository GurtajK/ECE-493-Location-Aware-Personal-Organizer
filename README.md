# ProxiPlan

**Setup Instructions:**

1. Clone tthe main branch of this repository.
2. Download the gradle.properties from the Final Submissions folder in Google Drive.
3. Replace the gradle.properties file in the root of this repository with the downloaded one.
4. Open the project (root of repository) with Android Studio.
5. Run the application:

    a) Using an emulator - Install an android emulator with a minimum sdk of 26.0

    b) Using a physical device - Connect via USB and enable USB Debugging on the device=

**User Guide:**

1. When/if prompted to allow location access, please select "At All Times" if it is visible or "While the app is open" otherwise.
2. If you don't already have an account, register for one by clicking on "Don't have an account? Sign Up"
3. Login to your account to be redirected to the task dashboard
4. To create new tasks, click the create new task button at the bottom of the app
5. When creating tasks, the "Task Location" field will be auto populated by location recommendations. Select the one you want when it appears.
6. To edit a task from the dashboard, click anywhere on the Task except the checkbox or trash icon.
7. To mark a task as completed, click on the checkbox on that task.
8. To delete a task, click the trash icon on that task and click delete on the confirmation dialog.
9. To search for tasks by title/description, type in the searchbar near the top of your screen.
10. To filter tasks by location or deadline, click the filter button at the top right of the dashboard page and type in a location (partial matches will succeed) and/or use the datetime picker to pick a deadline.
11. To change notification settings, click your username at the top right of the dashboard and click settings. Here you can enable Prioritized Task notifications which will make it so that ProxiPlan can update task priorities in the background every 30 minutes and send notifications for the task with the highest priority.
12. For best results, go to your device's settings and change ProxiPlan's location permissions to "Allow all the time". This will allow for background priority updates to happen without any problems as they require the user's current location.

