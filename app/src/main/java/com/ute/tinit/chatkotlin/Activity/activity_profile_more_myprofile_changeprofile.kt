package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ute.tinit.chatkotlin.R
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile_changeprofile.*
import android.app.DatePickerDialog
import android.app.Dialog

class activity_profile_more_myprofile_changeprofile : AppCompatActivity() {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_more_myprofile_changeprofile)
        spinner()
        tvdateselect()
        loadData()
        firebase()
    }

    override fun onBackPressed() {

    }
    fun firebase()
    {

    }
    fun loadData()
    {
        var intent=intent
       // var hoten:String=intent.getStringExtra("userid")
        var ten:String=intent.getStringExtra("username")
        var email:String=intent.getStringExtra("email")
        user_name.text=ten
        tv_email.text=email
    }
    fun tvdateselect()
    {

        tv_date_select.setOnClickListener {
            showDialog(999)
        }
    }

    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
        // arg1 = year
        // arg2 = month
        // arg3 = day
        var temp=arg2+1
        tv_date_select.setText(""+arg3+"/"+temp+"/"+arg1)
    }

    override fun onCreateDialog(id: Int): Dialog? {
        // TODO Auto-generated method stub
        return if (id == 999) {
            DatePickerDialog(this, myDateListener, year, month, day)
        } else null
    }

    fun spinner()
    {
// Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.sex, android.R.layout.simple_spinner_item)
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
// Apply the adapter to the spinner
        sex_spinner.adapter = adapter
        sex_spinner.setSelection(0)
    }


}
