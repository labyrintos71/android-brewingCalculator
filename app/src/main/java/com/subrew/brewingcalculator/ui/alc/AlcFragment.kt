package com.subrew.brewingcalculator.ui.alc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.subrew.brewingcalculator.databinding.FragmentAlcBinding
import java.math.BigDecimal
import java.math.RoundingMode

class AlcFragment : Fragment() {

    private var _binding: FragmentAlcBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alcViewModel =
            ViewModelProvider(this).get(AlcViewModel::class.java)

        _binding = FragmentAlcBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.alcDefaultweight.setText("1275")
        binding.alcFirstweight.setText("3651")
        binding.alcNowweight.setText("0")
        binding.alcDefaultweight.addTextChangedListener { calAlc() }
        binding.alcFirstweight.addTextChangedListener { calAlc() }
        binding.alcNowweight.addTextChangedListener { calAlc() }

//        val textView: TextView = binding.textGallery
//        alcViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calAlc() {
        val first = if (binding.alcFirstweight.text.toString()
                .isNotEmpty()
        ) binding.alcFirstweight.text.toString().toBigDecimal() else BigDecimal(3651)
        val now = if (binding.alcNowweight.text.toString()
                .isNotEmpty()
        ) binding.alcNowweight.text.toString().toBigDecimal() else BigDecimal(0)
        val def = if (binding.alcDefaultweight.text.toString()
                .isNotEmpty()
        ) binding.alcDefaultweight.text.toString().toBigDecimal() else BigDecimal(1275)


        // 알코올 도수 = (처음 총무게 - 현재 총 무게)/(현재 총무게 - 용기의 무게)
        binding.alcResult.text = "${
            first.minus(now).divide(now.minus(def),4, RoundingMode.HALF_EVEN).multiply(
                BigDecimal(100)
            ).setScale(2)
        }%"

    }
}