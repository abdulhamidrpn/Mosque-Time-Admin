package com.rpn.adminmosque.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import com.rpn.adminmosque.R
import com.rpn.adminmosque.ui.viewmodel.HomeViewModel
import com.rpn.adminmosque.utils.GeneralUtils.sdfDate
import com.rpn.adminmosque.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : CoroutineFragment() {

    val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val homeViewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.let {
            it.viewModel = homeViewModel
            it.executePendingBindings()
            it.lifecycleOwner = viewLifecycleOwner
        }

        homeViewModel.currentTimeSecond.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "currentSecond: $it")
            val aniSlide: Animation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.side_down
            )
            binding.tvTimeSecond.text = it
            binding.tvTimeSecond.startAnimation(aniSlide)
        })
        homeViewModel.currentTimeMinute.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "currentMinute: $it")
            val aniSlide: Animation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.side_down
            )
            binding.tvTimeMinute.text = it
            binding.tvTimeMinute.startAnimation(aniSlide)
        })
        homeViewModel.currentTimeHour.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "currentHour: $it")
            val aniSlide: Animation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.side_down
            )
            binding.tvTimeHour.text = it
            binding.tvTimeHour.startAnimation(aniSlide)
        })


        mainViewModel.masjidInfo.observe(viewLifecycleOwner, Observer {
            homeViewModel.masjidInfo.postValue(it)
        })
        homeViewModel.masjidInfo.observe(viewLifecycleOwner, Observer {
            homeViewModel.getMyMosqueTime {
                Log.d(TAG, "setTodayMosqueTime: " + it.peekContent().message)
            }
        })

        homeViewModel.todayMosqueTime.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                binding.apply {
                    container5Wakt.visibility = View.VISIBLE
                    containerHijriTime.visibility = View.VISIBLE
                    containerSunTime.visibility = View.VISIBLE
                    container5Wakt.visibility = View.VISIBLE
                    tvDate.text = sdfDate.format(homeViewModel.currentDate.value?.time)
                }
            }else{
                binding.apply {
                    container5Wakt.visibility = View.GONE
                    containerHijriTime.visibility = View.GONE
                    containerSunTime.visibility = View.GONE
                    container5Wakt.visibility = View.GONE
                    tvDate.text = sdfDate.format(Calendar.getInstance().time)
                }
            }
        })

    }


}