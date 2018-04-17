package com.itcebook.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.lang.reflect.Field
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter
import com.salt.formbuilder.FormElement
import com.salt.formbuilder.R
import kotlin.collections.HashMap


/**
 * Created by Salim.UM on 20-03-2018.
 */
class FormBuilder(private var context: Context, private var mLinearLayout: LinearLayout) {

    private val frmCntainerMap = HashMap<String?, String?>()
    private var frmObj: Any? = null
    private var textViewSize = 14f
    private var editTextSize = 14f
    private var btnTextSize = 14f
    private var fieldParamHight = 60f
    private var btnParamHight = 40f
    private val TAGBTNSBMT: String = "BTN_SUBMT"
    private var mListener: FormSubmitListener? = null
    private var mSpinListener: OnSpinnerSelect? = null
    private var mTxtWtchrListener: TextWatcherListener? = null
    val errorList = ArrayList<String>()


    fun build(frmObj: Any) {
        this.frmObj = frmObj
        val declaredFields = frmObj.javaClass.declaredFields
        orderDeclaredFields(declaredFields)

        declaredFields.forEach {
            it.isAccessible = true
            val annotaionType = it.getAnnotation(FormElement::class.java)
            addFieldToForm(annotaionType, it)
        }
    }



    fun addSubmitListener(mListener: FormSubmitListener) {
        this.mListener = mListener
    }

    fun addSpinnerListener(mSpinListener: OnSpinnerSelect) {
        this.mSpinListener = mSpinListener
    }

    fun addTxtWtchrListener(mTxtWtchrListener: TextWatcherListener) {
        this.mTxtWtchrListener = mTxtWtchrListener
    }

    private fun orderDeclaredFields(declaredFields: Array<Field>?) {

        Arrays.sort(declaredFields, object : Comparator<Field> {

            override fun compare(field1: Field?, field2: Field?): Int {
                field1?.isAccessible = true
                field2?.isAccessible = true

                val orderField1 = field1?.getAnnotation(FormElement::class.java)?.fieldOrder
                val orderField2 = field2?.getAnnotation(FormElement::class.java)?.fieldOrder

                return orderField1!! - orderField2!!

            }

        })

    }

