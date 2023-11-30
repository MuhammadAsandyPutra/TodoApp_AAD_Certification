package com.dicoding.todoapp.ui.list

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers

import com.dicoding.todoapp.ui.add.AddTaskActivity
import com.dicoding.todoapp.R
import org.junit.Before
import org.junit.Test

//TODO 16 : Write UI test to validate when user tap Add Task (+), the AddTaskActivity displayed
class TaskActivityTest {
    @Before
    fun setUp(){
        ActivityScenario.launch(TaskActivity::class.java)
    }

    @Test
    fun test(){
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(AddTaskActivity::class.java.name))
        Intents.release()
    }

}