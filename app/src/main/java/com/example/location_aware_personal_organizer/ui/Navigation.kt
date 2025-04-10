package com.example.location_aware_personal_organizer.ui

// FR 2 Registration.Form
// FR 13 Login.Form
// FR 17 Redirect.Login
// FR 18 Forgot.Password
// FR 24 Task.Create
// FR 32 Form.TaskEdit
// FR 49 Notify.PreferencesChange
// FR 55 Redirect.CompletedTask
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
    object ForgotPassword : Screen("forgot_password")
}