    private fun addFieldToForm(annotaion: FormElement?, frmField: Field?) {
        if (annotaion == null || annotaion.formType == null) {
            throw NullPointerException("Annotaion is null")
        }

        when (annotaion.formType) {
            EDIT_TEXT -> {
                addEditTextField(frmField, annotaion, InputType.TYPE_CLASS_TEXT)
            }
            EDIT_EMAIL -> {
                addEditTextField(frmField, annotaion, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            }
            EDIT_PHONE -> {
                addEditTextField(frmField, annotaion, InputType.TYPE_CLASS_PHONE)
            }
            TEXT_VIEW -> {
                addTextView(frmField, annotaion)
            }
            BUTTON -> {
                addSubmtBtnToForm(annotaion)
            }
            SPINNER -> {
                addSpinnerToForm(frmField)
            }
            CHECK_BOX -> {
                addCheckBxToForm(frmField, annotaion)
            }

        }


    }

    private fun addCheckBxToForm(frmField: Field?, annotaion: FormElement?) {
        if (frmField!!.get(frmObj) !is HashMap<*, *>?) {
            return
        }

        val dataSet = frmField.get(frmObj) as HashMap<String, String>?

        if (dataSet != null && dataSet.size > 0) {
            val layoutHorizontal = LinearLayout(context)
            layoutHorizontal.orientation = LinearLayout.HORIZONTAL
            layoutHorizontal.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)

            dataSet.entries.forEach {
                val checkBox = CheckBox(context)
                checkBox.tag = it.key
                checkBox.text = it.value
                if (annotaion?.fieldTextHight!! > 0) {
                    checkBox.textSize = annotaion.fieldTextHight
                }
                if (annotaion.fieldTextColor > 0) {
                    checkBox.setTextColor(ContextCompat.getColor(context, annotaion.fieldTextColor))
                }

                frmCntainerMap[it.key] = null
                layoutHorizontal.addView(checkBox)
                checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

                    override fun onCheckedChanged(btn: CompoundButton?, checked: Boolean) {
                        val tag = btn?.tag.toString()
                        if (checked) {
                            frmCntainerMap[tag] = "true"
                        } else {
                            frmCntainerMap[tag] = null
                        }
                    }

                })
            }
            mLinearLayout.addView(layoutHorizontal)
        }

    }

    private fun addSpinnerToForm(frmField: Field?) {
        if (frmField!!.get(frmObj) !is ArrayList<*>)
            return

        val spinner = Spinner(context)
        spinner.layoutParams = getLayoutParamsWithHight(fieldParamHight)

        val dataSet = frmField.get(frmObj) as ArrayList<String>?

        if (dataSet != null && dataSet.size > 0) {
            val dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dataSet)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = dataAdapter
            mLinearLayout.addView(spinner)

            frmCntainerMap[frmField.name] = dataSet[0]
        }

        if (mSpinListener != null) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {


                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                    frmCntainerMap[frmField.name] = dataSet?.get(position)
                    mSpinListener!!.itemSelected(position, dataSet?.get(position)!!)
                }

            }
        }


    }

    private val errorTag = "_error"

    private fun addEditTextField(frmField: Field?, annotaion: FormElement?, inputType: Int) {

        val ediText = EditText(context)
        ediText.textSize = pxFromDp(editTextSize)
        if (annotaion?.fieldTextHight!! > 0) {
            ediText.textSize = pxFromDp(annotaion.fieldTextHight)
        }
        ediText.layoutParams = getLayoutParamsWithHight(fieldParamHight)
        if (annotaion.fieldHight > 0) {
            ediText.layoutParams = getLayoutParamsWithHight(annotaion.fieldHight.toFloat())
        }

        ediText.setPadding(annotaion.fieldPaddingLeft, annotaion.fieldPaddingTop
                , annotaion.fieldPaddingRight, annotaion.fieldPaddingBottom)

        ediText.hint = frmField?.getClearName()
        if (annotaion.fieldHint > 0) {
            ediText.hint = context.getString(annotaion.fieldHint)
        }

        if (annotaion.fieldText > 0) {
            ediText.setText(context.getString(annotaion.fieldText))
        }

        ediText.tag = frmField?.name
        ediText.inputType = inputType
        ediText.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        if (annotaion.fieldTextColor > 0) {
            ediText.setTextColor(ContextCompat.getColor(context, annotaion.fieldTextColor))
        }
        if (mTxtWtchrListener != null) {
            ediText.addTextChangedListener(GenericWatcher(ediText))
        }

        frmCntainerMap[frmField?.name] = null
        mLinearLayout.addView(ediText)

        val tvErrorView = TextView(context)
        tvErrorView.tag = frmField?.name + errorTag
        if (annotaion.fieldError > 0) {
            tvErrorView.text = context.getString(annotaion.fieldError)
            tvErrorView.setTextColor(ContextCompat.getColor(context, R.color.red))
            tvErrorView.textSize = pxFromDp(12f)
            tvErrorView.visibility = View.GONE
        }
        mLinearLayout.addView(tvErrorView)
    }


    private fun addSubmtBtnToForm(annotaion: FormElement?) {
        val btn = Button(context)
        btn.textSize = pxFromDp(btnTextSize)
        btn.layoutParams = getLayoutParamsWithHight(btnParamHight)
        btn.tag = TAGBTNSBMT

        if (annotaion?.fieldBGresource ?: 0 > 0)
            btn.background = ContextCompat.getDrawable(context, annotaion?.fieldBGresource!!)

        btn.text = "Submit"
        if (annotaion?.fieldText!! > 0) {
            btn.text = context.getString(annotaion.fieldText)
        }

        btn.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        if (annotaion.fieldTextColor > 0) {
            btn.setTextColor(ContextCompat.getColor(context, annotaion.fieldTextColor))
        }

        mLinearLayout.addView(btn)
        onSubmitClicked()

    }


    private fun validateFrm(): Boolean {
        errorList.clear()
        frmCntainerMap.entries.forEach {
            val tag = it.key
            val view = mLinearLayout.findViewWithTag<View>(tag)

            if (view != null && view is EditText && view.text.isEmpty()) {
                errorList.add(tag!!)
                val viewError = mLinearLayout.findViewWithTag<TextView>(tag + errorTag)
                if (viewError != null) {
                    viewError.visibility = View.VISIBLE
                }

            }

        }
        if (errorList.size > 0) {
            return false
        }

        return true

    }

    private fun onSubmitClicked() {
        val btnView = mLinearLayout.findViewWithTag<Button>(TAGBTNSBMT)
        btnView.setOnClickListener {

            if (validateFrm()) {
                loadFrmTextToMap()
                mListener?.onSubmitForm(frmCntainerMap)
            } else {
                mListener?.onValidationError(errorList)
            }


        }
    }

    private fun loadFrmTextToMap() {

        frmCntainerMap.entries.forEach {
            val tag = it.key
            val view = mLinearLayout.findViewWithTag<View>(tag)
            if (view != null && view is EditText) {
                frmCntainerMap[tag] = view.text.toString()
            }

        }


    }


    private fun addTextView(frmField: Field?, annotaion: FormElement?) {
        val textView = TextView(context)
        textView.textSize = pxFromDp(textViewSize)
        if (annotaion?.fieldTextHight!! > 0) {
            textView.textSize = pxFromDp(annotaion.fieldTextHight)
        }
        textView.text = frmField?.getClearName()
        if (annotaion.fieldText > 0) {
            textView.text = context.getString(annotaion.fieldText)
        }
        textView.tag = frmField?.name
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        if (annotaion.fieldTextColor > 0) {
            textView.setTextColor(ContextCompat.getColor(context, annotaion.fieldTextColor))
        }
        mLinearLayout.addView(textView)

    }

    private fun getLayoutParamsWithHight(hight: Float): LinearLayout.LayoutParams {
        val deviceMtrx = pxFromDp(hight).toInt()
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, deviceMtrx)
    }

    private fun Field.getClearName(): String? {
        val nameArr = this.name.toCharArray()
        val newName = StringBuilder()


        nameArr.forEach {
            if (it.isUpperCase()) {
                newName.append(" ")
            }
            newName.append(it.toLowerCase())

        }

        return newName[0].toUpperCase() + "" + newName.toString().substring(1)

    }

    private fun pxFromDp(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }


    companion object {
        const val TEXT_VIEW = 1
        const val EDIT_TEXT = 2
        const val SPINNER = 3
        const val BUTTON = 4
        const val EDIT_NUMBER = 5
        const val EDIT_EMAIL = 6
        const val EDIT_PHONE = 7
        const val CHECK_BOX = 8
    }


    inner class GenericWatcher(var view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            if (text.isNullOrEmpty())
                return

            frmCntainerMap[view.tag.toString()] = text
            mTxtWtchrListener?.onTextChanged(frmCntainerMap)

            if (view !is EditText)
                return

            val viewError = mLinearLayout.findViewWithTag<TextView>(view.tag.toString() + errorTag)
            if (viewError != null) {
                viewError.visibility = View.GONE
            }


        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    interface FormSubmitListener {
        fun onSubmitForm(formValues: HashMap<String?, String?>)
        fun onValidationError(message: ArrayList<String>)
    }

    interface OnSpinnerSelect {
        fun itemSelected(position: Int, value: String)
    }

    interface TextWatcherListener {
        fun onTextChanged(frmContainer: HashMap<String?, String?>)
    }
}


