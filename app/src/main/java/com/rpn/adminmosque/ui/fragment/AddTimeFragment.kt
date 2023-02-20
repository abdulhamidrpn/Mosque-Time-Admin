package com.rpn.adminmosque.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.rpn.adminmosque.R
import com.rpn.adminmosque.databinding.FragmentAddTimeBinding
import com.rpn.adminmosque.model.Data
import com.rpn.adminmosque.model.DateDetails
import com.rpn.adminmosque.model.HijriLite
import com.rpn.adminmosque.model.TimingDetails
import com.rpn.adminmosque.ui.viewmodel.HomeViewModel
import com.rpn.adminmosque.utils.GeneralUtils.sdfDate
import com.rpn.adminmosque.utils.GeneralUtils.sdfDateMonth
import com.rpn.adminmosque.utils.GeneralUtils.setTimer
import com.rpn.adminmosque.utils.Status
import com.rpn.adminmosque.utils.displayText
import com.rpn.exchangebook.extensions.getHour
import com.rpn.exchangebook.extensions.getMinute
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


class AddTimeFragment : CoroutineFragment() {

    val TAG = "AddTimeFragment"
    private var _binding: FragmentAddTimeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    val homeViewModel by inject<HomeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTimeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setTimingOfDay()

        binding.calendarView
            .setOnDateChangeListener(
                CalendarView.OnDateChangeListener { view, year, monthOfYear, dayOfMonth ->
                    val cSelected = Calendar.getInstance()
                    cSelected[Calendar.YEAR] = year
                    cSelected[Calendar.MONTH] = monthOfYear
                    cSelected[Calendar.DAY_OF_MONTH] = dayOfMonth

                    // set this date in TextView for Display
                    binding.idTVDate.setText(sdfDate.format(cSelected.time))
                    mainViewModel.selectedDate.postValue(cSelected)
                })


        setSelectedDate()

        mainViewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            val dateMonth = date[Calendar.MONTH] + 1
            val dateYear = date[Calendar.YEAR]

            var dateData = mainViewModel.prayerTime.value?.data?.find {
                it.date?.readable == sdfDate.format(date.time)
            }

