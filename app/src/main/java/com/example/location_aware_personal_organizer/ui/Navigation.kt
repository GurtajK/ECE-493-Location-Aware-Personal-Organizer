package com.example.location_aware_personal_organizer.ui

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