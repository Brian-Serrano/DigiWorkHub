package com.serrano.dictproject.utils

enum class DashboardDialogs {
    NONE, NAME, ASSIGNEE, DUE, PRIORITY, STATUS, TYPE, FILTER, VIEW
}

enum class AddTaskDialogs {
    NONE, NAME, ASSIGNEE, DUE, PRIORITY, TYPE, DESCRIPTION, VIEW
}

enum class AboutTaskDialogs {
    NONE, TASK_NAME, TASK_ASSIGNEE, TASK_DUE, TASK_PRIORITY, TASK_STATUS, TASK_TYPE,
    TASK_DESCRIPTION, CHECKLIST_ASSIGNEE, SUB_ADD_DUE, SUB_ADD_PRIORITY,
    SUB_ADD_TYPE, SUB_ADD_ASSIGNEE, SUB_EDIT_DESCRIPTION, SUB_EDIT_STATUS,
    SUB_EDIT_ASSIGNEE, SUB_EDIT_PRIORITY, SUB_EDIT_DUE, SUB_EDIT_TYPE, VIEW
}

enum class SendMessageDialogs {
    NONE, RECEIVER
}

enum class ProfileDialogs {
    NONE, NAME, ROLE, IMAGE
}