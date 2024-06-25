package ru.hse.client.profile

// import android.os.Bundle
// import android.util.Log
// import com.applandeo.materialcalendarview.CalendarDay
// import com.applandeo.materialcalendarview.CalendarView
// import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
// import okhttp3.OkHttpClient
// import ru.hse.client.R
// import ru.hse.client.databinding.ActivityDeadlinesBinding
// import ru.hse.client.groups.enterGroup
// import ru.hse.client.utility.DrawerBaseActivity
// import ru.hse.client.utility.user
// import java.text.SimpleDateFormat
// import java.util.*
// import kotlin.collections.ArrayList
//
//
// class DeadlinesActivity: DrawerBaseActivity() {
//     private lateinit var binding: ActivityDeadlinesBinding
//     private lateinit var dataArrayList: ArrayList<DeadlineData?>
//     private lateinit var listViewAdapter: DeadlineAdapter
//     private var okHttpClient = OkHttpClient()
//
//     private lateinit var calendarView: CalendarView
//
//     override fun onCreate(savedInstanceState: Bundle?) {
//         user.setUserByLogin(this, user.getUserLogin())
//         super.onCreate(savedInstanceState)
//         binding = ActivityDeadlinesBinding.inflate(layoutInflater)
//         dataArrayList = ArrayList()
//         listViewAdapter = DeadlineAdapter(this, dataArrayList)
//
//
//         setContentView(binding.root)
//         allocateActivityTitle("Deadlines")
//         binding.deadlineList.adapter = listViewAdapter
//         calendarView = findViewById(R.id.calendar)
//         calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
//             override fun onClick(calendarDay: CalendarDay) {
//                 val clickedDayCalendar = calendarDay.calendar
//                 Log.e("SECOND", "KEK")
//                 Log.e("SECOND", clickedDayCalendar.get(Calendar.YEAR).toString())
//
//                 Log.e("SECOND", clickedDayCalendar.get(Calendar.MONTH).toString())
//
//                 Log.e("SECOND", clickedDayCalendar.get(Calendar.DAY_OF_MONTH).toString())
//                 clickedDayCalendar.set(Calendar.HOUR_OF_DAY, 0)
//                 clickedDayCalendar.set(Calendar.MINUTE, 0)
//                 clickedDayCalendar.set(Calendar.SECOND, 0)
//                 clickedDayCalendar.set(Calendar.MILLISECOND, 0)
//
//                 val timestampBegin = clickedDayCalendar.timeInMillis
//
//                 clickedDayCalendar.set(Calendar.HOUR_OF_DAY, 23)
//                 clickedDayCalendar.set(Calendar.MINUTE, 59)
//                 clickedDayCalendar.set(Calendar.SECOND, 59)
//                 clickedDayCalendar.set(Calendar.MILLISECOND, 0)
//
//                 val timestampEnd = clickedDayCalendar.timeInMillis
//
//                 drawDayDeadlines(timestampBegin, timestampEnd)
//             }
//         })
//         /*
//         calendarView.setOnCalendarDayClickListener { view, year, month, day ->
//             val calendar1 = Calendar.getInstance()
//             calendar1.set(Calendar.YEAR, year)
//             calendar1.set(Calendar.MONTH, month)
//             calendar1.set(Calendar.DAY_OF_MONTH, day)
//             calendar1.set(Calendar.HOUR_OF_DAY, 0)
//             calendar1.set(Calendar.MINUTE, 0)
//             calendar1.set(Calendar.SECOND, 0)
//             calendar1.set(Calendar.MILLISECOND, 0)
//             val timestampBegin = calendar1.timeInMillis
//
//             val calendar2 = Calendar.getInstance()
//             calendar2.set(Calendar.YEAR, year)
//             calendar2.set(Calendar.MONTH, month)
//             calendar2.set(Calendar.DAY_OF_MONTH, day)
//             calendar2.set(Calendar.HOUR_OF_DAY, 23)
//             calendar2.set(Calendar.MINUTE, 59)
//             calendar2.set(Calendar.SECOND, 59)
//             calendar2.set(Calendar.MILLISECOND, 0)
//
//             val timestampEnd = calendar2.timeInMillis
//
//             drawDayDeadlines(timestampBegin, timestampEnd)
//             Log.e("Deadline", timestampBegin.toString())
//             Log.e("Deadline", timestampEnd.toString())
//             Log.e("Deadline", year.toString())
//             Log.e("Deadline", month.toString())
//             Log.e("Deadline", day.toString())
//         }*/
//         drawAllDeadlines()
//     }
//
//     private fun countSolvedTests(chapterId: Long) : Int {
//         var solvedTests = 0
//         for (test in user.getUserModel()!!.passedTests.passedTestsList) {
//             if (test.chapterId == chapterId) {
//                 solvedTests++
//             }
//         }
//         return solvedTests
//     }
//
//     private fun drawAllDeadlines() {
//         val groups = user.getUserModel()!!.userOfGroups
//
//         dataArrayList.clear()
//         var listToHighlight = ArrayList<Calendar>()
//         Log.e("Deadline", groups.groupsList.size.toString())
//         for (group in groups.groupsList) {
//             val fullGroup = enterGroup(group, false, this, okHttpClient)
//             Log.e("Deadlines", group.name)
//             for (chapter in fullGroup!!.chapters.chaptersList) {
//                 if (countSolvedTests(chapter.id) != chapter.tests.testsList.size && chapter.deadlineTs.toInt() != -1
//                     && !isDeadlineOver(chapter.deadlineTs)) {
//                     val calendar = Calendar.getInstance()
//                     calendar.timeInMillis = chapter.deadlineTs
//                     listToHighlight.add(calendar)
//
//                     dataArrayList.add(
//                         DeadlineData(
//                             chapter.name,
//                             fullGroup.name,
//                             (chapter.tests.testsList.size - countSolvedTests(chapter.id)).toString() + " remaining",
//                             getTimeFromTimestamp(chapter.deadlineTs)
//                         )
//                     )
//                 }
//             }
//         }
//         calendarView.setHighlightedDays(ArrayList(listToHighlight))
//
//         listViewAdapter.notifyDataSetChanged()
//     }
//
//     private fun drawDayDeadlines(beginTs: Long, endTs: Long) {
//         val groups = user.getUserModel()!!.userOfGroups
//
//         dataArrayList.clear()
//         Log.e("Deadline", groups.groupsList.size.toString())
//         for (group in groups.groupsList) {
//             val fullGroup = enterGroup(group, false, this, okHttpClient)
//             Log.e("Deadlines", group.name)
//             for (chapter in fullGroup!!.chapters.chaptersList) {
//                 if (countSolvedTests(chapter.id) != chapter.tests.testsList.size && chapter.deadlineTs.toInt() != -1
//                     && !isDeadlineOver(chapter.deadlineTs) && chapter.deadlineTs >= beginTs && chapter.deadlineTs <= endTs) {
//                     dataArrayList.add(
//                         DeadlineData(
//                             chapter.name,
//                             fullGroup.name,
//                             (chapter.tests.testsList.size - countSolvedTests(chapter.id)).toString() + " remaining",
//                             getTimeFromTimestamp(chapter.deadlineTs)
//                         )
//                     )
//                 }
//             }
//         }
//         listViewAdapter.notifyDataSetChanged()
//     }
//
//     private fun getTimeFromTimestamp(timestamp: Long): String {
//         val calendar = Calendar.getInstance()
//         calendar.timeInMillis = timestamp
//
//         val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
//         val date = Date(timestamp)
//         val formattedDate = dateFormat.format(date)
//         return formattedDate
//     }
//
//     private fun isDeadlineOver(timestamp: Long): Boolean {
//         val calendar = Calendar.getInstance()
//         val currentTimestamp = calendar.timeInMillis
//         return timestamp < currentTimestamp
//     }
// }