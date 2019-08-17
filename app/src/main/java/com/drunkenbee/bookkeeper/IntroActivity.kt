package com.drunkenbee.bookkeeper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.github.paolorotolo.appintro.AppIntro2
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2Fragment
import com.github.paolorotolo.appintro.model.SliderPage



class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_intro)

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.slide_title_1)
        sliderPage1.description = getString(R.string.slide1_desc)
        sliderPage1.imageDrawable = R.drawable.ic_question
        sliderPage1.bgColor = ContextCompat.getColor(this, R.color.colorPink)
        addSlide(AppIntro2Fragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.slide_title_2)
        sliderPage2.description = getString(R.string.slide2_desc)
        sliderPage2.imageDrawable = R.drawable.ic_happiness
        sliderPage2.bgColor = ContextCompat.getColor(this, R.color.colorBlue)
        addSlide(AppIntro2Fragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.slide_title_3)
        sliderPage3.description = getString(R.string.slide3_desc)
        sliderPage3.imageDrawable = R.drawable.ic_bell
        sliderPage3.bgColor = ContextCompat.getColor(this, R.color.colorItem)
        addSlide(AppIntro2Fragment.newInstance(sliderPage3))
        setFadeAnimation()
        showSkipButton(false)
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)

        val sharedPref = getSharedPreferences(getString(R.string.shared_preference_key),Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(getString(R.string.first_time), false)
            apply()
        }

        val i = Intent(
            this@IntroActivity,
            MainActivity::class.java
        )
        //Intent is used to switch from one activity to another.

        startActivity(i)
        finish()
    }
}
