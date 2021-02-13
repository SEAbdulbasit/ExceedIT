package com.example.exceedit.viewmodel

import androidx.lifecycle.ViewModel
import com.example.exceedit.model.getDataList
import com.google.gson.Gson


class MainViewModel : ViewModel() {
    init {
        val gson = Gson().toJson(getDataList())
        print("Gson String $gson")
    }
}