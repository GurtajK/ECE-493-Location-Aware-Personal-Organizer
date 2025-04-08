package com.example.location_aware_personal_organizer.ui

// FR 2 Registration.Form
// FR 13 Login.Form
// FR 17 Redirect.Login
// FR 33 Task.Create
// FR 41 Form.TaskEdit
// FR 58 Notify.PreferencesChange
// FR 64 Redirect.CompletedTask
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object TaskCreation : Screen("task_creation")
    object NotificationSettings : Screen("notification_settings")
    object CompletedTasks : Screen("completed_tasks") {
        fun withTaskCompletedFlag() = "completed_tasks?taskCompleted=true"
    }
    object TaskUpdate : Screen("task_update") {
        fun withId(id: String) = "task_update?id=$id"
    }
}