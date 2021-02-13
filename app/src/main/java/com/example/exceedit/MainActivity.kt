package com.example.exceedit

import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.exceedit.adapter.ExpandableListAdapter
import com.example.exceedit.customview.SmoothLineChart
import com.example.exceedit.databinding.ActivityMainBinding
import com.example.exceedit.model.WeeklyScoreDataModel
import com.example.exceedit.model.getDataList
import com.example.exceedit.viewmodel.MainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    val calendar2: Calendar = Calendar.getInstance()

    private var isFirstDataLoading = true
    private lateinit var binding: ActivityMainBinding
    private var currentMonth = 0
    private lateinit var singleRowCalendar: SingleRowCalendar
    private lateinit var smoothLineChart: SmoothLineChart
    private var persons: List<WeeklyScoreDataModel>? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = getColor(R.color.purple_500)
        }

        initializeCalendar()
        initializeExpandableList()
        initializeChart()
        setClickListeners()

        retrieveFilesAndPopulateData()
    }

    private fun retrieveFilesAndPopulateData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val gson = Gson()
            val tokenType = object : TypeToken<List<WeeklyScoreDataModel>>() {}.type
            persons = gson.fromJson(retrieveFiles(), tokenType)
            withContext(Dispatchers.Main) {
                persons?.get(0)?.let { submitData(it) }
            }
        }
    }

    private fun submitData(dataModel: WeeklyScoreDataModel) {
        binding.textView4.text = dataModel.efforts
        binding.tvEffortsValue.text = dataModel.activities
        binding.tvInProgressValues.text = dataModel.inProgress

        var xIndex = 15
        val array = mutableListOf<PointF>()
        dataModel.list.forEach {
            array.add(PointF(xIndex.toFloat(), it.toFloat() * 10))
            xIndex += 15
        }
        //smoothLineChart.itemSize =45f
        smoothLineChart.setData(
            array.toTypedArray()
        )
    }

    private fun setClickListeners() {
        binding.btnRight.setOnClickListener {
            if (calendar.timeInMillis > calendar2.timeInMillis) {
                Toast.makeText(this, "No future dates", Toast.LENGTH_LONG).show()
            } else {
                singleRowCalendar.setDates(getDatesOfNextMonth())
                submitDateForLoading()
            }
        }

        binding.btnLeft.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfPreviousMonth())
            submitDateForLoading()
        }
    }

    private fun submitDateForLoading() {
        if (isFirstDataLoading) {
            isFirstDataLoading = false
            submitData(persons!![1])
        } else {
            isFirstDataLoading = true
            submitData(persons!![0])
        }
    }

    private fun initializeChart() {
        smoothLineChart = binding.smoothChart
        //SmoothLineChart.CHART_COLOR = ContextCompat.getColor(this, R.color.purple_500)
        //smoothLineChart.itemSize = 45f

    }

    private fun initializeExpandableList() {
        val adapter = ExpandableListAdapter()
        findViewById<RecyclerView>(R.id.rvExpandableList).adapter = adapter
        adapter.submitList(mutableListOf(1, 2, 3))
    }

    private fun initializeCalendar() {
        // calendar view manager is responsible for our displaying logic
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                return if (cal.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == calendar2.get(Calendar.MONDAY) &&
                    cal.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
                )
                    R.layout.today_calendar_item
                else
                    R.layout.calendar_item
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                holder.itemView.findViewById<TextView>(R.id.tv_date_calendar_item).text =
                    DateUtils.getDayNumber(date)
                holder.itemView.findViewById<TextView>(R.id.tv_day_calendar_item).text =
                    DateUtils.getDay3LettersName(date)

            }
        }
        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return false
            }
        }
        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        singleRowCalendar =
            findViewById<SingleRowCalendar>(R.id.main_single_row_calendar).apply {
                calendarViewManager = myCalendarViewManager
                calendarChangesObserver = myCalendarChangesObserver
                calendarSelectionManager = mySelectionManager
                setDates(getFutureDatesOfCurrentMonth())
                init()
            }

    }

    private fun getDatesOfNextMonth(): List<Date> {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 6)
        }
        val list = mutableListOf<Date>()

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        list.add(calendar.time)

        var daysCalculator = 0
        while (daysCalculator < 6) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            list.add(calendar.time)
            daysCalculator++
        }
        return list
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -6)
        }
        val list = mutableListOf<Date>()

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        list.add(calendar.time)

        var daysCalculator = 0
        while (daysCalculator < 6) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            list.add(calendar.time)
            daysCalculator++
        }
        return list.reversed()
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        var daysCalculator = 1
        list.add(calendar.time)
        while (daysCalculator != 7) {
            calendar.add(Calendar.DATE, +1)
            list.add(calendar.time)
            daysCalculator++
        }
        return list
    }

    private fun retrieveFiles(): String? {
        val jsonString: String
        try {
            jsonString = this.assets.open("jsonListing.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

}
