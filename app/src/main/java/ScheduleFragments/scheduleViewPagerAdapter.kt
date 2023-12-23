package ScheduleFragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class scheduleViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle ): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        if(position==0)
           return MondayFragment()
        else if(position==1)
            return TuesdayFragment()
        else if(position==2)
            return WednesdayFragment()
        else if(position==3)
            return ThursdayFragment()
        else if(position==4)
            return FridayFragment()
        else if(position==5)
            return SaturdayFragment()
        else if(position==6)
            return SundayFragment()
        else return MondayFragment()

    }

}