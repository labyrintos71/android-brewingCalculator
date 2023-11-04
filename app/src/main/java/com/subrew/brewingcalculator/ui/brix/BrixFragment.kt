package com.subrew.brewingcalculator.ui.brix

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.subrew.brewingcalculator.R
import com.subrew.brewingcalculator.databinding.FragmentBrixBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BrixFragment : Fragment() {

    private var _binding: FragmentBrixBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val brixViewModel =
            ViewModelProvider(this).get(BrixViewModel::class.java)

        _binding = FragmentBrixBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.editTotweight.addTextChangedListener {
            calcbrix()
        }
        binding.editFerweight.addTextChangedListener {
            calcbrix()
        }
        binding.editNowbrix.addTextChangedListener {
            calcbrix()
        }
        binding.editTargetbrix.addTextChangedListener {
            calcbrix()
        }
        val items = listOf("g", "kg")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.brixWeighttype.setText(adapter.getItem(0))
        binding.brixWeighttype.setAdapter(adapter)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun calcbrix() {
        if (binding.editTotweight.text.toString().isNotEmpty() &&
            binding.editNowbrix.text.toString().isNotEmpty() &&
            binding.editTargetbrix.text.toString().isNotEmpty()
        ) {
            //용기무게
            val fer = if (binding.editFerweight.text.toString()
                    .isEmpty()
            ) BigDecimal(0) else binding.editFerweight.text.toString().toBigDecimal()

            //용매
            val solvent = binding.editTotweight.text.toString().toBigDecimal().minus(fer)
                .multiply(BigDecimal(if (binding.brixWeighttype.text.toString() == "g") 1 else 1000))

            //현재당도
            val nowbrix = binding.editNowbrix.text.toString().toBigDecimal()

            //목표당도
            val targetbrix = binding.editTargetbrix.text.toString().toBigDecimal()

            //x = (tragetbrix-nowbrix)*solvent/100
            val sugar = targetbrix.subtract(nowbrix).multiply(solvent.divide(BigDecimal(100)))

            //nowbrix * solvent +75x = targetbrix * (solvent + x)
            //x= (targetbrix-nowbrix)solvent/(75-targetbrix)
            val honey =
                targetbrix.minus(nowbrix).multiply(solvent).div(BigDecimal(75).minus(targetbrix))

            //nowbrix * solvent = targetbrix * (solvent+x)
            //x=(nowbrix-targetbrix)*solvent/targetbrix
            val water = nowbrix.minus(targetbrix).multiply(solvent).div(targetbrix)

            binding.brixSugar.text = makeResult(sugar, solvent)
            binding.brixHoney.text = makeResult(honey, solvent)
            binding.brixWater.text = makeResult(water, solvent)
            binding.brixAlc.text =
                targetbrix.multiply(BigDecimal(0.58)).setScale(2, RoundingMode.HALF_EVEN)
                    .toString() + "%"
        }
    }

    private fun makeResult(soulte: BigDecimal, solvent: BigDecimal): String {
        if (binding.brixWeighttype.text.toString() == "g")
            return "${soulte.setScale(0, RoundingMode.HALF_EVEN)}g, 총 ${
                solvent.add(soulte).setScale(0, RoundingMode.HALF_EVEN)
            }g"
        else
            return "${
                soulte.divide(BigDecimal(1000)).setScale(3, RoundingMode.HALF_EVEN)
            }kg, 총 ${
                solvent.add(soulte).divide(BigDecimal(1000)).setScale(3, RoundingMode.HALF_EVEN)
            }kg"
    }
}