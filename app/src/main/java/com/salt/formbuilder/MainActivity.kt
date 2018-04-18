package com.salt.formbuilder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.itcebook.util.FormBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val formBuilder = FormBuilder(this, lVFormContainer)

        formBuilder.addTxtWtchrListener(object : FormBuilder.TextWatcherListener {
            override fun onTextChanged(frmContainer: HashMap<String?, String?>) {


            }

        })
        formBuilder.addSpinnerListener(object : FormBuilder.OnSpinnerSelect {
            override fun itemSelected(position: Int, value: String) {

             //   showToast(value)
            }

        })
        formBuilder.addSubmitListener(object : FormBuilder.FormSubmitListener {

            override fun onSubmitForm(formValues: HashMap<String?, String?>) {
              //  uploadFrmToServer(formValues)


            }

            override fun onValidationError(errorList: ArrayList<String>) {
             //   showToast(errorList.toString())
            }

        })

        formBuilder.build(DemoModel().apply {
            listTypes = getTypeList()
            listCategories = getCatsList()
        })

    }



    fun getTypeList(): java.util.ArrayList<String> {
        val list = java.util.ArrayList<String>()

        list.add("Atta quality A")
        list.add("Atta quality B")
        list.add("Atta quality C")
        list.add("Atta quality D")

        return list
    }

    fun getCatsList(): java.util.HashMap<String, String> {
        val list = java.util.HashMap<String, String>()

        list.put("formA", "Category lose")
        list.put("formB", "Category hard")

        return list
    }



}