            if (dateData != null) {
                Log.d(TAG, "onViewCreated: DateData Already feached ${getDateData(dateData)}")
                if (binding.switchUpdateTime.isChecked) {
                    setTimingOfDay(dateData)
                }
                mainViewModel.dateLiteUltra.postValue(getDateData(dateData))
            } else {
                Log.d(TAG, "onViewCreated: DateData need to feached for $dateMonth")

                binding.pbLoading.visibility = View.VISIBLE
                mainViewModel.getPrayerTime(dateMonth.toString(), dateYear.toString()) { listData ->
                    dateData = mainViewModel.prayerTime.value?.data?.find {
                        it.date?.readable == sdfDate.format(date.time)
                    }
                    if (binding.switchUpdateTime.isChecked) {
                        dateData?.let { setTimingOfDay(it) }
                    }
                    mainViewModel.dateLiteUltra.postValue(dateData?.let { getDateData(it) })


                    binding.pbLoading.visibility = View.GONE
                }
            }

        })

        binding.btnUploadMosqueTime.setOnClickListener {

            if (mainViewModel.dateLiteUltra.value != null) {
                binding.btnUploadProgress.visibility = View.VISIBLE
                binding.btnUploadMosqueTime.visibility = View.GONE

                mainViewModel.updateSingleDateTime(
                    sdfDateMonth.format(mainViewModel.selectedDate.value?.time),
                    mainViewModel.dateLiteUltra.value
                ) {
                    if (it.peekContent().status == Status.SUCCESS) {
                        Toast.makeText(
                            requireContext(),
                            sdfDate.format(mainViewModel.selectedDate.value?.time) + " ${it.peekContent().message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            " ${it.peekContent().message}" + sdfDate.format(mainViewModel.selectedDate.value?.time),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                    binding.btnUploadProgress.visibility = View.GONE
                    binding.btnUploadMosqueTime.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(requireContext(), "Select Date Again", Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.prayerTime.observe(viewLifecycleOwner, Observer {
            it.data?.forEach {
                Log.d(TAG, "Prayer Time: Date ${it.date?.readable}")
            }
            var dateData = mainViewModel.prayerTime.value?.data?.find {
                it.date?.readable == sdfDate.format(mainViewModel.selectedDate.value?.time)
            }
            dateData?.let { setTimingOfDay(it) }
        })

        mainViewModel.isMasjidActivated.observe(viewLifecycleOwner, Observer {
            checkMosqueActivated()
        })
    }


    fun checkMosqueActivated() {

        if (settingsUtility.mosqueDocumentId.isNotEmpty() && settingsUtility.mosqueDocumentId != "") {
            //Requested for mosque registration
            if (settingsUtility.mosqueActivated) {
                binding.tvMsg.visibility = View.GONE
                binding.containerInfo.visibility = View.VISIBLE
            } else {
                binding.tvMsg.visibility = View.VISIBLE
                binding.tvMsg.text = "Your Request is Pending"
                binding.containerInfo.visibility = View.GONE
            }
        } else {
            //Register New Mosque
            binding.tvMsg.visibility = View.VISIBLE
            binding.tvMsg.text = "Register New Mosque"
            binding.containerInfo.visibility = View.GONE
        }
    }

    fun setTimingOfDay(dateData: Data? = null) {

        binding.apply {
            etShuruq.setText(dateData?.timings?.Sunrise?.split("(")?.first())
            etSunset.setText(dateData?.timings?.Sunset?.split("(")?.first())
            etFajar.setText(dateData?.timings?.Fajr?.split("(")?.first())
            etDhuhr.setText(dateData?.timings?.Dhuhr?.split("(")?.first())
            etAsr.setText(dateData?.timings?.Asr?.split("(")?.first())
            etMagrib.setText(dateData?.timings?.Maghrib?.split("(")?.first())
            etIsha.setText(dateData?.timings?.Isha?.split("(")?.first())
            etImsak.setText(dateData?.timings?.Imsak?.split("(")?.first())

            var sdfTimeAm = if (binding.switch24HourFormat.isChecked) SimpleDateFormat("HH:mm")
            else SimpleDateFormat("hh:mm aa")

            binding.switch24HourFormat.setOnCheckedChangeListener { compoundButton, b ->
                sdfTimeAm = if (b) SimpleDateFormat("HH:mm")
                else SimpleDateFormat("hh:mm aa")
            }

            etShuruq.setOnClickListener {
                val currentHour = etShuruq.getHour()
                val currentMinute = etShuruq.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etShuruq.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.sunrise = sdfTimeAm.format(it)
                }
            }

            etSunset.setOnClickListener {
                val currentHour = etSunset.getHour()
                val currentMinute = etSunset.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etSunset.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.sunset = sdfTimeAm.format(it)
                }
            }

            etImsak.setOnClickListener {
                val currentHour = etImsak.getHour()
                val currentMinute = etImsak.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etImsak.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.imsak =
                        sdfTimeAm.format(it)
                }
            }


            etFajar.setOnClickListener {
                val currentHour = etFajar.getHour()
                val currentMinute = etFajar.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etFajar.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.fajr = sdfTimeAm.format(it)
                }
            }

            etDhuhr.setOnClickListener {
                val currentHour = etDhuhr.getHour()
                val currentMinute = etDhuhr.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etDhuhr.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.dhuhr =
                        sdfTimeAm.format(it)
                }
            }

            etAsr.setOnClickListener {
                val currentHour = etAsr.getHour()
                val currentMinute = etAsr.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etAsr.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.asr = sdfTimeAm.format(it)
                }
            }

            etMagrib.setOnClickListener {
                val currentHour = etMagrib.getHour()
                val currentMinute = etMagrib.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etMagrib.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.maghrib =
                        sdfTimeAm.format(it)
                }
            }

            etIsha.setOnClickListener {
                val currentHour = etIsha.getHour()
                val currentMinute = etIsha.getMinute()
                setTimer(
                    requireContext(), defaultHour = currentHour, defaultMinute = currentMinute,
                    is24HourView = binding.switch24HourFormat.isChecked
                ) {
                    etIsha.setText(sdfTimeAm.format(it))
                    mainViewModel.dateLiteUltra.value?.timingDetails?.isha = sdfTimeAm.format(it)
                }
            }
        }
    }


    fun getDateData(dateData: Data): DateDetails {
        return DateDetails(
            dateData.date?.readable,
            HijriLite(
                dateData.date?.hijri?.date,
                dateData.date?.hijri?.month,
                dateData.date?.hijri?.weekday,
                dateData.date?.hijri?.year
            ),
            TimingDetails(
                fajr = binding.etFajar.text.trim().toString(),
                dhuhr = binding.etDhuhr.text.trim().toString(),
                asr = binding.etAsr.text.trim().toString(),
                maghrib = binding.etMagrib.text.trim().toString(),
                isha = binding.etIsha.text.trim().toString(),
                imsak = binding.etImsak.text.trim().toString()
            ),
            dateData.date?.timestamp,
            binding.etShuruq.text.trim().toString(),
            binding.etSunset.text.trim().toString()
        )
    }


    //setting Custom and selectable calendar

    private val selectedDates = mutableListOf<LocalDate>()
    private var selectedDate = LocalDate.now()
    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy") //"03 Jan 2023"

    fun setSelectedDate() {

        homeViewModel.allMosqueTime.observe(viewLifecycleOwner) {
            val selectedDatesOnline =
                it?.date?.map { it.readable }?.map { LocalDate.parse(it, formatter) }
            selectedDatesOnline?.let { it1 -> selectedDates.addAll(it1) }
            binding.monthCalendarView.notifyCalendarChanged()
        }
        val daysOfWeek = daysOfWeek()
        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text = daysOfWeek[index].displayText()
                textView.setTextColor(resources.getColor(R.color.colorTitleText))
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)

    }


    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = view.findViewById<TextView>(R.id.exOneDayText)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        binding.monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)
            }
        }
        binding.monthCalendarView.monthScrollListener = { updateTitle() }
        binding.monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()

        if (isSelectable) {
            when {
                selectedDates.contains(date) -> {
                    textView.setTextColor(resources.getColor(R.color.black))
                    textView.setBackgroundResource(R.drawable.example_1_selected_bg)
                }
                selectedDate == date -> {
                    textView.setTextColor(resources.getColor(R.color.colorTitleText))
                    textView.setBackgroundResource(R.drawable.example_1_today_bg)
                }
                else -> {
                    textView.setTextColor(resources.getColor(R.color.colorTitleText))
                    textView.background = null
                }
            }
        } else {
            textView.setTextColor(resources.getColor(R.color.colorSubTitleText))
            textView.background = null
        }
    }

    private fun dateClicked(date: LocalDate) {
        // Refresh both calendar views..
        binding.monthCalendarView.notifyDateChanged(selectedDate)
        selectedDate = date
        binding.monthCalendarView.notifyDateChanged(date)
        val cSelected = Calendar.getInstance()
        cSelected[Calendar.YEAR] = date.year
        cSelected[Calendar.MONTH] = date.monthValue.minus(1)
        cSelected[Calendar.DAY_OF_MONTH] = date.dayOfMonth

        binding.idTVDate.setText(sdfDate.format(cSelected.time))
        mainViewModel.selectedDate.postValue(cSelected)

    }


    private fun updateTitle() {
        val month = binding.monthCalendarView.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text = month.month.displayText(short = false)

    }
}
