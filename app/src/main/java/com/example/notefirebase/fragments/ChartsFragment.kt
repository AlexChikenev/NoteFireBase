package com.example.notefirebase.fragments

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.text.StaticLayout
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.withTranslation
import com.example.notefirebase.databinding.FragmentChartsBinding


class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Convert dp to px
    fun Context.dpToPx(dp: Int): Float {
        return dp.toFloat() * this.resources.displayMetrics.density
    }

    // Convert sp to px
    fun Context.spToPx(sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), this.resources.displayMetrics);
    }

    // Draw text
    fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float){
        canvas.withTranslation(x, y) {
            draw(this)
        }
    }

    companion object {
        fun newInstance() = ChartsFragment()
    }
}