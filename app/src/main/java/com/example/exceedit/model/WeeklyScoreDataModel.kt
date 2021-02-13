package com.example.exceedit.model

data class WeeklyScoreDataModel(
    val efforts: String,
    val activities: String,
    val inProgress: String,
    val list: List<Int>
)

fun getDataList() = mutableListOf<WeeklyScoreDataModel>(
    WeeklyScoreDataModel(
        efforts = "3h 28m",
        activities = "0",
        inProgress = "0",
        list = mutableListOf(2, 6, 7, 8, 5, 1, 4)
    ), WeeklyScoreDataModel(
        efforts = "3h 28m",
        activities = "0",
        inProgress = "0",
        list = mutableListOf(2, 6, 7, 8, 5, 1, 4)
    )
)
