package com.example.sberproject.view.fragments

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sberproject.ui.articles.InstructionCallback

class InstructionAdapter(private val fragment: Fragment, private val instruction: Instruction) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        if (position == 0)
            return ImageSlideFragment.newInstance(instruction.before)
        if (position == 1)
            return TextSlideFragment.newInstance(instruction.instruction)
        (fragment as InstructionCallback?)?.makeButtonVisible()
        return ImageSlideFragment.newInstance(instruction.after)
    }
}