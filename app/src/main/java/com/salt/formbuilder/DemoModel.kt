package com.salt.formbuilder

/**
 * Created by Salim.UM on 18-04-2018.
 */
class DemoModel {

    @FormElement(formType = FormBuilder.EDIT_TEXT, fieldOrder = 1, fieldPaddingLeft = 10, fieldHint = R.string.msg_enter_usr_name)
    var userName: String? = "Enter user name"
    @FormElement(formType = FormBuilder.EDIT_TEXT, fieldOrder = 2, fieldPaddingLeft = 10)
    var userRole: String? = null
    @FormElement(formType = FormBuilder.EDIT_EMAIL, fieldOrder = 3, fieldPaddingLeft = 10)
    var userEmail: String? = null
    @FormElement(formType = FormBuilder.SPINNER, fieldOrder = 4)
    var listTypes: ArrayList<String>? = null
    @FormElement(formType = FormBuilder.CHECK_BOX, fieldOrder = 5)
    var listCategories: HashMap<String, String>? = null

    @FormElement(formType = FormBuilder.BUTTON, fieldOrder = 6, fieldBGresource = R.drawable.sbmt_bttn)
    var userBtnSbmt: String = "Submit"
}