package com.dicoding.storyapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

@SuppressLint("RestrictedApi")
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    crossinline action: Fragment.() -> Unit = {}
): ActivityScenario<HiltTestActivity> {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    )
    return ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        fragment.action()
    }
}
