package com.example.exceedit

import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.util.*


class MainActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = getColor(R.color.purple_500)
        }

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
                val cal2 = Calendar.getInstance()
                cal.time = date
                /*// if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
                return if (isSelected)
                    when (cal[Calendar.DAY_OF_WEEK]) {

                        else -> R.layout.selected_calendar_item
                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        else -> */

                return R.layout.calendar_item

                ///}

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
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
                //   tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                //    tvDay.text = DateUtils.getDayName(date)
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
        val singleRowCalendar =
            findViewById<SingleRowCalendar>(R.id.main_single_row_calendar).apply {
                calendarViewManager = myCalendarViewManager
                calendarChangesObserver = myCalendarChangesObserver
                calendarSelectionManager = mySelectionManager
                setDates(getFutureDatesOfCurrentMonth())
                init()
            }

        findViewById<Button>(R.id.btnRight).setOnClickListener {
            singleRowCalendar.setDates(getDatesOfNextMonth())
        }

        findViewById<Button>(R.id.btnLeft).setOnClickListener {
            singleRowCalendar.setDates(getDatesOfPreviousMonth())
        }


        val chart = findViewById<View>(R.id.smoothChart) as SmoothLineChart
        chart.setData(
            arrayOf(
                PointF(15F, 3F),  // {x, y}
                PointF(20F, 1F),
                PointF(25F, 7F),
                PointF(30F, 4F),
                PointF(35F, 5F),
                PointF(40F, 5F),
                PointF(45F, 1F),
            )
        )

        /*  val chartES = findViewById<View>(R.id.smoothChartES) as SmoothLineChartEquallySpaced
          chartES.setData(floatArrayOf(15f, 21f, 9f, 21f, 25f, 35f, 24f, 28f))*/

        val adapter = ExpandableListAdapter()
        findViewById<RecyclerView>(R.id.rvExpandableList).adapter = adapter
        adapter.submitList(mutableListOf(1, 2, 3))
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


}
